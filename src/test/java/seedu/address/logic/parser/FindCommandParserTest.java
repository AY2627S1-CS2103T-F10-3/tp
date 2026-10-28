package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.payment.PaidInMonthPredicate;
import seedu.address.model.payment.UnpaidInMonthPredicate;
import seedu.address.model.person.FieldsContainKeywordsPredicate;
import seedu.address.model.person.FieldsContainKeywordsPredicate.Field;
import seedu.address.model.person.NameContainsKeywordsPredicate;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    /**
     * Verifies valid paid searches parse and invalid payment-status arguments are rejected.
     */
    @Test
    public void parse_paidMonth_returnsFindCommand() {
        assertParseSuccess(parser, " paid/2026-10 ",
                new FindCommand(new PaidInMonthPredicate(YearMonth.of(2026, 10))));
        for (String value : List.of("", "2026", "2026-13", "2026-00", "2026-1", "2026-10 2026-11")) {
            assertParseFailure(parser, "paid/" + value, ParserUtil.MESSAGE_INVALID_MONTH);
        }
        assertParseFailure(parser, "paid/2026-10 paid/2026-11",
                seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_PAID));
        assertParseFailure(parser, "paid/2026-10 unpaid/2026-11",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    /**
     * Verifies valid unpaid searches parse and invalid billing months are rejected.
     */
    @Test
    public void parse_unpaidMonth_returnsFindCommand() {
        assertParseSuccess(parser, " unpaid/2026-10 ",
                new FindCommand(new UnpaidInMonthPredicate(YearMonth.of(2026, 10))));
        for (String value : List.of("", "2026", "2026-13", "2026-00", "2026-1", "2026-10 2026-11")) {
            assertParseFailure(parser, "unpaid/" + value, ParserUtil.MESSAGE_INVALID_MONTH);
        }
        assertParseFailure(parser, "unpaid/2026-10 unpaid/2026-11",
                seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_UNPAID));
        assertParseFailure(parser, "Alice unpaid/2026-10",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_lessonFields_returnsFindCommand() {
        assertParseSuccess(parser, "l/math 2026-MATH-MONDAY-TEN-ELEVEN",
                new FindCommand(new FieldsContainKeywordsPredicate(Map.of(Field.LESSON,
                        List.of("math", "2026-MATH-MONDAY-TEN-ELEVEN")))));
        assertParseSuccess(parser, "n/Alice\tl/monday",
                new FindCommand(new FieldsContainKeywordsPredicate(Map.of(
                        Field.NAME, List.of("Alice"), Field.LESSON, List.of("monday")))));
        assertParseFailure(parser, "l/   ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "l/math l/science",
                seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_LESSON));
    }

    @Test
    public void parse_additionalFields_returnsFindCommand() {
        Map<String, Field> fields = Map.of("e/", Field.EMAIL, "a/", Field.ADDRESS, "t/", Field.TAG,
                "ge/", Field.GUARDIAN_EMAIL, "ga/", Field.GUARDIAN_ADDRESS, "gr/", Field.GUARDIAN_RELATIONSHIP);
        fields.forEach((prefix, field) -> {
            assertParseSuccess(parser, prefix + "First Second",
                    new FindCommand(new FieldsContainKeywordsPredicate(Map.of(field, List.of("First", "Second")))));
            assertParseFailure(parser, prefix,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
            assertParseFailure(parser, prefix + "First " + prefix + "Second",
                    seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes(new Prefix(prefix)));
        });
        assertParseSuccess(parser, "n/Alice e/example ge/parent",
                new FindCommand(new FieldsContainKeywordsPredicate(Map.of(Field.NAME, List.of("Alice"),
                        Field.EMAIL, List.of("example"), Field.GUARDIAN_EMAIL, List.of("parent")))));
        assertParseSuccess(parser, "a/Block A/10 12/34 ga/Unit B/20 56/78",
                new FindCommand(new FieldsContainKeywordsPredicate(Map.of(
                        Field.ADDRESS, List.of("Block", "A/10", "12/34"),
                        Field.GUARDIAN_ADDRESS, List.of("Unit", "B/20", "56/78")))));
        assertParseSuccess(parser, "a/Block A/10 n/Alice",
                new FindCommand(new FieldsContainKeywordsPredicate(Map.of(
                        Field.ADDRESS, List.of("Block", "A/10"), Field.NAME, List.of("Alice")))));
    }

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
        for (String input : List.of("n/", "p/", "gn/", "gp/", "n/Alice gp/", "x/test", "n/Alice x/test",
                "Alice n/Bob")) {
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
