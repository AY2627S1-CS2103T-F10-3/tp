package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.model.lesson.Lesson.YEAR_CONSTRAINT;

import java.time.Year;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditLessonCommand;
import seedu.address.model.lesson.Day;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Time;

public class EditLessonCommandParserTest {
    private final EditLessonCommandParser parser = new EditLessonCommandParser();

    @Test
    public void parse_partialFields_success() {
        assertParseSuccess(parser, "2026-MATH-MONDAY-TEN-ELEVEN c/40",
                new EditLessonCommand("2026-MATH-MONDAY-TEN-ELEVEN", null, null, null, null, null, 40));
    }

    @Test
    public void parse_caseInsensitiveLessonFields_success() {
        assertParseSuccess(parser, "2026-MATH-MONDAY-TEN-ELEVEN y/2027 s/math d/mOnDaY st/ten et/eleven",
                new EditLessonCommand("2026-MATH-MONDAY-TEN-ELEVEN", Year.of(2027), Subject.MATH, Day.MONDAY,
                        Time.TEN, Time.ELEVEN, null));
    }

    @Test
    public void parse_noFields_failure() {
        assertParseFailure(parser, "2026-MATH-MONDAY-TEN-ELEVEN",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditLessonCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidYearFormat_failure() {
        assertParseFailure(parser, "2026-MATH-MONDAY-TEN-ELEVEN y/+99999", YEAR_CONSTRAINT);
    }
}
