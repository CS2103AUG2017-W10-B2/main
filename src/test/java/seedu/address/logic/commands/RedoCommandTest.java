package seedu.address.logic.commands;

import static seedu.address.logic.UndoRedoStackUtil.prepareStack;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.deleteFirstPerson;
import static seedu.address.testutil.StorageUtil.getNullStorage;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.storage.Storage;

public class RedoCommandTest {
    private static final CommandHistory EMPTY_COMMAND_HISTORY = new CommandHistory();
    private static final UndoRedoStack EMPTY_STACK = new UndoRedoStack();
    private static final Storage NULL_STORAGE = getNullStorage();

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final DeleteCommand deleteCommandOne = new DeleteByIndexCommand(Arrays.asList(INDEX_FIRST_PERSON));
    private final DeleteCommand deleteCommandTwo = new DeleteByIndexCommand(Arrays.asList(INDEX_FIRST_PERSON));

    @Before
    public void setUp() {
        deleteCommandOne.setData(model, NULL_STORAGE, EMPTY_COMMAND_HISTORY, EMPTY_STACK);
        deleteCommandTwo.setData(model, NULL_STORAGE, EMPTY_COMMAND_HISTORY, EMPTY_STACK);
    }

    @Test
    public void execute() {
        UndoRedoStack undoRedoStack = prepareStack(
                Collections.emptyList(), Arrays.asList(deleteCommandTwo, deleteCommandOne));
        RedoCommand redoCommand = new RedoCommand();
        redoCommand.setData(model, NULL_STORAGE, EMPTY_COMMAND_HISTORY, undoRedoStack);
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        // multiple commands in redoStack
        deleteFirstPerson(expectedModel);
        assertCommandSuccess(redoCommand, model, RedoCommand.MESSAGE_SUCCESS, expectedModel);

        // single command in redoStack
        deleteFirstPerson(expectedModel);
        assertCommandSuccess(redoCommand, model, RedoCommand.MESSAGE_SUCCESS, expectedModel);

        // no command in redoStack
        assertCommandFailure(redoCommand, model, RedoCommand.MESSAGE_FAILURE);
    }
}
