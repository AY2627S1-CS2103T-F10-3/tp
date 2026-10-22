package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_RELATIONSHIP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LESSON;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PAID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_UNPAID;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.payment.PaidInMonthPredicate;
import seedu.address.model.payment.UnpaidInMonthPredicate;
import seedu.address.model.person.FieldsContainKeywordsPredicate;
import seedu.address.model.person.FieldsContainKeywordsPredicate.Field;
import seedu.address.model.person.NameContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        Map<Prefix, Field> fields = Map.ofEntries(
                Map.entry(PREFIX_NAME, Field.NAME),
                Map.entry(PREFIX_PHONE, Field.PHONE),
                Map.entry(PREFIX_GUARDIAN_NAME, Field.GUARDIAN_NAME),
                Map.entry(PREFIX_GUARDIAN_PHONE, Field.GUARDIAN_PHONE),
                Map.entry(PREFIX_EMAIL, Field.EMAIL),
                Map.entry(PREFIX_ADDRESS, Field.ADDRESS),
                Map.entry(PREFIX_TAG, Field.TAG),
                Map.entry(PREFIX_GUARDIAN_EMAIL, Field.GUARDIAN_EMAIL),
                Map.entry(PREFIX_GUARDIAN_ADDRESS, Field.GUARDIAN_ADDRESS),
                Map.entry(PREFIX_GUARDIAN_RELATIONSHIP, Field.GUARDIAN_RELATIONSHIP),
                Map.entry(PREFIX_LESSON, Field.LESSON));
        Matcher prefixMatcher = Pattern.compile("(?:^|\\s)([^\\s/]+/)").matcher(trimmedArgs);
        boolean hasPrefixes = false;
        while (prefixMatcher.find()) {
            hasPrefixes = true;
            Prefix prefix = new Prefix(prefixMatcher.group(1));
            if (!fields.containsKey(prefix) && !PREFIX_UNPAID.equals(prefix) && !PREFIX_PAID.equals(prefix)) {
                throw invalidFormat();
            }
        }
        if (hasPrefixes) {
            Prefix[] supportedPrefixes = Stream.concat(fields.keySet().stream(), Stream.of(PREFIX_UNPAID, PREFIX_PAID))
                    .toArray(Prefix[]::new);
            ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" " + trimmedArgs.replaceAll("\\s+", " "),
                    supportedPrefixes);
            if (!arguments.getPreamble().isEmpty()) {
                throw invalidFormat();
            }
            arguments.verifyNoDuplicatePrefixesFor(supportedPrefixes);
            if (arguments.getValue(PREFIX_PAID).isPresent() && arguments.getValue(PREFIX_UNPAID).isPresent()) {
                throw invalidFormat();
            }
            Map<Field, List<String>> keywords = new EnumMap<>(Field.class);
            for (Map.Entry<Prefix, Field> entry : fields.entrySet()) {
                if (arguments.getValue(entry.getKey()).isPresent()) {
                    String value = arguments.getValue(entry.getKey()).orElseThrow().trim();
                    if (value.isEmpty()) {
                        throw invalidFormat();
                    }
                    keywords.put(entry.getValue(), List.of(value.split("\\s+")));
                }
            }
            FieldsContainKeywordsPredicate fieldPredicate = new FieldsContainKeywordsPredicate(keywords);
            if (arguments.getValue(PREFIX_PAID).isPresent()) {
                PaidInMonthPredicate paidPredicate = new PaidInMonthPredicate(
                        ParserUtil.parseMonth(arguments.getValue(PREFIX_PAID).orElseThrow()));
                return new FindCommand(keywords.isEmpty() ? paidPredicate : fieldPredicate.and(paidPredicate));
            }
            if (arguments.getValue(PREFIX_UNPAID).isPresent()) {
                UnpaidInMonthPredicate unpaidPredicate = new UnpaidInMonthPredicate(
                        ParserUtil.parseMonth(arguments.getValue(PREFIX_UNPAID).orElseThrow()));
                return new FindCommand(keywords.isEmpty() ? unpaidPredicate : fieldPredicate.and(unpaidPredicate));
            }
            return new FindCommand(fieldPredicate);
        }
        String[] nameKeywords = trimmedArgs.split("\\s+");

        return new FindCommand(new NameContainsKeywordsPredicate(List.of(nameKeywords)));
    }

    private ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }
}
