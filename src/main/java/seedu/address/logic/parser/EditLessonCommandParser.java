package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COST;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_YEAR;

import seedu.address.logic.commands.EditLessonCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class EditLessonCommandParser implements Parser<EditLessonCommand> {
    @Override
    public EditLessonCommand parse(String args) throws ParseException {
        ArgumentMultimap map = ArgumentTokenizer.tokenize(args, PREFIX_YEAR, PREFIX_SUBJECT, PREFIX_DAY,
                PREFIX_START_TIME, PREFIX_END_TIME, PREFIX_COST);
        String currentId = map.getPreamble().trim();
        if (currentId.isEmpty() || !hasAnyField(map)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditLessonCommand.MESSAGE_USAGE));
        }
        map.verifyNoDuplicatePrefixesFor(PREFIX_YEAR, PREFIX_SUBJECT, PREFIX_DAY, PREFIX_START_TIME,
                PREFIX_END_TIME, PREFIX_COST);
        return new EditLessonCommand(currentId,
                map.getValue(PREFIX_YEAR).isPresent() ? ParserUtil.parseYear(map.getValue(PREFIX_YEAR).get()) : null,
                map.getValue(PREFIX_SUBJECT).isPresent()
                        ? ParserUtil.parseSubject(map.getValue(PREFIX_SUBJECT).get()) : null,
                map.getValue(PREFIX_DAY).isPresent() ? ParserUtil.parseDay(map.getValue(PREFIX_DAY).get()) : null,
                map.getValue(PREFIX_START_TIME).isPresent()
                        ? ParserUtil.parseTime(map.getValue(PREFIX_START_TIME).get()) : null,
                map.getValue(PREFIX_END_TIME).isPresent()
                        ? ParserUtil.parseTime(map.getValue(PREFIX_END_TIME).get()) : null,
                map.getValue(PREFIX_COST).isPresent() ? ParserUtil.parseCost(map.getValue(PREFIX_COST).get()) : null);
    }

    private static boolean hasAnyField(ArgumentMultimap map) {
        return java.util.stream.Stream.of(PREFIX_YEAR, PREFIX_SUBJECT, PREFIX_DAY, PREFIX_START_TIME,
                PREFIX_END_TIME, PREFIX_COST).anyMatch(prefix -> map.getValue(prefix).isPresent());
    }
}
