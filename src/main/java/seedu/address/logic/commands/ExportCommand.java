package seedu.address.logic.commands;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.DisplayPhoto;
import seedu.address.model.person.Person;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.storage.Storage;

//@@author marvinchin
/**
 * Exports existing {@code Person}s to an external XML file.
 */
public class ExportCommand extends Command {

    public static final String COMMAND_WORD = "export";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Exports existing contacts.\n"
            + "Parameters: FILE PATH (must be a valid file path where the current user has write access\n"
            + "Example: " + COMMAND_WORD + " /Users/seedu/Documents/exportedData.xml";

    public static final String MESSAGE_EXPORT_CONTACTS_SUCCESS = "Contacts exported to: %1$s";
    public static final String MESSAGE_EXPORT_CONTACTS_FAILURE = "Unable to export contacts to: %1$s";

    private static final Logger logger = LogsCenter.getLogger(ExportCommand.class);

    private final Path exportFilePath;

    public ExportCommand(String filePath) {
        exportFilePath = Paths.get(filePath);
    }

    @Override
    public CommandResult execute() throws CommandException {
        ReadOnlyAddressBook currentAddressBook = model.getAddressBook();
        ReadOnlyAddressBook exportAddressBook = generateExportAddressBook(currentAddressBook);
        String absoluteExportFilePathString = exportFilePath.toAbsolutePath().toString();
        try {
            storage.saveAddressBook(exportAddressBook, absoluteExportFilePathString);
        } catch (IOException ioe) {
            logger.warning("Error writing to file at: " + absoluteExportFilePathString);
            throw new CommandException(String.format(MESSAGE_EXPORT_CONTACTS_FAILURE, absoluteExportFilePathString));
        }
        return new CommandResult(String.format(MESSAGE_EXPORT_CONTACTS_SUCCESS, absoluteExportFilePathString));
    }

    /**
     * Generates an address book for exporting that is equivalent to the input address book, but with all display
     * pictures removed.
     */
    private ReadOnlyAddressBook generateExportAddressBook(ReadOnlyAddressBook currentAddressBook) {
        AddressBook exportAddressBook = new AddressBook(currentAddressBook);

        for (ReadOnlyPerson person : exportAddressBook.getPersonList()) {
            Person personWithoutDisplayPicture = new Person(person);
            try {
                personWithoutDisplayPicture.setDisplayPhoto(new DisplayPhoto(null));
                exportAddressBook.updatePerson(person, personWithoutDisplayPicture);
            } catch (IllegalValueException ive) {
                assert false : "Display photo should not be invalid";
            } catch (PersonNotFoundException pnfe) {
                assert false : "Person should not be missing";
            }
        }

        return exportAddressBook;
    }

    @Override
    public void setData(Model model, Storage storage, CommandHistory commandHistory, UndoRedoStack undoRedoStack) {
        this.model = model;
        this.storage = storage;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ExportCommand // instanceof handles nulls
                && this.exportFilePath.equals(((ExportCommand) other).exportFilePath)); // state check
    }
}
