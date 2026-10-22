package seedu.address.model.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class AmountTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Amount(null));
    }

    @Test
    public void constructor_invalidAmount_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Amount("0"));
    }

    @Test
    public void isValidAmount() {
        // invalid amounts
        assertFalse(Amount.isValidAmount("")); // empty string
        assertFalse(Amount.isValidAmount("0")); // zero
        assertFalse(Amount.isValidAmount("-5")); // negative
        assertFalse(Amount.isValidAmount("12.50")); // not whole dollars
        assertFalse(Amount.isValidAmount("$12")); // currency symbol
        assertFalse(Amount.isValidAmount("1234567890")); // too many digits

        // valid amounts
        assertTrue(Amount.isValidAmount("1"));
        assertTrue(Amount.isValidAmount("120"));
        assertTrue(Amount.isValidAmount("007"));
        assertTrue(Amount.isValidAmount("999999999"));
    }

    @Test
    public void equals() {
        Amount amount = new Amount("120");

        assertTrue(amount.equals(new Amount("120")));
        assertTrue(amount.equals(new Amount("0120")));
        assertTrue(amount.equals(amount));
        assertFalse(amount.equals(null));
        assertFalse(amount.equals(120));
        assertFalse(amount.equals(new Amount("121")));
        assertEquals(amount.hashCode(), new Amount("120").hashCode());
    }

    @Test
    public void toStringMethod() {
        assertEquals("$120", new Amount("120").toString());
    }
}
