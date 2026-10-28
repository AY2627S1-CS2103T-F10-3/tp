package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.lesson.Day;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Time;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

public class ImportCsvCommandTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void execute_duplicateStudents_skipsAndReportsAllDuplicates() throws Exception {
        Path lessons = temporaryDirectory.resolve("importlessons.csv");
        Path students = temporaryDirectory.resolve("importstudents.csv");
        Files.writeString(lessons, "id,year,subject,day,start_time,end_time,cost\n"
                + "2026-ENGLISH-MONDAY-TEN-ELEVEN,2026,ENGLISH,MONDAY,TEN,ELEVEN,30\n");
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
    }

    @Test
    public void execute_conflictingExistingLesson_rejectsImport() throws Exception {
        Path lessons = temporaryDirectory.resolve("importlessons.csv");
        Path students = temporaryDirectory.resolve("importstudents.csv");
        Files.writeString(lessons, "id,year,subject,day,start_time,end_time,cost\n"
                + "2026-ENGLISH-MONDAY-TEN-ELEVEN,2026,ENGLISH,MONDAY,TEN,ELEVEN,50\n");
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
    }

    @Test
    public void execute_invalidLessonCsv_reportsLessonFile() throws Exception {
        Path lessons = temporaryDirectory.resolve("importlessons.csv");
        Path students = temporaryDirectory.resolve("importstudents.csv");
        Files.writeString(lessons, "id,year,subject,day,start_time,end_time,cost\n"
                + "BAD,2026,MATH,MONDAY,TEN,ELEVEN,30\n");
        Files.writeString(students, "name,phone,email,address\n"
                + "Alice Tan,91234567,alice@example.com,1 Main Street\n");

        CommandException exception = assertThrows(CommandException.class, () ->
                new ImportCsvCommand(students, lessons).execute(new ModelManager()));

        assertTrue(exception.getMessage().startsWith("Unable to import data/importlessons.csv:"));
        assertTrue(exception.getMessage().contains("Lesson id does not match lesson data: BAD"));
    }
}
