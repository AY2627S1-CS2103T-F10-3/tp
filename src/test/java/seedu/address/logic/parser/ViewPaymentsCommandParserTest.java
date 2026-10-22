package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ViewPaymentsCommand;

public class ViewPaymentsCommandParserTest {

    private final ViewPaymentsCommandParser parser = new ViewPaymentsCommandParser();

    @Test
    public void parse_validArgs_returnsViewPaymentsCommand() {
        assertParseSuccess(parser, "1", new ViewPaymentsCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewPaymentsCommand.MESSAGE_USAGE));
    }
}
