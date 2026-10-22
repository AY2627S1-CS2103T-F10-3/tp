package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.payment.Payment;
import seedu.address.model.person.Person;

/**
 * Lists the recorded payments of a student identified using its displayed index.
 */
public class ViewPaymentsCommand extends Command {

    public static final String COMMAND_WORD = "viewpayments";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists the payments of the student identified by the index number used in the displayed list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_HEADER = "Payments for %1$s:";
    public static final String MESSAGE_NO_PAYMENTS = "No payments recorded.";

    private final Index index;

    /**
     * Creates a ViewPaymentsCommand to list the payments of the student at {@code index}.
     */
    public ViewPaymentsCommand(Index index) {
        this.index = requireNonNull(index);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person student = lastShownList.get(index.getZeroBased());
        StringBuilder feedback = new StringBuilder(String.format(MESSAGE_HEADER, student.getName()));
        List<Payment> payments = student.getPayments();
        if (payments.isEmpty()) {
            feedback.append("\n").append(MESSAGE_NO_PAYMENTS);
        }
        for (int i = 0; i < payments.size(); i++) {
            feedback.append("\n").append(i + 1).append(". ").append(payments.get(i));
        }
        return new CommandResult(feedback.toString());
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ViewPaymentsCommand otherCommand
                && index.equals(otherCommand.index);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("index", index).toString();
    }
}
