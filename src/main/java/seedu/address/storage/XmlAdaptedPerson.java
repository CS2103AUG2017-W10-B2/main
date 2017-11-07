package seedu.address.storage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

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

/**
 * JAXB-friendly version of the Person.
 */
public class XmlAdaptedPerson {

    @XmlElement(required = true)
    private String name;
    @XmlElement(required = true)
    private String phone;
    @XmlElement(required = true)
    private String email;
    @XmlElement(required = true)
    private String address;
    @XmlElement
    private boolean favorite;
    @XmlElement
    private String displayPhoto;
    @XmlElement
    private List<XmlAdaptedTag> tagged = new ArrayList<>();
    @XmlElement
    private List<XmlAdaptedSocialInfo> addedSocialInfos = new ArrayList<>();
    @XmlElement(required = true)
    private long lastAccessDateEpoch;

    /**
     * Constructs an XmlAdaptedPerson.
     * This is the no-arg constructor that is required by JAXB.
     */
    public XmlAdaptedPerson() {}


    /**
     * Converts a given Person into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created XmlAdaptedPerson
     */
    public XmlAdaptedPerson(ReadOnlyPerson source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        address = source.getAddress().value;
        favorite = source.getFavorite().isFavorite();
        displayPhoto = source.getDisplayPhoto().value;
        tagged = new ArrayList<>();
        for (Tag tag : source.getTags()) {
            tagged.add(new XmlAdaptedTag(tag));
        }
        addedSocialInfos = new ArrayList<>();
        for (SocialInfo socialInfo : source.getSocialInfos()) {
            addedSocialInfos.add(new XmlAdaptedSocialInfo(socialInfo));
        }
        LastAccessDate lastAccessDate = source.getLastAccessDate();
        lastAccessDateEpoch = lastAccessDate.getDate().getTime();
    }

    /**
     * Converts this jaxb-friendly adapted person object into the model's Person object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (XmlAdaptedTag tag : tagged) {
            personTags.add(tag.toModelType());
        }
        final List<SocialInfo> personSocialInfos = new ArrayList<>();
        for (XmlAdaptedSocialInfo socialInfo : addedSocialInfos) {
            personSocialInfos.add(socialInfo.toModelType());
        }
        final Name name = new Name(this.name);
        final Phone phone = new Phone(this.phone);
        final Email email = new Email(this.email);
        final Address address = new Address(this.address);
        final Favorite favorite = new Favorite(this.favorite);
        final DisplayPhoto displayPhoto = new DisplayPhoto(this.displayPhoto);
        final Set<Tag> tags = new HashSet<>(personTags);
        final Set<SocialInfo> socialInfos = new HashSet<>(personSocialInfos);
        final LastAccessDate lastAccessDate = new LastAccessDate(new Date(lastAccessDateEpoch));
        return new Person(name, phone, email, address, favorite, displayPhoto, tags, socialInfos, lastAccessDate);
    }
}
