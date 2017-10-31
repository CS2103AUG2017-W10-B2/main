package seedu.address.logic.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.testutil.TypicalPersons.HOON;
import static seedu.address.testutil.TypicalPersons.IDA;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.storage.AddressBookStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.storage.XmlAddressBookStorage;
import seedu.address.testutil.modelstubs.ModelStub;
import seedu.address.testutil.modelstubs.ModelStubAcceptingPersonAdded;

//@@author marvinchin
public class ImportCommandTest {
    private static final String TEST_DATA_FOLDER = Paths.get("src/test/data/ImportCommandTest")
            .toAbsolutePath().toString() + File.separator;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // we can use null as a file path as we will not be using the instance file path
    private AddressBookStorage addressBookStorage = new XmlAddressBookStorage(null);
    private Storage storage = new StorageManager(addressBookStorage, null);

    @Test
    public void execute_validExportData_success() throws Exception {
        ModelStubAcceptingPersonAdded model = new ModelStubAcceptingPersonAdded();
        String absoluteImportFilePath = TEST_DATA_FOLDER + "validXmlExportData.xml";
        ImportCommand importCommand = prepareCommand(model, absoluteImportFilePath);

        CommandResult commandResult = importCommand.execute();

        String expectedMessage = String.format(ImportCommand.MESSAGE_IMPORT_CONTACTS_SUCCESS, absoluteImportFilePath);
        assertEquals(expectedMessage, commandResult.feedbackToUser);

        ArrayList<Person> expectedPersonsAdded = new ArrayList<>(Arrays.asList(new Person(HOON), new Person(IDA)));
        assertEquals(expectedPersonsAdded, model.personsAdded);
    }

    @Test
    public void execute_invalidExportData_throwsCommandException() throws Exception {
        ModelStub model = new ModelStub();
        String absoluteImportFilePath = TEST_DATA_FOLDER + "invalidExportData.txt";
        ImportCommand importCommand = prepareCommand(model, absoluteImportFilePath);

        thrown.expect(CommandException.class);
        thrown.expectMessage(String.format(ImportCommand.MESSAGE_IMPORT_CONTACTS_DCE_FAILURE, absoluteImportFilePath));

        importCommand.execute();
    }

    @Test
    public void execute_wrongFilePath_throwsCommandException() throws Exception {
        ModelStub model = new ModelStub();
        String absoluteImportFilePath = TEST_DATA_FOLDER + "nonexistentExportData.txt";
        ImportCommand importCommand = prepareCommand(model, absoluteImportFilePath);

        thrown.expect(CommandException.class);
        thrown.expectMessage(String.format(ImportCommand.MESSAGE_IMPORT_CONTACTS_FNF_FAILURE, absoluteImportFilePath));

        importCommand.execute();
    }

    @Test
    public void equals() {
        String someValidFilePath = TEST_DATA_FOLDER + "exported-data.xml";
        String anotherValidFilePath = TEST_DATA_FOLDER  + "more-exported-data.xml";
        ImportCommand importToSomeValidFilePathCommand = new ImportCommand(someValidFilePath);
        ImportCommand importToAnotherValidFilePathCommand = new ImportCommand(anotherValidFilePath);

        // same object -> returns true
        assertTrue(importToSomeValidFilePathCommand.equals(importToSomeValidFilePathCommand));

        // same values -> returns true
        ImportCommand importToSomeValidFilePathCommandCopy = new ImportCommand(someValidFilePath);
        assertTrue(importToSomeValidFilePathCommand.equals(importToSomeValidFilePathCommandCopy));

        // different types -> returns false
        assertFalse(importToSomeValidFilePathCommand.equals(1));

        // null -> returns false
        assertFalse(importToSomeValidFilePathCommand.equals(null));

        //different value -> returns false
        assertFalse(importToSomeValidFilePathCommand.equals(importToAnotherValidFilePathCommand));
    }

    private ImportCommand prepareCommand(Model model, String filePath) {
        ImportCommand importCommand = new ImportCommand(filePath);
        importCommand.setData(model, storage, new CommandHistory(), new UndoRedoStack());
        return importCommand;
    }

}
