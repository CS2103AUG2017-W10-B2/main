package seedu.address.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.StorageUtil.getNullStorage;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;

import org.junit.Test;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonDefaultComparator;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.TagsContainKeywordsPredicate;
import seedu.address.testutil.PersonBuilder;

//@@author marvinchin
/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code DeleteCommand}.
 */
public class SortByDefaultCommandCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_unfilteredList_success() throws Exception {
        SortByDefaultCommand sortCommand = prepareCommand();

        String expectedMessage = SortByDefaultCommand.MESSAGE_SORT_SUCCESS;
        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.sortPersons(new PersonDefaultComparator());

        assertCommandSuccess(sortCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() throws Exception {
        showFirstThreePersonsOnly(model);
        SortByDefaultCommand sortCommand = prepareCommand();

        String expectedMessage = SortByDefaultCommand.MESSAGE_SORT_SUCCESS;
        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showFirstThreePersonsOnly(expectedModel);
        expectedModel.sortPersons(new PersonDefaultComparator());

        assertCommandSuccess(sortCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void equals() {
        SortByDefaultCommand sortByDefaultCommandOne = new SortByDefaultCommand();
        SortByDefaultCommand sortByDefaultCommandTwo = new SortByDefaultCommand();

        // same object -> returns true
        assertTrue(sortByDefaultCommandOne.equals(sortByDefaultCommandOne));

        // same type -> returns true
        assertTrue(sortByDefaultCommandOne.equals(sortByDefaultCommandTwo));

        // different types -> returns false
        assertFalse(sortByDefaultCommandOne.equals(1));

        // null -> returns false
        assertFalse(sortByDefaultCommandOne.equals(null));
    }

    /**
     * Returns a {@code DeleteCommand} with the parameter {@code index}.
     */
    private SortByDefaultCommand prepareCommand() {
        SortByDefaultCommand sortCommand = new SortByDefaultCommand();
        sortCommand.setData(model, getNullStorage(), new CommandHistory(), new UndoRedoStack());
        return sortCommand;
    }

    /**
     * Updates {@code model}'s filtered list to show only the three persons in the {@code model}'s address book.
     */
    private void showFirstThreePersonsOnly(Model model) throws Exception {
        String testTag = "test";

        for (int i = 0; i < 3; i++) {
            ReadOnlyPerson person = model.getAddressBook().getPersonList().get(i);
            Person personWithTag = new PersonBuilder(person).withTags(testTag).build();
            model.updatePerson(person, personWithTag);
        }

        model.updateFilteredPersonList(new TagsContainKeywordsPredicate(Arrays.asList(testTag)));
        assert model.getFilteredPersonList().size() == 3;
    }
}
