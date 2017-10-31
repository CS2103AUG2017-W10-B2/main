package seedu.address.logic.commands;

import seedu.address.model.person.TagsContainKeywordsPredicate;

//@@author marvinchin
/**
 * Finds and lists all persons in address book whose tags contains any of the argument keywords.
 * Keyword matching is case sensitive.
 */
public class FindByTagsCommand extends FindCommand {
    public static final String COMMAND_OPTION = "tag";

    public FindByTagsCommand(TagsContainKeywordsPredicate predicate) {
        super(predicate);
    }
}
