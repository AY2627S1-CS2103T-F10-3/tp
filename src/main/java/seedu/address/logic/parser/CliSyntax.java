package seedu.address.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {
    public static final Prefix PREFIX_UNPAID = new Prefix("unpaid/");
    public static final Prefix PREFIX_PAID = new Prefix("paid/");

    /* Prefix definitions */
    public static final Prefix PREFIX_GUARDIAN_NAME = new Prefix("gn/");
    public static final Prefix PREFIX_GUARDIAN_PHONE = new Prefix("gp/");
    public static final Prefix PREFIX_GUARDIAN_EMAIL = new Prefix("ge/");
    public static final Prefix PREFIX_GUARDIAN_RELATIONSHIP = new Prefix("gr/");
    public static final Prefix PREFIX_GUARDIAN_ADDRESS = new Prefix("ga/");

    public static final Prefix PREFIX_NAME = new Prefix("n/");
    public static final Prefix PREFIX_PHONE = new Prefix("p/");
    public static final Prefix PREFIX_EMAIL = new Prefix("e/");
    public static final Prefix PREFIX_ADDRESS = new Prefix("a/");
    public static final Prefix PREFIX_TAG = new Prefix("t/");
    public static final Prefix PREFIX_LESSON = new Prefix("l/");

    public static final Prefix PREFIX_AMOUNT = new Prefix("amt/");
    public static final Prefix PREFIX_MONTH = new Prefix("m/");
    public static final Prefix PREFIX_DATE = new Prefix("d/");
    public static final Prefix PREFIX_PAYMENT_INDEX = new Prefix("i/");
    public static final Prefix PREFIX_ATTENDANCE_INDEX = new Prefix("i/");
    public static final Prefix PREFIX_STATUS = new Prefix("s/");

}
