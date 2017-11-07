package systemtests;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.SOCIAL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.testutil.TypicalPersons.AMY;

import java.util.Comparator;

import org.junit.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.FavoriteCommand;
import seedu.address.logic.commands.SortByDefaultCommand;
import seedu.address.logic.commands.SortByNameCommand;
import seedu.address.logic.commands.SortByRecentCommand;
import seedu.address.logic.commands.SortCommand;
import seedu.address.model.Model;
import seedu.address.model.person.PersonDefaultComparator;
import seedu.address.model.person.PersonNameComparator;
import seedu.address.model.person.PersonRecentComparator;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.testutil.PersonBuilder;

//@@author marvinchin
public class SortCommandSystemTest extends AddressBookSystemTest {

    @Test
    public void sort() throws DuplicatePersonException, PersonNotFoundException {
        /* ----------------- Performing sort operations while an unfiltered list is being shown ----------------- */

        /* Case: sort by name -> all persons in the list sorted by name */
        String command = SortCommand.COMMAND_WORD + " -" + SortByNameCommand.COMMAND_OPTION;
        String expectedResultMessage = SortByNameCommand.MESSAGE_SORT_SUCCESS;
        Comparator<ReadOnlyPerson> expectedComparator = new PersonNameComparator();
        assertSortCommandSuccess(command, expectedResultMessage, expectedComparator);

        /* Case: add a person -> persons in the list are still sorted by name */
        command = AddCommand.COMMAND_WORD + " "
                + NAME_DESC_AMY + "  "
                + PHONE_DESC_AMY + " "
                + EMAIL_DESC_AMY + "   "
                + ADDRESS_DESC_AMY + "   "
                + TAG_DESC_FRIEND + " "
                + SOCIAL_DESC_AMY + " ";
        ReadOnlyPerson toAdd = AMY;
        assertAddCommandRetainsSortOrder(command, toAdd, expectedComparator);

        /* Case: sort by recent -> all persons in the list sorted by last access time */
        command = SortCommand.COMMAND_WORD + " -" + SortByRecentCommand.COMMAND_OPTION;
        expectedResultMessage = SortByRecentCommand.MESSAGE_SORT_SUCCESS;
        expectedComparator = new PersonRecentComparator();
        assertSortCommandSuccess(command, expectedResultMessage, expectedComparator);

        /* Case: select a person -> persons in the list are still sorted by recent */
        Index editIndex = Index.fromOneBased(3);
        command = EditCommand.COMMAND_WORD + " " + editIndex.getOneBased() + NAME_DESC_BOB;
        ReadOnlyPerson editTarget = getPersonAtIndex(editIndex);
        ReadOnlyPerson editedPerson = new PersonBuilder(editTarget).withName(VALID_NAME_BOB).build();
        assertEditCommandRetainsSortOrder(command, editTarget, editedPerson, expectedComparator);

        /* Case: sort by default -> all persons in the list sorted based on default ordering */
        command = SortCommand.COMMAND_WORD;
        expectedResultMessage = SortByDefaultCommand.MESSAGE_SORT_SUCCESS;
        expectedComparator = new PersonDefaultComparator();
        assertSortCommandSuccess(command, expectedResultMessage, expectedComparator);

        /* Case: favorite a person -> persons in the list are still sorted by default ordering */
        Index favIndex = Index.fromOneBased(5);
        command = FavoriteCommand.COMMAND_WORD + " " + favIndex.getOneBased();
        ReadOnlyPerson favTarget = getPersonAtIndex(favIndex);
        assertFavoriteCommandRetainsSortOrder(command, favTarget, expectedComparator);

        /* ----------------- Performing sort operations while an filtered list is being shown ----------------- */

        showPersonsWithName("Meier");

        /* Case: sort by name -> all persons in the list sorted by name */
        command = SortCommand.COMMAND_WORD + " -" + SortByNameCommand.COMMAND_OPTION;
        expectedResultMessage = SortByNameCommand.MESSAGE_SORT_SUCCESS;
        expectedComparator = new PersonNameComparator();
        assertSortCommandSuccess(command, expectedResultMessage, expectedComparator);

        /* Case: sort by recent -> all persons in the list sorted by last access time */
        command = SortCommand.COMMAND_WORD + " -" + SortByRecentCommand.COMMAND_OPTION;
        expectedResultMessage = SortByRecentCommand.MESSAGE_SORT_SUCCESS;
        expectedComparator = new PersonRecentComparator();
        assertSortCommandSuccess(command, expectedResultMessage, expectedComparator);

        /* Case: sort by default -> all persons in the list sorted based on default ordering */
        command = SortCommand.COMMAND_WORD;
        expectedResultMessage = SortByDefaultCommand.MESSAGE_SORT_SUCCESS;
        expectedComparator = new PersonDefaultComparator();
        assertSortCommandSuccess(command, expectedResultMessage, expectedComparator);

        /* ----------------- Performing invalid sort operations ----------------- */
        // sort command with args
        command = SortCommand.COMMAND_WORD + " hello world";
        assertSortCommandFailure(command, expectedComparator);

        // sort command with invalid options
        command = SortCommand.COMMAND_WORD + " -somethinginvalid1234";
        assertSortCommandFailure(command, expectedComparator);

        // sort command with multiple options
        command = SortCommand.COMMAND_WORD + " "
            + "-" + SortByRecentCommand.COMMAND_OPTION + " "
            + "-" + SortByNameCommand.COMMAND_OPTION;
        assertSortCommandFailure(command, expectedComparator);
    }

