package seedu.address.commons.events.ui;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.events.BaseEvent;

/**
 * Indicates a request to jump to the list of persons
 */
public class JumpToListRequestEvent extends BaseEvent {

    public final int targetIndex;
    private String socialType = null;

    public JumpToListRequestEvent(Index targetIndex) {
        this.targetIndex = targetIndex.getZeroBased();
    }

    //@@author sarahnzx
    public JumpToListRequestEvent(Index targetIndex, String socialType) {
        this.targetIndex = targetIndex.getZeroBased();
        this.socialType = socialType;
    }
    //@@author

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    //@@author sarahnzx
    public String getSocialType() {
        return this.socialType;
    }
    //@@author

}
