package seedu.address.logic.commands;

import java.util.Comparator;

import seedu.address.model.person.PersonRecentComparator;
import seedu.address.model.person.ReadOnlyPerson;

//@@author marvinchin

/**
 * Sorts the displayed person list by the last time they were added, updated, or selected.
 */
public class SortByRecentCommand extends SortCommand {

    public static final String COMMAND_OPTION = "recent";

    @Override
    public Comparator<ReadOnlyPerson> getComparator() {
        return new PersonRecentComparator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || other instanceof SortByRecentCommand; // instanceof handles nulls
    }
}
