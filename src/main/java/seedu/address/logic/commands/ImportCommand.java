package seedu.address.logic.commands;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.storage.Storage;

//@@author marvinchin
/**
 * Imports contacts from an external XML file
 */
public class ImportCommand extends Command {

    public static final String COMMAND_WORD = "import";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Imports contacts from external file.\n"
            + "Parameters: FILE PATH (must be a path to a valid exported contacts data file\n"
            + "Example: " + COMMAND_WORD + " /Users/seedu/Documents/exportedData.xml";

    public static final String MESSAGE_IMPORT_CONTACTS_SUCCESS = "Contacts imported from: %1$s";
    public static final String MESSAGE_IMPORT_CONTACTS_DCE_FAILURE = "Unable to parse file at: %1$s";
    public static final String MESSAGE_IMPORT_CONTACTS_IO_FAILURE = "Unable to import contacts from: %1$s";
    public static final String MESSAGE_IMPORT_CONTACTS_FNF_FAILURE = "File at %1$s not found";

    private static final Logger logger = LogsCenter.getLogger(ImportCommand.class);

    private final Path importFilePath;

    public ImportCommand(String filePath) {
        importFilePath = Paths.get(filePath);
    }

    @Override
    public CommandResult execute() throws CommandException {
        String absoluteImportFilePathString = importFilePath.toAbsolutePath().toString();

        Optional<ReadOnlyAddressBook> optionalImportedAddressBook;
        try {
            optionalImportedAddressBook = storage.readAddressBook(absoluteImportFilePathString);
        } catch (DataConversionException dce) {
            logger.warning("Error converting file at: " + absoluteImportFilePathString);
            throw new CommandException(
                    String.format(MESSAGE_IMPORT_CONTACTS_DCE_FAILURE, absoluteImportFilePathString));
        } catch (IOException ioe) {
            logger.warning("Error reading file at: " + absoluteImportFilePathString);
            throw new CommandException(
                    String.format(MESSAGE_IMPORT_CONTACTS_IO_FAILURE, absoluteImportFilePathString));
        }

        ReadOnlyAddressBook importedAddressBook;
        if (optionalImportedAddressBook.isPresent()) {
            importedAddressBook = optionalImportedAddressBook.get();
        } else {
            // no address book returned, so we know that file was not found
            throw new CommandException(
                    String.format(MESSAGE_IMPORT_CONTACTS_FNF_FAILURE, absoluteImportFilePathString));
        }

        model.addPersons(importedAddressBook.getPersonList());
        return new CommandResult(String.format(MESSAGE_IMPORT_CONTACTS_SUCCESS, absoluteImportFilePathString));
    }

    @Override
    public void setData(Model model, Storage storage, CommandHistory commandHistory, UndoRedoStack undoRedoStack) {
        this.model = model;
        this.storage = storage;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ImportCommand // instanceof handles nulls
                && this.importFilePath.equals(((ImportCommand) other).importFilePath)); // state check
    }
}
