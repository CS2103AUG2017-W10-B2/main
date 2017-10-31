package seedu.address.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.StorageUtil.getNullStorage;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.TagsContainKeywordsPredicate;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code DeleteCommand}.
 */
public class DeleteByTagCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_unfilteredList_success() throws Exception {
        List<String> tagsToDelete = Arrays.asList("friends");
        TagsContainKeywordsPredicate predicate = new TagsContainKeywordsPredicate(tagsToDelete);
        List<ReadOnlyPerson> personsToDelete = model.getFilteredPersonList().stream()
                .filter(predicate).collect(Collectors.toList());
        StringBuilder deletedPersons = new StringBuilder();
        for (ReadOnlyPerson person : personsToDelete) {
            deletedPersons.append("\n" + person.toString());
        }

        DeleteCommand deleteCommand = prepareCommand(tagsToDelete);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, deletedPersons);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        for (ReadOnlyPerson person : personsToDelete) {
            expectedModel.deletePerson(person);
        }

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() throws Exception {
        NameContainsKeywordsPredicate filterPredicate = new NameContainsKeywordsPredicate(Arrays.asList("Alice"));
        model.updateFilteredPersonList(filterPredicate);
        List<ReadOnlyPerson> filteredPersons = model.getFilteredPersonList();

        List<String> tagsToDelete = Arrays.asList("friends");
        TagsContainKeywordsPredicate deletePredicate = new TagsContainKeywordsPredicate(tagsToDelete);

        List<ReadOnlyPerson> personsToDelete = filteredPersons.stream()
                .filter(deletePredicate).collect(Collectors.toList());
        StringBuilder deletedPersons = new StringBuilder();
        for (ReadOnlyPerson person : personsToDelete) {
            deletedPersons.append("\n" + person.toString());
        }

        DeleteCommand deleteCommand = prepareCommand(tagsToDelete);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, deletedPersons);
        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        for (ReadOnlyPerson person : personsToDelete) {
            expectedModel.deletePerson(person);
        }
        expectedModel.updateFilteredPersonList(filterPredicate);
        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void equals() {
        Set<String> firstDeleteTagSet = new HashSet<>(Arrays.asList("friends"));
        Set<String> secondDeleteTagSet = new HashSet<>(Arrays.asList("family"));
        DeleteCommand deleteFirstCommand = new DeleteByTagCommand(firstDeleteTagSet);
        DeleteCommand deleteSecondCommand = new DeleteByTagCommand(secondDeleteTagSet);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteByTagCommand(firstDeleteTagSet);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    /**
     * Returns a {@code DeleteCommand} with the parameter {@code index}.
     */
    private DeleteCommand prepareCommand(List<String> tags) {
        HashSet<String> tagSet = new HashSet<>(tags);
        DeleteCommand deleteCommand = new DeleteByTagCommand(tagSet);
        deleteCommand.setData(model, getNullStorage(), new CommandHistory(), new UndoRedoStack());
        return deleteCommand;
    }
}
