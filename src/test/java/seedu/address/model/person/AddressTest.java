package seedu.address.model.person;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AddressTest {

    @Test
    public void isValidAddress() {
        // valid addresses
        assertTrue(Address.isValidAddress("Blk 456, Den Road, #01-355"));
        assertTrue(Address.isValidAddress("-")); // one character
        assertTrue(Address.isValidAddress("Leng Inc; 1234 Market St; San Francisco CA 2349879; USA")); // long address
    }
}
