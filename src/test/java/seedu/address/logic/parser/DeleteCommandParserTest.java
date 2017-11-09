package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import seedu.address.logic.commands.DeleteByIndexCommand;
import seedu.address.logic.commands.DeleteByTagCommand;
import seedu.address.logic.commands.DeleteCommand;

/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the DeleteCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the DeleteCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class DeleteCommandParserTest {

    private DeleteCommandParser parser = new DeleteCommandParser();

    //@@author marvinchin
    @Test
    public void parse_moreThanOneOption_throwsParseException() {
        String input = "-tag -hello";
        assertParseFailure(parser, input, DeleteCommandParser.INVALID_DELETE_COMMAND_FORMAT_MESSAGE);
    }

    @Test
    public void parse_invalidOption_throwsParseException() {
        String input = "-hello";
        assertParseFailure(parser, input, DeleteCommandParser.INVALID_DELETE_COMMAND_FORMAT_MESSAGE);
    }

    @Test
    public void parse_tagOptionNoArgs_throwsParseException() {
        String input = "-tag    ";
        assertParseFailure(parser, input, DeleteCommandParser.INVALID_DELETE_COMMAND_FORMAT_MESSAGE);
    }

    @Test
    public void parse_validTagArgs_returnsDeleteCommand() {
        HashSet<String> keys = new HashSet<>(Arrays.asList("friends", "colleagues"));
        DeleteCommand expectedDeleteCommand = new DeleteByTagCommand(keys);
        String input = "-tag colleagues friends";
        String inputWithWhitespace = "-tag   \t friends \t\t\n colleagues";
        assertParseSuccess(parser, input, expectedDeleteCommand);
        assertParseSuccess(parser, inputWithWhitespace, expectedDeleteCommand);
    }
    //@@author

    @Test
    public void parse_noOptionValidArgs_returnsDeleteCommand() {
        String input = "1";
        assertParseSuccess(parser, input, new DeleteByIndexCommand(Arrays.asList(INDEX_FIRST_PERSON)));
    }

    @Test
    public void parse_noOptionInvalidArgs_throwsParseException() {
        String input = "a";
        assertParseFailure(parser, input, DeleteCommandParser.INVALID_DELETE_COMMAND_FORMAT_MESSAGE);
    }
}