    /**
     * Utility method to get the person at the given index in the model's filtered person list
     */
    private ReadOnlyPerson getPersonAtIndex(Index index) {
        Model model = getModel();
        return model.getFilteredPersonList().get(index.getZeroBased());
    }

    /**
     * Verifies that the add command succeeds and that the ordering of persons in the model's filtered person list
     * set by the last sort function still holds true
     */
    private void assertAddCommandRetainsSortOrder(String command, ReadOnlyPerson toAdd,
            Comparator<ReadOnlyPerson> expectedComparator) throws DuplicatePersonException {
        Model expectedModel = getModel();
        expectedModel.addPerson(toAdd);
        // expect the persons in the model to be sorted based on the input comparator
        expectedModel.sortPersons(expectedComparator);
        String expectedResultMessage = String.format(AddCommand.MESSAGE_SUCCESS, toAdd);
        assertCommandSuccessWithSyncStatusChanged(command, expectedModel, expectedResultMessage);
    }

    /**
     * Verifies that the edit command succeeds and that the ordering of persons in the model's filtered person list
     * set by the last sort function still holds true
     */
    private void assertEditCommandRetainsSortOrder(String command, ReadOnlyPerson target, ReadOnlyPerson editedPerson,
            Comparator<ReadOnlyPerson> expectedComparator) throws DuplicatePersonException, PersonNotFoundException {
        Model expectedModel = getModel();
        expectedModel.updatePerson(target, editedPerson);
        // expect the persons in the model to be sorted based on the input comparator
        expectedModel.sortPersons(expectedComparator);
        String expectedResultMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, editedPerson);
        assertCommandSuccessWithSyncStatusChanged(command, expectedModel, expectedResultMessage);
    }

    /**
     * Verifies that the favorite command succeeds and that the ordering of persons in the model's filtered person list
     * set by the last sort function still holds true
     */
    private void assertFavoriteCommandRetainsSortOrder(String command, ReadOnlyPerson toFav,
            Comparator<ReadOnlyPerson> expectedComparator) throws DuplicatePersonException, PersonNotFoundException {
        Model expectedModel = getModel();
        expectedModel.toggleFavoritePerson(toFav, FavoriteCommand.COMMAND_WORD);
        // expect the persons in the model to be sorted based on the input comparator
        expectedModel.sortPersons(expectedComparator);
        System.out.println(toFav.getName());
        String favoritePersonMessage = "\n\t★ " + toFav.getName().toString();
        System.out.println(favoritePersonMessage);
        String expectedResultMessage = FavoriteCommand.MESSAGE_FAVORITE_PERSON_SUCCESS + favoritePersonMessage;
        assertCommandSuccessWithSyncStatusChanged(command, expectedModel, expectedResultMessage);
    }

    /**
     * Verifies that the sort command succeeds and that the ordering of persons in the model's filtered person list is
     * correctly set by the sort function
     */
    private void assertSortCommandSuccess(String command, String expectedResultMessage,
            Comparator<ReadOnlyPerson> expectedComparator) {
        Model expectedModel = getModel();
        expectedModel.sortPersons(expectedComparator);
        assertCommandSuccessWithStatusBarUnchanged(command, expectedModel, expectedResultMessage);
    }

    /**
     * Executes {@code command} and in addition,<br>
     * 1. Asserts that the command box displays an empty string.<br>
     * 2. Asserts that the result display box displays {@code expectedResultMessage}.<br>
     * 3. Asserts that the model related components equal to {@code expectedModel}.<br>
     * 4. Asserts that no card is selected.<br>
     * 5. Asserts that the status bar's sync status does not change.<br>
     * 6. Asserts that the command box has the default style class.<br>
     * Verifications 1 to 3 are performed by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     * @see AddressBookSystemTest#assertSelectedCardChanged(Index)
     */
    private void assertCommandSuccessWithStatusBarUnchanged(String command, Model expectedModel,
            String expectedResultMessage) {
        executeCommand(command);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);
        assertSelectedCardDeselected();
        assertCommandBoxShowsDefaultStyle();
        assertStatusBarUnchanged();
    }

    /**
     * Performs the same verification as {@code assertCommandSuccessWithStatusBarUnchanged(String, Model, String)}
     * except that the sync status in the status bar changes.
     * @see SortCommandSystemTest#assertCommandSuccessWithStatusBarUnchanged(String, Model, String)
     */
    private void assertCommandSuccessWithSyncStatusChanged(String command, Model expectedModel,
            String expectedResultMessage) {
        executeCommand(command);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);
        assertSelectedCardDeselected();
        assertCommandBoxShowsDefaultStyle();
        assertStatusBarUnchangedExceptSyncStatus();
    }

    /**
     * Executes {@code command} and in addition,<br>
     * 1. Asserts that the command box displays {@code command}.<br>
     * 2. Asserts that result display box displays {@code expectedResultMessage}.<br>
     * 3. Asserts that the model related components equal to the current model, and that the ordering of the persons in
     * the model is correct.<br>
     * 4. Asserts that the browser url, selected card and status bar remain unchanged.<br>
     * 5. Asserts that the command box has the error style.<br>
     * Verifications 1 to 3 are performed by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertSortCommandFailure(String command, Comparator<ReadOnlyPerson> lastValidSortComparator) {
        Model expectedModel = getModel();
        // model should remain sorted with the last valid sort even if command fails
        expectedModel.sortPersons(lastValidSortComparator);
        String expectedResultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);
        executeCommand(command);
        assertApplicationDisplaysExpected(command, expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsErrorStyle();
        assertStatusBarUnchanged();
    }
}
