package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.FacebookAddCommand;
import seedu.address.logic.parser.exceptions.ParseException;

//@@author alexfoodw
/**
 * Parses the given {@code String} of arguments in the context of the FacebookAddCommand
 * and returns an FacebookAddCommand object for execution.
 * @throws ParseException if the user input does not conform the expected format
 */
public class FacebookAddCommandParser implements Parser<FacebookAddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FacebookAddCommand
     * and returns an FacebookAddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FacebookAddCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FacebookAddCommand.MESSAGE_USAGE));
        }

        return new FacebookAddCommand(trimmedArgs);
    }

}
