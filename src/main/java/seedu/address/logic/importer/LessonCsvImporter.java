package seedu.address.logic.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.lesson.Lesson;

/** Reads and validates lesson definitions from a CSV file. */
public class LessonCsvImporter {
    public Map<String, Lesson> read(Path path) throws IOException, ParseException {
        if (!Files.exists(path)) {
            throw new NoSuchFileException(path.toString());
        }
        Map<String, Lesson> lessons = new LinkedHashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            List<String> headers = CsvParser.parseLine(reader.readLine());
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                List<String> values = CsvParser.parseLine(line);
                String id = value(headers, values, "id");
                Lesson lesson = new Lesson(ParserUtil.parseYear(value(headers, values, "year")),
                        ParserUtil.parseSubject(value(headers, values, "subject").toUpperCase(Locale.ROOT)),
                        ParserUtil.parseDay(value(headers, values, "day").toUpperCase(Locale.ROOT)),
                        ParserUtil.parseTime(value(headers, values, "start_time").toUpperCase(Locale.ROOT)),
                        ParserUtil.parseTime(value(headers, values, "end_time").toUpperCase(Locale.ROOT)),
                        ParserUtil.parseCost(value(headers, values, "cost")));
                if (!lesson.getId().equals(id)) {
                    throw new ParseException("Lesson id does not match lesson data: " + id);
                }
                if (lessons.putIfAbsent(id, lesson) != null) {
                    throw new ParseException("Duplicate lesson id: " + id);
                }
            }
        }
        return lessons;
    }

    private static String value(List<String> headers, List<String> values, String field) throws ParseException {
        return new CsvRow(headers, values).get(field);
    }
}
