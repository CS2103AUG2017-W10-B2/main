package seedu.address.model.person;

//@@author marvinchin
/**
 * Utility class with useful methods for writing person comparators
 */
public class PersonComparatorUtil {

    /**
     * Compares two persons based on whether or not they are favorited. The favorited person will be ordered first.
     * If both persons have the same favorite status (yes/no), they are considered equal.
     */
    public static int compareFavorite(ReadOnlyPerson thisPerson, ReadOnlyPerson otherPerson) {
        boolean isThisPersonFavorite = thisPerson.getFavorite().isFavorite();
        boolean isOtherPersonFavorite = otherPerson.getFavorite().isFavorite();
        if (isThisPersonFavorite && !isOtherPersonFavorite) {
            return -1;
        } else if (!isThisPersonFavorite && isOtherPersonFavorite) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Compares two persons based on their names
     */
    public static int compareName(ReadOnlyPerson thisPerson, ReadOnlyPerson otherPerson) {
        String thisPersonName = thisPerson.getName().toString();
        String otherPersonName = otherPerson.getName().toString();
        return thisPersonName.compareToIgnoreCase(otherPersonName);
    }

    /**
     * Compares two persons based on their phones
     */
    public static int comparePhone(ReadOnlyPerson thisPerson, ReadOnlyPerson otherPerson) {
        String thisPersonPhone = thisPerson.getPhone().toString();
        String otherPersonPhone = otherPerson.getPhone().toString();
        return thisPersonPhone.compareToIgnoreCase(otherPersonPhone);
    }

    /**
     * Compares two persons based on their addresses
     */
    public static int compareAddress(ReadOnlyPerson thisPerson, ReadOnlyPerson otherPerson) {
        String thisPersonAddress = thisPerson.getAddress().toString();
        String otherPersonAddress = otherPerson.getAddress().toString();
        return thisPersonAddress.compareToIgnoreCase(otherPersonAddress);
    }

    /**
     * Compares two persons based on their emails
     */
    public static int compareEmail(ReadOnlyPerson thisPerson, ReadOnlyPerson otherPerson) {
        String thisPersonEmail = thisPerson.getEmail().toString();
        String otherPersonEmail = otherPerson.getEmail().toString();
        return thisPersonEmail.compareToIgnoreCase(otherPersonEmail);
    }
}
