package seedu.address.logic.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.storage.AddressBookStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.storage.XmlAddressBookStorage;

//@@author marvinchin
public class ExportCommandTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private AddressBook addressBook = getTypicalAddressBook();
    private Model model = new ModelManager(addressBook, new UserPrefs());
    // we can use null as a file path as we will not be using the instance file path
    private AddressBookStorage addressBookStorage = new XmlAddressBookStorage(null);
    private Storage storage = new StorageManager(addressBookStorage, null);

    @Test
    public void execute_validFilePath_success() throws Exception {
        String validFilePath = testFolder.getRoot().getPath() + "exportedData.xml";
        // if the file already exists, delete it
        Files.deleteIfExists(Paths.get(validFilePath));

        ExportCommand exportCommand = prepareCommand(validFilePath);

        String expectedMessage = String.format(ExportCommand.MESSAGE_EXPORT_CONTACTS_SUCCESS, validFilePath);
        Model expectedModel = model;
        assertCommandSuccess(exportCommand, model, expectedMessage, expectedModel);

        // check that the written file is correct
        ReadOnlyAddressBook readBack = storage.readAddressBook(validFilePath).get();
        ReadOnlyAddressBook readBackAddressBook = new AddressBook(readBack);
        assertEquals(addressBook, readBackAddressBook);
    }

    @Test
    public void equals() {
        String someValidFilePath = testFolder.getRoot().getPath() + "exported-data.xml";
        String anotherValidFilePath = testFolder.getRoot().getPath()  + "more-exported-data.xml";
        ExportCommand exportToSomeValidFilePathCommand = new ExportCommand(someValidFilePath);
        ExportCommand exportToAnotherValidFilePathCommand = new ExportCommand(anotherValidFilePath);

        // same object -> returns true
        assertTrue(exportToSomeValidFilePathCommand.equals(exportToSomeValidFilePathCommand));

        // same values -> returns true
        ExportCommand exportToSomeValidFilePathCommandCopy = new ExportCommand(someValidFilePath);
        assertTrue(exportToSomeValidFilePathCommand.equals(exportToSomeValidFilePathCommandCopy));

        // different types -> returns false
        assertFalse(exportToSomeValidFilePathCommand.equals(1));

        // null -> returns false
        assertFalse(exportToSomeValidFilePathCommand.equals(null));

        //different value -> returns false
        assertFalse(exportToSomeValidFilePathCommand.equals(exportToAnotherValidFilePathCommand));
    }

    private ExportCommand prepareCommand(String filePath) {
        ExportCommand exportCommand = new ExportCommand(filePath);
        exportCommand.setData(model, storage, new CommandHistory(), new UndoRedoStack());
        return exportCommand;
    }
}
