package seedu.address.logic.commands;

import java.util.Comparator;

import seedu.address.model.person.PersonNameComparator;
import seedu.address.model.person.ReadOnlyPerson;

//@@author marvinchin
/**
 * Sorts the displayed person list by their names in alphabetical order.
 */
public class SortByNameCommand extends SortCommand {

    public static final String COMMAND_OPTION = "name";

    @Override
    public Comparator<ReadOnlyPerson> getComparator() {
        return new PersonNameComparator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || other instanceof SortByNameCommand; // instanceof handles nulls
    }
}
