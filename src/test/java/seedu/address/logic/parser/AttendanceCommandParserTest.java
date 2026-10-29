package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LESSON;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalAttendance.ATTENDANCE_PRESENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AttendanceCommand;
import seedu.address.logic.commands.DeleteAttendanceCommand;
import seedu.address.logic.commands.ViewAttendanceCommand;
import seedu.address.model.attendance.Attendance;

public class AttendanceCommandParserTest {

    private static final String LESSON = " l/2026-MATH-MONDAY-TEN-ELEVEN";
    private static final String DATE = " d/2026-10-19";
    private static final String STATUS = " s/present";
    private static final String INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AttendanceCommand.MESSAGE_USAGE);
    private final AttendanceCommandParser parser = new AttendanceCommandParser();

    @Test
    public void parse_validFields_acceptsAnyOrderAndStatusCase() {
        AttendanceCommand expected = new AttendanceCommand(INDEX_FIRST_PERSON, ATTENDANCE_PRESENT);
        assertParseSuccess(parser, "1" + LESSON + DATE + STATUS, expected);
        assertParseSuccess(parser, " 1 s/PrEsEnT" + DATE + LESSON + " ", expected);
    }

    @Test
    public void parse_caseInsensitiveLessonId_success() {
        AttendanceCommand expected = new AttendanceCommand(INDEX_FIRST_PERSON, ATTENDANCE_PRESENT);
        assertParseSuccess(parser, "1 l/2026-mAtH-mOnDaY-tEn-ElEvEn" + DATE + STATUS, expected);
    }

    @Test
    public void parse_missingFields_failure() {
        assertParseFailure(parser, LESSON + DATE + STATUS, INVALID_FORMAT);
        assertParseFailure(parser, "1" + DATE + STATUS, INVALID_FORMAT);
        assertParseFailure(parser, "1" + LESSON + STATUS, INVALID_FORMAT);
        assertParseFailure(parser, "1" + LESSON + DATE, INVALID_FORMAT);
    }

    @Test
    public void parse_invalidFields_failure() {
        for (String index : new String[]{"0", "-1", "abc", "2147483648", "1 2"}) {
            assertParseFailure(parser, index + LESSON + DATE + STATUS, INVALID_FORMAT);
        }
        assertParseFailure(parser, "1 l/ " + DATE + STATUS, Attendance.MESSAGE_LESSON_CONSTRAINTS);
        for (String date : new String[]{"2026-02-29", "2026-11-31", "19-10-2026", "2026-1-1", ""}) {
            assertParseFailure(parser, "1" + LESSON + " d/" + date + STATUS, ParserUtil.MESSAGE_INVALID_DATE);
        }
        for (String status : new String[]{"late", "", "present extra"}) {
            assertParseFailure(parser, "1" + LESSON + DATE + " s/" + status, Attendance.MESSAGE_STATUS_CONSTRAINTS);
        }
    }

    @Test
    public void parse_duplicateFields_failure() {
        String valid = "1" + LESSON + DATE + STATUS;
        assertParseFailure(parser, valid + LESSON, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_LESSON));
        assertParseFailure(parser, valid + DATE, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DATE));
        assertParseFailure(parser, valid + STATUS, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_STATUS));
    }

    @Test
    public void addressBookParser_routesAttendanceCommands() throws Exception {
        AddressBookParser dispatcher = new AddressBookParser();
        assertEquals(new AttendanceCommand(INDEX_FIRST_PERSON, ATTENDANCE_PRESENT),
                dispatcher.parseCommand("attendance 1" + LESSON + DATE + STATUS));
        assertEquals(new ViewAttendanceCommand(INDEX_FIRST_PERSON), dispatcher.parseCommand("viewattendance 1"));
        assertEquals(new DeleteAttendanceCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON),
                dispatcher.parseCommand("deleteattendance 1 i/1"));
    }
}
