package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.VALID_LESSON_MATH;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.attendance.AttendanceStatus;
import seedu.address.model.lesson.Day;
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
    public void execute_editLesson_preservesHistoricalAttendanceLessonId() throws Exception {
        ModelManager model = new ModelManager();
        model.addLesson(VALID_LESSON_MATH);
        Attendance historicalAttendance = new Attendance(VALID_LESSON_MATH.getId(),
                LocalDate.of(2026, 10, 19), AttendanceStatus.PRESENT);
        model.addPerson(new PersonBuilder().withLessons(VALID_LESSON_MATH)
                .withAttendance(historicalAttendance).build());

        EditLessonCommand command = new EditLessonCommand(VALID_LESSON_MATH.getId(), null, null,
                Day.TUESDAY, null, null, null);
        command.execute(model);

        assertEquals(VALID_LESSON_MATH.getId(), model.getFilteredPersonList().getFirst()
                .getAttendance().getFirst().getLessonId());
    }
}
