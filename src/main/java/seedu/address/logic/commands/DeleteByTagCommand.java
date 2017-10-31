package seedu.address.logic.commands;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.TagsContainKeywordsPredicate;

//@@author marvinchin
/**
 * Deletes persons from the list identified by their indexes in the last displayed list in the address book.
 */
public class DeleteByTagCommand extends DeleteCommand {
    public static final String COMMAND_OPTION = "tag";

    private Set<String> targetTags;

    public DeleteByTagCommand(Set<String> tags) {
        targetTags = tags;
    }

    /**
     * Returns the list of persons in the last shown list referenced by the collection of tags provided.
     */
    private Collection<ReadOnlyPerson> mapPersonsToTags(Collection<String> tags) {
        TagsContainKeywordsPredicate predicate = new TagsContainKeywordsPredicate(tags);
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();
        List<ReadOnlyPerson> persons = lastShownList.stream().filter(predicate).collect(Collectors.toList());
        return persons;
    }

    @Override
    public Collection<ReadOnlyPerson> getPersonsToDelete() {
        return mapPersonsToTags(targetTags);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteByTagCommand // instanceof handles nulls
                && this.targetTags.equals(((DeleteByTagCommand) other).targetTags)); // state check
    }
}
