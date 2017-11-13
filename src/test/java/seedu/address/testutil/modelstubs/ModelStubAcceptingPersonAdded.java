package seedu.address.testutil.modelstubs;

import java.util.ArrayList;
import java.util.Collection;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;

//@@author marvinchin
/**
 * A {@code Model} stub that always accept the {@code Person} being added.
 */
public class ModelStubAcceptingPersonAdded extends ModelStub {
    public final ArrayList<Person> personsAdded = new ArrayList<>();

    @Override
    public void addPerson(ReadOnlyPerson person) throws DuplicatePersonException {
        personsAdded.add(new Person(person));
    }

    @Override
    public void addPersons(Collection<ReadOnlyPerson> persons) {
        for (ReadOnlyPerson person : persons) {
            personsAdded.add(new Person(person));
        }
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return new AddressBook();
    }
}
