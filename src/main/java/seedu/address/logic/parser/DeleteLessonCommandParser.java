package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.DeleteLessonCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses arguments for {@link DeleteLessonCommand}. */
public class DeleteLessonCommandParser implements Parser<DeleteLessonCommand> {
    @Override
    public DeleteLessonCommand parse(String args) throws ParseException {
        String lessonId = args.trim();
        if (lessonId.isEmpty() || lessonId.contains(" ")) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    DeleteLessonCommand.MESSAGE_USAGE));
        }
        return new DeleteLessonCommand(lessonId);
    }
}
