package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.ParserUtil.parseWhitespaceSeparatedStrings;

import java.util.List;

import seedu.address.logic.commands.FindByNameCommand;
import seedu.address.logic.commands.FindByTagsCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.TagsContainKeywordsPredicate;

//@@author marvinchin
/**
 * Parses input arguments and creates a new {@code FindCommand}.
 */
public class FindCommandParser implements Parser<FindCommand> {
    public static final String INVALID_FIND_COMMAND_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
    /**
     * Checks that the input {@code String} is not empty.
     * @throws ParseException if it is empty.
     */
    private void checkArgsNotEmpty(String args) throws ParseException {
        if (args == null || args.isEmpty()) {
            throw new ParseFindCommandException();
        }
    }

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns an FindCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format.
     */
    public FindCommand parse(String args) throws ParseException {
        // check that the raw args are not empty before processing
        checkArgsNotEmpty(args);
        OptionBearingArgument opArgs = new OptionBearingArgument(args);
        String filteredArgs = opArgs.getFilteredArgs();
        // check that the filtered args are not empty
        checkArgsNotEmpty(filteredArgs);

        if (opArgs.getOptions().size() > 1) {
            // args should have at most 1 option
            throw new ParseFindCommandException();
        }

        if (opArgs.getOptions().contains(FindByTagsCommand.COMMAND_OPTION)) {
            List<String> tagKeywords = parseWhitespaceSeparatedStrings(filteredArgs);
            TagsContainKeywordsPredicate predicate = new TagsContainKeywordsPredicate(tagKeywords);
            return new FindByTagsCommand(predicate);
        } else if (opArgs.getOptions().isEmpty()) {
            checkArgsNotEmpty(opArgs.getFilteredArgs());
            List<String> nameKeywords = parseWhitespaceSeparatedStrings(filteredArgs);
            NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(nameKeywords);
            return new FindByNameCommand(predicate);
        } else {
            // option is not a valid option
            throw new ParseFindCommandException();
        }
    }

    /**
     * Represents a {@code ParseException} encountered when parsing arguments for a {@code FindCommand}.
     */
    private class ParseFindCommandException extends ParseException {
        public ParseFindCommandException() {
            super(INVALID_FIND_COMMAND_FORMAT_MESSAGE);
        }
    }
}
