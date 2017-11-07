package seedu.address.logic.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.auth.AccessToken;
import javafx.scene.web.WebEngine;
import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.events.ui.NewResultAvailableEvent;
import seedu.address.logic.commands.exceptions.CommandException;

//@@author alexfoodw
/**
 * Connects the addressbook to a personal Facebook account.
 */
public class FacebookConnectCommand extends Command {
    public static final String COMMAND_WORD = "facebookconnect";
    public static final String COMMAND_ALIAS = "fbconnect";
    public static final String MESSAGE_SUCCESS = "Connected to your Facebook Account!";
    public static final String MESSAGE_STARTED_PROCESS = "Authentication has been initiated. "
            + "Please log into your Facebook account.";

    private static final String FACEBOOK_DOMAIN = "https://www.facebook.com/";
    private static final String FACEBOOK_APP_ID = "131555220900267";
    private static final String FACEBOOK_PERMISSIONS = "user_about_me,email,publish_actions,user_birthday,"
            + "user_education_history,user_friends,user_games_activity,user_hometown,user_likes,"
            + "user_location,user_photos,user_posts,user_relationship_details,user_relationships,"
            + "user_religion_politics,user_status,user_tagged_places,user_videos,user_website,user_work_history,"
            + "ads_management,ads_read,business_management,manage_pages,pages_manage_cta,publish_pages,"
            + "user_actions.books,user_actions.fitness,pages_messaging,read_custom_friendlists,"
            + "pages_messaging_phone_number,pages_messaging_subscriptions,pages_show_list,user_actions.music,"
            + "user_actions.news,read_page_mailboxes,rsvp_event,user_events,user_managed_groups,"
            + "pages_manage_instant_articles,user_actions.video,instagram_basic,instagram_manage_comments,"
            + "instagram_manage_insights,read_audience_network_insights,read_insights";
    private static final String FACEBOOK_AUTH_URL =
            "https://graph.facebook.com/oauth/authorize?type=user_agent&client_id=" + FACEBOOK_APP_ID
                    + "&redirect_uri=" + FACEBOOK_DOMAIN + "&scope=" + FACEBOOK_PERMISSIONS;
    private static boolean authenticated = false;
    private static String authenticatedUsername;
    private static Facebook facebookInstance;
    private static WebEngine webEngine;
    private static String accessToken;

    /**
     * Returns the existing WebEngine
     */
    public static WebEngine getWebEngine() {
        return webEngine;
    }

    /**
     * Sets a WebEngine
     */
    public static void setWebEngine(WebEngine webEngine) {
        FacebookConnectCommand.webEngine = webEngine;
    }

    /**
     * Returns the authenticated Facebook instance
     */
    public static Facebook getFacebookInstance() {
        return facebookInstance;
    }

    /**
     * Returns name of the authenticated user
     */
    public static String getAuthenticatedUsername() { return authenticatedUsername; }


    /**
     * Checks if there is an authenticated Facebook instance
     */
    public static boolean isAuthenticated() {
        return authenticated;
    }


    /**
     * Completes the authentication process
     */
    public static void completeAuth(String url) throws CommandException {
        Pattern p = Pattern.compile("access_token=(.*?)\\&");
        Matcher m = p.matcher(url);
        m.matches();
        m.find();
        accessToken = m.group(1);

        facebookInstance = new FacebookFactory().getInstance();
        facebookInstance.setOAuthPermissions(FACEBOOK_PERMISSIONS);
        facebookInstance.setOAuthAccessToken(new AccessToken(accessToken, null));
        try {
            authenticatedUsername = facebookInstance.getName();
        } catch (FacebookException e) {
            throw new CommandException("Error in Facebook Authorisation");
        }

        if (accessToken != null) {
            authenticated = true;
            EventsCenter.getInstance().post(new NewResultAvailableEvent(
                    MESSAGE_SUCCESS + " User name: " + authenticatedUsername, false));
        } else {
            throw new CommandException("Error in Facebook Authorisation");
        }
    }

    @Override
    public CommandResult execute() throws CommandException {
        webEngine.load(FACEBOOK_AUTH_URL);

        if (webEngine.getLocation().equals(FACEBOOK_AUTH_URL)) {
            return new CommandResult(MESSAGE_STARTED_PROCESS);
        } else {
            throw new CommandException("Error in Facebook Authorisation");
        }
    }
}
