package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ViewPaymentsCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ViewPaymentsCommand object
 */
public class ViewPaymentsCommandParser implements Parser<ViewPaymentsCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ViewPaymentsCommand
     * and returns a ViewPaymentsCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public ViewPaymentsCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new ViewPaymentsCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewPaymentsCommand.MESSAGE_USAGE), pe);
        }
    }
}
