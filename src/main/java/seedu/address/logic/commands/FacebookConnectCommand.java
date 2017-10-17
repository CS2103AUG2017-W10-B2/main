package seedu.address.logic.commands;

import facebook4j.Facebook;
import facebook4j.FacebookFactory;
import facebook4j.auth.AccessToken;
import seedu.address.logic.commands.exceptions.CommandException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Connects the addressbook to a personal Facebook account.
 */
public class FacebookConnectCommand extends Command {
    public static final String COMMAND_WORD = "facebook connect";
    public static final String COMMAND_ALIAS = "fbconnect";

    public static final String MESSAGE_SUCCESS = "Connected to your Facebook Account!";

    private static String FACEBOOK_DOMAIN = "https://www.facebook.com/";
    private static String APP_ID = "1985095851775955";
    private String accessToken;
    private static String commaSeparetedPermissions = "user_about_me,email,publish_actions,user_birthday,"
            + "user_education_history,user_friends,user_games_activity,user_hometown,user_likes,"
            + "user_location,user_photos,user_posts,user_relationship_details,user_relationships,"
            + "user_religion_politics,user_status,user_tagged_places,user_videos,user_website,user_work_history,"
            + "ads_management,ads_read,business_management,manage_pages,pages_manage_cta,publish_pages,"
            + "user_actions.books,user_actions.fitness,pages_messaging,read_custom_friendlists,"
            + "pages_messaging_phone_number,pages_messaging_subscriptions,pages_show_list,user_actions.music,"
            + "user_actions.news,read_page_mailboxes,rsvp_event,user_events,user_managed_groups,"
            + "pages_manage_instant_articles,user_actions.video,instagram_basic,instagram_manage_comments,"
            + "instagram_manage_insights,read_audience_network_insights,read_insights";

    private static String AUTH_URL = "https://graph.facebook.com/oauth/authorize?type=user_agent&client_id=" + APP_ID
            + "&redirect_uri=" + FACEBOOK_DOMAIN + "&scope=" + commaSeparetedPermissions;


    public static String getAuthUrl() {
        return AUTH_URL;
    }

    @Override
    public CommandResult execute() throws CommandException {

        System.setProperty("webdriver.chrome.driver", "chromedriver");

        WebDriver driver = new ChromeDriver();
        driver.get(AUTH_URL);
        while (true) {
            if (driver.getCurrentUrl().contains("facebook.com/?#access_token")) {
                Pattern p = Pattern.compile("access_token=(.*?)\\&");
                String url = driver.getCurrentUrl();
                Matcher m = p.matcher(url);
                m.matches();
                m.find();
                accessToken = m.group(1);
                driver.quit();

                Facebook facebook = new FacebookFactory().getInstance();
                facebook.setOAuthPermissions(commaSeparetedPermissions);
                facebook.setOAuthAccessToken(new AccessToken(accessToken, null));
                break;
            }
        }

        if(accessToken != null){
            return new CommandResult(MESSAGE_SUCCESS);
        } else {
            throw new CommandException("Error in Facebook Authorisation");
        }
    }
}
