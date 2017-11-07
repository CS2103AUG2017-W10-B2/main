package seedu.address.testutil;

import static seedu.address.logic.parser.SocialInfoMapping.parseSocialInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Address;
import seedu.address.model.person.DisplayPhoto;
import seedu.address.model.person.Email;
import seedu.address.model.person.Favorite;
import seedu.address.model.person.LastAccessDate;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.social.SocialInfo;
import seedu.address.model.tag.Tag;
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "Alice Pauline";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "alice@gmail.com";
    public static final String DEFAULT_ADDRESS = "123, Jurong West Ave 6, #08-111";
    public static final boolean DEFAULT_FAVORITE = true;
    public static final String DEFAULT_DISPLAY_PHOTO = null;
    public static final String DEFAULT_TAGS = "friends";
    public static final SocialInfo DEFAULT_SOCIAL =
            new SocialInfo("facebook", "default", "https://facebook.com/default");

    private Person person;

    public PersonBuilder() {
        try {
            Name defaultName = new Name(DEFAULT_NAME);
            Phone defaultPhone = new Phone(DEFAULT_PHONE);
            Email defaultEmail = new Email(DEFAULT_EMAIL);
            Address defaultAddress = new Address(DEFAULT_ADDRESS);
            Favorite defaultFavoriteStatus = new Favorite(DEFAULT_FAVORITE);
            DisplayPhoto defaultDisplayPhoto = new DisplayPhoto(DEFAULT_DISPLAY_PHOTO);
            Set<Tag> defaultTags = SampleDataUtil.getTagSet(DEFAULT_TAGS);
            Set<SocialInfo> defaultSocialInfos = SampleDataUtil.getSocialInfoSet(DEFAULT_SOCIAL);
            this.person = new Person(defaultName, defaultPhone, defaultEmail, defaultAddress,
                    defaultFavoriteStatus, defaultDisplayPhoto, defaultTags, defaultSocialInfos);
        } catch (IllegalValueException ive) {
            throw new AssertionError("Default person's values are invalid.");
        }
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(ReadOnlyPerson personToCopy) {
        this.person = new Person(personToCopy);
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        try {
            this.person.setName(new Name(name));
        } catch (IllegalValueException ive) {
            throw new IllegalArgumentException("name is expected to be unique.");
        }
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the {@code Person} that we are building.
     */
    public PersonBuilder withTags(String ... tags) {
        try {
            this.person.setTags(SampleDataUtil.getTagSet(tags));
        } catch (IllegalValueException ive) {
            // Note(Marvin): The exception here is actually thrown when the tag does not conform to the
            // constraints set for tags (alphanumeric).
            throw new IllegalArgumentException("tags are expected to be unique.");
        }
        return this;
    }

    //@@author marvinchin
    /**
     * Parses the {@code socialInfos} into a {@code Set<SocialInfo} and set it to the {@code Person}
     * that we are building.
     */
    public PersonBuilder withSocialInfos(String... rawSocialInfos) {
        try {
            ArrayList<SocialInfo> socialInfos = new ArrayList<>();
            for (String rawSocialInfo : rawSocialInfos) {
                socialInfos.add(parseSocialInfo(rawSocialInfo));
            }
            // convert to array to be passed as varargs in getSocialInfoSet
            SocialInfo[] socialInfosArray = socialInfos.toArray(new SocialInfo[rawSocialInfos.length]);
            this.person.setSocialInfos(SampleDataUtil.getSocialInfoSet(socialInfosArray));
        } catch (IllegalValueException ive) {
            throw new IllegalArgumentException("raw social infos must be valid.");
        }
        return this;
    }

    /**
     * Sets the {@code LastAccessDate} of the {@code Person} that we are building.
     */
    public PersonBuilder withLastAccessDate(Date date) {
        LastAccessDate lastAccessDate = new LastAccessDate(date);
        this.person.setLastAccessDate(lastAccessDate);

        return this;

    }
    //@@author

    /**
     * Sets the {@code Address} of the {@code Person} that we are building.
     */
    public PersonBuilder withAddress(String address) {
        try {
            this.person.setAddress(new Address(address));
        } catch (IllegalValueException ive) {
            throw new IllegalArgumentException("address is expected to be unique.");
        }
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        try {
            this.person.setPhone(new Phone(phone));
        } catch (IllegalValueException ive) {
            throw new IllegalArgumentException("phone is expected to be unique.");
        }
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Person} that we are building.
     */
    public PersonBuilder withEmail(String email) {
        try {
            this.person.setEmail(new Email(email));
        } catch (IllegalValueException ive) {
            throw new IllegalArgumentException("email is expected to be unique.");
        }
        return this;
    }

    //@@author keithsoc
    /**
     * Sets the {@code Favorite} of the {@code Person} that we are building.
     */
    public PersonBuilder withFavorite(boolean favorite) {
        this.person.setFavorite(new Favorite(favorite));
        return this;
    }

    /**
     * Sets the {@code DisplayPhoto} of the {@code Person} that we are building.
     */
    public PersonBuilder withDisplayPhoto(String displayPhoto) {
        try {
            this.person.setDisplayPhoto(new DisplayPhoto(displayPhoto));
        } catch (IllegalValueException ive) {
            throw new IllegalArgumentException("display photo file does not exist or it exceeded maximum size of 1MB");
        }
        return this;
    }
    //@@author

    public Person build() {
        return this.person;
    }

}
