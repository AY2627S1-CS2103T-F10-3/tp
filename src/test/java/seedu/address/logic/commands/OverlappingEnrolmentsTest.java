package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.ModelManager;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.attendance.AttendanceStatus;
import seedu.address.model.lesson.Day;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonRevision;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Time;
import seedu.address.model.lesson.exceptions.LessonException;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class OverlappingEnrolmentsTest {
    private static final YearMonth OCTOBER = YearMonth.of(2026, 10);
    private static final YearMonth NOVEMBER = OCTOBER.plusMonths(1);
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-10-29T12:00:00Z"), ZoneOffset.UTC);
    private static final String MONDAY_ID = "2026-MATH-MONDAY-TEN-ELEVEN";
    private static final String TUESDAY_ID = "2026-MATH-TUESDAY-TEN-ELEVEN";

    @TempDir
    private Path temporaryDirectory;

    private static Lesson mondayLesson() {
        return new Lesson(Year.of(2026), Subject.MATH, Day.MONDAY,
                Time.TEN, Time.ELEVEN, 30);
    }

    private static Lesson movedLesson() {
        return mondayLesson().withRevision(new LessonRevision(Year.of(2026),
                Subject.MATH, Day.TUESDAY, Time.TEN, Time.ELEVEN, 30, NOVEMBER), OCTOBER);
    }

    @Test
    public void execute_editToReusedSlot_rejectsWithoutChangingFeesOrAttendance() throws Exception {
        Lesson moved = movedLesson();
        ModelManager model = new ModelManager();
        Person original = new PersonBuilder().withLessons(moved).build();
        model.addPerson(original);
        model.addLesson(mondayLesson());
        new AttendanceCommand(INDEX_FIRST_PERSON,
                new Attendance(TUESDAY_ID, LocalDate.of(2026, 10, 19), AttendanceStatus.PRESENT)).execute(model);
        Person before = model.getFilteredPersonList().getFirst();
        List<Lesson> catalogBefore = List.copyOf(model.getAddressBook().getLessonList());

        assertCommandFailure(new AddressBookParser().parseCommand("edit 1 l/" + MONDAY_ID + " l/" + TUESDAY_ID),
                model, Lesson.MESSAGE_OVERLAPPING_ENROLMENTS);

        Person after = model.getFilteredPersonList().getFirst();
        assertEquals(before, after);
        assertEquals(30, after.getFeesDue(OCTOBER));
        assertEquals(before.getAttendance(), after.getAttendance());
        assertEquals(catalogBefore, model.getAddressBook().getLessonList());
        assertCommandFailure(new AttendanceCommand(INDEX_FIRST_PERSON,
                new Attendance(TUESDAY_ID, LocalDate.of(2026, 10, 19), AttendanceStatus.ABSENT)),
                model, Person.MESSAGE_DUPLICATE_ATTENDANCE);
    }

    @Test
    public void execute_addStudent_resolvesHistoryBeforeCheckingOverlap() {
        ModelManager model = new ModelManager();
        model.addLesson(movedLesson());
        model.addLesson(mondayLesson());
        Lesson tuesdayReference = new Lesson(Year.of(2026), Subject.MATH,
                Day.TUESDAY, Time.TEN, Time.ELEVEN, 0);
        Person input = new PersonBuilder().withLessons(mondayLesson(), tuesdayReference).build();

        assertCommandFailure(new AddStudentCommand(input), model, Lesson.MESSAGE_OVERLAPPING_ENROLMENTS);
        assertEquals(0, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_editLessonCreatingIntermediateOverlap_rejectsAtomically() {
        ModelManager model = new ModelManager();
        Lesson monday = mondayLesson();
        Lesson tuesday = new Lesson(Year.of(2026), Subject.MATH, Day.TUESDAY,
                Time.TEN, Time.ELEVEN, 30).withRevision(new LessonRevision(Year.of(2026),
                        Subject.MATH, Day.WEDNESDAY, Time.TEN, Time.ELEVEN, 30,
                        NOVEMBER.plusMonths(1)), OCTOBER);
        model.addPerson(new PersonBuilder().withLessons(monday, tuesday).build());
        List<Lesson> catalogBefore = List.copyOf(model.getAddressBook().getLessonList());

        assertCommandFailure(new EditLessonCommand(MONDAY_ID, Day.TUESDAY, null, null, null, NOVEMBER, CLOCK),
                model, Lesson.MESSAGE_OVERLAPPING_ENROLMENTS);
        assertEquals(catalogBefore, model.getAddressBook().getLessonList());
    }

    @Test
    public void execute_importOverlap_rejectsBeforeAddingStudentsOrLessons() throws Exception {
        ModelManager model = new ModelManager();
        model.addLesson(movedLesson());
        Path lessons = temporaryDirectory.resolve("lessons.csv");
        Path students = temporaryDirectory.resolve("students.csv");
        Files.writeString(lessons, "year,subject,day,start_time,end_time,cost\n"
                + "2026,MATH,TUESDAY,TEN,ELEVEN,30\n2026,MATH,MONDAY,TEN,ELEVEN,30\n");
        Files.writeString(students, "name,phone,email,address,lessons\n"
                + "Imported Student,91234567,import@example.com,1 Main Street,"
                + MONDAY_ID + ";" + TUESDAY_ID + "\n");

        CommandException error = assertThrows(CommandException.class, () ->
                new ImportCsvCommand(students, lessons).execute(model));
        assertEquals("Unable to import data/importstudents.csv: " + Lesson.MESSAGE_OVERLAPPING_ENROLMENTS,
                error.getMessage());
        assertEquals(1, model.getAddressBook().getLessonList().size());
        assertEquals(0, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void addPerson_overlappingRevisions_rejectsBeforeChangingCatalog() {
        ModelManager model = new ModelManager();
        Person overlapping = new PersonBuilder().withLessons(movedLesson(), mondayLesson()).build();

        assertThrows(LessonException.class, () -> model.addPerson(overlapping));
        assertEquals(0, model.getAddressBook().getPersonList().size());
        assertEquals(0, model.getAddressBook().getLessonList().size());
    }

    @Test
    public void addPerson_slotReusedInNonOverlappingMonths_succeeds() {
        Lesson moved = movedLesson();
        Lesson replacement = new Lesson("replacement", List.of(new LessonRevision(Year.of(2026),
                Subject.MATH, Day.MONDAY, Time.TEN, Time.ELEVEN, 30, NOVEMBER)));
        ModelManager model = new ModelManager();
        model.addPerson(new PersonBuilder().withLessons(moved, replacement).build());

        Person student = model.getFilteredPersonList().getFirst();
        assertEquals(30, student.getFeesDue(OCTOBER));
        assertEquals(60, student.getFeesDue(NOVEMBER));
    }
}
