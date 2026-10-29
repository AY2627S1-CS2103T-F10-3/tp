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
            + ": Finds students by name or prefixed fields.\n"
            + "Parameters: KEYWORDS or [n/KEYWORDS] [p/KEYWORDS] "
            + "[e/KEYWORDS] [a/KEYWORDS] [t/KEYWORDS] [gn/KEYWORDS] [gp/KEYWORDS] "
            + "[ge/KEYWORDS] [ga/KEYWORDS] [gr/KEYWORDS] [l/KEYWORDS] [paid/YYYY-MM | unpaid/YYYY-MM]\n"
            + "Examples: " + COMMAND_WORD + " Alice; " + COMMAND_WORD + " n/Alice gp/9876; "
            + COMMAND_WORD + " unpaid/2026-10";

    public static final String MESSAGE_NO_RESULTS = "No students found.";

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
        int resultCount = model.getFilteredPersonList().size();
        String resultMessage = resultCount == 0
                ? MESSAGE_NO_RESULTS
                : String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, resultCount);
        return new CommandResult(resultMessage);
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
