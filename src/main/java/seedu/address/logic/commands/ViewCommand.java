package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.time.Clock;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import seedu.address.model.Model;
import seedu.address.model.guardian.Guardian;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonRevision;
import seedu.address.model.person.Person;

/**
 * Lists all persons in the address book to the user.
 */
public class ViewCommand extends Command {

    public static final String COMMAND_WORD = "view";

    public static final String MESSAGE_SUCCESS = "Viewing all students.";
    public static final String MESSAGE_NO_STUDENTS = "There are no students in the directory.";
    public static final String MESSAGE_USAGE = "view: Lists students or distinct values and their frequencies.\n"
            + "Parameters: [/name | /phone | /email | /address | /tag | /lesson | /guardian | /payment | /attendance]\n"
            + "Example: view /email";

    private final String field;
    private final Clock clock;

    /**
     * Creates a command that lists all contacts.
     */
    public ViewCommand() {
        this("");
    }

    /**
     * Creates a view command; an empty field lists all contacts.
     */
    public ViewCommand(String field) {
        this(field, Clock.systemDefaultZone());
    }

    /** Creates a view command using {@code clock} to determine the current billing month. */
    ViewCommand(String field, Clock clock) {
        this.field = requireNonNull(field);
        this.clock = requireNonNull(clock);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        if (field.isEmpty()) {
            model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
            return new CommandResult(model.getFilteredPersonList().isEmpty() ? MESSAGE_NO_STUDENTS : MESSAGE_SUCCESS);
        }
        if (field.equals("lesson")) {
            return new CommandResult(formatLessonSummary(model, YearMonth.now(clock)));
        }
        Map<String, Integer> frequencies = new TreeMap<>();
        for (Person person : model.getAddressBook().getPersonList()) {
            valuesOf(person).distinct().forEach(value -> frequencies.merge(value, 1, Integer::sum));
        }
        StringBuilder feedback = new StringBuilder("Distinct " + field + " values: (frequency)");
        if (frequencies.isEmpty()) {
            feedback.append("\nNo values found.");
        } else {
            frequencies.forEach((value, count) -> feedback.append("\n").append(value)
                    .append(field.equals("lesson") ? ": " : " : ").append(count));
        }
        return new CommandResult(feedback.toString());
    }

    private Stream<String> valuesOf(Person person) {
        return switch (field) {
            case "name" -> Stream.of(person.getName().toString());
            case "phone" -> Stream.of(person.getPhone().toString());
            case "email" -> Stream.of(person.getEmail().toString());
            case "address" -> Stream.of(person.getAddress().toString());
            case "tag" -> person.getTags().stream().map(tag -> tag.tagName);
            case "lesson" -> throw new IllegalStateException("Lesson summaries are handled separately");
            case "guardian" -> person.getGuardian().stream().map(ViewCommand::formatGuardian);
            case "payment" -> person.getPayments().stream().map(Object::toString);
            case "attendance" -> person.getAttendance().stream().map(Object::toString);
            default -> throw new IllegalArgumentException("Unsupported view field: " + field);
        };
    }

    private static String formatGuardian(Guardian guardian) {
        return String.format("Name: %s | Phone: %s | Email: %s | Relationship: %s | Address: %s",
                guardian.getName(), guardian.getPhone(), guardian.getEmail(),
                guardian.getRelationship(), guardian.getAddress());
    }

    private static String formatLessonSummary(Model model, YearMonth currentMonth) {
        List<Lesson> lessons = model.getAddressBook().getLessonList().stream()
                .sorted(Comparator.comparing(lesson -> formatCurrentLesson(lesson, currentMonth)))
                .toList();
        Map<String, Integer> frequencies = new HashMap<>();
        lessons.forEach(lesson -> frequencies.put(lesson.getUid(), 0));
        for (Person person : model.getAddressBook().getPersonList()) {
            person.getLessons().stream().map(Lesson::getUid).distinct()
                    .forEach(uid -> frequencies.merge(uid, 1, Integer::sum));
        }

        StringBuilder feedback = new StringBuilder("Distinct lesson values: (frequency)");
        if (lessons.isEmpty()) {
            return feedback.append("\nNo values found.").toString();
        }
        for (Lesson lesson : lessons) {
            feedback.append("\n").append(formatCurrentLesson(lesson, currentMonth))
                    .append(": ").append(frequencies.getOrDefault(lesson.getUid(), 0));
            lesson.getRevisions().stream()
                    .filter(revision -> revision.getEffectiveFrom().isAfter(currentMonth))
                    .forEach(revision -> feedback.append("\n- ").append(revision.getId())
                            .append(" (cost: ").append(revision.getCost()).append(")")
                            .append(" (effective from ").append(revision.getEffectiveFrom()).append(")"));
        }
        return feedback.toString();
    }

    private static String formatCurrentLesson(Lesson lesson, YearMonth currentMonth) {
        LessonRevision current = lesson.getRevisionFor(currentMonth);
        return String.format("%s (cost: %d)", current.getId(), current.getCost());
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof ViewCommand otherCommand && field.equals(otherCommand.field));
    }

    @Override
    public int hashCode() {
        return field.hashCode();
    }

    @Override
    public String toString() {
        return new seedu.address.commons.util.ToStringBuilder(this)
                .add("field", field)
                .toString();
    }
}
