package systemtests;

import static org.junit.Assert.assertTrue;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.UnFavoriteCommand.MESSAGE_UNFAVORITE_PERSON_SUCCESS;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.KEYWORD_MATCHING_MEIER;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UnFavoriteCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.model.Model;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;

//@@author keithsoc
public class UnFavoriteCommandSystemTest extends AddressBookSystemTest {

    private static final String MESSAGE_INVALID_UNFAVORITE_COMMAND_FORMAT =
            String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, UnFavoriteCommand.MESSAGE_USAGE);

    @Test
    public void unFavorite() {
        /* ------------- Performing unfavorite operation while an unfiltered list is being shown ---------------- */

        /*
         * Case: unfavorites the 1st & 2nd person in the list,
         * command with leading spaces and trailing spaces -> unfavorited
         */
        Model expectedModel = getModel();
        String command = "     " + UnFavoriteCommand.COMMAND_WORD + "      " + INDEX_FIRST_PERSON.getOneBased()
                + "       " + INDEX_SECOND_PERSON.getOneBased();

        // Store in reverse just like how it would be sorted in the UnFavoriteCommandParser
        List<Index> indexList = Arrays.asList(INDEX_SECOND_PERSON, INDEX_FIRST_PERSON);
        StringBuilder names = new StringBuilder();
        for (Index index : indexList) {
            ReadOnlyPerson unFavoritedPerson = unFavoritePerson(expectedModel, index);
            names.append("\n\t- ").append(unFavoritedPerson.getName().toString());
        }
        String expectedResultMessage = MESSAGE_UNFAVORITE_PERSON_SUCCESS + names;
        assertCommandSuccess(command, expectedModel, expectedResultMessage);

        /* Case: undo unfavoriting the 1st & 2nd person in the list -> 1st & 2nd person unfavorited */
        command = UndoCommand.COMMAND_WORD;
        expectedResultMessage = UndoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, expectedModel, expectedResultMessage);

        /* Case: redo unfavoriting the 1st & 2nd person in the list -> 1st & 2nd person unfavorited again */
        command = RedoCommand.COMMAND_WORD;
        for (Index index : indexList) {
            unFavoritePerson(expectedModel, index);
        }
        expectedResultMessage = RedoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, expectedModel, expectedResultMessage);

        /* Case: we're undoing again so that we can perform unfavorite operations (with success message) below */
        command = UndoCommand.COMMAND_WORD;
        expectedResultMessage = UndoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, expectedModel, expectedResultMessage);

        /* ---------------- Performing unfavorite operation while a filtered list is being shown ------------------- */

        /* Case: filtered person list, unfavorite index within bounds of address book and person list -> unfavorited */
        showPersonsWithName(KEYWORD_MATCHING_MEIER);
        expectedModel = getModel();
        Index index = INDEX_FIRST_PERSON;
        assertTrue(index.getZeroBased() < getModel().getFilteredPersonList().size());
        command = UnFavoriteCommand.COMMAND_WORD + " " + index.getOneBased();
        ReadOnlyPerson unFavoritedPerson = unFavoritePerson(expectedModel, index);
        expectedResultMessage = MESSAGE_UNFAVORITE_PERSON_SUCCESS + "\n\t- " + unFavoritedPerson.getName().toString();
        assertCommandSuccess(command, expectedModel, expectedResultMessage);

        /*
         * Case: filtered person list, unfavorite index within bounds of address book but out of bounds of person list
         * -> rejected
         */
        showPersonsWithName(KEYWORD_MATCHING_MEIER);
        int invalidIndex = getModel().getAddressBook().getPersonList().size();
        command = UnFavoriteCommand.COMMAND_WORD + " " + invalidIndex;
        assertCommandFailure(command, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);

        /* ------------------ Performing unfavorite operation while a person card is selected --------------------- */

        /* Case: unfavorite the selected person -> person list panel selects the person before the unfavoriting */
        showAllPersons();
        expectedModel = getModel();
        Index selectedIndex = INDEX_FIRST_PERSON;
        Index expectedIndex = Index.fromZeroBased(selectedIndex.getZeroBased());
        selectPerson(selectedIndex);
        command = UnFavoriteCommand.COMMAND_WORD + " " + selectedIndex.getOneBased();
        unFavoritedPerson = unFavoritePerson(expectedModel, selectedIndex);
        expectedResultMessage = MESSAGE_UNFAVORITE_PERSON_SUCCESS + "\n\t- " + unFavoritedPerson.getName().toString();
        assertCommandSuccess(command, expectedModel, expectedResultMessage, expectedIndex);

        /* ------------------------------ Performing invalid unfavorite operation --------------------------------- */

        /* Case: multiple invalid indexes (0 0 0) -> rejected */
        command = UnFavoriteCommand.COMMAND_WORD + " 0 0 0";
        assertCommandFailure(command, MESSAGE_INVALID_UNFAVORITE_COMMAND_FORMAT);

        /* Case: multiple indexes with only one valid (1 0 -1 -2 -3) -> rejected */
        command = UnFavoriteCommand.COMMAND_WORD + " 1 0 -1 -2 -3";
        assertCommandFailure(command, MESSAGE_INVALID_UNFAVORITE_COMMAND_FORMAT);

        /* Case: invalid index (size + 1) -> rejected */
        Index outOfBoundsIndex = Index.fromOneBased(
                getModel().getAddressBook().getPersonList().size() + 1);
        command = UnFavoriteCommand.COMMAND_WORD + " " + outOfBoundsIndex.getOneBased();
        assertCommandFailure(command, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);

        /* Case: invalid arguments (alphabets) -> rejected */
        assertCommandFailure(UnFavoriteCommand.COMMAND_WORD + " 1 2 a",
                MESSAGE_INVALID_UNFAVORITE_COMMAND_FORMAT);

        /* Case: mixed case command word -> rejected */
        assertCommandFailure("UnFaV 1", MESSAGE_UNKNOWN_COMMAND);
    }

    /**
     * Unfavorites the {@code ReadOnlyPerson} at the specified {@code index} in {@code model}'s address book.
     * @return the unfavorited person
     */
    private ReadOnlyPerson unFavoritePerson(Model model, Index index) {
        ReadOnlyPerson targetPerson = model.getFilteredPersonList().get(index.getZeroBased());
        try {
            model.toggleFavoritePerson(targetPerson, UnFavoriteCommand.COMMAND_WORD);
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
     * @see UnFavoriteCommandSystemTest#assertCommandSuccess(String, Model, String)
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
