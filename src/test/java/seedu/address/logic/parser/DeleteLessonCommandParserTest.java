package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.DeleteLessonCommand;

public class DeleteLessonCommandParserTest {
    private final DeleteLessonCommandParser parser = new DeleteLessonCommandParser();

    @Test
    public void parse_validId_success() {
        assertParseSuccess(parser, " 2026-MATH-MONDAY-TEN-ELEVEN-30",
                new DeleteLessonCommand("2026-MATH-MONDAY-TEN-ELEVEN-30"));
    }

    @Test
    public void parse_caseInsensitiveId_success() {
        assertParseSuccess(parser, "2026-math-monday-ten-eleven",
                new DeleteLessonCommand("2026-MATH-MONDAY-TEN-ELEVEN"));
    }

    @Test
    public void parse_emptyId_failure() {
        assertParseFailure(parser, " ", String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                DeleteLessonCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_idWithSpaces_failure() {
        assertParseFailure(parser, "2026 MATH", String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                DeleteLessonCommand.MESSAGE_USAGE));
    }
}
