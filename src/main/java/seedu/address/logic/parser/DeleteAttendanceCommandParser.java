package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ATTENDANCE_INDEX;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteAttendanceCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeleteAttendanceCommand object
 */
public class DeleteAttendanceCommandParser implements Parser<DeleteAttendanceCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteAttendanceCommand
     * and returns a DeleteAttendanceCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public DeleteAttendanceCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_ATTENDANCE_INDEX);

        if (argMultimap.getValue(PREFIX_ATTENDANCE_INDEX).isEmpty() || argMultimap.getPreamble().isBlank()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteAttendanceCommand.MESSAGE_USAGE));
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ATTENDANCE_INDEX);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(DeleteAttendanceCommand.MESSAGE_INVALID_STUDENT_INDEX_FORMAT, pe);
        }
        Index attendanceIndex;
        try {
            attendanceIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_ATTENDANCE_INDEX).get());
        } catch (ParseException pe) {
            throw new ParseException(DeleteAttendanceCommand.MESSAGE_INVALID_ATTENDANCE_INDEX_FORMAT, pe);
        }
        return new DeleteAttendanceCommand(index, attendanceIndex);
    }
}
