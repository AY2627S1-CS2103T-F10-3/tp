package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ViewCommand;

/**
 * Tests the parsing of optional view command selectors.
 */
public class ViewCommandParserTest {

    private final ViewCommandParser parser = new ViewCommandParser();

    @Test
    public void parse_validSelectors_success() throws Exception {
        assertParseSuccess(parser, "  ", new ViewCommand(""));
        for (String field : new String[] {"name", "phone", "email", "address", "tag"}) {
            assertParseSuccess(parser, " /" + field + " ", new ViewCommand(field));
            assertEquals(new ViewCommand(field),
                    new AddressBookParser().parseCommand("view /" + field));
        }
    }

    @Test
    public void parse_invalidSelectors_failure() {
        for (String args : new String[] {"email", "/unknown", "/email /address", "/email value"}) {
            assertParseFailure(parser, args, String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE));
        }
    }

}
