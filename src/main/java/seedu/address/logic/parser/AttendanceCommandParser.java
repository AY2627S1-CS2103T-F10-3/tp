package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LESSON;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;

import java.time.LocalDate;
import java.util.Locale;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AttendanceCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.attendance.AttendanceStatus;

/**
 * Parses the student index, lesson ID, session date and attendance status.
 */
public class AttendanceCommandParser implements Parser<AttendanceCommand> {

    @Override
    public AttendanceCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_LESSON, PREFIX_DATE, PREFIX_STATUS);
        if (argMultimap.getValue(PREFIX_LESSON).isEmpty() || argMultimap.getValue(PREFIX_DATE).isEmpty()
                || argMultimap.getValue(PREFIX_STATUS).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AttendanceCommand.MESSAGE_USAGE));
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_LESSON, PREFIX_DATE, PREFIX_STATUS);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException exception) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AttendanceCommand.MESSAGE_USAGE),
                    exception);
        }
        String lessonId = argMultimap.getValue(PREFIX_LESSON).get().trim();
        if (lessonId.isBlank()) {
            throw new ParseException(Attendance.MESSAGE_LESSON_CONSTRAINTS);
        }
        LocalDate date = ParserUtil.parseDate(argMultimap.getValue(PREFIX_DATE).get());
        AttendanceStatus status;
        try {
            status = AttendanceStatus.valueOf(
                    argMultimap.getValue(PREFIX_STATUS).get().trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new ParseException(Attendance.MESSAGE_STATUS_CONSTRAINTS, exception);
        }
        return new AttendanceCommand(index, new Attendance(lessonId, date, status));
    }
}
