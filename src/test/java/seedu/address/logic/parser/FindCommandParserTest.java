package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.FieldsContainKeywordsPredicate;
import seedu.address.model.person.FieldsContainKeywordsPredicate.Field;
import seedu.address.model.person.NameContainsKeywordsPredicate;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_prefixedFields_returnsFindCommand() {
        assertParseSuccess(parser, "n/Alice p/9123 gn/Mary gp/9876",
                new FindCommand(new FieldsContainKeywordsPredicate(Map.of(
                        Field.NAME, List.of("Alice"), Field.PHONE, List.of("9123"),
                        Field.GUARDIAN_NAME, List.of("Mary"), Field.GUARDIAN_PHONE, List.of("9876")))));
        assertParseSuccess(parser, " \tgn/Mary Bob  ",
                new FindCommand(new FieldsContainKeywordsPredicate(Map.of(
                        Field.GUARDIAN_NAME, List.of("Mary", "Bob")))));
        assertParseSuccess(parser, "n/Alice\tgp/9876",
                new FindCommand(new FieldsContainKeywordsPredicate(Map.of(
                        Field.NAME, List.of("Alice"), Field.GUARDIAN_PHONE, List.of("9876")))));
    }

    @Test
    public void parse_invalidFields_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        for (String input : List.of("n/", "p/", "gn/", "gp/", "n/Alice gp/", "e/test", "Alice n/Bob")) {
            assertParseFailure(parser, input, message);
        }
        assertParseFailure(parser, "n/Alice n/Bob",
                seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NAME));
    }

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice", "Bob")));
        assertParseSuccess(parser, "Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedFindCommand);
    }

}
