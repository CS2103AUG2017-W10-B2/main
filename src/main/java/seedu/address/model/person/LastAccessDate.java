package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Date;

//@@author marvinchin
/**
 * Represents the last time a person is accessed.
 * Guarantees immutability.
 */
public class LastAccessDate implements Comparable<LastAccessDate> {
    private Date lastAccessDate;

    /**
     * Constructs a new LastAccessDate with the date set to the current date.
     */
    public LastAccessDate() {
        lastAccessDate = new Date();
    }

    /**
     * Constructs a new LastAccessDate with the date equivalent to the date.
     */
    public LastAccessDate(Date date) {
        requireNonNull(date);
        // save a copy instead of using input date directly to avoid reference to external objects that can be mutated
        lastAccessDate = copyDate(date);
    }

    public Date getDate() {
        // returns a copy of the date so that the internal date cannot be mutated by external methods
        return copyDate(lastAccessDate);
    }

    @Override
    public String toString() {
        return lastAccessDate.toString();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof LastAccessDate // instanceof handles nulls
                && this.lastAccessDate.equals(((LastAccessDate) other).lastAccessDate));
    }

    /**
     * Utility method to create a copy of the input date
     */
    private Date copyDate(Date originalDate) {
        // clone using constructor instead of clone method due to vulnerabilities in the clone method
        // see https://stackoverflow.com/questions/7082553/java-util-date-clone-or-copy-to-not-expose-internal-reference
        Date copiedDate = new Date(originalDate.getTime());
        return copiedDate;
    }

    @Override
    public int compareTo(LastAccessDate other) {
        return this.lastAccessDate.compareTo(other.lastAccessDate);
    }
}
