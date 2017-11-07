package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import seedu.address.model.social.SocialInfo;
import seedu.address.model.social.UniqueSocialInfoList;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.UniqueTagList;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated.
 */
public class Person implements ReadOnlyPerson {

    private ObjectProperty<Name> name;
    private ObjectProperty<Phone> phone;
    private ObjectProperty<Email> email;
    private ObjectProperty<Address> address;
    private ObjectProperty<Favorite> favorite;
    private ObjectProperty<DisplayPhoto> displayPhoto;
    private ObjectProperty<UniqueTagList> tags;
    private ObjectProperty<UniqueSocialInfoList> socialInfos;
    private ObjectProperty<LastAccessDate> lastAccessDate;

    /**
     * Every field must be present but can be null except 'name'.
     */
    public Person(Name name, Phone phone, Email email, Address address, Favorite favorite, DisplayPhoto displayPhoto,
                  Set<Tag> tags, Set<SocialInfo> socialInfos) {
        requireAllNonNull(name, phone, email, address, tags, socialInfos);
        this.name = new SimpleObjectProperty<>(name);
        this.phone = new SimpleObjectProperty<>(phone);
        this.email = new SimpleObjectProperty<>(email);
        this.address = new SimpleObjectProperty<>(address);
        this.favorite = new SimpleObjectProperty<>(favorite);
        this.displayPhoto = new SimpleObjectProperty<>(displayPhoto);
        // protect internal tags from changes in the arg list
        this.tags = new SimpleObjectProperty<>(new UniqueTagList(tags));
        this.socialInfos = new SimpleObjectProperty<>(new UniqueSocialInfoList(socialInfos));
        // set the last access date to now
        this.lastAccessDate = new SimpleObjectProperty<>(new LastAccessDate());
    }

    public Person(Name name, Phone phone, Email email, Address address, Favorite favorite,
                  DisplayPhoto displayPhoto, Set<Tag> tags, Set<SocialInfo> socialInfos,
                  LastAccessDate lastAccessDate) {
        this(name, phone, email, address, favorite, displayPhoto, tags, socialInfos);
        requireNonNull(lastAccessDate);
        this.lastAccessDate = new SimpleObjectProperty<>(lastAccessDate);
    }

    /**
     * Creates a copy of the given ReadOnlyPerson.
     */
    public Person(ReadOnlyPerson source) {
        this(source.getName(), source.getPhone(), source.getEmail(), source.getAddress(), source.getFavorite(),
                source.getDisplayPhoto(), source.getTags(), source.getSocialInfos(), source.getLastAccessDate());
    }

    public void setName(Name name) {
        this.name.set(requireNonNull(name));
    }

    @Override
    public ObjectProperty<Name> nameProperty() {
        return name;
    }

    @Override
    public Name getName() {
        return name.get();
    }

    public void setPhone(Phone phone) { this.phone.set(phone); }

    @Override
    public ObjectProperty<Phone> phoneProperty() {
        return phone;
    }

    @Override
    public Phone getPhone() {
        return phone.get();
    }

    public void setEmail(Email email) { this.email.set(email); }

    @Override
    public ObjectProperty<Email> emailProperty() {
        return email;
    }

    @Override
    public Email getEmail() {
        return email.get();
    }

    public void setAddress(Address address) { this.address.set(address); }

    @Override
    public ObjectProperty<Address> addressProperty() {
        return address;
    }

    @Override
    public Address getAddress() {
        return address.get();
    }

    //@@author keithsoc
    public void setFavorite(Favorite favorite) { this.favorite.set(requireNonNull(favorite)); }

    @Override
    public ObjectProperty<Favorite> favoriteProperty() {
        return favorite;
    }

    @Override
    public Favorite getFavorite() {
        return favorite.get();
    }

    public void setDisplayPhoto(DisplayPhoto displayPhoto) {
        this.displayPhoto.set(requireNonNull(displayPhoto));
    }

    @Override
    public ObjectProperty<DisplayPhoto> displayPhotoProperty() {
        return displayPhoto;
    }

    @Override
    public DisplayPhoto getDisplayPhoto() {
        return displayPhoto.get();
    }
    //@@author

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    @Override
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags.get().toSet());
    }

    public ObjectProperty<UniqueTagList> tagProperty() {
        return tags;
    }

    /**
     * Replaces this person's tags with the tags in the argument tag set.
     */
    public void setTags(Set<Tag> replacement) {
        tags.set(new UniqueTagList(replacement));
    }

    //@@author marvinchin
    @Override
    public ObjectProperty<UniqueSocialInfoList> socialInfoProperty() {
        return socialInfos;
    }

    @Override
    public Set<SocialInfo> getSocialInfos() {
        return Collections.unmodifiableSet(socialInfos.get().toSet());
    }

    public void setSocialInfos(Set<SocialInfo> replacement) {
        socialInfos.set(new UniqueSocialInfoList(replacement));
    }

    @Override
    public ObjectProperty<LastAccessDate> lastAccessDateProperty() {
        return lastAccessDate;
    }

    @Override
    public LastAccessDate getLastAccessDate() {
        return lastAccessDate.get();
    }

    public void setLastAccessDate(LastAccessDate replacement) {
        lastAccessDate.set(replacement);
    }

    public void setLastAccessDateToNow() {
        setLastAccessDate(new LastAccessDate());
    }
    //@@author

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ReadOnlyPerson // instanceof handles nulls
                && this.isSameStateAs((ReadOnlyPerson) other));
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, favorite, tags);
    }

    @Override
    public String toString() {
        return getAsText();
    }

}
