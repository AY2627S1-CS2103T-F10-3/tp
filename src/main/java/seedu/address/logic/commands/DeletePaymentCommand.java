package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PAYMENT_INDEX;

import java.util.ArrayList;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.payment.Payment;
import seedu.address.model.person.Person;

/**
 * Deletes one recorded payment of a student identified using its displayed index.
 */
public class DeletePaymentCommand extends Command {

    public static final String COMMAND_WORD = "deletepayment";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes a payment of the student identified by the index number used in the displayed list.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_PAYMENT_INDEX + "PAYMENT_INDEX (as shown by " + ViewPaymentsCommand.COMMAND_WORD + ")\n"
            + "Example: " + COMMAND_WORD + " 1 " + PREFIX_PAYMENT_INDEX + "2";

    public static final String MESSAGE_SUCCESS = "Deleted payment for %1$s: %2$s";
    public static final String MESSAGE_INVALID_PAYMENT_INDEX = "The payment index provided is invalid.";

    private final Index index;
    private final Index paymentIndex;

    /**
     * Creates a DeletePaymentCommand to delete the payment at {@code paymentIndex}
     * of the student at {@code index}.
     */
    public DeletePaymentCommand(Index index, Index paymentIndex) {
        requireNonNull(index);
        requireNonNull(paymentIndex);
        this.index = index;
        this.paymentIndex = paymentIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person student = lastShownList.get(index.getZeroBased());
        if (paymentIndex.getZeroBased() >= student.getPayments().size()) {
            throw new CommandException(MESSAGE_INVALID_PAYMENT_INDEX);
        }

        List<Payment> updatedPayments = new ArrayList<>(student.getPayments());
        Payment deletedPayment = updatedPayments.remove(paymentIndex.getZeroBased());
        model.setPerson(student, student.withPayments(updatedPayments));

        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(), deletedPayment));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof DeletePaymentCommand otherCommand
                && index.equals(otherCommand.index) && paymentIndex.equals(otherCommand.paymentIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("paymentIndex", paymentIndex)
                .toString();
    }
}
