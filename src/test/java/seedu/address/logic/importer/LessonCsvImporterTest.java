package seedu.address.logic.importer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.lesson.Lesson;

public class LessonCsvImporterTest {
    private static final String HEADER = "year,subject,day,start_time,end_time,cost\n";

    @TempDir
    private Path temporaryDirectory;

    @Test
    public void read_validLesson_success() throws Exception {
        Path file = temporaryDirectory.resolve("importlessons.csv");
        Files.writeString(file, HEADER + "2026,ENGLISH,MONDAY,TEN,ELEVEN,30\n");

        Map<String, Lesson> lessons = new LessonCsvImporter().read(file);

        assertEquals(1, lessons.size());
        assertEquals(30, lessons.get("2026-ENGLISH-MONDAY-TEN-ELEVEN").getCost());
    }

    @Test
    public void read_utf8BomHeader_success() throws Exception {
        Path file = temporaryDirectory.resolve("importlessons.csv");
        Files.writeString(file, "\uFEFF" + HEADER
                + "2026,ENGLISH,MONDAY,TEN,ELEVEN,30\n");

        assertEquals(1, new LessonCsvImporter().read(file).size());
    }

    @Test
    public void read_derivesLessonIdFromFields_success() throws Exception {
        Path file = temporaryDirectory.resolve("importlessons.csv");
        Files.writeString(file, HEADER + "2026,ENGLISH,MONDAY,TEN,ELEVEN,30\n");

        Map<String, Lesson> lessons = new LessonCsvImporter().read(file);

        assertEquals(30, lessons.get("2026-ENGLISH-MONDAY-TEN-ELEVEN").getCost());
    }

    @Test
    public void read_invalidDay_reportsRowNumber() throws Exception {
        Path file = temporaryDirectory.resolve("importlessons.csv");
        Files.writeString(file, HEADER + "2026,ENGLISH,NOT_A_DAY,TEN,ELEVEN,30\n");

        ParseException exception = assertThrows(ParseException.class, () -> new LessonCsvImporter().read(file));

        assertTrue(exception.getMessage().startsWith("Lesson CSV row 2: Day is any day of the week"));
    }

    @Test
    public void read_duplicateDerivedId_throwsParseException() throws Exception {
        Path file = temporaryDirectory.resolve("importlessons.csv");
        Files.writeString(file, HEADER + "2026,ENGLISH,MONDAY,TEN,ELEVEN,30\n"
                + "2026,ENGLISH,MONDAY,TEN,ELEVEN,40\n");

        ParseException exception = assertThrows(ParseException.class, () -> new LessonCsvImporter().read(file));

        assertEquals("Lesson CSV row 3: Duplicate lesson id: 2026-ENGLISH-MONDAY-TEN-ELEVEN",
                exception.getMessage());
    }

    @Test
    public void read_invalidYearFormat_throwsParseException() throws Exception {
        Path file = temporaryDirectory.resolve("importlessons.csv");
        Files.writeString(file, HEADER + "-5-ENGLISH-MONDAY-TEN-ELEVEN,-5,ENGLISH,MONDAY,TEN,ELEVEN,30\n");

        assertThrows(ParseException.class, () -> new LessonCsvImporter().read(file));
    }
}
