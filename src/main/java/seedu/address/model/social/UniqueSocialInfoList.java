package seedu.address.model.social;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.exceptions.DuplicateDataException;

//@@author marvinchin
/**
 * A list of social media information that enforces no nulls and
 * no duplicate social info types between its elements.
 */
public class UniqueSocialInfoList implements Iterable<SocialInfo> {

    private final ObservableList<SocialInfo> internalList = FXCollections.observableArrayList();

    /**
     * Constructs empty SocialInfoList.
     */
    public UniqueSocialInfoList() {}

    /**
     * Creates a UniqueSocialInfoList using given socialInfos.
     * Enforces no nulls.
     */
    public UniqueSocialInfoList(Set<SocialInfo> socialInfos) {
        requireAllNonNull(socialInfos);
        internalList.addAll(socialInfos);

        assert internalListSocialTypesUnique();
    }


    /**
     * Returns all the social media information in this list as a Set.
     */
    public Set<SocialInfo> toSet() {
        assert internalListSocialTypesUnique();
        return new HashSet<>(internalList);
    }

    /**
     * Replaces the SocialInfo in this list with those in the argument SocialInfo list.
     * @param socialInfos
     */
    public void setSocialInfos(Set<SocialInfo> socialInfos) {
        requireAllNonNull(socialInfos);
        internalList.setAll(socialInfos);

        assert internalListSocialTypesUnique();
    }

    /**
     * Returns true if the list contains a SocialInfo with the same type
     */
    public boolean containsSameType(SocialInfo toCheck) {
        requireNonNull(toCheck);

        String toCheckType = toCheck.getSocialType();
        for (SocialInfo socialInfo : internalList) {
            String thisSocialType = socialInfo.getSocialType();
            if (toCheckType.equals(thisSocialType)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Adds a SocialInfo to the list
     * @throws DuplicateSocialTypeException if the SocialInfo to add is a duplicate of an existing
     * SocialInfo in the list.
     */
    public void add(SocialInfo toAdd) throws DuplicateSocialTypeException {
        requireNonNull(toAdd);

        if (containsSameType(toAdd)) {
            throw new DuplicateSocialTypeException();
        }
        internalList.add(toAdd);

        assert internalListSocialTypesUnique();
    }

    /**
     * Checks that there are no SocialInfos of the same type in the collection
     */
    private boolean internalListSocialTypesUnique() {
        HashSet<String> socialTypes = new HashSet<>();

        for (SocialInfo socialInfo : internalList) {
            String socialType = socialInfo.getSocialType();
            if (socialTypes.contains(socialType)) {
                return false;
            } else {
                socialTypes.add(socialType);
            }
        }

        return true;
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     * @return
     */
    public ObservableList<SocialInfo> asObservableList() {
        assert internalListSocialTypesUnique();

        return FXCollections.unmodifiableObservableList(internalList);
    }

    @Override
    public Iterator<SocialInfo> iterator() {
        assert internalListSocialTypesUnique();
        return internalList.iterator();

    }

    @Override
    public boolean equals(Object other) {
        assert internalListSocialTypesUnique();

        return other == this
                || (other instanceof UniqueSocialInfoList
                && this.internalList.equals(((UniqueSocialInfoList) other).internalList));
    }

    /**
     * Returns true if the elements in this list is equal to the elements in {@code other}.
     * The elements do not have to be in the same order.
     */
    public boolean equalsOrderInsensitive(UniqueSocialInfoList other) {
        assert internalListSocialTypesUnique();
        assert other.internalListSocialTypesUnique();
        return other == this
                || new HashSet<>(internalList).equals(new HashSet<>(other.internalList));
    }

    /**
     * Signals that an operation would have violated the 'no duplicate social type' property of the list.
     */
    public static class DuplicateSocialTypeException extends DuplicateDataException {
        protected DuplicateSocialTypeException() {
            super("Operation would result in more than one SocialInfo of the same type");
        }
    }
}
