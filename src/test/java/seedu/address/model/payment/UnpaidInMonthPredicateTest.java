package seedu.address.model.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_LESSON_MATH;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.time.YearMonth;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class UnpaidInMonthPredicateTest {

    private static final YearMonth OCTOBER = YearMonth.of(2026, 10);
    private static final String FULL_FEE = String.valueOf(VALID_LESSON_MATH.getCost());

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new UnpaidInMonthPredicate(null));
    }

    @Test
    public void test_noPaymentForMonth_returnsTrue() {
        UnpaidInMonthPredicate predicate = new UnpaidInMonthPredicate(OCTOBER);
        assertTrue(predicate.test(new PersonBuilder().withLessons(VALID_LESSON_MATH).build()));

        // payment for a different month does not count
        assertTrue(predicate.test(new PersonBuilder().withLessons(VALID_LESSON_MATH)
                .withPayments(new Payment(new Amount(FULL_FEE), YearMonth.of(2026, 9), LocalDate.of(2026, 9, 1)))
                .build()));
    }

    @Test
    public void test_fullyPaid_returnsFalse() {
        UnpaidInMonthPredicate predicate = new UnpaidInMonthPredicate(OCTOBER);
        assertFalse(predicate.test(new PersonBuilder().withLessons(VALID_LESSON_MATH)
                .withPayments(new Payment(new Amount(FULL_FEE), OCTOBER, LocalDate.of(2026, 10, 1)))
                .build()));
    }

    @Test
    public void test_noFeesDue_returnsFalse() {
        UnpaidInMonthPredicate predicate = new UnpaidInMonthPredicate(OCTOBER);
        assertFalse(predicate.test(new PersonBuilder().build()));

        // lesson in a different year is not billed
        assertFalse(new UnpaidInMonthPredicate(YearMonth.of(2027, 1))
                .test(new PersonBuilder().withLessons(VALID_LESSON_MATH).build()));
    }

    @Test
    public void equals() {
        UnpaidInMonthPredicate predicate = new UnpaidInMonthPredicate(OCTOBER);

        assertTrue(predicate.equals(predicate));
        assertTrue(predicate.equals(new UnpaidInMonthPredicate(YearMonth.of(2026, 10))));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals(OCTOBER));
        assertFalse(predicate.equals(new UnpaidInMonthPredicate(YearMonth.of(2026, 11))));
        assertEquals(predicate.hashCode(), new UnpaidInMonthPredicate(OCTOBER).hashCode());
    }

    @Test
    public void toStringMethod() {
        assertEquals(UnpaidInMonthPredicate.class.getCanonicalName() + "{month=2026-10}",
                new UnpaidInMonthPredicate(OCTOBER).toString());
    }
}
