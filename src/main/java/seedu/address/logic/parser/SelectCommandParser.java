package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.SelectCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new SelectCommand object
 */
public class SelectCommandParser implements Parser<SelectCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the SelectCommand
     * and returns an SelectCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public SelectCommand parse(String args) throws ParseException {
        try {
            String trimmedArgs = args.trim();
            String[] tokens = trimmedArgs.split(" ");
            String indexStr = tokens[0];
            String socialType = null;
            if (tokens.length > 1) {
                // if there is more than one argument
                socialType = tokens[1];
            }
            Index index = ParserUtil.parseIndex(indexStr);
            // Index index = ParserUtil.parseIndex(args);
            // return new SelectCommand(index);
            return new SelectCommand(index, socialType);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SelectCommand.MESSAGE_USAGE));
        }
    }
}
