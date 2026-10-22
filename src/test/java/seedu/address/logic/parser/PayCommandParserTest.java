package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AMOUNT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MONTH;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPayments.PAYMENT_NOVEMBER;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.PayCommand;
import seedu.address.model.payment.Amount;
import seedu.address.model.payment.Payment;

public class PayCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, PayCommand.MESSAGE_USAGE);
    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-11-20T02:00:00Z"), ZoneOffset.UTC);

    private final PayCommandParser parser = new PayCommandParser(FIXED_CLOCK);

    @Test
    public void parse_allFieldsPresent_success() {
        assertParseSuccess(parser, "1 amt/100 m/2026-11 d/2026-11-02",
                new PayCommand(INDEX_FIRST_PERSON, PAYMENT_NOVEMBER));

        // fields in any order
        assertParseSuccess(parser, " 1 d/2026-11-02 m/2026-11 amt/100 ",
                new PayCommand(INDEX_FIRST_PERSON, PAYMENT_NOVEMBER));
    }

    @Test
    public void parse_dateMissing_defaultsToToday() {
        Payment paidToday = new Payment(new Amount("100"), PAYMENT_NOVEMBER.getMonth(), LocalDate.of(2026, 11, 20));
        assertParseSuccess(parser, "1 amt/100 m/2026-11", new PayCommand(INDEX_FIRST_PERSON, paidToday));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        assertParseFailure(parser, "amt/100 m/2026-11", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 m/2026-11", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 amt/100", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "0 amt/100 m/2026-11", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 amt/0 m/2026-11", Amount.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 amt/100 m/11-2026", ParserUtil.MESSAGE_INVALID_MONTH);
        assertParseFailure(parser, "1 amt/100 m/2026-11 d/2026-11-31", ParserUtil.MESSAGE_INVALID_DATE);
    }

    @Test
    public void parse_duplicatePrefixes_failure() {
        assertParseFailure(parser, "1 amt/100 amt/50 m/2026-11",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_AMOUNT));
        assertParseFailure(parser, "1 amt/100 m/2026-11 m/2026-10",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_MONTH));
    }
}
