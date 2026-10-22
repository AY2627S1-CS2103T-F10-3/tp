package seedu.address.model.payment;

import static java.util.Objects.requireNonNull;

import java.time.YearMonth;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Person;

/**
 * Tests that a {@code Person} has paid less than their fees due for the given month.
 */
public class UnpaidInMonthPredicate implements Predicate<Person> {

    private final YearMonth month;

    /**
     * Creates a predicate that matches students with outstanding fees in {@code month}.
     */
    public UnpaidInMonthPredicate(YearMonth month) {
        this.month = requireNonNull(month);
    }

    @Override
    public boolean test(Person person) {
        return person.getOutstandingFees(month) > 0;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof UnpaidInMonthPredicate otherPredicate
                && month.equals(otherPredicate.month);
    }

    @Override
    public int hashCode() {
        return month.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("month", month).toString();
    }
}
