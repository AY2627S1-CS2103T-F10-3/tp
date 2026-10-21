package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Finds and lists students by partial student or guardian details.
 * Matching is case insensitive, with AND across fields and OR across keywords within a field.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds students by partial student names, student phone numbers, guardian names, or guardian "
            + "phone numbers, or enrolled lesson IDs (case-insensitive), "
            + "and displays them as a list with index numbers.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]... or [n/KEYWORDS] [p/KEYWORDS] "
            + "[gn/KEYWORDS] [gp/KEYWORDS] [l/KEYWORDS]\n"
            + "Lesson IDs with all five components match exactly; shorter fragments match partially. "
            + "Omit lesson cost.\n"
            + "Without a prefix, only student names are searched. All specified fields must match; "
            + "any keyword within each field may match. Supply each prefix once with a non-empty value.\n"
            + "Examples: " + COMMAND_WORD + " alice bob; " + COMMAND_WORD + " n/Alice Bob gp/9876";

    private final Predicate<Person> predicate;

    /**
     * Constructs a command that lists students matching the predicate.
     *
     * @param predicate Search criteria for students.
     */
    public FindCommand(Predicate<Person> predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand otherFindCommand)) {
            return false;
        }

        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
