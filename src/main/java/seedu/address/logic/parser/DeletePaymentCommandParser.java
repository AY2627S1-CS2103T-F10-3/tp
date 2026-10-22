package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PAYMENT_INDEX;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeletePaymentCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeletePaymentCommand object
 */
public class DeletePaymentCommandParser implements Parser<DeletePaymentCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeletePaymentCommand
     * and returns a DeletePaymentCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public DeletePaymentCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_PAYMENT_INDEX);

        if (argMultimap.getValue(PREFIX_PAYMENT_INDEX).isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeletePaymentCommand.MESSAGE_USAGE));
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_PAYMENT_INDEX);

        try {
            Index index = ParserUtil.parseIndex(argMultimap.getPreamble());
            Index paymentIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_PAYMENT_INDEX).get());
            return new DeletePaymentCommand(index, paymentIndex);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeletePaymentCommand.MESSAGE_USAGE), pe);
        }
    }
}
