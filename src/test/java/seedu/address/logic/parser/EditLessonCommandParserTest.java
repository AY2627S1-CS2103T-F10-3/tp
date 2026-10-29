package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.YearMonth;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditLessonCommand;
import seedu.address.model.lesson.Day;
import seedu.address.model.lesson.Time;

public class EditLessonCommandParserTest {
    private final EditLessonCommandParser parser = new EditLessonCommandParser();

    @Test
    public void parse_partialFields_success() {
        assertParseSuccess(parser, "2026-MATH-MONDAY-TEN-ELEVEN c/40",
                new EditLessonCommand("2026-MATH-MONDAY-TEN-ELEVEN", null, null, null, 40));
    }

    @Test
    public void parse_caseInsensitiveLessonFields_success() {
        assertParseSuccess(parser, "2026-mAtH-mOnDaY-tEn-ElEvEn d/mOnDaY st/ten et/eleven",
                new EditLessonCommand("2026-MATH-MONDAY-TEN-ELEVEN", Day.MONDAY, Time.TEN, Time.ELEVEN, null));
    }

    @Test
    public void parse_noFields_failure() {
        assertParseFailure(parser, "2026-MATH-MONDAY-TEN-ELEVEN",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditLessonCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_effectiveMonth_success() {
        assertParseSuccess(parser, "2026-MATH-MONDAY-TEN-ELEVEN c/40 from/2026-11",
                new EditLessonCommand("2026-MATH-MONDAY-TEN-ELEVEN", null, null, null, 40,
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

    @Test
    public void parse_yearOrSubjectEdit_failure() {
        assertParseFailure(parser, "2026-MATH-MONDAY-TEN-ELEVEN y/2027 c/40",
                EditLessonCommand.MESSAGE_IMMUTABLE_FIELDS);
        assertParseFailure(parser, "2026-MATH-MONDAY-TEN-ELEVEN s/SCIENCE c/40",
                EditLessonCommand.MESSAGE_IMMUTABLE_FIELDS);
    }

}
