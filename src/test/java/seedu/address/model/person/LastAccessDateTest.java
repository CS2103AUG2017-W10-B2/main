package seedu.address.model.person;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

//@@author marvinchin
public class LastAccessDateTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void constructor_nullDate_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        new LastAccessDate(null);
    }

    @Test
    public void constructor_validDate_success() {
        Date date = new Date(1000);
        LastAccessDate lastAccessDate = new LastAccessDate(date);
        // string value of last access date should be equal to string value of date
        String originalDateString = date.toString();
        assertEquals(originalDateString, lastAccessDate.toString());

        // last access date should not be mutated when the original date is changed
        date.setTime(2000);
        assertEquals(originalDateString, lastAccessDate.toString());
    }

    @Test
    public void getDate_mutateReturnedDate_isNotMutated() {
        Date originalDate = new Date(1000);
        LastAccessDate lastAccessDate = new LastAccessDate(originalDate);
        Date date = lastAccessDate.getDate();
        // returned date value should be same as original date value
        assertEquals(originalDate, lastAccessDate.getDate());

        date.setTime(2000);
        // date value stored in get access date should not have been mutated
        assertEquals(originalDate, lastAccessDate.getDate());
    }

    @Test
    public void equals() {
        LastAccessDate lastAccessDateOne = new LastAccessDate(new Date(1000));
        LastAccessDate lastAccessDateTwo = new LastAccessDate(new Date(2000));

        // same object -> returns true
        assertTrue(lastAccessDateOne.equals(lastAccessDateOne));

        // same value -> returns true
        LastAccessDate lastAccessDateOneCopy = new LastAccessDate(new Date(1000));
        assertTrue(lastAccessDateOneCopy.equals(lastAccessDateOne));

        // different types -> returns false
        assertFalse(lastAccessDateOne.equals(new Date(1000)));

        // null -> returns false
        assertFalse(lastAccessDateOne.equals(null));

        // different last access date -> returns false
        assertFalse(lastAccessDateOne.equals(lastAccessDateTwo));
    }
}
