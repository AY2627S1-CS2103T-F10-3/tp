package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.TypicalAttendance.ATTENDANCE_ABSENT;
import static seedu.address.testutil.TypicalAttendance.ATTENDANCE_PRESENT;
import static seedu.address.testutil.TypicalAttendance.MATH_LESSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPayments.PAYMENT_NOVEMBER;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.attendance.AttendanceStatus;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.Subject;
import seedu.address.model.payment.Amount;
import seedu.address.model.payment.Payment;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class AttendanceCommandTest {

    private final Model model = new ModelManager();
    private final Person student = new PersonBuilder().withLessons(MATH_LESSON)
            .withPayments(PAYMENT_NOVEMBER).build();

    @Test
    public void execute_enrolledStudent_recordsBothStatusesAndKeepsPayments() throws Exception {
        model.addPerson(student);
        CommandResult result = new AttendanceCommand(INDEX_FIRST_PERSON, ATTENDANCE_PRESENT).execute(model);
        new AttendanceCommand(INDEX_FIRST_PERSON, ATTENDANCE_ABSENT).execute(model);
        Person updated = model.getFilteredPersonList().getFirst();
        assertEquals(List.of(ATTENDANCE_PRESENT, ATTENDANCE_ABSENT), updated.getAttendance());
        assertEquals(student.getPayments(), updated.getPayments());
        assertEquals(String.format(AttendanceCommand.MESSAGE_SUCCESS, student.getName(), ATTENDANCE_PRESENT),
                result.getFeedbackToUser());
    }

    @Test
    public void execute_duplicateOrConflictingStatus_rejectedWithoutMutation() throws Exception {
        model.addPerson(student);
        new AttendanceCommand(INDEX_FIRST_PERSON, ATTENDANCE_PRESENT).execute(model);
        assertCommandFailure(new AttendanceCommand(INDEX_FIRST_PERSON, ATTENDANCE_PRESENT), model,
                Person.MESSAGE_DUPLICATE_ATTENDANCE);
        Attendance conflict = new Attendance(MATH_LESSON.getId(), ATTENDANCE_PRESENT.getDate(),
                AttendanceStatus.ABSENT);
        assertCommandFailure(new AttendanceCommand(INDEX_FIRST_PERSON, conflict), model,
                Person.MESSAGE_DUPLICATE_ATTENDANCE);
    }

    @Test
    public void execute_differentLessonsOnSameDate_allowed() throws Exception {
        Lesson english = new Lesson(MATH_LESSON.getYear(), Subject.ENGLISH, MATH_LESSON.getDay(),
                MATH_LESSON.getStartTime(), MATH_LESSON.getEndTime(), 100);
        model.addPerson(new PersonBuilder(student).withLessons(MATH_LESSON, english).build());
        Attendance englishRecord = new Attendance(english.getId(), english.getUid(), ATTENDANCE_PRESENT.getDate(),
                AttendanceStatus.ABSENT);
        new AttendanceCommand(INDEX_FIRST_PERSON, ATTENDANCE_PRESENT).execute(model);
        new AttendanceCommand(INDEX_FIRST_PERSON, englishRecord).execute(model);
        assertEquals(List.of(ATTENDANCE_PRESENT, englishRecord),
                model.getFilteredPersonList().getFirst().getAttendance());
    }

    @Test
    public void execute_invalidIndexOrLesson_rejectedWithoutMutation() {
        model.addPerson(student);
        assertCommandFailure(new AttendanceCommand(INDEX_SECOND_PERSON, ATTENDANCE_PRESENT), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        Attendance unknown = new Attendance("unknown", ATTENDANCE_PRESENT.getDate(), AttendanceStatus.PRESENT);
        assertCommandFailure(new AttendanceCommand(INDEX_FIRST_PERSON, unknown), model,
                AttendanceCommand.MESSAGE_NOT_ENROLLED);
        model.setPerson(student, new PersonBuilder(student).withLessons().build());
        assertCommandFailure(new AttendanceCommand(INDEX_FIRST_PERSON, ATTENDANCE_PRESENT), model,
                AttendanceCommand.MESSAGE_NOT_ENROLLED);
    }

    @Test
    public void execute_wrongYearOrWeekday_rejectedWithoutMutation() {
        model.addPerson(student);
        for (LocalDate date : List.of(LocalDate.of(2025, 10, 20), LocalDate.of(2026, 10, 20))) {
            Attendance record = new Attendance(MATH_LESSON.getId(), date, AttendanceStatus.PRESENT);
            assertCommandFailure(new AttendanceCommand(INDEX_FIRST_PERSON, record), model,
                    AttendanceCommand.MESSAGE_INVALID_SESSION_DATE);
        }
    }

    @Test
    public void execute_filteredList_allCommandsUseDisplayedIndex() throws Exception {
        Person hidden = new PersonBuilder().withName("Hidden Student").build();
        model.addPerson(hidden);
        model.addPerson(student);
        model.updateFilteredPersonList(person -> person.getName().equals(student.getName()));
        new AttendanceCommand(INDEX_FIRST_PERSON, ATTENDANCE_PRESENT).execute(model);
        assertEquals(String.format(ViewAttendanceCommand.MESSAGE_HEADER, student.getName())
                + "\n1. " + ATTENDANCE_PRESENT,
                new ViewAttendanceCommand(INDEX_FIRST_PERSON).execute(model).getFeedbackToUser());
        assertEquals(hidden, model.getAddressBook().getPersonList().getFirst());
        new DeleteAttendanceCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON).execute(model);
        assertEquals(student, model.getFilteredPersonList().getFirst());
        assertCommandFailure(new AttendanceCommand(INDEX_SECOND_PERSON, ATTENDANCE_PRESENT), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_editAndPaymentCommands_preserveAttendance() throws Exception {
        model.addPerson(student.withAttendance(List.of(ATTENDANCE_PRESENT)));
        Payment balance = new Payment(new Amount("20"), PAYMENT_NOVEMBER.getMonth(), PAYMENT_NOVEMBER.getDate());
        new PayCommand(INDEX_FIRST_PERSON, balance).execute(model);
        assertEquals(List.of(ATTENDANCE_PRESENT), model.getFilteredPersonList().getFirst().getAttendance());
        new DeletePaymentCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON).execute(model);
        EditCommand.EditPersonDescriptor edits = new EditCommand.EditPersonDescriptor();
        edits.setName(new Name("Renamed Student"));
        edits.setLessons(Set.of());
        new EditCommand(INDEX_FIRST_PERSON, edits).execute(model);
        Person updated = model.getFilteredPersonList().getFirst();
        assertEquals(List.of(ATTENDANCE_PRESENT), updated.getAttendance());
        assertEquals(List.of(PAYMENT_NOVEMBER), updated.getPayments());
        // Historical records remain removable after unenrolment.
        new DeleteAttendanceCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON).execute(model);
        assertEquals(List.of(PAYMENT_NOVEMBER), model.getFilteredPersonList().getFirst().getPayments());
    }

    @Test
    public void equals_comparesStudentAndRecord() {
        AttendanceCommand command = new AttendanceCommand(INDEX_FIRST_PERSON, ATTENDANCE_PRESENT);
        assertEquals(command, new AttendanceCommand(INDEX_FIRST_PERSON, ATTENDANCE_PRESENT));
        assertNotEquals(command, new AttendanceCommand(INDEX_SECOND_PERSON, ATTENDANCE_PRESENT));
        assertNotEquals(command, new AttendanceCommand(INDEX_FIRST_PERSON, ATTENDANCE_ABSENT));
        assertNotEquals(command, null);
        assertNotEquals(command, new ClearCommand());
    }
}
