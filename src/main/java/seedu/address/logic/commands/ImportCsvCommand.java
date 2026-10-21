package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.importer.LessonCsvImporter;
import seedu.address.logic.importer.StudentCsvImporter;
import seedu.address.model.Model;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Person;

/** Imports students from {@code data/importstudents.csv}. */
public class ImportCsvCommand extends Command {
    public static final String COMMAND_WORD = "importcsv";
    private static final Path IMPORT_PATH = Path.of("data", "importstudents.csv");
    private static final Path LESSONS_PATH = Path.of("data", "importlessons.csv");
    private static final String MESSAGE_MISSING_FILES = "Both data/importstudents.csv and data/importlessons.csv "
            + "are required.\n"
            + "importstudents.csv fields: name, phone, email, address, tags, guardian_name, guardian_phone, "
            + "guardian_email, guardian_relationship, guardian_address, lessons.\n"
            + "importlessons.csv fields: id, year, subject, day, start_time, end_time, cost.";
    private static final String MESSAGE_SUCCESS = "Imported %d student(s) from data/importstudents.csv.";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        try {
            Map<String, Lesson> lessonsById = new LessonCsvImporter().read(LESSONS_PATH);
            List<Person> imported = new StudentCsvImporter().read(IMPORT_PATH, lessonsById);
            List<Person> studentsToAdd = new ArrayList<>();
            List<String> duplicateStudents = new ArrayList<>();
            for (Person person : imported) {
                if (model.hasPerson(person) || studentsToAdd.stream().anyMatch(person::isSamePerson)) {
                    duplicateStudents.add(person.getName().toString());
                } else {
                    studentsToAdd.add(person);
                }
            }
            studentsToAdd.forEach(model::addPerson);
            String result = String.format(MESSAGE_SUCCESS, studentsToAdd.size());
            if (!duplicateStudents.isEmpty()) {
                result += " Skipped duplicate student(s): " + String.join(", ", duplicateStudents) + ".";
            }
            return new CommandResult(result);
        } catch (NoSuchFileException exception) {
            throw new CommandException(MESSAGE_MISSING_FILES);
        } catch (Exception exception) {
            throw new CommandException("Unable to import data/importstudents.csv: " + exception.getMessage());
        }
    }

}
