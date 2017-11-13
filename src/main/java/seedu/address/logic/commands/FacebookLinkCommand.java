package seedu.address.logic.commands;

import java.net.MalformedURLException;
import java.net.URL;

import facebook4j.Facebook;
import facebook4j.FacebookException;

import javafx.scene.web.WebEngine;
import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.events.ui.NewResultAvailableEvent;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.ui.BrowserPanel;

//@@author alexfoodw
/**
 * Shares a link to a personal Facebook account.
 */
public class FacebookLinkCommand extends Command {
    public static final String COMMAND_WORD = "facebooklink";
    public static final String COMMAND_ALIAS = "fblink";
    public static final String EXAMPLE_LINK = "https://www.google.com.sg/";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": share a URL in link format to Facebook account\n"
            + "Alias: " + COMMAND_ALIAS + "\n"
            + "Parameters: MESSAGE\n"
            + "Example: " + COMMAND_WORD + " " + EXAMPLE_LINK;

    public static final String MESSAGE_FACEBOOK_LINK_SUCCESS = "Shared link on Facebook!";
    public static final String MESSAGE_FACEBOOK_LINK_INITIATED = "User not authenticated, log in to proceed.";

    private static String user;
    private static String link;
    private static WebEngine webEngine;

    /**
     * Creates an AddCommand to add the specified {@code ReadOnlyPerson}
     */
    public FacebookLinkCommand(String url) {
        link = url;
    }

    /**
     * Completes the Facebook Link command
     * @throws CommandException
     */
    public static void completeLink() throws CommandException {
        Facebook facebookInstance = FacebookConnectCommand.getFacebookInstance();
        user = null;
        try {
            user = facebookInstance.getName();
            try {
                facebookInstance.postLink(new URL(link));
            } catch (MalformedURLException e) {
                new Exception("Please enter a valid URL.");
            }
        } catch (FacebookException e) {
            // exception not handled because Facebook API still throws an exception even if success,
            // so exception is ignored for now
            e.printStackTrace();
        }

        EventsCenter.getInstance().post(new NewResultAvailableEvent(
                MESSAGE_FACEBOOK_LINK_SUCCESS + " (to " + user + "'s page.)", false));
        webEngine = FacebookConnectCommand.getWebEngine();
        webEngine.load(FacebookConnectCommand.getAuthenticatedUserPage());
    }

    @Override
    public CommandResult execute() throws CommandException {
        if (!FacebookConnectCommand.isAuthenticated()) {
            BrowserPanel.setProcessType(COMMAND_WORD);
            FacebookConnectCommand newFacebookConnect = new FacebookConnectCommand();
            newFacebookConnect.execute();
            return new CommandResult(MESSAGE_FACEBOOK_LINK_INITIATED);
        } else {
            completeLink();
            return new CommandResult(MESSAGE_FACEBOOK_LINK_SUCCESS + " (to " + user + "'s page.)");
        }
    }
}
