package seedu.address.logic.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.guardian.Guardian;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/** Reads students and their lesson references from a CSV file. */
public class StudentCsvImporter {
    public List<Person> read(Path path, Map<String, Lesson> lessonsById) throws IOException, ParseException {
        List<Person> students = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            List<String> headers = CsvParser.parseLine(reader.readLine());
            if (headers == null || headers.isEmpty()) {
                throw new ParseException("Student CSV must have a header row.");
            }
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    List<String> values = CsvParser.parseLine(line);
                    while (values.size() < headers.size()) {
                        values.add("");
                    }
                    students.add(createPerson(headers, values, lessonsById));
                }
            }
        }
        return students;
    }

    private static Person createPerson(List<String> headers, List<String> values,
            Map<String, Lesson> lessonsById) throws ParseException {
        CsvRow row = new CsvRow(headers, values);
        Set<Tag> tags = ParserUtil.parseTags(Arrays.stream(row.get("tags").split("[,;]"))
                .filter(tag -> !tag.isBlank()).map(String::trim).toList());
        Set<Lesson> lessons = new HashSet<>();
        for (String id : row.get("lessons").split("[,;]")) {
            if (!id.isBlank()) {
                Lesson lesson = lessonsById.get(id.trim());
                if (lesson == null) {
                    throw new ParseException("Unknown lesson id: " + id.trim());
                }
                lessons.add(lesson);
            }
        }
        Guardian guardian = new Guardian(ParserUtil.parseName(row.get("guardian_name")),
                ParserUtil.parsePhone(row.get("guardian_phone")),
                ParserUtil.parseEmail(row.get("guardian_email")),
                ParserUtil.parseRelationship(row.get("guardian_relationship")),
                ParserUtil.parseAddress(row.get("guardian_address")));
        return new Person(ParserUtil.parseName(row.get("name")), ParserUtil.parsePhone(row.get("phone")),
                ParserUtil.parseEmail(row.get("email")), ParserUtil.parseAddress(row.get("address")), tags, lessons,
                Optional.of(guardian));
    }
}
