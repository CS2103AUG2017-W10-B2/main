package seedu.address.testutil;

import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_FAVORITE_NO;
import static seedu.address.logic.commands.CommandTestUtil.VALID_FAVORITE_YES;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_SOCIAL_AMY_INSTAGRAM;
import static seedu.address.logic.commands.CommandTestUtil.VALID_SOCIAL_BOB_FACEBOOK;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import seedu.address.model.AddressBook;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;

/**
 * A utility class containing a list of {@code Person} objects to be used in tests.
 */
public class TypicalPersons {

    public static final ReadOnlyPerson ALICE = new PersonBuilder()
            .withName("Alice Pauline")
            .withAddress("123, Jurong West Ave 6, #08-111")
            .withEmail("alice@example.com")
            .withPhone("85355255")
            .withFavorite(true)
            .withDisplayPhoto(null)
            .withTags("friends")
            .withLastAccessDate(new Date(1000))
            .build();
    public static final ReadOnlyPerson BENSON = new PersonBuilder()
            .withName("Benson Meier")
            .withAddress("311, Clementi Ave 2, #02-25")
            .withEmail("johnd@example.com")
            .withPhone("98765432")
            .withFavorite(true)
            .withDisplayPhoto(null)
            .withTags("owesMoney", "friends")
            .withLastAccessDate(new Date(2000))
            .build();
    public static final ReadOnlyPerson CARL = new PersonBuilder()
            .withName("Carl Kurz")
            .withPhone("95352563")
            .withEmail("heinz@example.com")
            .withAddress("wall street")
            .withFavorite(false)
            .withDisplayPhoto(null)
            .withLastAccessDate(new Date(3000))
            .build();
    public static final ReadOnlyPerson DANIEL = new PersonBuilder()
            .withName("Daniel Meier")
            .withPhone("87652533")
            .withEmail("cornelia@example.com")
            .withAddress("10th street")
            .withFavorite(false)
            .withDisplayPhoto(null)
            .withLastAccessDate(new Date(4000))
            .build();
    public static final ReadOnlyPerson ELLE = new PersonBuilder()
            .withName("Elle Meyer")
            .withPhone("9482224")
            .withEmail("werner@example.com")
            .withAddress("michegan ave")
            .withFavorite(false)
            .withDisplayPhoto(null)
            .withLastAccessDate(new Date(5000))
            .build();
    public static final ReadOnlyPerson FIONA = new PersonBuilder()
            .withName("Fiona Kunz")
            .withPhone("9482427")
            .withEmail("lydia@example.com")
            .withAddress("little tokyo")
            .withFavorite(false)
            .withDisplayPhoto(null)
            .withLastAccessDate(new Date(6000))
            .build();
    public static final ReadOnlyPerson GEORGE = new PersonBuilder()
            .withName("George Best")
            .withPhone("9482442")
            .withEmail("anna@example.com")
            .withAddress("4th street")
            .withFavorite(false)
            .withDisplayPhoto(null)
            .withLastAccessDate(new Date(7000))
            .build();

    // Manually added
    public static final ReadOnlyPerson HOON = new PersonBuilder()
            .withName("Hoon Meier")
            .withPhone("8482424")
            .withEmail("stefan@example.com")
            .withAddress("little india")
            .withFavorite(false)
            .withDisplayPhoto(null)
            .withLastAccessDate(new Date(8000))
            .build();
    public static final ReadOnlyPerson IDA = new PersonBuilder()
            .withName("Ida Mueller")
            .withPhone("8482131")
            .withEmail("hans@example.com")
            .withAddress("chicago ave")
            .withFavorite(false)
            .withDisplayPhoto(null)
            .withLastAccessDate(new Date(9000))
            .build();

    // Manually added - Person's details found in {@code CommandTestUtil}
    public static final ReadOnlyPerson AMY = new PersonBuilder()
            .withName(VALID_NAME_AMY)
            .withPhone(VALID_PHONE_AMY)
            .withEmail(VALID_EMAIL_AMY)
            .withAddress(VALID_ADDRESS_AMY)
            .withFavorite(VALID_FAVORITE_NO)
            .withDisplayPhoto(null)
            .withTags(VALID_TAG_FRIEND)
            .withSocialInfos(VALID_SOCIAL_AMY_INSTAGRAM)
            .build();
    public static final ReadOnlyPerson BOB = new PersonBuilder()
            .withName(VALID_NAME_BOB)
            .withPhone(VALID_PHONE_BOB)
            .withEmail(VALID_EMAIL_BOB)
            .withAddress(VALID_ADDRESS_BOB)
            .withFavorite(VALID_FAVORITE_YES)
            .withDisplayPhoto(null)
            .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND)
            .withSocialInfos(VALID_SOCIAL_BOB_FACEBOOK)
            .build();

    public static final String KEYWORD_MATCHING_MEIER = "Meier"; // A keyword that matches MEIER
    public static final String KEYWORD_MATCHING_KUNZ = "Kunz"; // A keyword that matches KUNZ

    private TypicalPersons() {} // prevents instantiation

    /**
     * Returns an {@code AddressBook} with all the typical persons.
     */
    public static AddressBook getTypicalAddressBook() {
        AddressBook ab = new AddressBook();
        for (ReadOnlyPerson person : getTypicalPersons()) {
            try {
                ab.addPerson(person);
            } catch (DuplicatePersonException e) {
                assert false : "not possible";
            }
        }
        return ab;
    }

    public static List<ReadOnlyPerson> getTypicalPersons() {
        return new ArrayList<>(Arrays.asList(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE));
    }
}
