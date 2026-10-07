package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses an optional field selector for the view command. */
public class ViewCommandParser implements Parser<ViewCommand> {
    @Override
    public ViewCommand parse(String args) throws ParseException {
        String selector = args.trim();
        return switch (selector) {
            case "" -> new ViewCommand("");
            case "/name", "/phone", "/email", "/address", "/tag" -> new ViewCommand(selector.substring(1));
            default -> throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE));
        };
    }
}
