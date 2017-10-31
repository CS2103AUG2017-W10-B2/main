package seedu.address.commons.events.ui;

import seedu.address.commons.events.BaseEvent;
import seedu.address.ui.PersonCard;

/**
 * Represents a selection change in the Person List Panel
 */
public class PersonPanelSelectionChangedEvent extends BaseEvent {


    private final PersonCard newSelection;
    private String socialType = null;

    public PersonPanelSelectionChangedEvent(PersonCard newSelection) {
        this.newSelection = newSelection;
    }

    //@@author sarahnzx
    public PersonPanelSelectionChangedEvent(PersonCard newSelection, String socialType) {
        this.newSelection = newSelection;
        this.socialType = socialType;
    }
    //@@author

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public PersonCard getNewSelection() {
        return newSelection;
    }

    //@@author sarahnzx
    public String getSocialType() {
        return socialType;
    }
}
