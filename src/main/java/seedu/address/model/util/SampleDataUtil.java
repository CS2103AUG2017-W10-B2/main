package seedu.address.model.util;

import java.util.HashSet;
import java.util.Set;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Favorite;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.social.SocialInfo;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    public static Person[] getSamplePersons() {
        try {
            Person alex = new Person(
                    new Name("Alex Yeoh"),
                    new Phone("87438807"),
                    new Email("alexyeoh@example.com"),
                    new Address("Blk 30 Geylang Street 29, #06-40"),
                    new Favorite(true),
                    getTagSet("friends"),
                    getSocialInfoSet(
                            new SocialInfo("facebook", "alex", "https://facebook.com/alex")));
            Person bernice = new Person(
                    new Name("Bernice Yu"),
                    new Phone("99272758"),
                    new Email("berniceyu@example.com"),
                    new Address("Blk 30 Lorong 3 Serangoon Gardens, #07-18"),
                    new Favorite(false),
                    getTagSet("colleagues", "friends"),
                    getSocialInfoSet(
                            new SocialInfo("facebook", "bernice", "https://facebook.com/bernice"),
                            new SocialInfo("instagram", "bernice", "https://instagram.com/bernice")));
            Person charlotte = new Person(
                    new Name("Charlotte Oliveiro"),
                    new Phone("93210283"),
                    new Email("charlotte@example.com"),
                    new Address("Blk 11 Ang Mo Kio Street 74, #11-04"),
                    new Favorite(false),
                    getTagSet("neighbours"),
                    getSocialInfoSet());
            Person david = new Person(
                    new Name("David Li"),
                    new Phone("91031282"),
                    new Email("lidavid@example.com"),
                    new Address("Blk 436 Serangoon Gardens Street 26, #16-43"),
                    new Favorite(true),
                    getTagSet("family"),
                    getSocialInfoSet());
            Person irfan = new Person(
                    new Name("Irfan Ibrahim"),
                    new Phone("92492021"),
                    new Email("irfan@example.com"),
                    new Address("Blk 47 Tampines Street 20, #17-35"),
                    new Favorite(false),
                    getTagSet("classmates"),
                    getSocialInfoSet());
            Person roy = new Person(
                    new Name("Roy Balakrishnan"),
                    new Phone("92624417"),
                    new Email("royb@example.com"),
                    new Address("Blk 45 Aljunied Street 85, #11-31"),
                    new Favorite(false),
                    getTagSet("colleagues"),
                    getSocialInfoSet());

            return new Person[] {alex, bernice, charlotte, david, irfan, roy};
        } catch (IllegalValueException e) {
            throw new AssertionError("sample data cannot be invalid", e);
        }
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        try {
            AddressBook sampleAb = new AddressBook();
            for (Person samplePerson : getSamplePersons()) {
                sampleAb.addPerson(samplePerson);
            }
            return sampleAb;
        } catch (DuplicatePersonException e) {
            throw new AssertionError("sample data cannot contain duplicate persons", e);
        }
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) throws IllegalValueException {
        HashSet<Tag> tags = new HashSet<>();
        for (String s : strings) {
            tags.add(new Tag(s));
        }
        return tags;
    }

    /**
     * Returns a set containing the list of SocialInfo given
     */
    public static Set<SocialInfo> getSocialInfoSet(SocialInfo... socialInfos) {
        HashSet<SocialInfo> socialInfoSet = new HashSet<>();
        for (SocialInfo socialInfo : socialInfos) {
            socialInfoSet.add(socialInfo);
        }

        return socialInfoSet;
    }

}
