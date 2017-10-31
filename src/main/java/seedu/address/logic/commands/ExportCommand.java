package seedu.address.logic.commands;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.storage.Storage;

//@@author marvinchin
/**
 * Exports existing contacts to external XML file
 */
public class ExportCommand extends Command {

    public static final String COMMAND_WORD = "export";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Exports existing contacts.\n"
            + "Parameters: FILE PATH (must be a valid file path where the current user has write access\n"
            + "Example: " + COMMAND_WORD + " /Users/seedu/Documents/exportedData.xml";

    public static final String MESSAGE_EXPORT_CONTACTS_SUCCESS = "Contacts exported to: %1$s";
    public static final String MESSAGE_EXPORT_CONTACTS_FAILURE = "Unable to export contacts to: %1$s";

    private final Path exportFilePath;

    public ExportCommand(String filePath) {
        // we store it as a Path rather than a String so that we can get the absolute file path
        // this makes it clearer to the user where the file is saved
        exportFilePath = Paths.get(filePath);
    }

    @Override
    public CommandResult execute() throws CommandException {
        ReadOnlyAddressBook readOnlyAddressBook = model.getAddressBook();
        String absoluteExportFilePathString = exportFilePath.toAbsolutePath().toString();
        try {
            storage.saveAddressBook(readOnlyAddressBook, absoluteExportFilePathString);
        } catch (IOException ioe) {
            throw new CommandException(String.format(MESSAGE_EXPORT_CONTACTS_FAILURE, absoluteExportFilePathString));
        }
        return new CommandResult(String.format(MESSAGE_EXPORT_CONTACTS_SUCCESS, absoluteExportFilePathString));
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
