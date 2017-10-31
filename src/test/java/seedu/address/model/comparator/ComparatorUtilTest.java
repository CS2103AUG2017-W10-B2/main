package seedu.address.model.comparator;

import static org.junit.Assert.assertEquals;
import static seedu.address.model.person.PersonComparatorUtil.compareAddress;
import static seedu.address.model.person.PersonComparatorUtil.compareEmail;
import static seedu.address.model.person.PersonComparatorUtil.compareFavorite;
import static seedu.address.model.person.PersonComparatorUtil.compareName;
import static seedu.address.model.person.PersonComparatorUtil.comparePhone;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BOB;

import org.junit.Test;

import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.testutil.PersonBuilder;

//@@author marvinchin
public class ComparatorUtilTest {

    @Test
    public void compareFavorite_sameFavorite_returnZero() {
        ReadOnlyPerson favoritePersonOne = new PersonBuilder(AMY).withFavorite(true).build();
        ReadOnlyPerson favoritePersonTwo = new PersonBuilder(BOB).withFavorite(true).build();
        assertEquals(0, compareFavorite(favoritePersonOne, favoritePersonTwo));

        ReadOnlyPerson unfavoritePersonOne = new PersonBuilder(AMY).withFavorite(false).build();
        ReadOnlyPerson unfavoritePersonTwo = new PersonBuilder(BOB).withFavorite(false).build();
        assertEquals(0, compareFavorite(unfavoritePersonOne, unfavoritePersonTwo));
    }

    @Test
    public void compareFavorite_differentFavorite_returnCorrectOrder() {
        ReadOnlyPerson favoritePersonOne = new PersonBuilder(AMY).withFavorite(true).build();
        ReadOnlyPerson unfavoritePersonTwo = new PersonBuilder(BOB).withFavorite(false).build();
        assertEquals(-1, compareFavorite(favoritePersonOne, unfavoritePersonTwo));

        ReadOnlyPerson unfavoritePersonOne = new PersonBuilder(AMY).withFavorite(false).build();
        ReadOnlyPerson favoritePersonTwo = new PersonBuilder(BOB).withFavorite(true).build();
        assertEquals(1, compareFavorite(unfavoritePersonOne, favoritePersonTwo));
    }

    @Test
    public void compareName_sameName_returnZero() {
        ReadOnlyPerson personOne = new PersonBuilder(AMY).withName("Bob").build();
        ReadOnlyPerson personTwo = new PersonBuilder(BOB).withName("Bob").build();
        assertEquals(0, compareName(personOne, personTwo));
    }

    @Test
    public void compareName_differentName_returnCorrectOrder() {
        ReadOnlyPerson personOne = new PersonBuilder(AMY).withName("Amy").build();
        ReadOnlyPerson personTwo = new PersonBuilder(BOB).withName("Bob").build();
        assertEquals(-1, compareName(personOne, personTwo));
        assertEquals(1, compareName(personTwo, personOne));
    }

    @Test
    public void comparePhone_samePhone_returnZero() {
        ReadOnlyPerson personOne = new PersonBuilder(AMY).withPhone("11111111").build();
        ReadOnlyPerson personTwo = new PersonBuilder(BOB).withPhone("11111111").build();
        assertEquals(0, comparePhone(personOne, personTwo));
    }

    @Test
    public void comparePhone_differentPhone_returnCorrectOrder() {
        ReadOnlyPerson personOne = new PersonBuilder(AMY).withPhone("11111111").build();
        ReadOnlyPerson personTwo = new PersonBuilder(BOB).withPhone("22222222").build();
        assertEquals(-1, comparePhone(personOne, personTwo));
        assertEquals(1, comparePhone(personTwo, personOne));
    }

    @Test
    public void compareAddress_sameAddress_returnZero() {
        ReadOnlyPerson personOne = new PersonBuilder(AMY).withAddress("Address 1").build();
        ReadOnlyPerson personTwo = new PersonBuilder(BOB).withAddress("Address 1").build();
        assertEquals(0, compareAddress(personOne, personTwo));
    }

    @Test
    public void compareAddress_differentAddress_returnCorrectOrder() {
        ReadOnlyPerson personOne = new PersonBuilder(AMY).withAddress("Address 1").build();
        ReadOnlyPerson personTwo = new PersonBuilder(BOB).withAddress("Address 2").build();
        assertEquals(-1, compareAddress(personOne, personTwo));
        assertEquals(1, compareAddress(personTwo, personOne));
    }

    @Test
    public void compareEmail_sameEmail_returnZero() {
        ReadOnlyPerson personOne = new PersonBuilder(AMY).withEmail("amy@example.com").build();
        ReadOnlyPerson personTwo = new PersonBuilder(BOB).withEmail("amy@example.com").build();
        assertEquals(0, compareEmail(personOne, personTwo));
    }

    @Test
    public void compareEmail_differentEmail_returnCorrectOrder() {
        ReadOnlyPerson personOne = new PersonBuilder(AMY).withEmail("amy@example.com").build();
        ReadOnlyPerson personTwo = new PersonBuilder(BOB).withEmail("bob@example.com").build();
        assertEquals(-1, compareEmail(personOne, personTwo));
        assertEquals(1, compareEmail(personTwo, personOne));
    }
}
