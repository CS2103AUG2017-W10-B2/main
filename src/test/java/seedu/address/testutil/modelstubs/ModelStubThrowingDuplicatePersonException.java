package seedu.address.testutil.modelstubs;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;

//@@author marvinchin
/**
 * A Model stub that always throw a DuplicatePersonException when trying to add a person.
 */
public class ModelStubThrowingDuplicatePersonException extends ModelStub {
    @Override
    public void addPerson(ReadOnlyPerson person) throws DuplicatePersonException {
        throw new DuplicatePersonException();
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return new AddressBook();
    }
}
