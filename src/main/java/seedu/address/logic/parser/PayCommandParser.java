package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AMOUNT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MONTH;

import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.PayCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.payment.Amount;
import seedu.address.model.payment.Payment;

/**
 * Parses input arguments and creates a new PayCommand object
 */
public class PayCommandParser implements Parser<PayCommand> {

    private final Clock clock;

    /**
     * Creates a parser that uses the system clock for the default payment date.
     */
    public PayCommandParser() {
        this(Clock.systemDefaultZone());
    }

    /**
     * Creates a parser that uses {@code clock} for the default payment date.
     */
    public PayCommandParser(Clock clock) {
        this.clock = requireNonNull(clock);
    }

    /**
     * Parses the given {@code String} of arguments in the context of the PayCommand
     * and returns a PayCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public PayCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_AMOUNT, PREFIX_MONTH, PREFIX_DATE);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, PayCommand.MESSAGE_USAGE), pe);
        }

        if (argMultimap.getValue(PREFIX_AMOUNT).isEmpty() || argMultimap.getValue(PREFIX_MONTH).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, PayCommand.MESSAGE_USAGE));
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_AMOUNT, PREFIX_MONTH, PREFIX_DATE);

        Amount amount = ParserUtil.parseAmount(argMultimap.getValue(PREFIX_AMOUNT).get());
        YearMonth month = ParserUtil.parseMonth(argMultimap.getValue(PREFIX_MONTH).get());
        LocalDate date = argMultimap.getValue(PREFIX_DATE).isPresent()
                ? ParserUtil.parseDate(argMultimap.getValue(PREFIX_DATE).get())
                : LocalDate.now(clock);

        return new PayCommand(index, new Payment(amount, month, date));
    }
}
