package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COST;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_YEAR;

import seedu.address.logic.commands.AddLessonCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.lesson.Lesson;

/** Parses arguments for {@link AddLessonCommand}. */
public class AddLessonCommandParser implements Parser<AddLessonCommand> {
    @Override
    public AddLessonCommand parse(String args) throws ParseException {
        ArgumentMultimap map = ArgumentTokenizer.tokenize(args, PREFIX_YEAR, PREFIX_SUBJECT, PREFIX_DAY,
                PREFIX_START_TIME, PREFIX_END_TIME, PREFIX_COST);
        if (!map.getPreamble().isEmpty() || !arePrefixesPresent(map, PREFIX_YEAR, PREFIX_SUBJECT, PREFIX_DAY,
                PREFIX_START_TIME, PREFIX_END_TIME, PREFIX_COST)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddLessonCommand.MESSAGE_USAGE));
        }
        map.verifyNoDuplicatePrefixesFor(PREFIX_YEAR, PREFIX_SUBJECT, PREFIX_DAY, PREFIX_START_TIME,
                PREFIX_END_TIME, PREFIX_COST);
        Lesson lesson = new Lesson(
                ParserUtil.parseYear(map.getValue(PREFIX_YEAR).get()),
                ParserUtil.parseSubject(map.getValue(PREFIX_SUBJECT).get()),
                ParserUtil.parseDay(map.getValue(PREFIX_DAY).get()),
                ParserUtil.parseTime(map.getValue(PREFIX_START_TIME).get()),
                ParserUtil.parseTime(map.getValue(PREFIX_END_TIME).get()),
                ParserUtil.parseCost(map.getValue(PREFIX_COST).get()));
        return new AddLessonCommand(lesson);
    }

    private static boolean arePrefixesPresent(ArgumentMultimap map, Prefix... prefixes) {
        return java.util.stream.Stream.of(prefixes).allMatch(prefix -> map.getValue(prefix).isPresent());
    }
}
