package seedu.address.model.payment;

import static java.util.Objects.requireNonNull;

import java.time.YearMonth;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Person;

/**
 * Matches students whose positive monthly fees have been fully paid.
 */
public class PaidInMonthPredicate implements Predicate<Person> {
    private final YearMonth month;

    /**
     * Constructs a paid-status predicate for a billing month.
     *
     * @param month Billing month to check.
     */
    public PaidInMonthPredicate(YearMonth month) {
        this.month = requireNonNull(month);
    }

    /**
     * Returns whether positive fees are fully covered, including overpayments.
     */
    @Override
    public boolean test(Person person) {
        return person.getFeesDue(month) > 0 && person.getOutstandingFees(month) == 0;
    }

    /**
     * Returns whether the other predicate checks the same billing month.
     */
    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof PaidInMonthPredicate otherPredicate
                && month.equals(otherPredicate.month);
    }

    /**
     * Returns the hash code of the billing month.
     */
    @Override
    public int hashCode() {
        return month.hashCode();
    }

    /**
     * Returns a description containing the billing month.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).add("month", month).toString();
    }
}
