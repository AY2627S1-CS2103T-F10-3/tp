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
    private static final String MESSAGE_MISSING_FILES = "Both data/importstudents.csv and data/importlessons.csv "
            + "are required.\n"
            + "importstudents.csv fields: name, phone, email, address, tags, guardian_name, guardian_phone, "
            + "guardian_email, guardian_relationship, guardian_address, lessons.\n"
            + "importlessons.csv fields: year, subject, day, start_time, end_time, cost.";
    private static final String MESSAGE_SUCCESS = "Imported %d student(s) and %d lesson(s) "
            + "from data/importstudents.csv and data/importlessons.csv.";
    private static final String IMPORT_STUDENTS_FILENAME = "importstudents.csv";
    private static final String IMPORT_LESSONS_FILENAME = "importlessons.csv";
    private final Path importPath;
    private final Path lessonsPath;

    /**
     * Creates an ImportCsvCommand object.
     */
    public ImportCsvCommand() {
        this(Path.of("data", IMPORT_STUDENTS_FILENAME), Path.of("data", IMPORT_LESSONS_FILENAME));
    }

    /**
     * Creates an import command with the specified student and lesson CSV files.
     *
     * @param importPath path to the student CSV file
     * @param lessonsPath path to the lesson CSV file
     */
    ImportCsvCommand(Path importPath, Path lessonsPath) {
        this.importPath = importPath;
        this.lessonsPath = lessonsPath;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Map<String, Lesson> lessonsById;
        try {
            lessonsById = new LessonCsvImporter().read(lessonsPath);
        } catch (NoSuchFileException exception) {
            throw new CommandException(MESSAGE_MISSING_FILES);
        } catch (Exception exception) {
            throw new CommandException("Unable to import data/importlessons.csv: " + exception.getMessage());
        }

        validateLessonDefinitions(model, lessonsById);
        // Reuse the catalog's identity and full revision history before resolving student enrolments.
        for (Lesson existing : model.getAddressBook().getLessonList()) {
            if (lessonsById.containsKey(existing.getId())) {
                lessonsById.put(existing.getId(), existing);
            }
        }

        List<Person> imported;
        try {
            imported = new StudentCsvImporter().read(importPath, lessonsById);
        } catch (NoSuchFileException exception) {
            throw new CommandException(MESSAGE_MISSING_FILES);
        } catch (Exception exception) {
            throw new CommandException("Unable to import data/importstudents.csv: " + exception.getMessage());
        }

        try {
            List<Person> studentsToAdd = new ArrayList<>();
            List<String> duplicateStudents = new ArrayList<>();
            for (Person person : imported) {
                if (model.hasPerson(person) || studentsToAdd.stream().anyMatch(person::isSamePerson)) {
                    duplicateStudents.add(person.getName().toString());
                } else {
                    studentsToAdd.add(person);
                }
            }
            int skippedLessons = 0;
            for (Lesson lesson : lessonsById.values()) {
                if (model.hasLesson(lesson)) {
                    skippedLessons++;
                } else {
                    model.addLesson(lesson);
                }
            }
            studentsToAdd.forEach(model::addPerson);
            int importedLessons = lessonsById.size() - skippedLessons;
            String result = String.format(MESSAGE_SUCCESS, studentsToAdd.size(), importedLessons);
            if (!duplicateStudents.isEmpty()) {
                result += " Skipped duplicate student(s): " + String.join(", ", duplicateStudents) + ".";
            }
            if (skippedLessons > 0) {
                result += " Skipped " + skippedLessons + " existing lesson(s).";
            }
            return new CommandResult(result);
        } catch (Exception exception) {
            throw new CommandException("Unable to import data/importstudents.csv: " + exception.getMessage());
        }
    }

    private static void validateLessonDefinitions(Model model, Map<String, Lesson> importedLessons)
            throws CommandException {
        for (Lesson existingLesson : model.getAddressBook().getLessonList()) {
            Lesson importedLesson = importedLessons.get(existingLesson.getId());
            if (importedLesson != null
                    && !existingLesson.getLatestRevision().hasSameDetails(importedLesson.getLatestRevision())) {
                throw new CommandException("Lesson ID conflict for " + existingLesson.getId()
                        + ": the app already stores a different lesson definition or cost.");
            }
        }
    }

}
