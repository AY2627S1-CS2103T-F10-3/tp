package seedu.address.model.payment;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a payment amount in whole dollars.
 * Guarantees: immutable; is valid as declared in {@link #isValidAmount(String)}
 */
public class Amount {

    public static final String MESSAGE_CONSTRAINTS = "Amount must be a positive whole number of dollars, eg. 120";

    private static final String VALIDATION_REGEX = "[0-9]{1,9}";

    public final int value;

    /**
     * Constructs an {@code Amount}.
     *
     * @param amount A valid amount.
     */
    public Amount(String amount) {
        requireNonNull(amount);
        checkArgument(isValidAmount(amount), MESSAGE_CONSTRAINTS);
        value = Integer.parseInt(amount);
    }

    /**
     * Returns true if the given string is a valid amount.
     */
    public static boolean isValidAmount(String test) {
        return test.matches(VALIDATION_REGEX) && Integer.parseInt(test) > 0;
    }

    @Override
    public String toString() {
        return "$" + value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof Amount otherAmount && value == otherAmount.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}
