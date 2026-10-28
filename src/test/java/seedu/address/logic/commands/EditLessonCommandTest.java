package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.VALID_LESSON_MATH;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPayments.PAYMENT_NOVEMBER;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.attendance.AttendanceStatus;
import seedu.address.model.attendance.exceptions.AttendanceConflictException;
import seedu.address.model.lesson.Day;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Time;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class EditLessonCommandTest {
    @Test
    public void execute_partialEdit_updatesLesson() throws Exception {
        ModelManager model = new ModelManager();
        model.addLesson(VALID_LESSON_MATH);

        EditLessonCommand command = new EditLessonCommand(VALID_LESSON_MATH.getId(), null, null,
                null, null, null, 40);
        command.execute(model);

        assertEquals(40, model.getAddressBook().getLessonList().get(0).getCost());
    }

    @Test
    public void execute_editEnrolledLesson_updatesStudentReference() throws Exception {
        ModelManager model = new ModelManager();
        model.addLesson(VALID_LESSON_MATH);
        Person student = new PersonBuilder().withLessons(VALID_LESSON_MATH).build();
        model.addPerson(student);

        EditLessonCommand command = new EditLessonCommand(VALID_LESSON_MATH.getId(), null, Subject.MATH,
                Day.MONDAY, Time.TEN, Time.ELEVEN, 40);
        command.execute(model);

        assertEquals(Set.of(model.getAddressBook().getLessonList().get(0)),
                model.getAddressBook().getPersonList().get(0).getLessons());
        assertEquals(40, model.getAddressBook().getPersonList().get(0).getLessons().iterator().next().getCost());
    }

    @Test
    public void execute_missingLesson_throwsCommandException() {
        ModelManager model = new ModelManager();
        EditLessonCommand command = new EditLessonCommand(VALID_LESSON_MATH.getId(), null, null,
                null, null, null, 40);

        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void execute_invalidFinalTimeRange_throwsCommandException() {
        ModelManager model = new ModelManager();
        model.addLesson(VALID_LESSON_MATH);

        EditLessonCommand command = new EditLessonCommand(VALID_LESSON_MATH.getId(), null, null,
                null, Time.TWELVE, null, null);

        assertThrows(CommandException.class, EditLessonCommand.MESSAGE_INVALID_TIME_RANGE, () ->
                command.execute(model));
        assertEquals(VALID_LESSON_MATH, model.getAddressBook().getLessonList().get(0));
    }

    @Test
    public void execute_editLesson_updatesAttendanceIdButPreservesHistoricalDateAndStatus() throws Exception {
        ModelManager model = new ModelManager();
        model.addLesson(VALID_LESSON_MATH);
        Attendance historicalAttendance = new Attendance(VALID_LESSON_MATH.getId(),
                LocalDate.of(2026, 10, 19), AttendanceStatus.PRESENT);
        model.addPerson(new PersonBuilder().withLessons(VALID_LESSON_MATH)
                .withAttendance(historicalAttendance).build());

        EditLessonCommand command = new EditLessonCommand(VALID_LESSON_MATH.getId(), null, null,
                Day.TUESDAY, null, null, null);
        command.execute(model);

        assertEquals(List.of(new Attendance(model.getAddressBook().getLessonList().getFirst().getId(),
                historicalAttendance.getDate(), historicalAttendance.getStatus())),
                model.getFilteredPersonList().getFirst().getAttendance());
    }

    @Test
    public void execute_editEndTime_rejectsConflictingStatusForExistingSession() throws Exception {
        ModelManager model = new ModelManager();
        Attendance recorded = new Attendance(VALID_LESSON_MATH.getId(),
                LocalDate.of(2026, 10, 19), AttendanceStatus.PRESENT);
        model.addPerson(new PersonBuilder().withLessons(VALID_LESSON_MATH).withAttendance(recorded).build());
        new EditLessonCommand(VALID_LESSON_MATH.getId(), null, null, null, null, Time.TWELVE, null)
                .execute(model);

        String newId = model.getAddressBook().getLessonList().getFirst().getId();
        Attendance conflicting = new Attendance(newId, recorded.getDate(), AttendanceStatus.ABSENT);
        assertCommandFailure(new AttendanceCommand(INDEX_FIRST_PERSON, conflicting), model,
                Person.MESSAGE_DUPLICATE_ATTENDANCE);
    }

    @Test
    public void execute_editLesson_updatesFormerStudentsAndPreservesOtherData() throws Exception {
        ModelManager model = new ModelManager();
        model.addLesson(VALID_LESSON_MATH);
        Attendance recorded = new Attendance(VALID_LESSON_MATH.getId(),
                LocalDate.of(2026, 10, 19), AttendanceStatus.ABSENT);
        Attendance unrelated = new Attendance("2026-ENGLISH-MONDAY-TEN-ELEVEN",
                recorded.getDate(), AttendanceStatus.PRESENT);
        Person formerStudent = new PersonBuilder().withLessons().withPayments(PAYMENT_NOVEMBER)
                .withAttendance(recorded, unrelated).build();
        model.addPerson(formerStudent);
        new EditLessonCommand(VALID_LESSON_MATH.getId(), null, null, null, null, Time.TWELVE, null)
                .execute(model);

        String newId = model.getAddressBook().getLessonList().getFirst().getId();
        assertEquals(formerStudent.withAttendance(List.of(
                new Attendance(newId, recorded.getDate(), recorded.getStatus()), unrelated)),
                model.getAddressBook().getPersonList().getFirst());
    }

    @Test
    public void execute_editLessonWithHistoricalIdCollision_failsWithoutPartialChanges() {
        ModelManager model = new ModelManager();
        Lesson edited = new Lesson(VALID_LESSON_MATH.getYear(), VALID_LESSON_MATH.getSubject(),
                VALID_LESSON_MATH.getDay(), VALID_LESSON_MATH.getStartTime(), Time.TWELVE,
                VALID_LESSON_MATH.getCost());
        Attendance recorded = new Attendance(VALID_LESSON_MATH.getId(),
                LocalDate.of(2026, 10, 19), AttendanceStatus.PRESENT);
        Attendance conflicting = new Attendance(edited.getId(), recorded.getDate(), AttendanceStatus.ABSENT);
        model.addPerson(new PersonBuilder().withName("First Student").withLessons(VALID_LESSON_MATH)
                .withAttendance(recorded).build());
        model.addPerson(new PersonBuilder().withName("Second Student").withLessons(VALID_LESSON_MATH)
                .withAttendance(recorded, conflicting).build());

        assertCommandFailure(new EditLessonCommand(VALID_LESSON_MATH.getId(), null, null,
                null, null, Time.TWELVE, null), model, new AttendanceConflictException().getMessage());
        assertEquals(List.of(VALID_LESSON_MATH), model.getAddressBook().getLessonList());
    }
}
