# alexfoodw
###### /main/java/seedu/address/commons/events/ui/BrowserUrlChangeEvent.java
``` java
/**
 * Indicates a change in browser page
 */
public class BrowserUrlChangeEvent extends BaseEvent {
    private String processType;

    public BrowserUrlChangeEvent(String processType) {
        this.processType = processType;
    }

    public String getProcessType() {
        return processType;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
```
###### /main/java/seedu/address/logic/commands/AddFacebookContactCommand.java
``` java
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
```
###### /main/java/seedu/address/logic/commands/FacebookConnectCommand.java
``` java
/**
 * Connects the addressbook to a personal Facebook account.
 */
public class FacebookConnectCommand extends Command {
    public static final String COMMAND_WORD = "facebook connect";
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
    private static String welcomeUser;
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
     * Sets the authenticated Facebook instance
     */
    public static void setFacebookInstance(Facebook facebookInstance) {
        FacebookConnectCommand.facebookInstance = facebookInstance;
    }

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
            welcomeUser = facebookInstance.getName();
        } catch (FacebookException e) {
            throw new CommandException("Error in Facebook Authorisation");
        }

        if (accessToken != null) {
            authenticated = true;
            EventsCenter.getInstance().post(new NewResultAvailableEvent(
                    MESSAGE_SUCCESS + " User name: " + welcomeUser, false));
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
```
###### /main/java/seedu/address/logic/commands/FacebookLinkCommand.java
``` java
/**
 * Shares a link to a personal Facebook account.
 */
public class FacebookLinkCommand extends Command {
    public static final String COMMAND_WORD = "facebook link";
    public static final String COMMAND_ALIAS = "fblink";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": share a URL in link format to Facebook account\n"
            + "Alias: " + COMMAND_ALIAS + "\n"
            + "Parameters: MESSAGE\n"
            + "Example: " + COMMAND_WORD + " https://www.google.com.sg/";

    public static final String MESSAGE_FACEBOOK_LINK_SUCCESS = "Shared link on Facebook!";
    public static final String MESSAGE_FACEBOOK_LINK_INITIATED = "User not authenticated, log in to proceed.";

    private static String user;
    private static String link;

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
        BrowserPanel.setLink(false);
    }

    @Override
    public CommandResult execute() throws CommandException {
        if (!FacebookConnectCommand.isAuthenticated()) {
            FacebookConnectCommand newFacebookConnect = new FacebookConnectCommand();
            BrowserPanel.setLink(true);
            newFacebookConnect.execute();
            return new CommandResult(MESSAGE_FACEBOOK_LINK_INITIATED);
        } else {
            completeLink();
            return new CommandResult(MESSAGE_FACEBOOK_LINK_SUCCESS + " (to " + user + "'s page.)");
        }
    }
}
```
###### /main/java/seedu/address/logic/commands/FacebookPostCommand.java
``` java
/**
 * Posts a message to a personal Facebook account.
 */
public class FacebookPostCommand extends Command {
    public static final String COMMAND_WORD = "facebook post";
    public static final String COMMAND_ALIAS = "fbpost";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": post a message to Facebook account\n"
            + "Alias: " + COMMAND_ALIAS + "\n"
            + "Parameters: MESSAGE\n"
            + "Example: " + COMMAND_WORD + " hello world!";

    public static final String MESSAGE_FACEBOOK_POST_SUCCESS = "Posted to Facebook!";
    public static final String MESSAGE_FACEBOOK_POST_INITIATED = "User not authenticated, log in to proceed.";
    public static final String MESSAGE_FACEBOOK_POST_ERROR = "Error posting to Facebook";

    private static String user;
    private static String toPost;

    /**
     * Creates an AddCommand to add the specified {@code ReadOnlyPerson}
     */
    public FacebookPostCommand(String message) {
        toPost = message;
    }

    /**
     * Completes the post command
     * @throws CommandException
     */
    public static void completePost() throws CommandException {
        Facebook facebookInstance = FacebookConnectCommand.getFacebookInstance();
        user = null;
        try {
            user = facebookInstance.getName();
            facebookInstance.postStatusMessage(toPost);
        } catch (FacebookException e) {
            // exception not handled because Facebook API still throws an exception even if success,
            // so exception is ignored for now
            e.printStackTrace();
        }

        EventsCenter.getInstance().post(new NewResultAvailableEvent(
                MESSAGE_FACEBOOK_POST_SUCCESS + " (to " + user + "'s page.)", false));
        BrowserPanel.setPost(false);
    }

    @Override
    public CommandResult execute() throws CommandException {
        if (!FacebookConnectCommand.isAuthenticated()) {
            FacebookConnectCommand newFacebookConnect = new FacebookConnectCommand();
            BrowserPanel.setPost(true);
            newFacebookConnect.execute();
            return new CommandResult(MESSAGE_FACEBOOK_POST_INITIATED);
        } else {
            completePost();
            return new CommandResult(MESSAGE_FACEBOOK_POST_SUCCESS + " (to " + user + "'s page.)");
        }
    }
}
```
###### /main/java/seedu/address/logic/parser/AddFacebookContactParser.java
``` java
/**
 * Parses the given {@code String} of arguments in the context of the AddFacebookContactCommand
 * and returns an AddFacebookContactCommand object for execution.
 * @throws ParseException if the user input does not conform the expected format
 */
public class AddFacebookContactParser implements Parser<AddFacebookContactCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddFacebookContactCommand
     * and returns an AddFacebookContactCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddFacebookContactCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddFacebookContactCommand.MESSAGE_USAGE));
        }

        try {
            Facebook facebook = new FacebookFactory().getInstance();
            ResponseList<User> contactsList = facebook.searchUsers(trimmedArgs);
            User user = contactsList.get(0);

            Email email;
            // create new person object
            if (user.getEmail() != null) {
                email = new Email(user.getEmail());
            } else {
                // Placeholder for users without username
                email = new Email("placeholder@example.com");
            }

            Set<Tag> tags = new HashSet<>();
            tags.add(new Tag("facebookcontact"));

            Favorite favorite = new Favorite(false);

            Set<SocialInfo> socialInfos = new HashSet<>();
            // TODO(Marvin): Make social media identifiers public
            SocialInfo facebookInfo = SocialInfoMapping.parseSocialInfo("facebook " + user.getUsername());
            socialInfos.add(facebookInfo);


            Person newPerson = new Person(
                    new Name(user.getName()),
                    new Phone("000"), // Placeholder phone number
                    email,
                    new Address("Placeholder Address"), // Placeholder address
                    favorite,
                    tags,
                    socialInfos);

            return new AddFacebookContactCommand(newPerson);
        } catch (FacebookException e) {
            // TODO(Alex): Properly handle exceptions here
            e.printStackTrace();
        } catch (IllegalValueException e) {
            e.printStackTrace();
        }

        throw new ParseException(
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddFacebookContactCommand.MESSAGE_USAGE));
    }

}
```
###### /main/java/seedu/address/logic/parser/FacebookLinkCommandParser.java
``` java
/**
 * Parses input arguments and creates a new FacebookPostCommand object
 */
public class FacebookLinkCommandParser implements Parser<FacebookLinkCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FacebookLinkCommand
     * and returns an FacebookLinkCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public FacebookLinkCommand parse(String userInput) throws ParseException {
        String trimmedArgs = userInput.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FacebookLinkCommand.MESSAGE_USAGE));
        }

        return new FacebookLinkCommand(trimmedArgs);
    }
}
```
###### /main/java/seedu/address/logic/parser/FacebookPostCommandParser.java
``` java
/**
 * Parses input arguments and creates a new FacebookPostCommand object
 */
public class FacebookPostCommandParser implements Parser<FacebookPostCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddFacebookContactCommand
     * and returns an AddFacebookContactCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public FacebookPostCommand parse(String userInput) throws ParseException {
        String trimmedArgs = userInput.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FacebookPostCommand.MESSAGE_USAGE));
        }

        return new FacebookPostCommand(trimmedArgs);
    }
}
```
###### /main/java/seedu/address/ui/BrowserPanel.java
``` java
    /**
     * Identifies if in the midst of posting process
     * @param bool
     */
    public static void setPost(boolean bool) {
        isPost = bool;
    }

    /**
     * Identifies if in the midst of linking process
     * @param bool
     */
    public static void setLink(boolean bool) {
        isLink = bool;
    }
```
###### /main/java/seedu/address/ui/BrowserPanel.java
``` java
    private void setEventHandlerForBrowserUrlChangeEvent() {
        location.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue.contains("access_token")) {
                        if (isPost) {
                            logger.fine("browser url changed to : '" + newValue + "'");
                            raise(new BrowserUrlChangeEvent(FacebookPostCommand.COMMAND_ALIAS));
                        } else if (isLink) {
                            logger.fine("browser url changed to : '" + newValue + "'");
                            raise(new BrowserUrlChangeEvent(FacebookLinkCommand.COMMAND_ALIAS));
                        } else {
                            logger.fine("browser url changed to : '" + newValue + "'");
                            raise(new BrowserUrlChangeEvent(FacebookConnectCommand.COMMAND_ALIAS));
                        }
                    }
                });
        isPost = false;
    }
```
###### /main/java/seedu/address/ui/BrowserPanel.java
``` java
    @Subscribe
    private void handleBrowserUrlChangeEvent(BrowserUrlChangeEvent event) throws CommandException {
        switch (event.getProcessType()) {

        case FacebookConnectCommand.COMMAND_ALIAS:
            logger.info(LogsCenter.getEventHandlingLogMessage(event));
            FacebookConnectCommand.completeAuth(browser.getEngine().getLocation());
            break;

        case FacebookPostCommand.COMMAND_ALIAS:
            logger.info(LogsCenter.getEventHandlingLogMessage(event));
            FacebookConnectCommand.completeAuth(browser.getEngine().getLocation());
            FacebookPostCommand.completePost();
            break;

        case FacebookLinkCommand.COMMAND_ALIAS:
            logger.info(LogsCenter.getEventHandlingLogMessage(event));
            FacebookConnectCommand.completeAuth(browser.getEngine().getLocation());
            FacebookLinkCommand.completeLink();
            break;

        default:
            throw new CommandException("Url change error.");
        }
    }
```
