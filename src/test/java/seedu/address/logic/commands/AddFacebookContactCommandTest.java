package seedu.address.logic.commands;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.logic.parser.AddFacebookContactParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

public class AddFacebookContactCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_personAcceptedByModel_addSuccessful() throws Exception {
        assertCommandSuccess(prepareCommand(), model, AddFacebookContactCommand.MESSAGE_VERSION_ONE_TEST, model);
    }

    /**
     * Returns an {@code AddFacebookContactCommand}.
     */
    private AddFacebookContactCommand prepareCommand() throws ParseException {
        AddFacebookContactCommand newCommand = new AddFacebookContactParser().parse("Khairul Rusydi");

        newCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return newCommand;
    }
}
