package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.address.commons.core.ComponentManager;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.model.AddressBookChangedEvent;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonDefaultComparator;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.tag.Tag;

/**
 * Represents the in-memory model of the address book data.
 * All changes to any model should be synchronized.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final SortedList<ReadOnlyPerson> sortedPersons;
    private final FilteredList<ReadOnlyPerson> filteredPersons;

    private Predicate<ReadOnlyPerson> lastFilterPredicate;
    private Comparator<ReadOnlyPerson> lastSortComparator;

    //@@author marvinchin
    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, UserPrefs userPrefs) {
        super();
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        sortedPersons = new SortedList<>(this.addressBook.getPersonList());
        filteredPersons = new FilteredList<>(sortedPersons);
        // To avoid having to re-sort upon every change in filter, we first sort the list before applying the filter
        // This was we only need to re-sort when there is a change in the backing person list
        sortPersons(new PersonDefaultComparator());
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }
    //@@author

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    @Override
    public void resetData(ReadOnlyAddressBook newData) {
        addressBook.resetData(newData);
        indicateAddressBookChanged();
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    /** Raises an event to indicate the model has changed */
    private void indicateAddressBookChanged() {
        raise(new AddressBookChangedEvent(addressBook));
    }

    @Override
    public synchronized void deletePerson(ReadOnlyPerson target) throws PersonNotFoundException {
        addressBook.removePerson(target);
        indicateAddressBookChanged();
    }

    @Override
    public synchronized void addPerson(ReadOnlyPerson person) throws DuplicatePersonException {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        indicateAddressBookChanged();
    }

    //@@author marvinchin
    @Override
    public synchronized void addPersons(Collection<ReadOnlyPerson> persons) {
        for (ReadOnlyPerson person : persons) {
            try {
                addressBook.addPerson(person);
            } catch (DuplicatePersonException e) {
                logger.info("Person already in address book: " + person.toString());
                continue;
            }
        }
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        indicateAddressBookChanged();
    }

    //@@author sarahnzx
    @Override
    public void removeTag(Tag tag) throws PersonNotFoundException, DuplicatePersonException {
        for (int i = 0; i < addressBook.getPersonList().size(); i++) {
            ReadOnlyPerson oldPerson = addressBook.getPersonList().get(i);
            Person newPerson = new Person(oldPerson);
            Set<Tag> newTags = newPerson.getTags();
            newTags.remove(tag);
            newPerson.setTags(newTags);
            addressBook.updatePerson(oldPerson, newPerson);
        }
    }
    //@@author

    @Override
    public void updatePerson(ReadOnlyPerson target, ReadOnlyPerson editedPerson)
            throws DuplicatePersonException, PersonNotFoundException {
        requireAllNonNull(target, editedPerson);
        indicatePersonAccessed(target);
        addressBook.updatePerson(target, editedPerson);
        indicateAddressBookChanged();
    }

    //@@author keithsoc
    @Override
    public void toggleFavoritePerson(ReadOnlyPerson target, String type)
            throws DuplicatePersonException, PersonNotFoundException {
        requireAllNonNull(target, type);
        addressBook.toggleFavoritePerson(target, type);
        indicateAddressBookChanged();
    }

    //@@author marvinchin
    @Override
    public void selectPerson(ReadOnlyPerson target) throws PersonNotFoundException {
        indicatePersonAccessed(target);
        //TODO(Marvin): Since IO operations are expensive, consider if we can defer this operation instead of saving
        // on every access (which includes select)
        indicateAddressBookChanged();
    }

    private void indicatePersonAccessed(ReadOnlyPerson target) throws PersonNotFoundException {
        addressBook.indicatePersonAccessed(target);
    }
    //@@author

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code ReadOnlyPerson} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<ReadOnlyPerson> getFilteredPersonList() {
        return FXCollections.unmodifiableObservableList(filteredPersons);
    }

    @Override
    public void updateFilteredPersonList(Predicate<ReadOnlyPerson> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
        lastFilterPredicate = predicate;
    }

    //@@author marvinchin
    @Override
    public Model makeCopy() {
        // initialize new UserPrefs for now as address book doesn't make use of it
        ModelManager copy = new ModelManager(this.getAddressBook(), new UserPrefs());
        copy.sortPersons(lastSortComparator);
        copy.updateFilteredPersonList(lastFilterPredicate);

        return copy;
    }

    @Override
    public void sortPersons(Comparator<ReadOnlyPerson> comparator) {
        sortedPersons.setComparator(comparator);
        lastSortComparator = comparator;
    }
    //@@author

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return addressBook.equals(other.addressBook)
                && filteredPersons.equals(other.filteredPersons);
    }

}
