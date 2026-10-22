package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedPayment.INVALID_DATE_MESSAGE;
import static seedu.address.storage.JsonAdaptedPayment.INVALID_MONTH_MESSAGE;
import static seedu.address.storage.JsonAdaptedPayment.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPayments.PAYMENT_NOVEMBER;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.payment.Amount;

public class JsonAdaptedPaymentTest {
    private static final String VALID_AMOUNT = "100";
    private static final String VALID_MONTH = "2026-11";
    private static final String VALID_DATE = "2026-11-02";

    @Test
    public void toModelType_validPaymentDetails_returnsPayment() throws Exception {
        assertEquals(PAYMENT_NOVEMBER, new JsonAdaptedPayment(PAYMENT_NOVEMBER).toModelType());
        assertEquals(PAYMENT_NOVEMBER, new JsonAdaptedPayment(VALID_AMOUNT, VALID_MONTH, VALID_DATE).toModelType());
    }

    @Test
    public void toModelType_invalidAmount_throwsIllegalValueException() {
        JsonAdaptedPayment payment = new JsonAdaptedPayment("-5", VALID_MONTH, VALID_DATE);
        assertThrows(IllegalValueException.class, Amount.MESSAGE_CONSTRAINTS, payment::toModelType);
    }

    @Test
    public void toModelType_invalidMonth_throwsIllegalValueException() {
        JsonAdaptedPayment payment = new JsonAdaptedPayment(VALID_AMOUNT, "2026-13", VALID_DATE);
        assertThrows(IllegalValueException.class, INVALID_MONTH_MESSAGE, payment::toModelType);
    }

    @Test
    public void toModelType_invalidDate_throwsIllegalValueException() {
        JsonAdaptedPayment payment = new JsonAdaptedPayment(VALID_AMOUNT, VALID_MONTH, "2026-02-30");
        assertThrows(IllegalValueException.class, INVALID_DATE_MESSAGE, payment::toModelType);
    }

    @Test
    public void toModelType_missingFields_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, String.format(MISSING_FIELD_MESSAGE_FORMAT, "amount"),
                new JsonAdaptedPayment(null, VALID_MONTH, VALID_DATE)::toModelType);
        assertThrows(IllegalValueException.class, String.format(MISSING_FIELD_MESSAGE_FORMAT, "month"),
                new JsonAdaptedPayment(VALID_AMOUNT, null, VALID_DATE)::toModelType);
        assertThrows(IllegalValueException.class, String.format(MISSING_FIELD_MESSAGE_FORMAT, "date"),
                new JsonAdaptedPayment(VALID_AMOUNT, VALID_MONTH, null)::toModelType);
    }
}
