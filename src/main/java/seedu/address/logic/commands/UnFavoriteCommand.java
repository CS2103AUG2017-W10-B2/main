package seedu.address.logic.commands;

import java.util.Collections;
import java.util.List;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;

/**
 * Unfavorites the person(s) identified using it's last displayed index from the address book.
 */
public class UnFavoriteCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "unfav";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Unfavorites the person(s) identified by the index number used in the last person listing.\n"
            + "Parameters: INDEX (must be a positive integer) or INDEXES\n"
            + "Example: " + COMMAND_WORD + " 1 OR " + COMMAND_WORD + " 1 2 3";

    public static final String MESSAGE_UNFAVORITE_PERSON_SUCCESS = "Removed from favorite contact(s): %1$s";

    private final List<Index> targetIndexList;

    public UnFavoriteCommand(List<Index> targetIndexList) {
        this.targetIndexList = targetIndexList;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();
        String names = "";

        /*
         * First, efficiently check whether user input any index larger than address book size with Collections.max
         * This is to avoid the following situation:
         *
         * E.g. AddressBook size is 100
         * Execute "fav 6 7 101 8 9" ->
         * persons at indexes 7 and 8 gets favorited but method halts due to CommandException for index 101 and
         * person at index 9 and beyond does not get favorited
         */
        if (Collections.max(targetIndexList).getOneBased() > lastShownList.size()) {
            if (targetIndexList.size() > 1) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX_MULTI);
            } else {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
        }

        /*
         * Then, as long no exception is thrown above i.e. all index are within boundaries,
         * the following loop will run
         */
        for (Index targetIndex : targetIndexList) {
            ReadOnlyPerson personToUnFavorite = lastShownList.get(targetIndex.getZeroBased());
            names += "\n" + personToUnFavorite.getName().toString();

            try {
                model.toggleFavoritePerson(personToUnFavorite, COMMAND_WORD);
            } catch (DuplicatePersonException dpe) {
                throw new CommandException(EditCommand.MESSAGE_DUPLICATE_PERSON);
            } catch (PersonNotFoundException pnfe) {
                throw new AssertionError("The target person cannot be missing");
            }
        }

        return new CommandResult(String.format(MESSAGE_UNFAVORITE_PERSON_SUCCESS, names));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UnFavoriteCommand // instanceof handles nulls
                && this.targetIndexList.equals(((UnFavoriteCommand) other).targetIndexList)); // state check
    }
}