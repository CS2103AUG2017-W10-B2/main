package seedu.address.logic.commands;

import java.util.Comparator;

import seedu.address.model.person.PersonDefaultComparator;
import seedu.address.model.person.ReadOnlyPerson;

//@@author marvinchin
/**
 * Sorts the displayed person list by their names in alphabetical order.
 */
public class SortByDefaultCommand extends SortCommand {
    @Override
    public Comparator<ReadOnlyPerson> getComparator() {
        return new PersonDefaultComparator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || other instanceof SortByDefaultCommand; // instanceof handles nulls
    }
}
