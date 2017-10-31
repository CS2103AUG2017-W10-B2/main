package seedu.address.testutil;

import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;

//@@author marvinchin
/**
 * Utility class for storage utilities
 */
public class StorageUtil {

    /**
     * Returns a null storage for tests where storage does not need to be used
     */
    public static Storage getNullStorage() {
        return new StorageManager(null, null);
    }
}
