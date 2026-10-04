package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;

/**
 * Adds, replaces, or clears the remark of a person in the displayed list.
 */
public class RemarkCommand extends Command {
    public static final String COMMAND_WORD = "remark";
    public static final String MESSAGE_USAGE = "remark: Updates a person's remark.\n"
            + "Parameters: INDEX (a positive integer) [r/REMARK]\n"
            + "Example: remark 1 r/Likes to swim. Omit the remark to clear it.";
    public static final String MESSAGE_ADD_REMARK_SUCCESS = "Updated remark for %1$s: %2$s";
    public static final String MESSAGE_DELETE_REMARK_SUCCESS = "Removed remark from %1$s";

    private final Index index;
    private final Remark remark;

    /**
     * Creates a command targeting the given index in the displayed list.
     */
    public RemarkCommand(Index index, Remark remark) {
        requireAllNonNull(index, remark);
        this.index = index;
        this.remark = remark;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> displayed = model.getFilteredPersonList();
        if (index.getZeroBased() >= displayed.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        Person original = displayed.get(index.getZeroBased());
        Person updated = new Person(original.getName(), original.getPhone(), original.getEmail(),
                original.getAddress(), remark, original.getTags());
        model.setPerson(original, updated);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        String feedback = remark.value.isEmpty()
                ? String.format(MESSAGE_DELETE_REMARK_SUCCESS, updated.getName())
                : String.format(MESSAGE_ADD_REMARK_SUCCESS, updated.getName(), remark);
        return new CommandResult(feedback);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof RemarkCommand otherCommand
                && index.equals(otherCommand.index) && remark.equals(otherCommand.remark));
    }
}
