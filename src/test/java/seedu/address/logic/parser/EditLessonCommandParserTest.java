package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.YearMonth;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditLessonCommand;

public class EditLessonCommandParserTest {
    private final EditLessonCommandParser parser = new EditLessonCommandParser();

    @Test
    public void parse_partialFields_success() {
        assertParseSuccess(parser, "2026-MATH-MONDAY-TEN-ELEVEN c/40",
                new EditLessonCommand("2026-MATH-MONDAY-TEN-ELEVEN", null, null, null, null, null, 40));
    }

    @Test
    public void parse_noFields_failure() {
        assertParseFailure(parser, "2026-MATH-MONDAY-TEN-ELEVEN",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditLessonCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_effectiveMonth_success() {
        assertParseSuccess(parser, "2026-MATH-MONDAY-TEN-ELEVEN c/40 from/2026-11",
                new EditLessonCommand("2026-MATH-MONDAY-TEN-ELEVEN", null, null, null, null, null, 40,
                        YearMonth.of(2026, 11)));
    }

    @Test
    public void parse_onlyEffectiveMonth_failure() {
        assertParseFailure(parser, "2026-MATH-MONDAY-TEN-ELEVEN from/2026-11",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditLessonCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidEffectiveMonth_failure() {
        assertParseFailure(parser, "2026-MATH-MONDAY-TEN-ELEVEN c/40 from/2026-13", ParserUtil.MESSAGE_INVALID_MONTH);
    }
}
