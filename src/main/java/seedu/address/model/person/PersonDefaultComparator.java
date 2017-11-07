package seedu.address.model.person;

import static seedu.address.model.person.PersonComparatorUtil.compareAddress;
import static seedu.address.model.person.PersonComparatorUtil.compareEmail;
import static seedu.address.model.person.PersonComparatorUtil.compareFavorite;
import static seedu.address.model.person.PersonComparatorUtil.compareName;
import static seedu.address.model.person.PersonComparatorUtil.comparePhone;

import java.util.Comparator;

//@@author marvinchin
/**
 * Default comparator for persons. Sorts first by favorites, then by name in alphabetical order,
 * then by phone in numeric order, then by address in alphabetical order, then by email in alphabetical order,
 * then by last access date
 */
public class PersonDefaultComparator implements Comparator<ReadOnlyPerson> {
    @Override
    public int compare(ReadOnlyPerson thisPerson, ReadOnlyPerson otherPerson) {
        if (!thisPerson.getFavorite().equals(otherPerson.getFavorite())) {
            return compareFavorite(thisPerson, otherPerson);
        } else if (!thisPerson.getName().equals(otherPerson.getName())) {
            return compareName(thisPerson, otherPerson);
        } else if (!thisPerson.getPhone().equals(otherPerson.getPhone())) {
            return comparePhone(thisPerson, otherPerson);
        } else if (!thisPerson.getAddress().equals(otherPerson.getAddress())) {
            return compareAddress(thisPerson, otherPerson);
        } else {
            return compareEmail(thisPerson, otherPerson);
        }
    }
}
