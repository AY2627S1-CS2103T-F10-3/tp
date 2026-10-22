package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PAYMENT_INDEX;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.DeletePaymentCommand;

public class DeletePaymentCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeletePaymentCommand.MESSAGE_USAGE);

    private final DeletePaymentCommandParser parser = new DeletePaymentCommandParser();

    @Test
    public void parse_validArgs_returnsDeletePaymentCommand() {
        assertParseSuccess(parser, "1 i/2", new DeletePaymentCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON));
    }

    @Test
    public void parse_invalidArgs_failure() {
        assertParseFailure(parser, "1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "i/2", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "a i/2", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 i/0", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 i/2 i/3", Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PAYMENT_INDEX));
    }
}
