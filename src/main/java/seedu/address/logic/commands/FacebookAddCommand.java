package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.Set;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.ResponseList;
import facebook4j.User;
import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.events.ui.NewResultAvailableEvent;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.SocialInfoMapping;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Favorite;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.social.SocialInfo;
import seedu.address.model.tag.Tag;
import seedu.address.ui.BrowserPanel;

//@@author alexfoodw
/**
 * Adds a facebook contact to the address book.
 */
public class FacebookAddCommand extends UndoableCommand {
    public static final String COMMAND_WORD = "facebookadd";
    public static final String COMMAND_ALIAS = "fbadd";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a facebook friend to the address book.\n"
            + "Alias: " + COMMAND_ALIAS + "\n"
            + "Parameters: FACEBOOK_CONTACT_NAME\n"
            + "Example: " + COMMAND_WORD + "alice fong";

    public static final String MESSAGE_SUCCESS = "New person added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book";
    public static final String MESSAGE_FACEBOOK_ADD_SUCCESS = " has been imported from Facebook!";
    public static final String MESSAGE_FACEBOOK_ADD_INITIATED = "User not authenticated, log in to proceed.";
    public static final String MESSAGE_FACEBOOK_ADD_ERROR = "Error with Facebook API call.";
    public static final String MESSAGE_FACEBOOK_ADD_PERSON_ERROR = "Error with creating Person Object";

    private static String userName = "";
    private String toAddName;
    private Person toAdd;

    /**
     * Creates an FacebookAddCommand to add the specified Facebook contact
     * @param trimmedArgs
     */
    public FacebookAddCommand(String trimmedArgs) {
        userName = trimmedArgs;
    }

    /**
     * Completes the Facebook Add command
     * @throws CommandException
     */
    public void completeAdd() throws CommandException {
        Facebook facebookInstance = FacebookConnectCommand.getFacebookInstance();
        ResponseList<User> friendList = null;

        // fetch data from Facebook
        try {
            friendList = facebookInstance.searchUsers(userName);
        } catch (FacebookException e) {
            throw new CommandException(MESSAGE_FACEBOOK_ADD_ERROR);
        }
        User user = friendList.get(0);
        toAddName = user.getName();

        // Assign data to Person object
        try {
            Set<SocialInfo> socialInfos = new HashSet<>();
            SocialInfo facebookInfo = null;
            facebookInfo = SocialInfoMapping.parseSocialInfo("facebook " + user.getId());
            socialInfos.add(facebookInfo);


            Set<Tag> tags = new HashSet<>();
            tags.add(new Tag("facebookFriend"));

            toAdd = new Person(new Name(toAddName), new Phone(), new Email(), new Address(),
                    new Favorite(false), tags, socialInfos);
        } catch (IllegalValueException e) {
            throw new CommandException(MESSAGE_FACEBOOK_ADD_PERSON_ERROR);
        }


        addContactToAddressBook();
        EventsCenter.getInstance().post(new NewResultAvailableEvent(
                toAddName + MESSAGE_FACEBOOK_ADD_SUCCESS, false));
    }

    /**
     * Adds the contact to addressbook
     * @throws CommandException
     */
    private void addContactToAddressBook() throws CommandException {
        // add to model and return
        try {
            requireNonNull(model);
            model.addPerson(toAdd);
        } catch (DuplicatePersonException e) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }
    }

    @Override
    protected CommandResult executeUndoableCommand() throws CommandException {
        if (!FacebookConnectCommand.isAuthenticated()) {
            BrowserPanel.setProcessType(COMMAND_ALIAS);
            BrowserPanel.setTrimmedArgs(userName);

            FacebookConnectCommand newFacebookConnect = new FacebookConnectCommand();
            newFacebookConnect.execute();

            return new CommandResult(MESSAGE_FACEBOOK_ADD_INITIATED);
        } else {
            completeAdd();
            return new CommandResult(toAddName + MESSAGE_FACEBOOK_ADD_SUCCESS);
        }
    }

}
