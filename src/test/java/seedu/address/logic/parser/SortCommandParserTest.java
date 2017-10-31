package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.Test;

import seedu.address.logic.commands.SortByDefaultCommand;
import seedu.address.logic.commands.SortByNameCommand;
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
    public void parse_noOptions_returnsFindByDefaultCommand() {
        SortCommand expectedSortCommand = new SortByDefaultCommand();
        assertParseSuccess(parser, "", expectedSortCommand);
    }

    @Test
    public void parse_nameOption_returnsFindByDefaultCommand() {
        SortCommand expectedSortCommand = new SortByNameCommand();
        assertParseSuccess(parser, "-" + SortByNameCommand.COMMAND_OPTION, expectedSortCommand);
    }
}
