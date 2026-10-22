package seedu.address.model.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.time.YearMonth;

import org.junit.jupiter.api.Test;

public class PaymentTest {

    private static final Amount AMOUNT = new Amount("120");
    private static final YearMonth MONTH = YearMonth.of(2026, 10);
    private static final LocalDate DATE = LocalDate.of(2026, 10, 3);

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Payment(null, MONTH, DATE));
        assertThrows(NullPointerException.class, () -> new Payment(AMOUNT, null, DATE));
        assertThrows(NullPointerException.class, () -> new Payment(AMOUNT, MONTH, null));
    }

    @Test
    public void equals() {
        Payment payment = new Payment(AMOUNT, MONTH, DATE);

        assertTrue(payment.equals(new Payment(new Amount("120"), YearMonth.of(2026, 10), LocalDate.of(2026, 10, 3))));
        assertTrue(payment.equals(payment));
        assertFalse(payment.equals(null));
        assertFalse(payment.equals(AMOUNT));
        assertFalse(payment.equals(new Payment(new Amount("100"), MONTH, DATE)));
        assertFalse(payment.equals(new Payment(AMOUNT, YearMonth.of(2026, 11), DATE)));
        assertFalse(payment.equals(new Payment(AMOUNT, MONTH, LocalDate.of(2026, 10, 4))));
        assertEquals(payment.hashCode(), new Payment(AMOUNT, MONTH, DATE).hashCode());
    }

    @Test
    public void toStringMethod() {
        assertEquals("$120 for 2026-10, paid on 2026-10-03", new Payment(AMOUNT, MONTH, DATE).toString());
    }
}
