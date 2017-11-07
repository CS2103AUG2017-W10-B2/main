package seedu.address.ui;

import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import seedu.address.commons.core.Config;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.ThemeSettings;
import seedu.address.commons.events.ui.ExitAppRequestEvent;
import seedu.address.commons.events.ui.ShowHelpRequestEvent;
import seedu.address.commons.util.FxViewUtil;
import seedu.address.logic.Logic;
import seedu.address.model.UserPrefs;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Region> {

    private static final String ICON = "/images/kaypoh_icon_32.png";
    private static final String FXML = "MainWindow.fxml";
    //@@author keithsoc
    private static final int MIN_HEIGHT = 700;
    private static final int MIN_WIDTH = 600;
    //@@author
    private double xOffset = 0;
    private double yOffset = 0;

    private final Logger logger = LogsCenter.getLogger(this.getClass());

    private Stage primaryStage;
    //@@author keithsoc
    private Scene scene;
    //@@author
    private Logic logic;

    // Independent Ui parts residing in this Ui container
    private BrowserPanel browserPanel;
    private PersonListPanel personListPanel;
    private Config config;
    private UserPrefs prefs;

    //@@author keithsoc
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem helpMenuItem;
    @FXML
    private Button minimiseButton;
    @FXML
    private Button maximiseButton;
    //@@author
    @FXML
    private StackPane browserPlaceholder;
    @FXML
    private StackPane commandBoxPlaceholder;
    @FXML
    private StackPane personListPanelPlaceholder;
    @FXML
    private StackPane resultDisplayPlaceholder;
    @FXML
    private StackPane statusbarPlaceholder;

    public MainWindow(Stage primaryStage, Config config, UserPrefs prefs, Logic logic) {
        super(FXML);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;
        this.config = config;
        this.prefs = prefs;

        // Configure the UI
        setTitle(config.getAppTitle());
        setIcon(ICON);
        setWindowMinSize();
        setWindowDefaultSize(prefs);

        // Set theme
        scene = new Scene(getRoot());
        //@@author keithsoc
        scene.setFill(Color.TRANSPARENT);
        setDefaultTheme(prefs, scene);
        UiTheme.getInstance().setScene(scene);
        //@@author
        primaryStage.setScene(scene);

        //@@author keithsoc
        // Enable window navigation
        enableMovableWindow();
        enableMinimiseWindow();
        enableMaximiseWindow();
        UiResize.enableResizableWindow(primaryStage, MIN_WIDTH, MIN_HEIGHT, Double.MAX_VALUE, Double.MAX_VALUE);
        //@@author

        setAccelerators();
        registerAsAnEventHandler(this);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in later version of SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField, and
         * ResultDisplay contains a TextArea, thus some accelerators (e.g F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add following event filter to capture such key events and open
         * help window purposely so to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        browserPanel = new BrowserPanel(scene);
        //@@author alexfoodw
        browserPanel.setLogic(logic);
        //@@author
        UiTheme.getInstance().setBrowserPanel(browserPanel);
        browserPlaceholder.getChildren().add(browserPanel.getRoot());

        personListPanel = new PersonListPanel(logic.getFilteredPersonList());
        personListPanelPlaceholder.getChildren().add(personListPanel.getRoot());

        ResultDisplay resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(prefs.getAddressBookFilePath(),
                logic.getFilteredPersonList().size());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        CommandBox commandBox = new CommandBox(logic);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());
    }

    void hide() {
        primaryStage.hide();
    }

    private void setTitle(String appTitle) {
        primaryStage.setTitle(appTitle);
    }

    /**
     * Sets the given image as the icon of the main window.
     * @param iconSource e.g. {@code "/images/help_icon.png"}
     */
    private void setIcon(String iconSource) {
        FxViewUtil.setStageIcon(primaryStage, iconSource);
    }

    /**
     * Sets the default size based on user preferences.
     */
    private void setWindowDefaultSize(UserPrefs prefs) {
        primaryStage.setHeight(prefs.getGuiSettings().getWindowHeight());
        primaryStage.setWidth(prefs.getGuiSettings().getWindowWidth());
        if (prefs.getGuiSettings().getWindowCoordinates() != null) {
            primaryStage.setX(prefs.getGuiSettings().getWindowCoordinates().getX());
            primaryStage.setY(prefs.getGuiSettings().getWindowCoordinates().getY());
        }
    }

    private void setWindowMinSize() {
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setMinWidth(MIN_WIDTH);
    }

    //@@author keithsoc
    /**
     * Sets the default theme based on user preferences.
     */
    private void setDefaultTheme(UserPrefs prefs, Scene scene) {
        scene.getStylesheets().addAll(prefs.getThemeSettings().getTheme(),
                prefs.getThemeSettings().getThemeExtensions());
    }

    /**
     * Returns the current theme applied.
     */
    ThemeSettings getCurrentThemeSetting() {
        String cssMain = scene.getStylesheets().get(0);
        String cssExtensions = scene.getStylesheets().get(1);
        return new ThemeSettings(cssMain, cssExtensions);
    }
    //@@author

    /**
     * Returns the current size and the position of the main Window.
     */
    GuiSettings getCurrentGuiSetting() {
        return new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
    }

    /**
     * Opens the help window.
     */
    @FXML
    public void handleHelp() {
        HelpWindow helpWindow = new HelpWindow();
        helpWindow.show();
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        raise(new ExitAppRequestEvent());
    }

    public PersonListPanel getPersonListPanel() {
        return this.personListPanel;
    }

    void releaseResources() {
        browserPanel.freeResources();
    }

    @Subscribe
    private void handleShowHelpEvent(ShowHelpRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        handleHelp();
    }

    //@@author keithsoc
    /**
     * Enable movable window.
     */
    private void enableMovableWindow() {
        menuBar.setOnMousePressed((event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        menuBar.setOnMouseDragged((event) -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });
    }

    /**
     * Enable minimising of window.
     */
    private void enableMinimiseWindow() {
        minimiseButton.setOnMouseClicked((event) ->
                primaryStage.setIconified(true)
        );
    }

    /**
     * Enable maximising and restoring pre-maximised state of window.
     * Change button images respectively via css.
     */
    private void enableMaximiseWindow() {
        maximiseButton.setOnMouseClicked((event) -> {
            primaryStage.setMaximized(true);
            maximiseButton.setId("restoreButton");
        });

        maximiseButton.setOnMousePressed((event) -> {
            primaryStage.setMaximized(false);
            maximiseButton.setId("maximiseButton");
        });
    }
    //@@author
}
