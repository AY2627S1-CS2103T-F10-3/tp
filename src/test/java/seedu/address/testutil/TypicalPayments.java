package seedu.address.testutil;

import java.time.LocalDate;
import java.time.YearMonth;

import seedu.address.model.payment.Amount;
import seedu.address.model.payment.Payment;

/**
 * A utility class containing a list of {@code Payment} objects to be used in tests.
 */
public class TypicalPayments {

    public static final YearMonth OCTOBER_2026 = YearMonth.of(2026, 10);
    public static final YearMonth NOVEMBER_2026 = YearMonth.of(2026, 11);

    public static final Payment PAYMENT_OCTOBER_PARTIAL =
            new Payment(new Amount("10"), OCTOBER_2026, LocalDate.of(2026, 10, 3));
    public static final Payment PAYMENT_OCTOBER_REST =
            new Payment(new Amount("20"), OCTOBER_2026, LocalDate.of(2026, 10, 17));
    public static final Payment PAYMENT_NOVEMBER =
            new Payment(new Amount("100"), NOVEMBER_2026, LocalDate.of(2026, 11, 2));

    private TypicalPayments() {} // prevents instantiation
}
