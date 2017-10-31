package seedu.address.ui;

import java.net.URL;
import java.util.Iterator;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.web.WebView;
import seedu.address.MainApp;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.ui.BrowserUrlChangeEvent;
import seedu.address.commons.events.ui.PersonPanelSelectionChangedEvent;
import seedu.address.logic.commands.FacebookConnectCommand;
import seedu.address.logic.commands.FacebookLinkCommand;
import seedu.address.logic.commands.FacebookPostCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Person;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.social.SocialInfo;

/**
 * The Browser Panel of the App.
 */
public class BrowserPanel extends UiPart<Region> {

    //@@author keithsoc
    public static final String DEFAULT_PAGE_DAY = "defaultDay.html";
    public static final String DEFAULT_PAGE_NIGHT = "defaultNight.html";
    //@@author
    public static final String GOOGLE_SEARCH_URL_PREFIX = "https://www.google.com.sg/search?safe=off&q=";
    public static final String GOOGLE_SEARCH_URL_SUFFIX = "&cad=h";

    private static final String FXML = "BrowserPanel.fxml";
    private static boolean isPost = false;
    private static boolean isLink = false;

    private final Logger logger = LogsCenter.getLogger(this.getClass());

    @FXML
    private WebView browser;

    private Label location;

    public BrowserPanel(Scene scene) {
        super(FXML);

        // To prevent triggering events for typing inside the loaded Web page.
        getRoot().setOnKeyPressed(Event::consume);

        loadDefaultPage(scene);

        FacebookConnectCommand.setWebEngine(browser.getEngine());
        location = new Label();
        location.textProperty().bind(browser.getEngine().locationProperty());
        setEventHandlerForBrowserUrlChangeEvent();
        registerAsAnEventHandler(this);
    }

    private void loadPersonPage(ReadOnlyPerson person) {
        loadPage(GOOGLE_SEARCH_URL_PREFIX + person.getName().fullName.replaceAll(" ", "+")
                + GOOGLE_SEARCH_URL_SUFFIX);
    }

    public void loadPage(String url) {
        Platform.runLater(() -> browser.getEngine().load(url));
    }

    //@@author alexfoodw
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
    //@@author

    //@@author keithsoc
    /**
     * Loads a default HTML file with a background that matches the current theme.
     */
    public void loadDefaultPage(Scene scene) {
        URL defaultPage;
        if (scene.getStylesheets().get(0).equals(UiTheme.THEME_DAY)) {
            defaultPage = MainApp.class.getResource(FXML_FILE_FOLDER + DEFAULT_PAGE_DAY);
        } else {
            defaultPage = MainApp.class.getResource(FXML_FILE_FOLDER + DEFAULT_PAGE_NIGHT);
        }
        loadPage(defaultPage.toExternalForm());
    }
    //@@author

    //@@author alexfoodw
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
    //@@author

    /**
     * Frees resources allocated to the browser.
     */
    public void freeResources() {
        browser = null;
    }

    //@@author sarahnzx
    @Subscribe
    private void handlePersonPanelSelectionChangedEvent(PersonPanelSelectionChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        ReadOnlyPerson person = event.getNewSelection().person;
        String requestedSocialType = event.getSocialType();
        Person p = new Person(person);
        Iterator<SocialInfo> iterator = p.getSocialInfos().iterator();
        if (iterator.hasNext()) {
            // if there is SocialInfo stored
            SocialInfo social = iterator.next();
            String socialType = social.getSocialType();
            while (!socialType.equals(requestedSocialType) && iterator.hasNext() && requestedSocialType != null) {
                // if no social type is specified, the default social type shown will be Instagram
                social = iterator.next();
            }

            String url = social.getSocialUrl();
            loadPage(url);
        } else {
            loadPersonPage(event.getNewSelection().person);
        }
    }

    //@@author alexfoodw
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
    //@@author
}
