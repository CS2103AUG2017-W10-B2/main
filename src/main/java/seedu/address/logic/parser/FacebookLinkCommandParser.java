package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_URL;

import seedu.address.logic.commands.FacebookLinkCommand;
import seedu.address.logic.parser.exceptions.ParseException;

//@@author alexfoodw
/**
 * Parses input arguments and creates a new FacebookPostCommand object
 */
public class FacebookLinkCommandParser implements Parser<FacebookLinkCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FacebookLinkCommand
     * and returns an FacebookLinkCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public FacebookLinkCommand parse(String userInput) throws ParseException {
        String trimmedArgs = userInput.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FacebookLinkCommand.MESSAGE_USAGE));
        }

        if (!ParserUtil.isValidUrl(trimmedArgs)) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_URL, "Example: " + FacebookLinkCommand.EXAMPLE_LINK));
        }

        return new FacebookLinkCommand(trimmedArgs);
    }
}
