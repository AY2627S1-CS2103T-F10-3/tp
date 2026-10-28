package seedu.address.logic.importer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.lesson.Day;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Time;
import seedu.address.model.person.Person;

public class StudentCsvImporterTest {
    private static final String HEADER = "name,phone,email,address,tags,guardian_name,guardian_phone,"
            + "guardian_email,guardian_relationship,guardian_address,lessons\n";

    @TempDir
    private Path temporaryDirectory;

    @Test
    public void read_validStudent_success() throws Exception {
        Path file = temporaryDirectory.resolve("importstudents.csv");
        Files.writeString(file, HEADER + "Alice Tan,91234567,alice@example.com,1 Main Street,friend;paid,"
                + "Mary Tan,81234567,mary@example.com,Mother,2 Main Street,2026-ENGLISH-MONDAY-TEN-ELEVEN\n");
        Lesson lesson = new Lesson(Year.of(2026), Subject.ENGLISH, Day.MONDAY, Time.TEN, Time.ELEVEN, 30);

        Map<String, Lesson> lessons = Map.of(lesson.getId(), lesson);
        Person student = new StudentCsvImporter().read(file, lessons).get(0);

        assertEquals("Alice Tan", student.getName().toString());
        assertEquals(2, student.getTags().size());
        assertEquals(1, student.getLessons().size());
        assertEquals("Mary Tan", student.getGuardian().get().getName().toString());
    }

    @Test
    public void read_optionalColumnsMissing_success() throws Exception {
        Path file = temporaryDirectory.resolve("importstudents.csv");
        Files.writeString(file, "name,phone,email,address\nAlice Tan,91234567,alice@example.com,1 Main Street\n");

        Person student = new StudentCsvImporter().read(file, Map.of()).get(0);

        assertEquals("Alice Tan", student.getName().toString());
        assertEquals(0, student.getTags().size());
        assertEquals(0, student.getLessons().size());
        assertFalse(student.getGuardian().isPresent());
    }

    @Test
    public void read_unknownLessonId_throwsParseException() throws Exception {
        Path file = temporaryDirectory.resolve("importstudents.csv");
        Files.writeString(file, HEADER + "Alice Tan,91234567,alice@example.com,1 Main Street,,Mary Tan,"
                + "81234567,mary@example.com,Mother,2 Main Street,unknown-lesson\n");

        assertThrows(ParseException.class, () -> new StudentCsvImporter().read(file, Map.of()));
    }

    @Test
    public void read_missingEmailColumn_throwsParseException() throws Exception {
        Path file = temporaryDirectory.resolve("importstudents.csv");
        Files.writeString(file, "name,phone,address\nAlice Tan,91234567,1 Main Street\n");

        assertThrows(ParseException.class, () -> new StudentCsvImporter().read(file, Map.of()));
    }

    @Test
    public void read_missingNameOrPhoneColumn_throwsParseException() throws Exception {
        Path file = temporaryDirectory.resolve("importstudents.csv");
        Files.writeString(file, "phone,email,address\n91234567,alice@example.com,1 Main Street\n");
        assertThrows(ParseException.class, () -> new StudentCsvImporter().read(file, Map.of()));

        Files.writeString(file, "name,email,address\nAlice Tan,alice@example.com,1 Main Street\n");
        assertThrows(ParseException.class, () -> new StudentCsvImporter().read(file, Map.of()));
    }

    @Test
    public void read_blankEmailOrAddress_throwsParseException() throws Exception {
        Path file = temporaryDirectory.resolve("importstudents.csv");
        Files.writeString(file, HEADER + "Alice Tan,91234567,,1 Main Street,,Mary Tan,81234567,"
                + "mary@example.com,Mother,2 Main Street,\n");
        assertThrows(ParseException.class, () -> new StudentCsvImporter().read(file, Map.of()));

        Files.writeString(file, HEADER + "Alice Tan,91234567,alice@example.com,,,Mary Tan,81234567,"
                + "mary@example.com,Mother,,\n");
        assertThrows(ParseException.class, () -> new StudentCsvImporter().read(file, Map.of()));
    }

    @Test
    public void read_blankNameOrPhone_throwsParseException() throws Exception {
        Path file = temporaryDirectory.resolve("importstudents.csv");
        Files.writeString(file, HEADER + ",91234567,alice@example.com,1 Main Street,,,"
                + ",,,\n");
        assertThrows(ParseException.class, () -> new StudentCsvImporter().read(file, Map.of()));

        Files.writeString(file, HEADER + "Alice Tan,,alice@example.com,1 Main Street,,,"
                + ",,,\n");
        assertThrows(ParseException.class, () -> new StudentCsvImporter().read(file, Map.of()));
    }

    @Test
    public void read_invalidRow_reportsRowNumberAndStudentName() throws Exception {
        Path file = temporaryDirectory.resolve("importstudents.csv");
        Files.writeString(file, HEADER + "Alice Tan,91234567,alice@example.com,1 Main Street,,Mary Tan,81234567,"
                + "mary@example.com,Mother,2 Main Street,\n"
                + "Bob Tan,12,bob@example.com,2 Main Street,"
                + ",Mary Tan,81234567,mary@example.com,Mother,2 Main Street,\n");

        ParseException exception = assertThrows(ParseException.class, () ->
                new StudentCsvImporter().read(file, Map.of()));

        assertEquals("Student CSV row 3 (Bob Tan): "
                + "Phone numbers should only contain digits, and should be at least 3 digits long",
                exception.getMessage());
    }

    @Test
    public void read_invalidEmail_reportsRowNumberAndStudentName() throws Exception {
        Path file = temporaryDirectory.resolve("importstudents.csv");
        Files.writeString(file, HEADER + "Alice Tan,91234567,alice@example.com,1 Main Street,,Mary Tan,81234567,"
                + "mary@example.com,Mother,2 Main Street,\n"
                + "Bob Tan,91234568,not-an-email,2 Main Street,"
                + ",Mary Tan,81234567,mary@example.com,Mother,2 Main Street,\n");

        ParseException exception = assertThrows(ParseException.class, () ->
                new StudentCsvImporter().read(file, Map.of()));

        org.junit.jupiter.api.Assertions.assertTrue(exception.getMessage().startsWith(
                "Student CSV row 3 (Bob Tan): Emails should be of the format"));
    }
}
