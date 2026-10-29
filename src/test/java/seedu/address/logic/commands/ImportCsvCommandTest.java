package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.attendance.AttendanceStatus;
import seedu.address.model.lesson.Day;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonRevision;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Time;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.testutil.PersonBuilder;

public class ImportCsvCommandTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void execute_duplicateStudents_skipsAndReportsAllDuplicates() throws Exception {
        Path lessons = temporaryDirectory.resolve("importlessons.csv");
        Path students = temporaryDirectory.resolve("importstudents.csv");
        Files.writeString(lessons, "year,subject,day,start_time,end_time,cost\n"
                + "2026,ENGLISH,MONDAY,TEN,ELEVEN,30\n");
        String header = "name,phone,email,address,lessons\n";
        String alice = "Alice Tan,91234567,alice@example.com,1 Main Street,"
                + "2026-ENGLISH-MONDAY-TEN-ELEVEN\n";
        String bob = "Bob Lim,92345678,bob@example.com,2 Main Street,"
                + "2026-ENGLISH-MONDAY-TEN-ELEVEN\n";
        Files.writeString(students, header + alice + bob + alice);

        ModelManager model = new ModelManager();
        CommandResult result = new ImportCsvCommand(students, lessons).execute(model);

        assertEquals(2, model.getAddressBook().getPersonList().size());
        assertTrue(result.getFeedbackToUser().contains("Imported 2 student(s)"));
        assertTrue(result.getFeedbackToUser().contains("data/importstudents.csv"));
        assertTrue(result.getFeedbackToUser().contains("data/importlessons.csv"));
        assertTrue(result.getFeedbackToUser().contains("Alice Tan"));
        assertEquals(1, model.getAddressBook().getLessonList().size());
    }

    @Test
    public void execute_unreferencedLesson_retainsLessonInCatalog() throws Exception {
        Path lessons = temporaryDirectory.resolve("importlessons.csv");
        Path students = temporaryDirectory.resolve("importstudents.csv");
        Files.writeString(lessons, "id,year,subject,day,start_time,end_time,cost\n"
                + "2026-MATH-MONDAY-TEN-ELEVEN,2026,MATH,MONDAY,TEN,ELEVEN,30\n");
        Files.writeString(students, "name,phone,email,address\n"
                + "Alpha Import Student,91234567,alpha@example.com,1 Main Street\n");

        ModelManager model = new ModelManager();
        CommandResult result = new ImportCsvCommand(students, lessons).execute(model);

        assertEquals(1, model.getAddressBook().getLessonList().size());
        assertEquals("2026-MATH-MONDAY-TEN-ELEVEN",
                model.getAddressBook().getLessonList().get(0).getId());
        assertTrue(result.getFeedbackToUser().contains("1 lesson(s)"));
    }

    @Test
    public void execute_mixedReferencedAndUnreferencedLessons_retainsBoth() throws Exception {
        Path lessons = temporaryDirectory.resolve("importlessons.csv");
        Path students = temporaryDirectory.resolve("importstudents.csv");
        Files.writeString(lessons, "id,year,subject,day,start_time,end_time,cost\n"
                + "2026-MATH-MONDAY-TEN-ELEVEN,2026,MATH,MONDAY,TEN,ELEVEN,30\n"
                + "2026-SCIENCE-TUESDAY-TEN-ELEVEN,2026,SCIENCE,TUESDAY,TEN,ELEVEN,40\n");
        Files.writeString(students, "name,phone,email,address,lessons\n"
                + "Alpha Import Student,91234567,alpha@example.com,1 Main Street,"
                + "2026-MATH-MONDAY-TEN-ELEVEN\n");

        ModelManager model = new ModelManager();
        new ImportCsvCommand(students, lessons).execute(model);

        assertEquals(2, model.getAddressBook().getLessonList().size());
    }

    @Test
    public void execute_conflictingExistingLesson_rejectsImport() throws Exception {
        Path lessons = temporaryDirectory.resolve("importlessons.csv");
        Path students = temporaryDirectory.resolve("importstudents.csv");
        Files.writeString(lessons, "year,subject,day,start_time,end_time,cost\n"
                + "2026,ENGLISH,MONDAY,TEN,ELEVEN,50\n");
        Files.writeString(students, "name,phone,email,address\nAlice Tan,91234567,alice@example.com,1 Main Street\n");

        ModelManager model = new ModelManager();
        Lesson existingLesson = new Lesson(Year.of(2026), Subject.ENGLISH, Day.MONDAY,
                Time.TEN, Time.ELEVEN, 30);
        model.addPerson(new Person(new Name("Existing Student"), new Phone("91234568"),
                new Email("existing@example.com"), new Address("2 Main Street"), Set.of(), Set.of(existingLesson),
                Optional.empty()));

        CommandException exception = assertThrows(CommandException.class, () ->
                new ImportCsvCommand(students, lessons).execute(model));

        assertTrue(exception.getMessage().contains("Lesson ID conflict"));
        assertEquals(1, model.getAddressBook().getPersonList().size());
        assertEquals(1, model.getAddressBook().getLessonList().size());
    }

    @Test
    public void execute_invalidLessonCsv_reportsLessonFile() throws Exception {
        Path lessons = temporaryDirectory.resolve("importlessons.csv");
        Path students = temporaryDirectory.resolve("importstudents.csv");
        Files.writeString(lessons, "year,subject,day,start_time,end_time,cost\n"
                + "2026,MATH,FUNDAY,TEN,ELEVEN,30\n");
        Files.writeString(students, "name,phone,email,address\n"
                + "Alice Tan,91234567,alice@example.com,1 Main Street\n");

        CommandException exception = assertThrows(CommandException.class, () ->
                new ImportCsvCommand(students, lessons).execute(new ModelManager()));

        assertTrue(exception.getMessage().startsWith("Unable to import data/importlessons.csv:"));
        assertTrue(exception.getMessage().contains("Lesson CSV row 2:"));
    }

    @Test
    public void execute_reusedSlot_keepsIndependentUidAndPropagatesLaterEdits() throws Exception {
        YearMonth october = YearMonth.of(2026, 10);
        Clock clock = Clock.fixed(Instant.parse("2026-10-29T12:00:00Z"), ZoneOffset.UTC);
        Lesson original = new Lesson(Year.of(2026), Subject.MATH, Day.MONDAY, Time.TEN, Time.ELEVEN, 30);
        Lesson moved = original.withRevision(new LessonRevision(Year.of(2026), Subject.MATH, Day.TUESDAY,
                Time.TEN, Time.ELEVEN, 40, october.plusMonths(1)), october);
        ModelManager model = new ModelManager();
        model.addPerson(new PersonBuilder().withName("Original Student").withLessons(moved).build());
        Path lessons = temporaryDirectory.resolve("lessons.csv");
        Path students = temporaryDirectory.resolve("students.csv");
        Files.writeString(lessons, "year,subject,day,start_time,end_time,cost\n"
                + "2026,MATH,MONDAY,TEN,ELEVEN,30\n");
        Files.writeString(students, "name,phone,email,address,lessons\n"
                + "Imported Student,91234567,imported@example.com,1 Main Street," + original.getId() + "\n");

        new ImportCsvCommand(students, lessons).execute(model);
        Lesson imported = model.getLesson(original.getId()).orElseThrow();
        assertNotEquals(moved.getUid(), imported.getUid());
        assertSame(imported, model.getFilteredPersonList().get(1).getLessons().iterator().next());
        new EditLessonCommand(imported.getId(), null, null, null, 50, october.plusMonths(2), clock).execute(model);
        assertEquals(50, model.getFilteredPersonList().get(1).getFeesDue(october.plusMonths(2)));
        assertEquals(40, model.getFilteredPersonList().getFirst().getFeesDue(october.plusMonths(2)));
        new AttendanceCommand(Index.fromOneBased(2), new Attendance(imported.getId(), LocalDate.of(2026, 11, 2),
                AttendanceStatus.PRESENT)).execute(model);
        assertEquals(imported.getUid(), model.getFilteredPersonList().get(1).getAttendance().getFirst()
                .getLessonUid().orElseThrow());
    }

    @Test
    public void execute_existingRevisedLesson_reusesCatalogHistoryAndUid() throws Exception {
        YearMonth october = YearMonth.of(2026, 10);
        Lesson original = new Lesson(Year.of(2026), Subject.MATH, Day.MONDAY, Time.TEN, Time.ELEVEN, 30);
        Lesson revised = original.withRevision(new LessonRevision(Year.of(2026), Subject.MATH, Day.TUESDAY,
                Time.TEN, Time.ELEVEN, 40, october.plusMonths(1)), october);
        ModelManager model = new ModelManager();
        model.addLesson(revised);
        Path lessons = temporaryDirectory.resolve("lessons.csv");
        Path students = temporaryDirectory.resolve("students.csv");
        Files.writeString(lessons, "year,subject,day,start_time,end_time,cost\n"
                + "2026,MATH,TUESDAY,TEN,ELEVEN,40\n");
        Files.writeString(students, "name,phone,email,address,lessons\n"
                + "Imported Student,91234567,imported@example.com,1 Main Street," + revised.getId() + "\n");

        new ImportCsvCommand(students, lessons).execute(model);

        assertSame(revised, model.getFilteredPersonList().getFirst().getLessons().iterator().next());
        assertEquals(30, model.getFilteredPersonList().getFirst().getFeesDue(october));
        assertEquals(1, model.getAddressBook().getLessonList().size());
    }

}
