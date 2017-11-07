package seedu.address.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showFirstPersonOnly;
import static seedu.address.testutil.StorageUtil.getNullStorage;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_FOURTH_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.ReadOnlyPerson;

//@@author keithsoc
/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code FavoriteCommand}.
 */
public class FavoriteCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() throws Exception {
        ReadOnlyPerson personToFavorite = model.getFilteredPersonList().get(INDEX_THIRD_PERSON.getZeroBased());
        FavoriteCommand favoriteCommand = prepareCommand(Arrays.asList(INDEX_THIRD_PERSON));

        String expectedMessage = FavoriteCommand.MESSAGE_FAVORITE_PERSON_SUCCESS
                + "\n\t★ " + personToFavorite.getName().toString();

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.toggleFavoritePerson(personToFavorite, FavoriteCommand.COMMAND_WORD);

        assertCommandSuccess(favoriteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validMultiIndexesUnfilteredList_success() throws Exception {
        ReadOnlyPerson personAlice = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        ReadOnlyPerson personDaniel = model.getFilteredPersonList().get(INDEX_FOURTH_PERSON.getZeroBased());

        Set<ReadOnlyPerson> targetPersonList = new LinkedHashSet<>();
        targetPersonList.add(personAlice);
        targetPersonList.add(personDaniel);

        FavoriteCommand favoriteCommand = prepareCommand(Arrays.asList(INDEX_FIRST_PERSON, INDEX_FOURTH_PERSON));

        // In TypicalPersons, Alice is already a favorite contact while Daniel is not
        String expectedMessage = FavoriteCommand.MESSAGE_FAVORITE_PERSON_SUCCESS
                + "\n\t★ " + personDaniel.getName().toString()
                + "\n" + FavoriteCommand.MESSAGE_FAVORITE_PERSON_FAILURE
                + "\n\t- " + personAlice.getName().toString();

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        for (ReadOnlyPerson personToFavorite : targetPersonList) {
            expectedModel.toggleFavoritePerson(personToFavorite, FavoriteCommand.COMMAND_WORD);
        }

        assertCommandSuccess(favoriteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() throws Exception {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        FavoriteCommand favoriteCommand = prepareCommand(Arrays.asList(outOfBoundIndex));

        assertCommandFailure(favoriteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() throws Exception {
        showFirstPersonOnly(model);

        ReadOnlyPerson personToFavorite = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        FavoriteCommand favoriteCommand = prepareCommand(Arrays.asList(INDEX_FIRST_PERSON));

        // In TypicalPersons, first person (Alice) is already a favorite.
        // Assert command success as this is not a command failure,
        // it's because we disallow favoriting of an already favorited person.
        String expectedMessage = FavoriteCommand.MESSAGE_FAVORITE_PERSON_FAILURE
                + "\n\t- " + personToFavorite.getName().toString();

        assertCommandSuccess(favoriteCommand, model, expectedMessage, model);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showFirstPersonOnly(model);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        FavoriteCommand favoriteCommand = prepareCommand(Arrays.asList(outOfBoundIndex));

        assertCommandFailure(favoriteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        FavoriteCommand favoriteFirstCommand = new FavoriteCommand(Arrays.asList(INDEX_FIRST_PERSON));
        FavoriteCommand favoriteSecondCommand = new FavoriteCommand(Arrays.asList(INDEX_SECOND_PERSON));

        // same object -> returns true
        assertTrue(favoriteFirstCommand.equals(favoriteFirstCommand));

        // same values -> returns true
        FavoriteCommand favoriteFirstCommandCopy = new FavoriteCommand(Arrays.asList(INDEX_FIRST_PERSON));
        assertTrue(favoriteFirstCommand.equals(favoriteFirstCommandCopy));

        // different types -> returns false
        assertFalse(favoriteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(favoriteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(favoriteFirstCommand.equals(favoriteSecondCommand));
    }

    /**
     * Returns a {@code FavoriteCommand} with the parameter {@code index}.
     */
    private FavoriteCommand prepareCommand(List<Index> indexList) {
        FavoriteCommand favoriteCommand = new FavoriteCommand(indexList);
        favoriteCommand.setData(model, getNullStorage(), new CommandHistory(), new UndoRedoStack());
        return favoriteCommand;
    }
}
//@@author
