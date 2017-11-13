package seedu.address.testutil.modelstubs;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;

//@@author marvinchin
/**
 * A {@code Model} stub that always throw a {@code DuplicatePersonException} when trying to add a {@code Person}.
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
