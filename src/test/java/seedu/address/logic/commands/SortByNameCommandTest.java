package seedu.address.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.SortCommandTestUtil.assertFilteredSortCommandSuccess;
import static seedu.address.logic.commands.SortCommandTestUtil.assertUnfilteredSortCommandSuccess;
import static seedu.address.testutil.StorageUtil.getNullStorage;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.Test;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.PersonNameComparator;

//@@author marvinchin
/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code SortByNameCommand}.
 */
public class SortByNameCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_unfilteredList_success() throws Exception {
        SortByNameCommand sortCommand = prepareCommand();
        String expectedMessage = SortByNameCommand.MESSAGE_SORT_SUCCESS;

        assertUnfilteredSortCommandSuccess(sortCommand, model, new PersonNameComparator(), expectedMessage);
    }

    @Test
    public void execute_filteredList_success() throws Exception {
        SortByNameCommand sortCommand = prepareCommand();
        String expectedMessage = SortByNameCommand.MESSAGE_SORT_SUCCESS;

        assertFilteredSortCommandSuccess(sortCommand, model, new PersonNameComparator(), expectedMessage);
    }

    @Test
    public void equals() {
        SortByNameCommand sortByNameCommandOne = new SortByNameCommand();
        SortByNameCommand sortByNameCommandTwo = new SortByNameCommand();

        // same object -> returns true
        assertTrue(sortByNameCommandOne.equals(sortByNameCommandOne));

        // same type -> returns true
        assertTrue(sortByNameCommandOne.equals(sortByNameCommandTwo));

        // different types -> returns false
        assertFalse(sortByNameCommandOne.equals(1));

        // null -> returns false
        assertFalse(sortByNameCommandOne.equals(null));
    }

    /**
     * Returns a {@code DeleteCommand} with the parameter {@code index}.
     */
    private SortByNameCommand prepareCommand() {
        SortByNameCommand sortCommand = new SortByNameCommand();
        sortCommand.setData(model, getNullStorage(), new CommandHistory(), new UndoRedoStack());
        return sortCommand;
    }
}
