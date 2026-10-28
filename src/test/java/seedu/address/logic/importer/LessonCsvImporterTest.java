package seedu.address.logic.importer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
    public void read_derivesLessonIdFromFields_success() throws Exception {
        Path file = temporaryDirectory.resolve("importlessons.csv");
        Files.writeString(file, HEADER + "2026,ENGLISH,MONDAY,TEN,ELEVEN,30\n");

        Map<String, Lesson> lessons = new LessonCsvImporter().read(file);

        assertEquals(30, lessons.get("2026-ENGLISH-MONDAY-TEN-ELEVEN").getCost());
    }
}
