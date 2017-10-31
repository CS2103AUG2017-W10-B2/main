package seedu.address.logic.commands;

import seedu.address.model.person.NameContainsKeywordsPredicate;

//@@author marvinchin
/**
 * Finds and lists all persons in address book whose name contains any of the argument keywords.
 * Keyword matching is case sensitive.
 */
public class FindByNameCommand extends FindCommand {

    public FindByNameCommand(NameContainsKeywordsPredicate predicate) {
        super(predicate);
    }
}
