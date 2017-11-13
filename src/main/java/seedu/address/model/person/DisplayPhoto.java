package seedu.address.model.person;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.IllegalValueException;

//@@author keithsoc
/**
 * Represents a Person's Display Photo in the address book.
 * Guarantees: immutability and validity.
 */
public class DisplayPhoto {
    // Display photo for sample persons in SampleDataUtil
    public static final String SAMPLE_PHOTO = "/images/sample_dp_one.png";

    private static final Logger logger = LogsCenter.getLogger(DisplayPhoto.class);
    private static final String MESSAGE_PHOTO_CONSTRAINTS = "Display photo: "
            + "specified file does not exist or it exceeded maximum size of 1MB.";
    private static final String MESSAGE_PHOTO_COPY_ERROR = "Error copying photo to application's data directory";
    private static final String DEFAULT_SAVE_DIR = "data" + File.separator;
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
        } else if (isSamplePhoto(photoPath)) {
            this.value = photoPath;
        } else {
            String trimmedPhotoPath = photoPath.trim();
            if (!isValidPhoto(trimmedPhotoPath)) {
                throw new IllegalValueException(MESSAGE_PHOTO_CONSTRAINTS);
            } else {
                File from = new File(trimmedPhotoPath);
                this.value = DEFAULT_SAVE_DIR + from.getName();
                Path to = Paths.get(this.value);
                copyPhotoToDefaultDir(from.toPath(), to);
            }
        }
    }

    /**
     * Returns if a given string is a valid person display photo file and of correct size.
     */
    private static boolean isValidPhoto(String test) {
        File file = new File(test);
        return file.exists()
                && file.length() <= MAX_SIZE
                && (test.endsWith(ALLOWED_TYPE_JPG)
                || test.endsWith(ALLOWED_TYPE_JPEG)
                || test.endsWith(ALLOWED_TYPE_PNG));
    }

    /**
     * Returns if a given string is a path to one of the sample photos
     */
    private static boolean isSamplePhoto(String path) {
        return path.equals(SAMPLE_PHOTO);
    }

    /**
     * Copies the photo from Path {@code from} to Path {@code to}
     * @throws IllegalValueException
     */
    private static void copyPhotoToDefaultDir(Path from, Path to) throws IllegalValueException {
        try {
            // Create the default data directory to store the display photos if it doesn't already exist
            Files.createDirectories(Paths.get(DEFAULT_SAVE_DIR));

            // Copy file to user's "data" directory.
            // If the target file exists, then the target file is replaced if it is not a non-empty directory.
            Files.copy(from, to, REPLACE_EXISTING);
        } catch (IOException io) {
            logger.info("Display photo error: " + io.toString());
            throw new IllegalValueException(MESSAGE_PHOTO_COPY_ERROR);
        }
    }

    /**
     * Returns the absolute file path for user-specified display photos.
     */
    public String getAbsoluteFilePath() {
        if (isSamplePhoto(value)) {
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
