package seedu.address.model.payment;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;

/**
 * Represents a fee payment made by a student for one billing month.
 * Guarantees: details are present and not null, immutable.
 */
public class Payment {

    private final Amount amount;
    private final YearMonth month;
    private final LocalDate date;

    /**
     * Constructs a {@code Payment}.
     *
     * @param amount Amount paid.
     * @param month Billing month the payment is for.
     * @param date Date the payment was received.
     */
    public Payment(Amount amount, YearMonth month, LocalDate date) {
        requireAllNonNull(amount, month, date);
        this.amount = amount;
        this.month = month;
        this.date = date;
    }

    public Amount getAmount() {
        return amount;
    }

    public YearMonth getMonth() {
        return month;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Payment otherPayment)) {
            return false;
        }
        return amount.equals(otherPayment.amount) && month.equals(otherPayment.month)
                && date.equals(otherPayment.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, month, date);
    }

    @Override
    public String toString() {
        return String.format("%s for %s, paid on %s", amount, month, date);
    }
}
