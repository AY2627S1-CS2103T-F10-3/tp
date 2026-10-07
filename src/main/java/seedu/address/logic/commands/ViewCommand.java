package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Lists all persons in the address book to the user.
 */
public class ViewCommand extends Command {

    public static final String COMMAND_WORD = "view";

    public static final String MESSAGE_SUCCESS = "Viewing all students.";
    public static final String MESSAGE_USAGE = "view: Lists contacts or distinct values and their frequencies.\n"
            + "Parameters: [/name | /phone | /email | /address | /tag]\n"
            + "Example: view /email";

    private final String field;

    /** Creates a command that lists all contacts. */
    public ViewCommand() {
        this("");
    }

    /** Creates a view command; an empty field lists all contacts. */
    public ViewCommand(String field) {
        this.field = requireNonNull(field);
    }


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        if (field.isEmpty()) {
            model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
            return new CommandResult(MESSAGE_SUCCESS);
        }
        Map<String, Integer> frequencies = new TreeMap<>();
        for (Person person : model.getAddressBook().getPersonList()) {
            valuesOf(person).forEach(value -> frequencies.merge(value, 1, Integer::sum));
        }
        StringBuilder feedback = new StringBuilder("Distinct " + field + " values (frequency):");
        if (frequencies.isEmpty()) {
            feedback.append("\nNo values found.");
        } else {
            frequencies.forEach((value, count) -> feedback.append("\n").append(value).append(" : ").append(count));
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
        default -> throw new IllegalArgumentException("Unsupported view field: " + field);
        };
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof ViewCommand otherCommand && field.equals(otherCommand.field));
    }

    @Override
    public int hashCode() {
        return field.hashCode();
    }
}
