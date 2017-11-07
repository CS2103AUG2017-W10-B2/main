package systemtests;

import static org.junit.Assert.assertTrue;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.FavoriteCommand.MESSAGE_FAVORITE_PERSON_SUCCESS;
import static seedu.address.testutil.TestUtil.getLastIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIFTH_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_FOURTH_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;
import static seedu.address.testutil.TypicalPersons.KEYWORD_MATCHING_KUNZ;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.FavoriteCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.model.Model;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;

//@@author keithsoc
public class FavoriteCommandSystemTest extends AddressBookSystemTest {

    private static final String MESSAGE_INVALID_FAVORITE_COMMAND_FORMAT =
            String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FavoriteCommand.MESSAGE_USAGE);

    @Test
    public void favorite() {
        /* -------------- Performing favorite operation while an unfiltered list is being shown ----------------- */

        /*
         * Case: favorites the 3rd, 4th & 5th person in the list,
         * command with leading spaces and trailing spaces -> favorited
         */
        Model expectedModel = getModel();
        String command = "     " + FavoriteCommand.COMMAND_WORD + "      " + INDEX_THIRD_PERSON.getOneBased()
                + "       " + INDEX_FOURTH_PERSON.getOneBased() + "        " + INDEX_FIFTH_PERSON.getOneBased();

        List<Index> indexList = Arrays.asList(INDEX_THIRD_PERSON, INDEX_FOURTH_PERSON, INDEX_FIFTH_PERSON);
        StringBuilder names = new StringBuilder();
        for (Index index : indexList) {
            ReadOnlyPerson favoritedPerson = favoritePerson(expectedModel, index);
            names.append("\n\t★ ").append(favoritedPerson.getName().toString());
        }
        String expectedResultMessage = MESSAGE_FAVORITE_PERSON_SUCCESS + names;
        assertCommandSuccess(command, expectedModel, expectedResultMessage);

        /* Case: undo favoriting the 3rd, 4th & 5th person in the list -> 3rd, 4th & 5th person unfavorited */
        command = UndoCommand.COMMAND_WORD;
        expectedResultMessage = UndoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, expectedModel, expectedResultMessage);

        /* Case: redo favoriting the 3rd, 4th & 5th person in the list -> 3rd, 4th & 5th person favorited again */
        command = RedoCommand.COMMAND_WORD;
        for (Index index : indexList) {
            favoritePerson(expectedModel, index);
        }
        expectedResultMessage = RedoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, expectedModel, expectedResultMessage);

        /* ---------------- Performing favorite operation while a filtered list is being shown ------------------- */

        /* Case: filtered person list, favorite index within bounds of address book and person list -> favorited */
        showPersonsWithName(KEYWORD_MATCHING_KUNZ);
        expectedModel = getModel();
        Index index = INDEX_FIRST_PERSON;
        assertTrue(index.getZeroBased() < getModel().getFilteredPersonList().size());
        command = FavoriteCommand.COMMAND_WORD + " " + index.getOneBased();
        ReadOnlyPerson favoritedPerson = favoritePerson(expectedModel, index);
        expectedResultMessage = MESSAGE_FAVORITE_PERSON_SUCCESS + "\n\t★ " + favoritedPerson.getName().toString();
        assertCommandSuccess(command, expectedModel, expectedResultMessage);

        /*
         * Case: filtered person list, favorite index within bounds of address book but out of bounds of person list
         * -> rejected
         */
        showPersonsWithName(KEYWORD_MATCHING_KUNZ);
        int invalidIndex = getModel().getAddressBook().getPersonList().size();
        command = FavoriteCommand.COMMAND_WORD + " " + invalidIndex;
        assertCommandFailure(command, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);

        /* ------------------- Performing favorite operation while a person card is selected ---------------------- */

        /* Case: favorite the selected person -> person list panel selects the person before the favoriting */
        showAllPersons();
        expectedModel = getModel();
        Index selectedIndex = getLastIndex(expectedModel);
        Index expectedIndex = Index.fromZeroBased(selectedIndex.getZeroBased());
        selectPerson(selectedIndex);
        command = FavoriteCommand.COMMAND_WORD + " " + selectedIndex.getOneBased();
        favoritedPerson = favoritePerson(expectedModel, selectedIndex);
        expectedResultMessage = MESSAGE_FAVORITE_PERSON_SUCCESS + "\n\t★ " + favoritedPerson.getName().toString();
        assertCommandSuccess(command, expectedModel, expectedResultMessage, expectedIndex);

        /* ------------------------------- Performing invalid favorite operation ---------------------------------- */

        /* Case: multiple invalid indexes (0 0 0) -> rejected */
        command = FavoriteCommand.COMMAND_WORD + " 0 0 0";
        assertCommandFailure(command, MESSAGE_INVALID_FAVORITE_COMMAND_FORMAT);

        /* Case: multiple indexes with only one valid (1 0 -1 -2 -3) -> rejected */
        command = FavoriteCommand.COMMAND_WORD + " 1 0 -1 -2 -3";
        assertCommandFailure(command, MESSAGE_INVALID_FAVORITE_COMMAND_FORMAT);

        /* Case: invalid index (size + 1) -> rejected */
        Index outOfBoundsIndex = Index.fromOneBased(
                getModel().getAddressBook().getPersonList().size() + 1);
        command = FavoriteCommand.COMMAND_WORD + " " + outOfBoundsIndex.getOneBased();
        assertCommandFailure(command, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);

        /* Case: invalid arguments (alphabets) -> rejected */
        assertCommandFailure(FavoriteCommand.COMMAND_WORD + " 1 2 a", MESSAGE_INVALID_FAVORITE_COMMAND_FORMAT);

        /* Case: mixed case command word -> rejected */
        assertCommandFailure("FaV 1", MESSAGE_UNKNOWN_COMMAND);
    }

    /**
     * Favorites the {@code ReadOnlyPerson} at the specified {@code index} in {@code model}'s address book.
     * @return the favorited person
     */
    private ReadOnlyPerson favoritePerson(Model model, Index index) {
        ReadOnlyPerson targetPerson = model.getFilteredPersonList().get(index.getZeroBased());
        try {
            model.toggleFavoritePerson(targetPerson, FavoriteCommand.COMMAND_WORD);
        } catch (DuplicatePersonException dpe) {
            throw new AssertionError(EditCommand.MESSAGE_DUPLICATE_PERSON);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("targetPerson is retrieved from model.");
        }
        return targetPerson;
    }

    /**
     * Executes {@code command} and in addition,<br>
     * 1. Asserts that the command box displays an empty string.<br>
     * 2. Asserts that the result display box displays {@code expectedResultMessage}.<br>
     * 3. Asserts that the model related components equal to {@code expectedModel}.<br>
     * 4. Asserts that the browser url and selected card remains unchanged.<br>
     * 5. Asserts that the status bar's sync status changes.<br>
     * 6. Asserts that the command box has the default style class.<br>
     * Verifications 1 to 3 are performed by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage) {
        assertCommandSuccess(command, expectedModel, expectedResultMessage, null);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Model, String)} except that the
     * browser url and selected card are expected to update accordingly depending on the card
     * at {@code expectedSelectedCardIndex}.
     * @see FavoriteCommandSystemTest#assertCommandSuccess(String, Model, String)
     * @see AddressBookSystemTest#assertSelectedCardChanged(Index)
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage,
                                      Index expectedSelectedCardIndex) {
        executeCommand(command);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);

        if (expectedSelectedCardIndex != null) {
            assertSelectedCardChanged(expectedSelectedCardIndex);
        } else {
            assertSelectedCardUnchanged();
        }

        assertCommandBoxShowsDefaultStyle();
        assertStatusBarUnchangedExceptSyncStatus();
    }

    /**
     * Executes {@code command} and in addition,<br>
     * 1. Asserts that the command box displays {@code command}.<br>
     * 2. Asserts that result display box displays {@code expectedResultMessage}.<br>
     * 3. Asserts that the model related components equal to the current model.<br>
     * 4. Asserts that the browser url, selected card and status bar remain unchanged.<br>
     * 5. Asserts that the command box has the error style.<br>
     * Verifications 1 to 3 are performed by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandFailure(String command, String expectedResultMessage) {
        Model expectedModel = getModel();

        executeCommand(command);
        assertApplicationDisplaysExpected(command, expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsErrorStyle();
        assertStatusBarUnchanged();
    }
}
//@@author
