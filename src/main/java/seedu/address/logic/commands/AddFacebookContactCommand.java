package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;

/**
 * Adds a facebook contact to the address book.
 */
public class AddFacebookContactCommand extends UndoableCommand {
    public static final String COMMAND_WORD = "addfacebookcontact";
    public static final String COMMAND_ALIAS = "afbc";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a facebook friend to the address book.\n"
            + "Alias: " + COMMAND_ALIAS + "\n"
            + "Parameters: FACEBOOK_CONTACT_NAME\n"
            + "Example: " + COMMAND_WORD + "alice fong";

    public static final String MESSAGE_VERSION_ONE_TEST = "New person added: Khairul Rusydi Phone: 000 "
            + "Email: a@b.c Address: - Tags: [facebookcontact]";
    public static final String MESSAGE_SUCCESS = "New person added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book";
    private final Person toAdd;

    /**
     * Creates an AddFacebookContactCommand to add the specified Facebook contact
     * @param person
     */
    public AddFacebookContactCommand(Person person) {
        toAdd = new Person(person);
    }

    @Override
    protected CommandResult executeUndoableCommand() throws CommandException {
        requireNonNull(model);
        try {
            model.addPerson(toAdd);
            return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
        } catch (DuplicatePersonException e) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }
    }

}
