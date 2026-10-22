package seedu.address.storage;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.payment.Amount;
import seedu.address.model.payment.Payment;

/**
 * Jackson-friendly version of {@link Payment}.
 */
class JsonAdaptedPayment {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Payment's %s field is missing!";
    public static final String INVALID_MONTH_MESSAGE = "Payment month must be in the form yyyy-MM, eg. 2026-10";
    public static final String INVALID_DATE_MESSAGE = "Payment date must be in the form yyyy-MM-dd, eg. 2026-10-03";

    private final String amount;
    private final String month;
    private final String date;

    /**
     * Constructs a {@code JsonAdaptedPayment} with the given payment details.
     */
    @JsonCreator
    public JsonAdaptedPayment(@JsonProperty("amount") String amount, @JsonProperty("month") String month,
            @JsonProperty("date") String date) {
        this.amount = amount;
        this.month = month;
        this.date = date;
    }

    /**
     * Converts a given {@code Payment} into this class for Jackson use.
     */
    public JsonAdaptedPayment(Payment source) {
        amount = String.valueOf(source.getAmount().value);
        month = source.getMonth().toString();
        date = source.getDate().toString();
    }

    /**
     * Converts this Jackson-friendly adapted payment object into the model's {@code Payment} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted payment.
     */
    public Payment toModelType() throws IllegalValueException {
        if (amount == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "amount"));
        }
        if (!Amount.isValidAmount(amount)) {
            throw new IllegalValueException(Amount.MESSAGE_CONSTRAINTS);
        }
        if (month == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "month"));
        }
        if (date == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "date"));
        }

        final YearMonth modelMonth;
        try {
            modelMonth = YearMonth.parse(month);
        } catch (DateTimeParseException exception) {
            throw new IllegalValueException(INVALID_MONTH_MESSAGE, exception);
        }
        final LocalDate modelDate;
        try {
            modelDate = LocalDate.parse(date);
        } catch (DateTimeParseException exception) {
            throw new IllegalValueException(INVALID_DATE_MESSAGE, exception);
        }
        return new Payment(new Amount(amount), modelMonth, modelDate);
    }
}
