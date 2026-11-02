package seedu.address.logic.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
    /**
     * Reads and validates students and their lesson references from a CSV file.
     *
     * @param path path to the student CSV file
     * @param lessonsById lessons indexed by their identifiers
     * @return parsed students
     * @throws IOException if the file cannot be read
     * @throws ParseException if the CSV data is invalid
     */
    public List<Person> read(Path path, Map<String, Lesson> lessonsById) throws IOException, ParseException {
        List<Person> students = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            List<String> headers = CsvParser.parseLine(reader.readLine());
            if (headers == null || headers.isEmpty()) {
                throw new ParseException("Student CSV must have a header row.");
            }
            String line;
            int rowNumber = 1;
            int recordStartRow = 2;
            StringBuilder record = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                rowNumber++;
                if (record.length() > 0) {
                    record.append('\n');
                } else {
                    recordStartRow = rowNumber;
                }
                record.append(line);
                if (!CsvParser.hasUnclosedQuote(record.toString())) {
                    if (!record.toString().isBlank()) {
                        try {
                            students.add(createPerson(headers, CsvParser.parseLine(record.toString()), lessonsById));
                        } catch (ParseException exception) {
                            throw new ParseException("Student CSV row " + recordStartRow + ": "
                                    + exception.getMessage(), exception);
                        }
                    }
                    record.setLength(0);
                }
            }
            if (record.length() > 0) {
                try {
                    students.add(createPerson(headers, CsvParser.parseLine(record.toString()), lessonsById));
                } catch (ParseException exception) {
                    throw new ParseException("Student CSV row " + recordStartRow + ": "
                            + exception.getMessage(), exception);
                }
            }
        }
        return students;
    }

    private static Person createPerson(List<String> headers, List<String> values,
            Map<String, Lesson> lessonsById) throws ParseException {
        CsvRow row = new CsvRow(headers, values);
        Set<Tag> tags = ParserUtil.parseTags(Arrays.stream(valueOrEmpty(row, "tags").split("[,;]"))
                .filter(tag -> !tag.isBlank()).map(String::trim).toList());
        Set<Lesson> lessons = new HashSet<>();
        for (String id : valueOrEmpty(row, "lessons").split("[,;]")) {
            if (!id.isBlank()) {
                String lessonId = id.trim().toUpperCase(Locale.ROOT);
                Lesson lesson = lessonsById.get(lessonId);
                if (lesson == null) {
                    throw new ParseException("Unknown lesson id: " + lessonId);
                }
                lessons.add(lesson);
            }
        }
        Optional<Guardian> guardian = parseGuardian(row);
        return new Person(ParserUtil.parseName(row.get("name")), ParserUtil.parsePhone(row.get("phone")),
                ParserUtil.parseEmail(row.get("email")), ParserUtil.parseAddress(row.get("address")), tags, lessons,
                guardian);
    }

    private static Optional<Guardian> parseGuardian(CsvRow row) throws ParseException {
        String[] fields = {"guardian_name", "guardian_phone", "guardian_email", "guardian_relationship",
            "guardian_address"};
        boolean hasGuardianData = Arrays.stream(fields).anyMatch(row::hasField);
        if (!hasGuardianData) {
            return Optional.empty();
        }
        for (String field : fields) {
            if (!row.hasField(field)) {
                throw new ParseException("Missing required column: " + field);
            }
        }
        String guardianName = row.get("guardian_name");
        String guardianPhone = row.get("guardian_phone");
        String guardianEmail = row.get("guardian_email");
        String guardianRelationship = row.get("guardian_relationship");
        String guardianAddress = row.get("guardian_address");
        String[] values = {guardianName, guardianPhone, guardianEmail, guardianRelationship, guardianAddress};
        if (Arrays.stream(values).allMatch(String::isBlank)) {
            return Optional.empty();
        }
        for (int i = 0; i < values.length; i++) {
            if (values[i].isBlank()) {
                throw new ParseException("Guardian columns must either all be blank or all contain values; column '"
                        + fields[i] + "' is blank.");
            }
        }
        try {
            Guardian guardian = new Guardian(ParserUtil.parseName(guardianName),
                    ParserUtil.parsePhone(guardianPhone), ParserUtil.parseEmail(guardianEmail),
                    ParserUtil.parseRelationship(guardianRelationship), ParserUtil.parseAddress(guardianAddress));
            return Optional.of(guardian);
        } catch (ParseException exception) {
            throw new ParseException("Invalid guardian details: " + exception.getMessage(), exception);
        }
    }

    private static String valueOrEmpty(CsvRow row, String field) throws ParseException {
        return row.hasField(field) ? row.get(field) : "";
    }

}
