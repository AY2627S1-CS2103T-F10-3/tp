package seedu.address.logic.parser;

import static seedu.address.logic.commands.CommandTestUtil.VALID_LESSON_MATH;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COST;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.model.lesson.Lesson.COST_CONSTRAINT;
import static seedu.address.model.lesson.Lesson.DAY_CONSTRAINT;
import static seedu.address.model.lesson.Lesson.SUBJECT_CONSTRAINT;
import static seedu.address.model.lesson.Lesson.TIME_CONSTRAINT;
import static seedu.address.model.lesson.Lesson.TIME_ORDER_CONSTRAINT;
import static seedu.address.model.lesson.Lesson.YEAR_CONSTRAINT;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddLessonCommand;

public class AddLessonCommandParserTest {
    private final AddLessonCommandParser parser = new AddLessonCommandParser();
    private final String validArgs = " y/2026 s/MATH d/MONDAY st/TEN et/ELEVEN c/30";

    @Test
    public void parse_validArgs_success() {
        assertParseSuccess(parser, validArgs, new AddLessonCommand(VALID_LESSON_MATH));
    }

    @Test
    public void parse_caseInsensitiveLessonFields_success() {
        String args = " y/2026 s/math d/mOnDaY st/ten et/eleven c/30";
        assertParseSuccess(parser, args, new AddLessonCommand(VALID_LESSON_MATH));
    }

    @Test
    public void parse_missingPrefix_failure() {
        String expected = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, AddLessonCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " y/2026 s/MATH d/MONDAY st/TEN et/ELEVEN", expected);
    }

    @Test
    public void parse_duplicatePrefix_failure() {
        assertParseFailure(parser, validArgs + " " + PREFIX_COST + "40",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_COST));
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, validArgs.replace("s/MATH", "s/HISTORY"), SUBJECT_CONSTRAINT);
    }

    @Test
    public void parse_invalidIndividualFields_failure() {
        assertParseFailure(parser, validArgs.replace("y/2026", "y/26"), YEAR_CONSTRAINT);
        assertParseFailure(parser, validArgs.replace("d/MONDAY", "d/FUNDAY"), DAY_CONSTRAINT);
        assertParseFailure(parser, validArgs.replace("st/TEN", "st/NOON"), TIME_CONSTRAINT);
        assertParseFailure(parser, validArgs.replace("et/ELEVEN", "et/NOON"), TIME_CONSTRAINT);
        assertParseFailure(parser, validArgs.replace("c/30", "c/-1"), COST_CONSTRAINT);
        assertParseFailure(parser, validArgs.replace("st/TEN et/ELEVEN", "st/ELEVEN et/TEN"),
                TIME_ORDER_CONSTRAINT);
    }

    @Test
    public void parse_multipleInvalidValues_failure() {
        String args = validArgs.replace("s/MATH", "s/HISTORY")
                .replace("d/MONDAY", "d/FUNDAY")
                .replace("st/TEN", "st/NOON");
        assertParseFailure(parser, args, "Invalid fields: subject, day, start time.");
    }

    @Test
    public void parse_invalidYearFormat_failure() {
        String[] invalidYears = {"-5", "0", "26", "+99999"};
        for (String invalidYear : invalidYears) {
            assertParseFailure(parser, validArgs.replace("y/2026", "y/" + invalidYear), YEAR_CONSTRAINT);
        }
    }
}
