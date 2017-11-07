package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Collections;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.UnFavoriteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

//@@author keithsoc
/**
 * Parses input arguments and creates a new UnFavoriteCommand object
 */
public class UnFavoriteCommandParser implements Parser<UnFavoriteCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the UnFavoriteCommand
     * and returns an UnFavoriteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public UnFavoriteCommand parse(String args) throws ParseException {
        try {
            List<Index> indexList = ParserUtil.parseMultipleIndexes(args);
            // Sorts indexes in descending order
            Collections.sort(indexList, Collections.reverseOrder());
            return new UnFavoriteCommand(indexList);
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnFavoriteCommand.MESSAGE_USAGE));
        }
    }
}
//@@author
