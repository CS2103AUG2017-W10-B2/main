# alexfoodw
###### \java\seedu\address\commons\core\Messages.java
``` java
    public static final String MESSAGE_INVALID_URL = "Invalid URL! \n%1$s";
```
###### \java\seedu\address\commons\events\ui\BrowserUrlChangeEvent.java
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
###### \java\seedu\address\logic\commands\FacebookAddAllFriendsCommand.java
``` java
/**
 * Adds all available friends from a personal Facebook account.
 */

public class FacebookAddAllFriendsCommand extends Command {
    public static final String COMMAND_WORD = "facebookaddallfriends";
    public static final String COMMAND_ALIAS = "fbaddall";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": adds all available friends from an authenticated Facebook account.\n"
            + "Alias: " + COMMAND_ALIAS + "\n";
    public static final String MESSAGE_FACEBOOK_ADD_ALL_FRIENDS_ERROR = "Error with Facebook Tagable Friends API call."
            + "User may not be registered as 'Test User'";
    public static final String MESSAGE_FACEBOOK_ADD_ALL_FRIENDS_PAGING_ERROR = "Error with getting next page";
    public static final String MESSAGE_FACEBOOK_ADD_ALL_FRIENDS_SUCCESS = " valid friends added from Facebook!";
    public static final String MESSAGE_FACEBOOK_ADD_ALL_FRIENDS_INITIATION_ERROR = "User not authenticated, "
            + "please input 'facebookconnect' command first.";
    public static final String MESSAGE_FACEBOOK_ADD_ALL_FRIENDS_INITIATED = "Adding all friends from Facebook...";
    public static final String EXTRACT_USER_ID_REGEX = "set=a.(.*?)\\&type";

    private static Facebook facebookInstance;
    private static String currentUserId;
    private static String currentUserName;
    private static ResponseList<TaggableFriend> currentList;
    private static Paging<TaggableFriend> currentPaging;
    private static String currentPhotoID;
    private static int totalFriendsAdded = 0;
    private static int friendIndex = 0;

    /**
     * Returns the current Facebook ID of the user being added
     * @return currentUserId
     */
    public static String getCurrentUserId() {
        return currentUserId;
    }

    /**
     * Returns the current Facebook Username of the user being added
     * @return currentUserName
     */
    public static String getCurrentUserName() {
        return currentUserName;
    }

    /**
     * Increments the counter of total friends added so far
     */
    public static void incrementTotalFriendsAdded() {
        totalFriendsAdded++;
    }

    /**
     * Adds all facebook contacts to addressbook
     * @throws CommandException
     */
    public static void addFirstFriend() throws CommandException {
        facebookInstance = FacebookConnectCommand.getFacebookInstance();
        try {
            currentList = facebookInstance.getTaggableFriends();
            currentPaging = currentList.getPaging();
            addNextFriend();
        } catch (FacebookException e) {
            throw new CommandException(MESSAGE_FACEBOOK_ADD_ALL_FRIENDS_ERROR);
        }

    }

    /**
     * Proceeds to add the next available friend from facebook contacts to addressbook
     * @throws CommandException
     */
    public static void addNextFriend() throws CommandException {
        if (friendIndex >= currentList.size()) {
            // go to next list
            try {
                currentList = facebookInstance.fetchNext(currentPaging);
            } catch (FacebookException e) {
                throw new CommandException(MESSAGE_FACEBOOK_ADD_ALL_FRIENDS_PAGING_ERROR);
            }
            if (currentList == null) {
                finishFacebookAddAllFriends();
                return;
            }
            friendIndex = 0;
            currentPaging = currentList.getPaging();
        }
        TaggableFriend friend = currentList.get(friendIndex);
        currentUserName = friend.getName();
        // extract photo ID
        String photoUrl = friend.getPicture().getURL().toString();
        Pattern p = Pattern.compile("_(.*?)\\_");
        Matcher m = p.matcher(photoUrl);
        m.matches();
        m.find();
        currentPhotoID = m.group(1);

        // initialise getting user ID
        WebEngine webEngine = FacebookConnectCommand.getWebEngine();
        webEngine.load(FacebookConnectCommand.FACEBOOK_DOMAIN + currentPhotoID);
    }

    /**
     * Sets up the counter and adds the next Facebook Contact
     */
    public static void setupNextFriend() {
        friendIndex++;
        try {
            addNextFriend();
        } catch (CommandException e) {
            new CommandException(MESSAGE_FACEBOOK_ADD_ALL_FRIENDS_ERROR);
        }
    }

    /**
     * Extracts the user id for the required URL
     */
    public static void setUserId(String url) {
        // extract photo ID
        Pattern p = Pattern.compile(EXTRACT_USER_ID_REGEX);
        Matcher m = p.matcher(url);
        m.matches();
        m.find();
        String groupId = m.group(1);
        String[] parts = groupId.split("\\.");
        currentUserId = parts[2];
    }

    /**
     * Completes and exits the command
     */
    private static void finishFacebookAddAllFriends() {
        FacebookConnectCommand.loadUserPage();
        EventsCenter.getInstance().post(new NewResultAvailableEvent(totalFriendsAdded
                + MESSAGE_FACEBOOK_ADD_ALL_FRIENDS_SUCCESS + " (From "
                + FacebookConnectCommand.getAuthenticatedUsername() + "'s account)", false));
        friendIndex = 0;
        totalFriendsAdded = 0;
    }

    @Override
    public CommandResult execute() throws CommandException {
        if (!FacebookConnectCommand.isAuthenticated()) {
            throw new CommandException(MESSAGE_FACEBOOK_ADD_ALL_FRIENDS_INITIATION_ERROR);
        } else {
            BrowserPanel.setProcessType(COMMAND_WORD);
            addFirstFriend();
            return new CommandResult(MESSAGE_FACEBOOK_ADD_ALL_FRIENDS_INITIATED);
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof FacebookAddAllFriendsCommand); // instanceof handles nulls
    }
}
```
###### \java\seedu\address\logic\commands\FacebookAddCommand.java
``` java
/**
 * Adds a facebook contact to the address book.
 */
public class FacebookAddCommand extends Command {
    public static final String COMMAND_WORD = "facebookadd";
    public static final String COMMAND_ALIAS = "fbadd";
    public static final String EXAMPLE_NAME = "Barack Obama";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a facebook user to the address book.\n"
            + "Alias: " + COMMAND_ALIAS + "\n"
            + "Parameters: FACEBOOK_USER_NAME\n"
            + "Example: " + COMMAND_WORD + " " + EXAMPLE_NAME;

    public static final String MESSAGE_SUCCESS = "New person added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book";
    public static final String MESSAGE_FACEBOOK_ADD_SUCCESS = " has been imported from Facebook!";
    public static final String MESSAGE_FACEBOOK_ADD_INITIATED = "User not authenticated, log in to proceed.";
    public static final String MESSAGE_FACEBOOK_ADD_ERROR = "Error with Facebook API call.";
    public static final String MESSAGE_FACEBOOK_ADD_PERSON_ERROR = "Error with creating Person Object";

    private String userName;
    private String toAddName;
    private String toAddId;
    private Person toAdd;
    private boolean isAddAll;

    /**
     * Creates an FacebookAddCommand to add the specified Facebook contact
     * @param trimmedArgs
     */
    public FacebookAddCommand(String trimmedArgs) {
        userName = trimmedArgs;
    }

    /**
     * Creates an alternative FacebookAddCommand to add the specified Facebook contact initiated by facebook
     * add all friends command.
     */
    public FacebookAddCommand(boolean isAddAll) {
        this.isAddAll = isAddAll;
        toAddName = FacebookAddAllFriendsCommand.getCurrentUserName();
        toAddId = FacebookAddAllFriendsCommand.getCurrentUserId();
    }

    /**
     * Completes the Facebook Add command
     * @throws CommandException
     */
    public void completeAdd() throws CommandException {
        // if add is not called from the FacebookAddAllFriends Command
        if (!isAddAll) {
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
            toAddId = user.getId();
        }

        // Assign data to Person object
        try {
            Set<SocialInfo> socialInfos = new HashSet<>();
            SocialInfo facebookInfo = null;
            facebookInfo = SocialInfoMapping.parseSocialInfo("facebook " + toAddId);
            socialInfos.add(facebookInfo);
            UniqueSocialInfoList uniqueSocialInfoList = new UniqueSocialInfoList(socialInfos);

            Set<Tag> tags = new HashSet<>();
            tags.add(new Tag("facebookFriend"));

            toAdd = new Person(new Name(toAddName), new Phone(), new Email(), new Address(),
                    new Favorite(false), new DisplayPhoto(null), tags, uniqueSocialInfoList);
        } catch (IllegalValueException e) {
            throw new CommandException(MESSAGE_FACEBOOK_ADD_PERSON_ERROR);
        }

        addContactToAddressBook();
        EventsCenter.getInstance().post(new NewResultAvailableEvent(
                toAddName + MESSAGE_FACEBOOK_ADD_SUCCESS, false));
    }

    /**
     * Adds the facebook contact to addressbook
     * @throws CommandException
     */
    private void addContactToAddressBook() throws CommandException {
        // add to model and return
        try {
            requireNonNull(model);
            System.out.println(toAdd);
            model.addPerson(toAdd);
        } catch (DuplicatePersonException e) {
            new CommandException(MESSAGE_DUPLICATE_PERSON);
        }
    }

    @Override
    public CommandResult execute() throws CommandException {
        if (!FacebookConnectCommand.isAuthenticated()) {
            BrowserPanel.setProcessType(COMMAND_WORD);
            BrowserPanel.setTrimmedArgs(userName);

            FacebookConnectCommand newFacebookConnect = new FacebookConnectCommand();
            newFacebookConnect.execute();

            return new CommandResult(MESSAGE_FACEBOOK_ADD_INITIATED);
        } else {
            completeAdd();
            return new CommandResult(toAddName + MESSAGE_FACEBOOK_ADD_SUCCESS);
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof FacebookAddCommand // instanceof handles nulls
                && userName.equals(((FacebookAddCommand) other).userName));
    }

}
```
###### \java\seedu\address\logic\commands\FacebookConnectCommand.java
``` java
/**
 * Connects the addressbook to a personal Facebook account.
 */
public class FacebookConnectCommand extends Command {
    public static final String COMMAND_WORD = "facebookconnect";
    public static final String COMMAND_ALIAS = "fbconnect";
    public static final String MESSAGE_SUCCESS = "Connected to your Facebook Account!";
    public static final String MESSAGE_STARTED_PROCESS = "Authentication has been initiated. "
            + "Please log into your Facebook account.";
    public static final String FACEBOOK_DOMAIN = "https://www.facebook.com/";

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
    private static String authenticatedUserId;
    private static String authenticatedUserPage;
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
    public static String getAuthenticatedUsername() {
        return authenticatedUsername;
    }

    /**
     * Returns page of the authenticated user
     */
    public static String getAuthenticatedUserPage() {
        return authenticatedUserPage;
    }

    /**
     * Checks if there is an authenticated Facebook instance
     */
    public static boolean isAuthenticated() {
        return authenticated;
    }


    /**
     * Loads user page
     */
    public static void loadUserPage() {
        webEngine.load(authenticatedUserPage);
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
            authenticatedUserId = facebookInstance.getMe().getId();
        } catch (FacebookException e) {
            throw new CommandException("Error in Facebook Authorisation");
        }

        if (accessToken != null) {
            authenticated = true;
            authenticatedUserPage = "https://www.facebook.com/" + authenticatedUserId;
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

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof FacebookConnectCommand); // instanceof handles null
    }
}
```
###### \java\seedu\address\logic\commands\FacebookLinkCommand.java
``` java
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

    private String toPost;
    /**
     * Creates an AddCommand to add the specified {@code ReadOnlyPerson}
     */
    public FacebookLinkCommand(String url) {
        link = url;
        toPost = url;
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

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof FacebookLinkCommand // instanceof handles nulls
                && toPost.equals(((FacebookLinkCommand) other).toPost));
    }
}
```
###### \java\seedu\address\logic\commands\FacebookPostCommand.java
``` java
/**
 * Posts a message to a personal Facebook account.
 */
public class FacebookPostCommand extends Command {
    public static final String COMMAND_WORD = "facebookpost";
    public static final String COMMAND_ALIAS = "fbpost";
    public static final String EXAMPLE_POST = "Hello World!";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": post a message to Facebook account\n"
            + "Alias: " + COMMAND_ALIAS + "\n"
            + "Parameters: MESSAGE\n"
            + "Example: " + COMMAND_WORD + " " + EXAMPLE_POST;

    public static final String MESSAGE_FACEBOOK_POST_SUCCESS = "Posted to Facebook!";
    public static final String MESSAGE_FACEBOOK_POST_INITIATED = "User not authenticated, log in to proceed.";
    public static final String MESSAGE_FACEBOOK_POST_ERROR = "Error posting to Facebook";

    private static String currentPost;
    private static WebEngine webEngine;

    private String toPost;

    /**
     * Creates an AddCommand to add the specified {@code ReadOnlyPerson}
     */
    public FacebookPostCommand(String message) {
        currentPost = message;
        toPost = message;
    }

    /**
     * Completes the post command
     * @throws CommandException
     */
    public static void completePost() throws CommandException {
        Facebook facebookInstance = FacebookConnectCommand.getFacebookInstance();
        try {
            facebookInstance.postStatusMessage(currentPost);
        } catch (FacebookException e) {
            // exception not handled because Facebook API still throws an exception even if success,
            // so exception is ignored for now
            e.printStackTrace();
            new CommandException(MESSAGE_FACEBOOK_POST_ERROR);
        }

        EventsCenter.getInstance().post(new NewResultAvailableEvent(MESSAGE_FACEBOOK_POST_SUCCESS
                + " (to " + FacebookConnectCommand.getAuthenticatedUsername() + "'s page.)", false));
        webEngine = FacebookConnectCommand.getWebEngine();
        webEngine.load(FacebookConnectCommand.getAuthenticatedUserPage());
    }

    @Override
    public CommandResult execute() throws CommandException {
        if (!FacebookConnectCommand.isAuthenticated()) {
            BrowserPanel.setProcessType(COMMAND_WORD);
            FacebookConnectCommand newFacebookConnect = new FacebookConnectCommand();
            newFacebookConnect.execute();
            return new CommandResult(MESSAGE_FACEBOOK_POST_INITIATED);
        } else {
            completePost();
            return new CommandResult(MESSAGE_FACEBOOK_POST_SUCCESS + " (to "
                    + FacebookConnectCommand.getAuthenticatedUsername() + "'s page.)");
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof FacebookPostCommand // instanceof handles nulls
                && toPost.equals(((FacebookPostCommand) other).toPost));
    }
}
```
###### \java\seedu\address\logic\LogicManager.java
``` java
    /**
     * Completes the final step of FacebookAddCommand
     * Stores the current model and adds the contact to the model.
     */
    public void completeFacebookAddCommand(FacebookAddCommand command, String commandText) throws CommandException {
        command.setData(model, storage, history, undoRedoStack);
        command.completeAdd();
        undoRedoStack.push(command);
        history.add(commandText);
    }
```
###### \java\seedu\address\logic\parser\AddressBookParser.java
``` java
        case FacebookAddCommand.COMMAND_WORD:
        case FacebookAddCommand.COMMAND_ALIAS:
            BrowserPanel.setProcessType(commandWord);
            return new FacebookAddCommandParser().parse(arguments);

        case FacebookAddAllFriendsCommand.COMMAND_WORD:
        case FacebookAddAllFriendsCommand.COMMAND_ALIAS:
            BrowserPanel.setProcessType(commandWord);
            return new FacebookAddAllFriendsCommand();

        case FacebookConnectCommand.COMMAND_WORD:
        case FacebookConnectCommand.COMMAND_ALIAS:
            BrowserPanel.setProcessType(commandWord);
            return new FacebookConnectCommand();

        case FacebookPostCommand.COMMAND_WORD:
        case FacebookPostCommand.COMMAND_ALIAS:
            BrowserPanel.setProcessType(commandWord);
            return new FacebookPostCommandParser().parse(arguments);

        case FacebookLinkCommand.COMMAND_WORD:
        case FacebookLinkCommand.COMMAND_ALIAS:
            BrowserPanel.setProcessType(commandWord);
            return new FacebookLinkCommandParser().parse(arguments);
```
###### \java\seedu\address\logic\parser\FacebookAddCommandParser.java
``` java
/**
 * Parses the given {@code String} of arguments in the context of the FacebookAddCommand
 * and returns an FacebookAddCommand object for execution.
 * @throws ParseException if the user input does not conform the expected format
 */
public class FacebookAddCommandParser implements Parser<FacebookAddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FacebookAddCommand
     * and returns an FacebookAddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FacebookAddCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FacebookAddCommand.MESSAGE_USAGE));
        }

        return new FacebookAddCommand(trimmedArgs);
    }

}
```
###### \java\seedu\address\logic\parser\FacebookLinkCommandParser.java
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

        if (!ParserUtil.isValidUrl(trimmedArgs)) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_URL, "Example: " + FacebookLinkCommand.EXAMPLE_LINK));
        }

        return new FacebookLinkCommand(trimmedArgs);
    }
}
```
###### \java\seedu\address\logic\parser\FacebookPostCommandParser.java
``` java
/**
 * Parses input arguments and creates a new FacebookPostCommand object
 */
