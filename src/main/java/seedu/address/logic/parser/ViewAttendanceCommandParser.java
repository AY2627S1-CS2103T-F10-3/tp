package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ViewAttendanceCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ViewAttendanceCommand object
 */
public class ViewAttendanceCommandParser implements Parser<ViewAttendanceCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ViewAttendanceCommand
     * and returns a ViewAttendanceCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public ViewAttendanceCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new ViewAttendanceCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewAttendanceCommand.MESSAGE_USAGE), pe);
        }
    }
}
