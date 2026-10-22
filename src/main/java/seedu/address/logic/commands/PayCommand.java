package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AMOUNT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MONTH;

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
 * Records a fee payment for a student identified using its displayed index.
 */
public class PayCommand extends Command {

    public static final String COMMAND_WORD = "pay";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Records a fee payment for the student identified by the index number used in the displayed list.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_AMOUNT + "AMOUNT "
            + PREFIX_MONTH + "MONTH "
            + "[" + PREFIX_DATE + "DATE]\n"
            + "MONTH is the billing month (yyyy-MM). DATE is when the payment was received (yyyy-MM-dd) "
            + "and defaults to today.\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_AMOUNT + "120 "
            + PREFIX_MONTH + "2026-10 "
            + PREFIX_DATE + "2026-10-03";

    public static final String MESSAGE_SUCCESS = "Recorded payment for %1$s: %2$s\n"
            + "Fees for %3$s: $%4$d due, $%5$d paid, $%6$d outstanding";

    private final Index index;
    private final Payment payment;

    /**
     * Creates a PayCommand to record {@code payment} for the student at {@code index}.
     */
    public PayCommand(Index index, Payment payment) {
        requireNonNull(index);
        requireNonNull(payment);
        this.index = index;
        this.payment = payment;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person student = lastShownList.get(index.getZeroBased());
        List<Payment> updatedPayments = new ArrayList<>(student.getPayments());
        updatedPayments.add(payment);
        Person updatedStudent = student.withPayments(updatedPayments);
        model.setPerson(student, updatedStudent);

        return new CommandResult(String.format(MESSAGE_SUCCESS, updatedStudent.getName(), payment,
                payment.getMonth(), updatedStudent.getFeesDue(payment.getMonth()),
                updatedStudent.getAmountPaid(payment.getMonth()),
                updatedStudent.getOutstandingFees(payment.getMonth())));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof PayCommand otherCommand
                && index.equals(otherCommand.index) && payment.equals(otherCommand.payment);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("payment", payment)
                .toString();
    }
}