public class FacebookPostCommandParser implements Parser<FacebookPostCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FacebookAddCommand
     * and returns an FacebookAddCommand object for execution.
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
###### \java\seedu\address\logic\parser\ParserUtil.java
``` java
    /**
     * Validates if url is valid
     */
    public static boolean isValidUrl(String url) {
        Pattern p = Pattern.compile("(http|https):\\/\\/[A-Za-z\\.]*\\.(com|org|net)[^\\s]*");
        Matcher m = p.matcher(url);
        return m.matches();
    }
```
###### \java\seedu\address\MainApp.java
``` java
    /**
     * Returns the current logic Manager
     */
    public Logic getLogic() {
        return logic;
    }
```
###### \java\seedu\address\model\person\Address.java
``` java
    /**
     * Constructs a blank address field
     */
    public Address() {
        this.value = BLANK_ADDRESS;
    }
```
###### \java\seedu\address\model\person\Address.java
``` java
        // allow blank address
        if ((" ").equals(test)) {
            return true;
        }
```
###### \java\seedu\address\model\person\Email.java
``` java
    /**
     * Constructs a blank email field
     */
    public Email() {
        this.value = " ";
    }
```
###### \java\seedu\address\model\person\Email.java
``` java
        // allow blank email
        if (test.isEmpty()) {
            return true;
        }
```
###### \java\seedu\address\model\person\Name.java
``` java
    public static final String MESSAGE_NAME_CONSTRAINTS = "Person names should not be blank";

    public final String fullName;

    /**
     * Validates given name.
     *
     * @throws IllegalValueException if given name string is invalid.
     */
    public Name(String name) throws IllegalValueException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!isValidName(trimmedName)) {
            throw new IllegalValueException(MESSAGE_NAME_CONSTRAINTS);
        }
        this.fullName = trimmedName;
    }

    /**
     * Returns true if a given string is a valid person name.
     */
    public static boolean isValidName(String test) {
        return !("").equals(test);
    }
```
###### \java\seedu\address\model\person\Phone.java
``` java
    /**
     * Constructs a blank phone field
     */
    public Phone() {
        this.value = " ";
        this.phonelist = null;
    }
```
###### \java\seedu\address\model\person\Phone.java
``` java
        // allow blank phone number
        if (test.isEmpty()) {
            return true;
        }
```
###### \java\seedu\address\model\tag\Tag.java
``` java
    /**
     * Constructs a null tag field
     */
    public Tag() {
        this.tagName = null;
    }
```
###### \java\seedu\address\ui\BrowserPanel.java
``` java
    private static String processType;
    private static String trimmedArgs;
```
###### \java\seedu\address\ui\BrowserPanel.java
``` java
    private Logic logic;
    private Label location;
```
###### \java\seedu\address\ui\BrowserPanel.java
``` java
    /**
     * Sets the current logic manager
     * @param logic
     */
    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    /**
     * Identifies which facebook command process is being executed
     * @param type
     */
    public static void setProcessType(String type) {
        processType = type;
    }

    /**
     * Set arguments for the required facebook command
     * @param trimmedArgs
     */
    public static void setTrimmedArgs(String trimmedArgs) {
        BrowserPanel.trimmedArgs = trimmedArgs;
    }
```
###### \java\seedu\address\ui\BrowserPanel.java
``` java
    //method to convert Document to String
    public String getStringFromDocument(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (TransformerException ex) {
            new CommandException("Transform Doc to String Error.");
            return null;
        }
    }
    private void setEventHandlerForBrowserUrlLoadEvent() {
        browser.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            // Case: handle facebook related page loads
            if (Worker.State.SUCCEEDED.equals(newValue) && browser.getEngine().getLocation().contains("facebook")) {
                String currentContent = getStringFromDocument(browser.getEngine().getDocument());
                // handle invalid friend to be added
                if (currentContent.contains("Sorry, this content isn't available right now")
                        || currentContent.contains("This page isn't available")
                        || currentContent.contains("Sorry, this content isn't available at the moment")
                        || currentContent.contains("may have been expired")) {
                    FacebookAddAllFriendsCommand.setupNextFriend();
                }
            }
        });
    }

    private void setEventHandlerForBrowserUrlChangeEvent() {
        location.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    // listens for post-accesstoken generation next step
                    if (newValue.contains("access_token")) {
                        switch (processType) {

                        case FacebookConnectCommand.COMMAND_WORD:
                        case FacebookConnectCommand.COMMAND_ALIAS:
                            logger.fine("browser url changed to : '" + newValue + "'");
                            raise(new BrowserUrlChangeEvent(FacebookConnectCommand.COMMAND_ALIAS));
                            break;

                        case FacebookPostCommand.COMMAND_WORD:
                        case FacebookPostCommand.COMMAND_ALIAS:
                            logger.fine("browser url changed to : '" + newValue + "'");
                            raise(new BrowserUrlChangeEvent(FacebookPostCommand.COMMAND_ALIAS));
                            break;

                        case FacebookLinkCommand.COMMAND_WORD:
                        case FacebookLinkCommand.COMMAND_ALIAS:
                            logger.fine("browser url changed to : '" + newValue + "'");
                            raise(new BrowserUrlChangeEvent(FacebookLinkCommand.COMMAND_ALIAS));
                            break;

                        case FacebookAddCommand.COMMAND_WORD:
                        case FacebookAddCommand.COMMAND_ALIAS:
                            logger.fine("browser url changed to : '" + newValue + "'");
                            raise(new BrowserUrlChangeEvent(FacebookAddCommand.COMMAND_ALIAS));
                            break;

                        case FacebookAddAllFriendsCommand.COMMAND_WORD:
                        case FacebookAddAllFriendsCommand.COMMAND_ALIAS:
                            logger.fine("browser url changed to : '" + newValue + "'");
                            raise(new BrowserUrlChangeEvent(FacebookAddAllFriendsCommand.COMMAND_ALIAS));
                            break;

                        default:
                            break;
                        }
                    } else if (newValue.contains("photo.php?fbid")) {
                        logger.fine("browser url changed to : '" + newValue + "'");
                        raise(new BrowserUrlChangeEvent(FacebookAddAllFriendsCommand.COMMAND_ALIAS));
                    }
                });
        // reset after execution
        processType = null;
    }
```
###### \java\seedu\address\ui\BrowserPanel.java
``` java
    @Subscribe
    private void handleBrowserUrlChangeEvent(BrowserUrlChangeEvent event) throws CommandException {
        switch (event.getProcessType()) {

        case FacebookConnectCommand.COMMAND_WORD:
        case FacebookConnectCommand.COMMAND_ALIAS:
            logger.info(LogsCenter.getEventHandlingLogMessage(event));
            FacebookConnectCommand.completeAuth(browser.getEngine().getLocation());
            break;

        case FacebookPostCommand.COMMAND_WORD:
        case FacebookPostCommand.COMMAND_ALIAS:
            logger.info(LogsCenter.getEventHandlingLogMessage(event));
            FacebookConnectCommand.completeAuth(browser.getEngine().getLocation());
            FacebookPostCommand.completePost();
            break;

        case FacebookLinkCommand.COMMAND_WORD:
        case FacebookLinkCommand.COMMAND_ALIAS:
            logger.info(LogsCenter.getEventHandlingLogMessage(event));
            FacebookConnectCommand.completeAuth(browser.getEngine().getLocation());
            FacebookLinkCommand.completeLink();
            break;

        case FacebookAddCommand.COMMAND_WORD:
        case FacebookAddCommand.COMMAND_ALIAS:
            logger.info(LogsCenter.getEventHandlingLogMessage(event));
            FacebookConnectCommand.completeAuth(browser.getEngine().getLocation());
            FacebookAddCommand facebookAddCommand = new FacebookAddCommand(trimmedArgs);
            logic.completeFacebookAddCommand(facebookAddCommand, processType);
            break;

        case FacebookAddAllFriendsCommand.COMMAND_WORD:
        case FacebookAddAllFriendsCommand.COMMAND_ALIAS:
            logger.info(LogsCenter.getEventHandlingLogMessage(event));
            FacebookAddAllFriendsCommand.setUserId(browser.getEngine().getLocation());
            FacebookAddCommand facebookAddCommandForAddAll = new FacebookAddCommand(true);
            logic.completeFacebookAddCommand(facebookAddCommandForAddAll, processType);

            // go on to add next friend
            FacebookAddAllFriendsCommand.incrementTotalFriendsAdded();
            FacebookAddAllFriendsCommand.setupNextFriend();
            break;

        default:
            throw new CommandException("URL change error.");
        }
    }
```
