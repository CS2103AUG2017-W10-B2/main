package seedu.address.model.person;

import java.io.File;
import java.io.FileNotFoundException;

import seedu.address.commons.exceptions.IllegalValueException;

//@@author keithsoc
/**
 * Represents a Person's Profile Photo in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPath(String)} and {@link #isValidFile(String)}
 */
public class ProfilePhoto {

    public static final String MESSAGE_PHOTO_INVALID_PATH = "File path is invalid.";
    public static final String MESSAGE_PHOTO_INVALID_FILE = "File does not exist.";
    public static final String PHOTO_VALIDATION_REGEX = "([a-zA-Z]:)?(\\\\\\\\[a-zA-Z0-9_.-]+)+\\\\\\\\?";

    public final String value;

    /**
     * Validates given profile photo.
     *
     * @throws IllegalValueException if given profile photo string is invalid.
     */
    public ProfilePhoto(String photoPath) throws IllegalValueException, FileNotFoundException {
        String trimmedPhotoPath = photoPath.trim();
        if (!isValidPath(trimmedPhotoPath)) {
            throw new IllegalValueException(MESSAGE_PHOTO_INVALID_PATH);
        }
        if (!isValidFile(trimmedPhotoPath)) {
            throw new FileNotFoundException(MESSAGE_PHOTO_INVALID_FILE);
        }
        this.value = trimmedPhotoPath;
    }

    /**
     * Returns if a given string is a valid person profile photo path.
     */
    public static boolean isValidPath(String test) {
        return test.matches(PHOTO_VALIDATION_REGEX);
    }

    /**
     * Returns if a given string is a valid person profile photo file.
     */
    public static boolean isValidFile(String test) {
        File file = new File(test);
        return file.exists();
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ProfilePhoto // instanceof handles nulls
                && this.value.equals(((ProfilePhoto) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
//@@author
