package seedu.address.logic.commands;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;

//@@author keithsoc
/**
 * Favorites the person(s) identified using it's last displayed index from the address book.
 */
public class FavoriteCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "fav";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Favorites the person(s) identified by the index number used in the last person listing.\n"
            + "Parameters: INDEX [ADDITIONAL INDEXES] (INDEX must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1\n"
            + "Example: " + COMMAND_WORD + " 1 2 3";

    public static final String MESSAGE_FAVORITE_PERSON_SUCCESS = "Added as favorite contact(s): ";
    public static final String MESSAGE_FAVORITE_PERSON_FAILURE = "These contact(s) has already been "
            + "added as favorites: ";

    private final List<Index> targetIndexList;
    private final Set<ReadOnlyPerson> targetPersonList;
    private final StringBuilder allNameList;
    private final StringBuilder successNameList;
    private final StringBuilder failureNameList;

    public FavoriteCommand(List<Index> targetIndexList) {
        this.targetIndexList = targetIndexList;
        this.targetPersonList = new LinkedHashSet<>();
        this.allNameList = new StringBuilder();
        this.successNameList = new StringBuilder();
        this.failureNameList = new StringBuilder();
    }

    /**
     * Efficiently check whether user input any index larger than address book size with Collections.max
     * This is to avoid the following situation:
     *
     * E.g. AddressBook size is 100
     * Execute "fav 6 7 101 8 9" ->
     * persons at indexes 7 and 8 gets favorited but method halts due to CommandException for index 101 and
     * person at index 9 and beyond does not get favorited.
     */
    private void checkIndexBoundaries(List<ReadOnlyPerson> lastShownList) throws CommandException {
        if (Collections.max(targetIndexList).getOneBased() > lastShownList.size()) {
            if (targetIndexList.size() > 1) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX_MULTI);
            } else {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
        }
    }

    /**
     * Stores all persons to favorite into {@code targetPersonList} found from the index(es) used in last shown list.
     * {@code targetPersonList} uses a LinkedHashSet implementation for the following purposes:
     * 1. Prevent duplicates (of persons) resulting from such an input: fav 1 1 1
     * 2. Preserve insertion order (since {@code targetIndexList} is sorted)
     */
    private void getPersonsToFavorite(List<ReadOnlyPerson> lastShownList) {
        for (Index targetIndex : targetIndexList) {
            ReadOnlyPerson personToFavorite = lastShownList.get(targetIndex.getZeroBased());
            if (!isAlreadyFavorite(personToFavorite)) { // Add person into set
                targetPersonList.add(personToFavorite);
            } else { // Do not add person into set, append into failure name list instead
                failureNameList.append("\n\t- ");
                failureNameList.append(personToFavorite.getName().toString());
            }
        }
    }

    /**
     * Checks if {@code person} is already a favorite contact.
     */
    private boolean isAlreadyFavorite(ReadOnlyPerson person) {
        return person.getFavorite().isFavorite();
    }

    /**
     * Adds all appropriate String messages into {@code allNameList}.
     */
    private String compileAllNames() {
        if (successNameList.length() != 0 && failureNameList.length() != 0) {
            allNameList.append(MESSAGE_FAVORITE_PERSON_SUCCESS);
            allNameList.append(successNameList);
            allNameList.append("\n");
            allNameList.append(MESSAGE_FAVORITE_PERSON_FAILURE);
            allNameList.append(failureNameList);
        } else if (successNameList.length() != 0 && failureNameList.length() == 0) {
            allNameList.append(MESSAGE_FAVORITE_PERSON_SUCCESS);
            allNameList.append(successNameList);
        } else {
            allNameList.append(MESSAGE_FAVORITE_PERSON_FAILURE);
            allNameList.append(failureNameList);
        }
        return allNameList.toString();
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        checkIndexBoundaries(lastShownList);
        /*
         * As long no exception is thrown above i.e. all index are within boundaries,
         * the following codes will run
         */
        getPersonsToFavorite(lastShownList);

        for (ReadOnlyPerson personToFavorite : targetPersonList) {
            try {
                model.toggleFavoritePerson(personToFavorite, COMMAND_WORD);
                successNameList.append("\n\t★ ");
                successNameList.append(personToFavorite.getName().toString());
            } catch (DuplicatePersonException dpe) {
                throw new CommandException(EditCommand.MESSAGE_DUPLICATE_PERSON);
            } catch (PersonNotFoundException pnfe) {
                throw new AssertionError("The target person cannot be missing");
            }
        }

        return new CommandResult(compileAllNames());
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof FavoriteCommand // instanceof handles nulls
                && this.targetIndexList.equals(((FavoriteCommand) other).targetIndexList)); // state check
    }
}
//@@author
