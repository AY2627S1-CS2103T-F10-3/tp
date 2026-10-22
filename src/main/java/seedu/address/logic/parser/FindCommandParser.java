package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_RELATIONSHIP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
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

        Map<Prefix, Field> fields = Map.of(PREFIX_NAME, Field.NAME, PREFIX_PHONE, Field.PHONE,
                PREFIX_GUARDIAN_NAME, Field.GUARDIAN_NAME, PREFIX_GUARDIAN_PHONE, Field.GUARDIAN_PHONE,
                PREFIX_EMAIL, Field.EMAIL, PREFIX_ADDRESS, Field.ADDRESS, PREFIX_TAG, Field.TAG,
                PREFIX_GUARDIAN_EMAIL, Field.GUARDIAN_EMAIL, PREFIX_GUARDIAN_ADDRESS, Field.GUARDIAN_ADDRESS,
                PREFIX_GUARDIAN_RELATIONSHIP, Field.GUARDIAN_RELATIONSHIP);
        Matcher prefixMatcher = Pattern.compile("(?:^|\\s)([^\\s/]+/)").matcher(trimmedArgs);
        boolean hasPrefixes = false;
        while (prefixMatcher.find()) {
            hasPrefixes = true;
            if (!fields.containsKey(new Prefix(prefixMatcher.group(1)))) {
                throw invalidFormat();
            }
        }
        if (hasPrefixes) {
            Prefix[] supportedPrefixes = fields.keySet().toArray(Prefix[]::new);
            ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" " + trimmedArgs.replaceAll("\\s+", " "),
                    supportedPrefixes);
            if (!arguments.getPreamble().isEmpty()) {
                throw invalidFormat();
            }
            arguments.verifyNoDuplicatePrefixesFor(supportedPrefixes);
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
            return new FindCommand(new FieldsContainKeywordsPredicate(keywords));
        }
        String[] nameKeywords = trimmedArgs.split("\\s+");

        return new FindCommand(new NameContainsKeywordsPredicate(List.of(nameKeywords)));
    }

    private ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }
}
