package seedu.address.model.person;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import seedu.address.commons.exceptions.IllegalValueException;

//@@author keithsoc
/**
 * Represents a Person's Display Photo in the address book.
 * Guarantees: immutability and validity.
 */
public class DisplayPhoto {

    public static final String MESSAGE_PHOTO_CONSTRAINTS = "Display photo: "
            + "specified file does not exist or it exceeded maximum size of 1MB.";

    /* Display photos for sample persons in SampleDataUtil */
    public static final String SAMPLE_PHOTO_ALEX = "/images/sample_dp_one.png";

    private static final String DEFAULT_SAVE_DIR = "data/";
    private static final String ALLOWED_TYPE_JPG = ".jpg";
    private static final String ALLOWED_TYPE_JPEG = ".jpeg";
    private static final String ALLOWED_TYPE_PNG = ".png";
    private static final int MAX_SIZE = 1000000; // Sets allowable maximum display photo size to 1MB

    public final String value;

    /**
     * Validates given Display Photo.
     *
     * @throws IllegalValueException if given display photo string is invalid.
     */
    public DisplayPhoto(String photoPath) throws IllegalValueException {
        // Allow null values
        if (photoPath == null || photoPath.isEmpty()) {
            this.value = null;
        } else if (photoPath.equals(SAMPLE_PHOTO_ALEX)) { // Display photos for sample persons
            this.value = photoPath;
        } else {
            String trimmedPhotoPath = photoPath.trim();
            if (!isValidFile(trimmedPhotoPath)) {
                throw new IllegalValueException(MESSAGE_PHOTO_CONSTRAINTS);
            } else {
                File from = new File(trimmedPhotoPath);
                this.value = DEFAULT_SAVE_DIR + from.getName();
                Path to = Paths.get(this.value);
                try {
                    // Copy file to user's "data" directory.
                    // If the target file exists, then the target file is replaced if it is not a non-empty directory.
                    Files.copy(from.toPath(), to, REPLACE_EXISTING);
                } catch (IOException io) {
                    throw new IllegalValueException(io.toString());
                }
            }
        }
    }

    /**
     * Returns if a given string is a valid person display photo file and of correct size.
     */
    public static boolean isValidFile(String test) {
        File file = new File(test);
        return file.exists()
                && file.length() <= MAX_SIZE
                && (test.endsWith(ALLOWED_TYPE_JPG)
                || test.endsWith(ALLOWED_TYPE_JPEG)
                || test.endsWith(ALLOWED_TYPE_PNG));
    }

    /**
     * Returns the absolute file path for user-specified display photos.
     */
    public String getAbsoluteFilePath() {
        if (value.equals(SAMPLE_PHOTO_ALEX)) {
            return value;
        } else {
            return "file://" + Paths.get(value).toAbsolutePath().toUri().getPath();
        }
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DisplayPhoto // instanceof handles nulls
                && this.value.equals(((DisplayPhoto) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
//@@author
