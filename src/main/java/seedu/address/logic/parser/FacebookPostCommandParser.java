package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.FacebookPostCommand;
import seedu.address.logic.parser.exceptions.ParseException;

//@@author alexfoodw
/**
 * Parses input arguments and creates a new FacebookPostCommand object
 */
public class FacebookPostCommandParser implements Parser<FacebookPostCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FacebookAddCommand
     * and returns an FacebookAddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public FacebookPostCommand parse(String userInput) throws ParseException {
        String trimmedArgs = userInput.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FacebookPostCommand.MESSAGE_USAGE));
        }

        return new FacebookPostCommand(trimmedArgs);
    }
}
