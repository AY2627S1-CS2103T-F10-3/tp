package seedu.address.model.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;

import org.junit.jupiter.api.Test;

import seedu.address.model.lesson.Day;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Time;
import seedu.address.testutil.PersonBuilder;

public class PaidInMonthPredicateTest {
    @Test
    public void constructor_nullMonth_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PaidInMonthPredicate(null));
    }

    @Test
    public void equals_sameMonth_returnsTrue() {
        PaidInMonthPredicate predicate = new PaidInMonthPredicate(YearMonth.of(2026, 10));
        PaidInMonthPredicate same = new PaidInMonthPredicate(YearMonth.of(2026, 10));
        assertEquals(predicate, predicate);
        assertEquals(predicate, same);
        assertEquals(predicate.hashCode(), same.hashCode());
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals(new PaidInMonthPredicate(YearMonth.of(2026, 11))));
        assertFalse(predicate.equals(new UnpaidInMonthPredicate(YearMonth.of(2026, 10))));
    }

    @Test
    public void test_zeroFeesWithPayment_returnsFalse() {
        YearMonth month = YearMonth.of(2026, 10);
        Lesson freeLesson = new Lesson(Year.of(2026), Subject.MATH, Day.MONDAY, Time.TEN, Time.ELEVEN, 0);
        Payment payment = new Payment(new Amount("100"), month, LocalDate.of(2026, 10, 1));
        assertFalse(new PaidInMonthPredicate(month).test(new PersonBuilder().withLessons(freeLesson)
                .withPayments(payment).build()));
    }
}
