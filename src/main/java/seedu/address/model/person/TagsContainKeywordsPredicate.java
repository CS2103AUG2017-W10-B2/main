package seedu.address.model.person;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.model.tag.Tag;

//@@author marvinchin
/**
 * Tests that a {@code ReadOnlyPerson}'s {@code Name} matches any of the keywords given.
 */
public class TagsContainKeywordsPredicate implements Predicate<ReadOnlyPerson> {
    private final Collection<String> keywords;

    public TagsContainKeywordsPredicate(Collection<String> keywords) {
        this.keywords = keywords;
    }

    private boolean personTagsMatchKeyword(ReadOnlyPerson person, String keyword) {
        Set<Tag> tags = person.getTags();
        return tags.stream().anyMatch(tag -> StringUtil.containsWordIgnoreCase(tag.tagName, keyword));
    }

    @Override
    public boolean test(ReadOnlyPerson person) {
        return keywords.stream()
                .anyMatch(keyword -> personTagsMatchKeyword(person, keyword));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof TagsContainKeywordsPredicate // instanceof handles nulls
                && this.keywords.equals(((TagsContainKeywordsPredicate) other).keywords)); // state check
    }

}
