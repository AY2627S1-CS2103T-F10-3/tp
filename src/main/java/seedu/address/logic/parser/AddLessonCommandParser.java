package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COST;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_YEAR;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.commands.AddLessonCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.lesson.Day;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Time;

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

        List<String> invalidFields = new ArrayList<>();
        List<ParseException> parseExceptions = new ArrayList<>();
        Year year = parseField(map, PREFIX_YEAR, "year", ParserUtil::parseYear,
                invalidFields, parseExceptions);
        Subject subject = parseField(map, PREFIX_SUBJECT, "subject", ParserUtil::parseSubject,
                invalidFields, parseExceptions);
        Day day = parseField(map, PREFIX_DAY, "day", ParserUtil::parseDay,
                invalidFields, parseExceptions);
        Time startTime = parseField(map, PREFIX_START_TIME, "start time", ParserUtil::parseTime,
                invalidFields, parseExceptions);
        Time endTime = parseField(map, PREFIX_END_TIME, "end time", ParserUtil::parseTime,
                invalidFields, parseExceptions);
        Integer cost = parseField(map, PREFIX_COST, "cost", ParserUtil::parseCost,
                invalidFields, parseExceptions);

        if (startTime != null && endTime != null && !Lesson.isValidTimeRange(startTime, endTime)) {
            invalidFields.add("start time and end time");
            parseExceptions.add(new ParseException(Lesson.TIME_ORDER_CONSTRAINT));
        }
        if (!parseExceptions.isEmpty()) {
            if (parseExceptions.size() == 1 && invalidFields.size() == 1) {
                throw parseExceptions.get(0);
            }
            throw new ParseException("Invalid fields: " + String.join(", ", invalidFields) + ".");
        }

        Lesson lesson = new Lesson(
                year,
                subject,
                day,
                startTime,
                endTime,
                cost);
        return new AddLessonCommand(lesson);
    }

    private static <T> T parseField(ArgumentMultimap map, Prefix prefix, String fieldName,
                                    FieldParser<T> parser, List<String> invalidFields,
                                    List<ParseException> parseExceptions) {
        try {
            return parser.parse(map.getValue(prefix).get());
        } catch (ParseException exception) {
            invalidFields.add(fieldName);
            parseExceptions.add(exception);
            return null;
        }
    }

    @FunctionalInterface
    private interface FieldParser<T> {
        T parse(String value) throws ParseException;
    }

    private static boolean arePrefixesPresent(ArgumentMultimap map, Prefix... prefixes) {
        return java.util.stream.Stream.of(prefixes).allMatch(prefix -> map.getValue(prefix).isPresent());
    }
}
