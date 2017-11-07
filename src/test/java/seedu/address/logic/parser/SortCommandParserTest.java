package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.Test;

import seedu.address.logic.commands.SortByDefaultCommand;
import seedu.address.logic.commands.SortByNameCommand;
import seedu.address.logic.commands.SortByRecentCommand;
import seedu.address.logic.commands.SortCommand;

//@@author marvinchin
/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the SortCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the SortCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class SortCommandParserTest {

    private SortCommandParser parser = new SortCommandParser();

    @Test
    public void parse_noOptionsAndHasParams_throwsParseException() {
        String input = "param";
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);
        assertParseFailure(parser, input, expectedMessage);
    }

    @Test
    public void parse_validOptionsAndHasParams_throwsParseException() {
        String input = "-" + SortByNameCommand.COMMAND_OPTION + " param";
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);
        assertParseFailure(parser, input, expectedMessage);
    }

    @Test
    public void parse_invalidOption_throwsParseException() {
        String input = "-someinvalidoption123";
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);
        assertParseFailure(parser, input, expectedMessage);
    }

    @Test
    public void parse_multipleOptions_throwsParseException() {
        String input = "-" + SortByNameCommand.COMMAND_OPTION + " "
                + "-" + SortByRecentCommand.COMMAND_OPTION;
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);
        assertParseFailure(parser, input, expectedMessage);
    }

    @Test
    public void parse_noOptionsAndNoParams_returnsFindByDefaultCommand() {
        String input = "";
        SortCommand expectedSortCommand = new SortByDefaultCommand();
        assertParseSuccess(parser, input, expectedSortCommand);
    }

    @Test
    public void parse_nameOptionAndNoParams_returnsFindByDefaultCommand() {
        String input = "-" + SortByNameCommand.COMMAND_OPTION;
        SortCommand expectedSortCommand = new SortByNameCommand();
        assertParseSuccess(parser, input, expectedSortCommand);
    }

    @Test
    public void parse_recentOptionAndNoParams_returnsFindByRecentCommand() {
        String input = "-" + SortByRecentCommand.COMMAND_OPTION;
        SortCommand expectedSortCommand = new SortByRecentCommand();
        assertParseSuccess(parser, input, expectedSortCommand);
    }
}
