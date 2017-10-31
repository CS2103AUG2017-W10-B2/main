package seedu.address.logic.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.PersonNotFoundException;

/**
 * Represents a command that deletes persons from the addressbook identified by some input parameter
 */
public abstract class DeleteCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "delete";
    public static final String COMMAND_ALIAS = "d";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the specified persons from the address book\n"
            + "Alias: " + COMMAND_ALIAS + "\n"
            + "Parameters: [OPTION] IDENTIFIER [MORE_IDENTIFIERS]...\n"
            + "Options: \n"
            + "\tdefault - Deletes the persons identified by the index numbers "
            + "(must be positive integers) used in the last person listing.\n"
            + "\t" + DeleteByTagCommand.COMMAND_OPTION
            + " - Deletes the perons in the last person listing with the specified tags.\n"
            + "Example:\n"
            + COMMAND_WORD + " 1 2\n"
            + COMMAND_WORD + " -" + DeleteByTagCommand.COMMAND_OPTION + " friends colleagues";


    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person(s): %1$s";

    private List<Index> targetIndexList = new ArrayList<>();

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        Collection<ReadOnlyPerson> personsToDelete = getPersonsToDelete();
        StringBuilder deletedPersons = new StringBuilder();

        for (ReadOnlyPerson personToDelete : personsToDelete) {
            try {
                model.deletePerson(personToDelete);
            } catch (PersonNotFoundException pnfe) {
                assert false : "The target person cannot be missing";
            }

            deletedPersons.append("\n");
            deletedPersons.append(personToDelete);
        }
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, deletedPersons));
    }

    //@@author marvinchin
    /**
     * Returns the collection of persons to be deleted.
     * To be implemented by the classes inheriting this class.
     */
    public abstract Collection<ReadOnlyPerson> getPersonsToDelete() throws CommandException;
    //@@author

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteCommand // instanceof handles nulls
                && this.targetIndexList.equals(((DeleteCommand) other).targetIndexList)); // state check
    }
}
