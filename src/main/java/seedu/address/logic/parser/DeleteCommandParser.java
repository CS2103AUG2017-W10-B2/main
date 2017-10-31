package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.ParserUtil.parseWhitespaceSeparatedStrings;

import java.util.HashSet;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.DeleteByIndexCommand;
import seedu.address.logic.commands.DeleteByTagCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeleteCommand object
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {
    //@@author marvinchin
    /**
     * Utility function to check that the input arguments is not empty.
     * Throws a parse exception if it is empty.
     */
    private void checkArgsNotEmpty(String args) throws ParseException {
        if (args == null || args.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }
    }
    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns an DeleteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteCommand parse(String args) throws ParseException {
        // check that the raw args are not empty before processing
        checkArgsNotEmpty(args);
        OptionBearingArgument opArgs = new OptionBearingArgument(args);
        String filteredArgs = opArgs.getFilteredArgs();
        // check that the filtered args are not empty
        checkArgsNotEmpty(filteredArgs);

        if (opArgs.getOptions().contains(DeleteByTagCommand.COMMAND_OPTION)) {
            List<String> tags = parseWhitespaceSeparatedStrings(filteredArgs);
            HashSet<String> tagSet = new HashSet<>(tags);
            return new DeleteByTagCommand(tagSet);
        } else {
            try {
                List<Index> indexes = ParserUtil.parseMultipleIndexes(filteredArgs);
                return new DeleteByIndexCommand(indexes);
            } catch (IllegalValueException ive) {
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
            }
        }
    }
    //@@author
}
