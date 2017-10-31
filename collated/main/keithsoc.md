# keithsoc
###### /main/java/seedu/address/commons/core/GuiSettings.java
``` java
    private static final double DEFAULT_HEIGHT = 900;
    private static final double DEFAULT_WIDTH = 1600;
```
###### /main/java/seedu/address/commons/core/index/Index.java
``` java
    /**
     * Implement comparable for usage such as {@code Collections.max}
     */
    @Override
    public int compareTo(Index idx) {
        return Double.compare(getOneBased(), idx.getOneBased());
    }
```
###### /main/java/seedu/address/commons/core/Messages.java
``` java
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX_MULTI = "One or more indexes provided is invalid";
```
###### /main/java/seedu/address/commons/core/ThemeSettings.java
``` java
/**
 * A Serializable class that contains the Theme settings.
 */
public class ThemeSettings implements Serializable {

    private static final String DEFAULT_THEME = "view/ThemeDay.css";
    private static final String DEFAULT_THEME_EXTENSIONS = "view/ThemeDayExtensions.css";

    private String theme;
    private String themeExtensions;

    public ThemeSettings() {
        this.theme = DEFAULT_THEME;
        this.themeExtensions = DEFAULT_THEME_EXTENSIONS;
    }

    public ThemeSettings(String theme, String themeExtensions) {
        this.theme = theme;
        this.themeExtensions = themeExtensions;
    }

    public String getTheme() {
        return theme;
    }

    public String getThemeExtensions() {
        return themeExtensions;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ThemeSettings)) { // this handles null as well.
            return false;
        }

        ThemeSettings o = (ThemeSettings) other;

        return Objects.equals(theme, o.theme)
                && Objects.equals(themeExtensions, o.themeExtensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theme, themeExtensions);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Theme : " + theme + "\n");
        sb.append("Theme Extensions : " + themeExtensions);
        return sb.toString();
    }
}
```
###### /main/java/seedu/address/logic/commands/FavoriteCommand.java
``` java
/**
 * Favorites the person(s) identified using it's last displayed index from the address book.
 */
public class FavoriteCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "fav";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Favorites the person(s) identified by the index number used in the last person listing.\n"
            + "Parameters: INDEX [ADDITIONAL INDEXES] (INDEX must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1\n"
            + "Example: " + COMMAND_WORD + " 1 2 3";

    public static final String MESSAGE_FAVORITE_PERSON_SUCCESS = "Added as favorite contact(s): %1$s";

    private final List<Index> targetIndexList;

    public FavoriteCommand(List<Index> targetIndexList) {
        this.targetIndexList = targetIndexList;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();
        StringBuilder names = new StringBuilder();

        /*
         * First, efficiently check whether user input any index larger than address book size with Collections.max
         * This is to avoid the following situation:
         *
         * E.g. AddressBook size is 100
         * Execute "fav 6 7 101 8 9" ->
         * persons at indexes 7 and 8 gets favorited but method halts due to CommandException for index 101 and
         * person at index 9 and beyond does not get favorited
         */
        if (Collections.max(targetIndexList).getOneBased() > lastShownList.size()) {
            if (targetIndexList.size() > 1) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX_MULTI);
            } else {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
        }

        /*
         * Then, as long no exception is thrown above i.e. all index are within boundaries,
         * the following loop will run
         */
        for (Index targetIndex : targetIndexList) {
            ReadOnlyPerson personToFavorite = lastShownList.get(targetIndex.getZeroBased());

            try {
                model.toggleFavoritePerson(personToFavorite, COMMAND_WORD);
                names.append("\n★ ");
                names.append(personToFavorite.getName().toString());
            } catch (DuplicatePersonException dpe) {
                throw new CommandException(EditCommand.MESSAGE_DUPLICATE_PERSON);
            } catch (PersonNotFoundException pnfe) {
                throw new AssertionError("The target person cannot be missing");
            }
        }

        return new CommandResult(String.format(MESSAGE_FAVORITE_PERSON_SUCCESS, names));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof FavoriteCommand // instanceof handles nulls
                && this.targetIndexList.equals(((FavoriteCommand) other).targetIndexList)); // state check
    }
}
```
###### /main/java/seedu/address/logic/commands/ListCommand.java
``` java
    public static final String COMMAND_OPTION_FAV = PREFIX_OPTION + FavoriteCommand.COMMAND_WORD;

    public static final String MESSAGE_SUCCESS_LIST_ALL = "Listed all persons";
    public static final String MESSAGE_SUCCESS_LIST_FAV = "Listed all favorite persons";

    private boolean hasOptionFav = false;

    public ListCommand(String args) {
        if (args.trim().equals(COMMAND_OPTION_FAV)) {
            hasOptionFav = true;
        }
    }

    @Override
    public CommandResult execute() {
        if (hasOptionFav) {
            model.updateFilteredPersonList(PREDICATE_SHOW_FAV_PERSONS);
            return new CommandResult(MESSAGE_SUCCESS_LIST_FAV);
        } else {
            model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
            return new CommandResult(MESSAGE_SUCCESS_LIST_ALL);
        }
    }
```
###### /main/java/seedu/address/logic/commands/ThemeCommand.java
``` java
/**
 * Changes the application theme to the user specified option.
 */
public class ThemeCommand extends Command {
    public static final String COMMAND_WORD = "theme";
    public static final String COMMAND_ALIAS = "t";
    public static final String COMMAND_OPTION_DAY = "day";
    public static final String COMMAND_OPTION_NIGHT = "night";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Changes the application theme to the specified option.\n"
            + "Alias: " + COMMAND_ALIAS + "\n"
            + "Parameters: -OPTION\n"
            + "Options: \n"
            + "\t" + COMMAND_OPTION_DAY + " - Changes the application theme to a light color scheme.\n"
            + "\t" + COMMAND_OPTION_NIGHT + " - Changes the application theme to a dark color scheme.\n"
            + "Example: \n"
            + "\t" + COMMAND_WORD + " -" + COMMAND_OPTION_DAY + "\n"
            + "\t" + COMMAND_WORD + " -" + COMMAND_OPTION_NIGHT + "\n";

    public static final String MESSAGE_THEME_CHANGE_SUCCESS = "Theme successfully applied! ✓";

    private final String optedTheme;

    public ThemeCommand (String args) {
        this.optedTheme = args;
    }

    @Override
    public CommandResult execute() throws CommandException {
        UiTheme.getInstance().changeTheme(optedTheme);
        return new CommandResult(MESSAGE_THEME_CHANGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ThemeCommand // instanceof handles nulls
                && this.optedTheme.equals(((ThemeCommand) other).optedTheme)); // state check
    }
}
```
###### /main/java/seedu/address/logic/commands/UnFavoriteCommand.java
``` java
/**
 * Unfavorites the person(s) identified using it's last displayed index from the address book.
 */
public class UnFavoriteCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "unfav";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Unfavorites the person(s) identified by the index number used in the last person listing.\n"
            + "Parameters: INDEX (must be a positive integer) or INDEXES\n"
            + "Example: " + COMMAND_WORD + " 1 OR " + COMMAND_WORD + " 1 2 3";

    public static final String MESSAGE_UNFAVORITE_PERSON_SUCCESS = "Removed from favorite contact(s): %1$s";

    private final List<Index> targetIndexList;

    public UnFavoriteCommand(List<Index> targetIndexList) {
        this.targetIndexList = targetIndexList;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();
        StringBuilder names = new StringBuilder();

        /*
         * First, efficiently check whether user input any index larger than address book size with Collections.max
         * This is to avoid the following situation:
         *
         * E.g. AddressBook size is 100
         * Execute "fav 6 7 101 8 9" ->
         * persons at indexes 7 and 8 gets favorited but method halts due to CommandException for index 101 and
         * person at index 9 and beyond does not get favorited
         */
        if (Collections.max(targetIndexList).getOneBased() > lastShownList.size()) {
            if (targetIndexList.size() > 1) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX_MULTI);
            } else {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
        }

        /*
         * Then, as long no exception is thrown above i.e. all index are within boundaries,
         * the following loop will run
         */
        for (Index targetIndex : targetIndexList) {
            ReadOnlyPerson personToUnFavorite = lastShownList.get(targetIndex.getZeroBased());

            try {
                model.toggleFavoritePerson(personToUnFavorite, COMMAND_WORD);
                names.append("\n");
                names.append(personToUnFavorite.getName().toString());
            } catch (DuplicatePersonException dpe) {
                throw new CommandException(EditCommand.MESSAGE_DUPLICATE_PERSON);
            } catch (PersonNotFoundException pnfe) {
                throw new AssertionError("The target person cannot be missing");
            }
        }

        return new CommandResult(String.format(MESSAGE_UNFAVORITE_PERSON_SUCCESS, names));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UnFavoriteCommand // instanceof handles nulls
                && this.targetIndexList.equals(((UnFavoriteCommand) other).targetIndexList)); // state check
    }
}
```
###### /main/java/seedu/address/logic/parser/AddressBookParser.java
``` java
        case FavoriteCommand.COMMAND_WORD:
            return new FavoriteCommandParser().parse(arguments);

        case UnFavoriteCommand.COMMAND_WORD:
            return new UnFavoriteCommandParser().parse(arguments);
```
###### /main/java/seedu/address/logic/parser/AddressBookParser.java
``` java
        case ListCommand.COMMAND_WORD:
        case ListCommand.COMMAND_ALIAS:
            return new ListCommand(arguments);
```
###### /main/java/seedu/address/logic/parser/AddressBookParser.java
``` java
        case ThemeCommand.COMMAND_WORD:
        case ThemeCommand.COMMAND_ALIAS:
            return new ThemeCommandParser().parse(arguments);
```
###### /main/java/seedu/address/logic/parser/ArgumentMultimap.java
``` java
    /**
     * Returns a boolean value that indicates whether a prefix is present in user input
     */
    public boolean isPrefixPresent(Prefix prefix) {
        return argMultimap.containsKey(prefix);
    }
```
###### /main/java/seedu/address/logic/parser/CliSyntax.java
``` java
    public static final Prefix PREFIX_FAV = new Prefix("f/");
    public static final Prefix PREFIX_UNFAV = new Prefix("uf/");
```
###### /main/java/seedu/address/logic/parser/CliSyntax.java
``` java
    public static final Prefix PREFIX_OPTION = new Prefix("-");
```
###### /main/java/seedu/address/logic/parser/FavoriteCommandParser.java
``` java
/**
 * Parses input arguments and creates a new FavoriteCommand object
 */
public class FavoriteCommandParser implements Parser<FavoriteCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the FavoriteCommand
     * and returns an FavoriteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FavoriteCommand parse(String args) throws ParseException {
        try {
            List<Index> indexList = ParserUtil.parseMultipleIndexes(args);
            return new FavoriteCommand(indexList);
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FavoriteCommand.MESSAGE_USAGE));
        }
    }
}
```
###### /main/java/seedu/address/logic/parser/ParserUtil.java
``` java
    /**
     * Parses {@code args} into an {@code List<Index>} and returns it.
     * Used for commands that need to parse multiple indexes
     * @throws IllegalValueException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static List<Index> parseMultipleIndexes(String args) throws IllegalValueException {
        // Example of proper args: " 1 2 3" (has a space in front) -> Hence apply trim() first then split
        List<String> argsList = Arrays.asList(args.trim().split("\\s+")); // split by one or more whitespaces
        List<Index> indexList = new ArrayList<>();

        for (String index : argsList) {
            indexList.add(parseIndex(index)); // Add each valid index into indexList
        }
        return indexList;
    }
```
###### /main/java/seedu/address/logic/parser/ParserUtil.java
``` java
    /**
     * Checks if favorite and unfavorite prefixes are present in {@code ArgumentMultimap argMultimap}
     * Catered for both AddCommandParser and EditCommandParser usage
     */
    public static Optional<Favorite> parseFavorite(ArgumentMultimap argMultimap,
                                         Prefix prefixFav,
                                         Prefix prefixUnFav) throws ParseException {

        // Disallow both f/ and uf/ to be present in the same instance of user input when editing
        if (argMultimap.isPrefixPresent(prefixFav) && argMultimap.isPrefixPresent(prefixUnFav)) {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                    EditCommand.MESSAGE_USAGE));
        } else if (argMultimap.isPrefixPresent(prefixFav)) { // Allow favoriting simply by supplying prefix
            if (!argMultimap.getValue(prefixFav).get().isEmpty()) { // Disallow text after prefix
                throw new ParseException(Favorite.MESSAGE_FAVORITE_CONSTRAINTS);
            } else {
                return Optional.of(new Favorite(true));
            }
        } else if (argMultimap.isPrefixPresent(prefixUnFav)) { // Allow unfavoriting simply by supplying prefix
            if (!argMultimap.getValue(prefixUnFav).get().isEmpty()) { // Disallow text after prefix
                throw new ParseException(Favorite.MESSAGE_FAVORITE_CONSTRAINTS);
            } else {
                return Optional.of(new Favorite(false));
            }
        } else {
            return Optional.empty();
        }
    }
```
###### /main/java/seedu/address/logic/parser/ThemeCommandParser.java
``` java
/**
 * Parses input arguments and creates a new ThemeCommand object
 */
public class ThemeCommandParser implements Parser<ThemeCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ThemeCommand
     * and returns an ThemeCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public ThemeCommand parse(String args) throws ParseException {
        OptionBearingArgument opArgs = new OptionBearingArgument(args);
        String trimmedArgs = opArgs.getRawArgs();

        if (trimmedArgs.isEmpty()
                || (!opArgs.getOptions().contains(ThemeCommand.COMMAND_OPTION_DAY)
                && !opArgs.getOptions().contains(ThemeCommand.COMMAND_OPTION_NIGHT))) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ThemeCommand.MESSAGE_USAGE));
        }

        return new ThemeCommand(trimmedArgs);
    }
}
```
###### /main/java/seedu/address/logic/parser/UnFavoriteCommandParser.java
``` java
/**
 * Parses input arguments and creates a new UnFavoriteCommand object
 */
public class UnFavoriteCommandParser implements Parser<UnFavoriteCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the UnFavoriteCommand
     * and returns an UnFavoriteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public UnFavoriteCommand parse(String args) throws ParseException {
        try {
            List<Index> indexList = ParserUtil.parseMultipleIndexes(args);
            return new UnFavoriteCommand(indexList);
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnFavoriteCommand.MESSAGE_USAGE));
        }
    }
}
```
###### /main/java/seedu/address/MainApp.java
``` java
    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting AddressBook " + MainApp.VERSION);
        /*
         * Remove default window decorations
         * Have to be placed here instead of MainWindow or UiManager to prevent the following exception:
         * "Cannot set style once stage has been set visible"
         */
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        ui.start(primaryStage);
    }
```
###### /main/java/seedu/address/model/AddressBook.java
``` java
    /**
     * Sorts the persons in the address book based on the provided {@code comparator}.
     */
    public void sortPersons(Comparator<ReadOnlyPerson> comparator) {
        persons.sortPersons(comparator);
    }

    /**
     * Sets {@code personToFav} favorite field to true or false according to {@code type}.
     * Replaces the given person {@code target} in the list with {@code personToFav}.
     *
     * @throws DuplicatePersonException if updating the person's details causes the person to be equivalent to
     *      another existing person in the list.
     * @throws PersonNotFoundException if {@code target} could not be found in the list.
     */
    public void toggleFavoritePerson(ReadOnlyPerson target, String type)
            throws DuplicatePersonException, PersonNotFoundException {
        if (persons.contains(target)) {
            Person personToFav = new Person(target);
            if (type.equals(FavoriteCommand.COMMAND_WORD)) {
                personToFav.setFavorite(new Favorite(true));  // Favorite
            } else {
                personToFav.setFavorite(new Favorite(false)); // UnFavorite
            }
            persons.setPerson(target, personToFav);
        } else {
            throw new PersonNotFoundException();
        }
    }
```
###### /main/java/seedu/address/model/Model.java
``` java
    /** {@code Predicate} that consists of all ReadOnlyPerson who has been favorited */
    Predicate<ReadOnlyPerson> PREDICATE_SHOW_FAV_PERSONS = p -> p.getFavorite().isFavorite();
```
###### /main/java/seedu/address/model/Model.java
``` java
    /** Sorts the persons in the address book based on the input {@code comparator} */
    void sortPersons(Comparator<ReadOnlyPerson> comparator);

    /** Favorites or unfavorites the given person */
    void toggleFavoritePerson(ReadOnlyPerson target, String type)
            throws DuplicatePersonException, PersonNotFoundException;
```
###### /main/java/seedu/address/model/ModelManager.java
``` java
    @Override
    public void toggleFavoritePerson(ReadOnlyPerson target, String type)
            throws DuplicatePersonException, PersonNotFoundException {
        requireAllNonNull(target, type);
        addressBook.toggleFavoritePerson(target, type);
        indicateAddressBookChanged();
    }
```
###### /main/java/seedu/address/model/person/Favorite.java
``` java
/**
 * Represents a Favorite status in the address book.
 */
public class Favorite {

    public static final String MESSAGE_FAVORITE_CONSTRAINTS = "Only prefix is required.";
    private boolean value;

    /**
     * Allow only 'true' or 'false' values specified in AddCommandParser, EditCommandParser and test files.
     * If user specifies "f/"  : pass in 'true'
     * If user specifies "uf/" : pass in 'false'
     */
    public Favorite(boolean isFav) {
        this.value = isFav;
    }

    /**
     * Getter-method for returning favorite status
     */
    public boolean isFavorite() {
        return this.value;
    }

    /**
     * Formats 'true'/'false' values to "Yes"/"No" Strings to be displayed to user
     */
    @Override
    public String toString() {
        return isFavorite() ? "Yes" : "No";
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Favorite // instanceof handles nulls
                && this.value == (((Favorite) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }

}
```
###### /main/java/seedu/address/model/person/Person.java
``` java
    private ObjectProperty<Favorite> favorite;
```
###### /main/java/seedu/address/model/person/Person.java
``` java
    public void setFavorite(Favorite favorite) {
        this.favorite.set(requireNonNull(favorite));
    }

    @Override
    public ObjectProperty<Favorite> favoriteProperty() {
        return favorite;
    }

    @Override
    public Favorite getFavorite() {
        return favorite.get();
    }
```
###### /main/java/seedu/address/model/person/ReadOnlyPerson.java
``` java
    ObjectProperty<Favorite> favoriteProperty();
    Favorite getFavorite();
```
###### /main/java/seedu/address/model/UserPrefs.java
``` java
    private ThemeSettings themeSettings;
```
###### /main/java/seedu/address/model/UserPrefs.java
``` java
    private String addressBookName = "KayPoh!";
```
###### /main/java/seedu/address/model/UserPrefs.java
``` java
    public UserPrefs() {
        this.setGuiSettings(1600, 900, 0, 0);
        this.setThemeSettings("view/ThemeDay.css", "view/ThemeDayExtensions.css");
    }
```
###### /main/java/seedu/address/model/UserPrefs.java
``` java
    public ThemeSettings getThemeSettings() {
        return themeSettings == null ? new ThemeSettings() : themeSettings;
    }

    public void updateLastUsedThemeSetting(ThemeSettings themeSettings) {
        this.themeSettings = themeSettings;
    }

    public void setThemeSettings(String theme, String themeExtensions) {
        themeSettings = new ThemeSettings(theme, themeExtensions);
    }
```
###### /main/java/seedu/address/storage/XmlAdaptedPerson.java
``` java
    @XmlElement
    private boolean favorite;
```
###### /main/java/seedu/address/ui/BrowserPanel.java
``` java
    public static final String DEFAULT_PAGE_DAY = "defaultDay.html";
    public static final String DEFAULT_PAGE_NIGHT = "defaultNight.html";
```
###### /main/java/seedu/address/ui/BrowserPanel.java
``` java
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
```
###### /main/java/seedu/address/ui/MainWindow.java
``` java
    private static final int MIN_HEIGHT = 900;
    private static final int MIN_WIDTH = 1600;
```
###### /main/java/seedu/address/ui/MainWindow.java
``` java
    private Scene scene;
```
###### /main/java/seedu/address/ui/MainWindow.java
``` java
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem helpMenuItem;
    @FXML
    private Button minimiseButton;
    @FXML
    private Button maximiseButton;
```
###### /main/java/seedu/address/ui/MainWindow.java
``` java
        scene.setFill(Color.TRANSPARENT);
        setDefaultTheme(prefs, scene);
        UiTheme.getInstance().setScene(scene);
```
###### /main/java/seedu/address/ui/MainWindow.java
``` java
        // Enable window navigation
        enableMovableWindow();
        enableMinimiseWindow();
        enableMaximiseWindow();
        UiResize.enableResizableWindow(primaryStage);
```
###### /main/java/seedu/address/ui/MainWindow.java
``` java
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
```
###### /main/java/seedu/address/ui/MainWindow.java
``` java
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
```
###### /main/java/seedu/address/ui/PersonCard.java
``` java
    private static HashMap<ReadOnlyPerson, String> personColors = new HashMap<>();
    private static HashMap<String, String> tagColors = new HashMap<>();
    private static Random random = new Random();
    private static final String defaultThemeTagColor = "#fc4465";
    private static final double GOLDEN_RATIO = 0.618033988749895;
```
###### /main/java/seedu/address/ui/PersonCard.java
``` java
    @FXML
    private StackPane profilePhotoStackPane;
    @FXML
    private ImageView profilePhotoImageView;
```
###### /main/java/seedu/address/ui/PersonCard.java
``` java
    /**
     * Generates a random pastel color for profile photos.
     * @return String containing hex value of the color.
     */
    private String generateRandomPastelColor() {
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);

        Color mixer = new Color(235, 235, 235);
        red = (red + mixer.getRed()) / 2;
        green = (green + mixer.getGreen()) / 2;
        blue = (blue + mixer.getBlue()) / 2;

        Color result = new Color(red, green, blue);
        return String.format("#%02x%02x%02x", result.getRed(), result.getGreen(), result.getBlue());
    }

    /**
     * Generates a random bright color (using golden ratio for even color distribution) for tag labels.
     * @return String containing hex value of the color.
     */
    private String generateRandomColor() {
        float randomHue = random.nextFloat();
        randomHue += GOLDEN_RATIO;
        randomHue = randomHue % 1;

        Color result = Color.getHSBColor(randomHue, 0.5f, 0.85f);
        return String.format("#%02x%02x%02x", result.getRed(), result.getGreen(), result.getBlue());
    }

    /**
     * Binds a profile photo background with a random pastel color and store it into personColors HashMap.
     */
    private String getColorForPerson(ReadOnlyPerson person) {
        if (!personColors.containsKey(person)) {
            personColors.put(person, generateRandomPastelColor());
        }
        return personColors.get(person);
    }


    /**
     * Binds a tag label with a specific or random color and store it into tagColors HashMap.
     */
    private String getColorForTag(String tagValue) {
        if (!tagColors.containsKey(tagValue)) {
            if (tagValue.equalsIgnoreCase("family")) {
                tagColors.put(tagValue, defaultThemeTagColor); // Assign a default value for "family" tags
            } else {
                tagColors.put(tagValue, generateRandomColor());
            }
        }
        return tagColors.get(tagValue);
    }
```
###### /main/java/seedu/address/ui/PersonCard.java
``` java
    /**
     * Adds a profile photo for each {@code person}.
     * TODO: This method will be modified for upcoming addPhoto command
     */
    private void initProfilePhoto (ReadOnlyPerson person) {
        // Round profile photo
        double value = profilePhotoImageView.getFitWidth() / 2;
        Circle clip = new Circle(value, value, value);
        profilePhotoImageView.setClip(clip);

        // Add background circle with a random pastel color
        Circle backgroundCircle = new Circle(value);
        backgroundCircle.setFill(Paint.valueOf(getColorForPerson(person)));

        // Add text
        Text personInitialsText = new Text(extractInitials(person));
        personInitialsText.setFill(Paint.valueOf("white"));
        profilePhotoStackPane.getChildren().addAll(backgroundCircle, personInitialsText);
    }

    /**
     * Extracts the initials from the name of the given {@code person}.
     * Extract only one initial if the name contains a single word;
     * Extract two initials if the name contains more than one word.
     */
    private String extractInitials (ReadOnlyPerson person) {
        String name = person.getName().toString().trim();
        int noOfInitials = 1;
        if (name.split("\\s+").length > 1) {
            noOfInitials = 2;
        }
        return name.replaceAll("(?<=\\w)\\w+(?=\\s)\\s+", "").substring(0, noOfInitials);
    }

    /**
     * Adds a star metaphor icon for each favorite {@code person}
     */
    private void initFavorite(ReadOnlyPerson person) {
        if (person.getFavorite().isFavorite()) {
            favoriteImageView.setId("favorite");
        }
    }
```
###### /main/java/seedu/address/ui/UiTheme.java
``` java
/**
 * A singleton class that manages the changing of scene graph's stylesheets at runtime.
 */
public class UiTheme {
    public static final String THEME_DAY = "view/ThemeDay.css";
    public static final String THEME_NIGHT = "view/ThemeNight.css";
    public static final String THEME_DAY_EXTENSIONS = "view/ThemeDayExtensions.css";
    public static final String THEME_NIGHT_EXTENSIONS = "view/ThemeNightExtensions.css";

    private static UiTheme uiTheme = new UiTheme();
    private Scene scene;
    private BrowserPanel browserPanel;

    private UiTheme() {
        // Prevents any other class from instantiating
    }

    /**
     * @return instance of UiTheme
     */
    public static UiTheme getInstance() {
        return uiTheme;
    }

    /**
     * Sets the root scene graph obtained from MainWindow.
     * @param scene
     */
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    /**
     * Sets the browser panel obtained from MainWindow right after its instance is created.
     * @param browserPanel
     */
    public void setBrowserPanel(BrowserPanel browserPanel) {
        this.browserPanel = browserPanel;
    }

    /**
     * Changes the theme based on user input and
     * loads the corresponding default html page.
     * @param option
     */
    public void changeTheme(String option) {
        scene.getStylesheets().clear();

        if (option.equals(PREFIX_OPTION + ThemeCommand.COMMAND_OPTION_DAY)) {
            scene.getStylesheets().setAll(THEME_DAY, THEME_DAY_EXTENSIONS);
            browserPanel.loadDefaultPage(scene);
        } else {
            scene.getStylesheets().setAll(THEME_NIGHT, THEME_NIGHT_EXTENSIONS);
            browserPanel.loadDefaultPage(scene);
        }
    }
}
```
###### /main/resources/view/MainWindow.fxml
``` fxml
<VBox fx:id="rootVBox" xmlns="http://javafx.com/javafx/8.0.121" xmlns:fx="http://javafx.com/fxml/1">
    <HBox fx:id="rootHBox">
        <MenuBar fx:id="menuBar" HBox.hgrow="ALWAYS">
            <Menu mnemonicParsing="false" text="File">
                <MenuItem mnemonicParsing="false" onAction="#handleExit" text="Exit" />
            </Menu>
            <Menu mnemonicParsing="false" text="Help">
                <MenuItem fx:id="helpMenuItem" mnemonicParsing="false" onAction="#handleHelp" text="Help" />
            </Menu>
        </MenuBar>
        <Button fx:id="minimiseButton" mnemonicParsing="false" />
        <Button fx:id="maximiseButton" mnemonicParsing="false" />
        <Button fx:id="closeButton" mnemonicParsing="false" onAction="#handleExit" />
    </HBox>
```
###### /main/resources/view/PersonListCard.fxml
``` fxml
    <VBox alignment="CENTER" minHeight="105" prefHeight="105" prefWidth="120">
        <StackPane fx:id="profilePhotoStackPane" styleClass="profile-photo-pane">
            <ImageView fx:id="profilePhotoImageView" fitHeight="85" fitWidth="85"
                       preserveRatio="true" styleClass="profile-photo" />
        </StackPane>
    </VBox>
```
###### /main/resources/view/PersonListCard.fxml
``` fxml
    <VBox alignment="TOP_RIGHT" minHeight="105" prefWidth="40">
        <padding>
            <Insets bottom="5" left="5" right="10" top="8" />
        </padding>
        <ImageView fx:id="favoriteImageView" fitHeight="32" fitWidth="32" preserveRatio="true" />
    </VBox>
```
###### /main/resources/view/ThemeDay.css
``` css
/* Begin Styling for Default Web Page (used in default.html file) */

.background {
    background-color: #f4f4f4;
    background-image: url("../images/background_day.png");
}

.center {
    display: flex;
    position: fixed;
    align-items: center;
    justify-content: center;
    height: 100%;
    width: 100%;
}

.text {
    font-family: "Segoe UI";
    font-size: 25px;
    color: white;

    display: inline-block;
    text-align: center;

    background-color: rgba(0, 0, 0, 0.3);
    border-radius: 50px;
    padding: 10px 30px 12px 30px;
}

/* Begin Styling for JavaFX components */

/* Round Borders */

#rootVBox, #rootHBox {
    -fx-border-radius: 10;
    -fx-background-radius: 10;
}

#statusBarFooter, #syncStatus, #saveLocationStatus {
    -fx-border-radius: 0 0 10 10;
    -fx-background-radius: 0 10 10 10;
}

/* Tab Pane */

.tab-pane {
    -fx-padding: 0 0 0 1;
}

.tab-pane .tab-header-area {
    -fx-padding: 0 0 0 0;
    -fx-min-height: 0;
    -fx-max-height: 0;
}

/* Table View */

.table-view {
    -fx-base: #d13438;
    -fx-control-inner-background: #d13438;
    -fx-background-color: #d13438;
    -fx-table-cell-border-color: transparent;
    -fx-table-header-border-color: transparent;
    -fx-padding: 5;
}

.table-view .column-header-background {
    -fx-background-color: transparent;
}

.table-view .column-header, .table-view .filler {
    -fx-size: 35;
    -fx-border-width: 0 0 1 0;
    -fx-background-color: transparent;
    -fx-border-color: transparent transparent derive(-fx-base, 80%) transparent;
    -fx-border-insets: 0 10 1 0;
}

.table-view .column-header .label {
    -fx-font-size: 20pt;
    -fx-font-family: "Segoe UI Light";
    -fx-text-fill: white;
    -fx-alignment: center-left;
}

.table-view:focused .table-row-cell:filled:focused:selected {
    -fx-background-color: -fx-focus-color;
}

/* Split Pane Divider */

.split-pane:horizontal .split-pane-divider {
    -fx-background-color: derive(#cdcdcd, 20%);
    -fx-border-radius: 10 10 0 0;
    -fx-background-radius: 10 10 0 0;
    -fx-background-insets: 5 0 0 0;
}

/* Split Pane Consisting of Person List Cards + Browser Panel */

.split-pane {
    -fx-border-width: 0 1 0 1;
    -fx-border-color: #cdcdcd;
    -fx-background-color: #ffffff;
}

/* Profile Photo */

.profile-photo-pane {
    -fx-font-family: "Segoe UI Light";
    -fx-font-size: 30px;
}

/* Person List Card Cells */

.list-view {
    -fx-background-color: #ffffff;
    -fx-background-insets: 0;
    -fx-padding: 0;
}

.list-cell {
    -fx-label-padding: 0 0 0 0;
    -fx-graphic-text-gap: 0;
    -fx-padding: 0 0 0 0;
    -fx-border-radius: 5;
    -fx-background-radius: 5;
}

.list-cell:filled:even {
    -fx-background-color: #f4f4f4;
}

.list-cell:filled:odd {
    -fx-background-color: #ffffff;
}

.list-cell:filled:selected {
    -fx-background-color: #b3e5ff;
}

/* Person List Card Fonts */

.cell_big_label {
    -fx-font-family: "Helvetica";
    -fx-font-size: 25px;
    -fx-text-fill: black;
    -fx-effect: dropshadow(one-pass-box, rgba(0, 0, 0, 0.5), 1, 0, 0, 0); /* Add extra darkness to font albeit subtly */
}

.cell_small_label {
    -fx-font-family: "Helvetica";
    -fx-font-size: 16px;
    -fx-text-fill: #1f1f1f;
}

/* Command Box & Result Display Box Background */

.anchor-pane {
    -fx-background-color: #ffffff;
}

.pane-with-border {
    -fx-background-color: #ffffff;
    -fx-border-top-width: 1px;
    -fx-border-width: 0 1 0 1;
    -fx-border-color: #cdcdcd;
}

/* Result Display Box */

.result-display {
    -fx-background-color: #f4f4f4;
    -fx-border-radius: 5;
    -fx-background-radius: 5;
    -fx-font-family: "Segoe UI";
    -fx-font-size: 14pt;
    -fx-text-fill: black;
}

.result-display .label {
    -fx-text-fill: black !important;
}

/* Status Bar */

.status-bar {
    -fx-background-color: #1f1f1f;
    -fx-text-fill: white;
}

.status-bar .label {
    -fx-font-family: "Segoe UI";
    -fx-text-fill: white;
}

/* Grid */

.grid-pane {
    -fx-background-color: derive(#1f1f1f, 10%);
    -fx-border-color: derive(#1f1f1f, 10%);
    -fx-border-width: 5px;
}

.grid-pane .anchor-pane {
    -fx-background-color: derive(#1f1f1f, 10%);
}

/* Context Menu */

.context-menu {
    -fx-background-color: derive(#d13438, 10%);
}

.context-menu .label {
    -fx-text-fill: white;
}

/* Menu */

.menu-bar {
    -fx-background-color: #d13438;
    -fx-border-radius: 10 0 0 0;
    -fx-background-radius: 10 0 0 0;
    -fx-border-color: #d13438;
    -fx-border-width: 5px;
}

.menu-bar .label {
    -fx-font-size: 14pt;
    -fx-font-family: "Segoe UI";
    -fx-text-fill: white;
}

.menu:hover, .menu:showing {
    -fx-background-color: #d55b5e;
}

.menu-item:hover, .menu-item:focused {
    -fx-background-color: #d55b5e;
}

.menu-item:pressed {
    -fx-background-color: #f1707a;
}

/* Button */

.button {
    -fx-padding: 12 25 12 35;
    -fx-background-radius: 0;
    -fx-background-color: #d13438;
    -fx-font-family: "Segoe UI", Helvetica, Arial, sans-serif;
    -fx-font-size: 11pt;
    -fx-text-fill: #d8d8d8;
    -fx-background-insets: 0 0 0 0, 0, 1, 2;
}

.button:hover {
    -fx-background-color: #d55b5e;
}

.button:pressed, .button:default:hover:pressed {
    -fx-background-color: #f1707a;
}

.button:disabled, .button:default:disabled {
    -fx-opacity: 0.4;
    -fx-background-color: #d13438;
    -fx-text-fill: white;
}

/* Dialog */

.dialog-pane {
    -fx-background-color: #1d1d1d;
}

.dialog-pane > *.button-bar > *.container {
    -fx-background-color: #1d1d1d;
}

.dialog-pane > *.label.content {
    -fx-font-size: 14px;
    -fx-font-weight: bold;
    -fx-text-fill: black;
}

.dialog-pane:header *.header-panel {
    -fx-background-color: derive(#1d1d1d, 25%);
}

.dialog-pane:header *.header-panel *.label {
    -fx-font-size: 18px;
    -fx-font-style: italic;
    -fx-fill: black;
    -fx-text-fill: black;
}

/* Scroll Bar */

.scroll-bar {
    -fx-background-color: transparent;
}

.scroll-bar .thumb {
    -fx-background-color: derive(#d13438, 50%);
    -fx-background-insets: 3;
}

.scroll-bar .increment-button, .scroll-bar .decrement-button {
    -fx-background-color: transparent;
    -fx-padding: 0 0 0 0;
}

.scroll-bar .increment-arrow, .scroll-bar .decrement-arrow {
    -fx-shape: " ";
}

.scroll-bar:vertical .increment-arrow, .scroll-bar:vertical .decrement-arrow {
    -fx-padding: 1 6 1 6;
}

.scroll-bar:horizontal .increment-arrow, .scroll-bar:horizontal .decrement-arrow {
    -fx-padding: 6 1 7 6;
}

/* Command Box */

#commandTextField {
    -fx-padding: 11 11 11 11;
    -fx-background-color: #f4f4f4;
    -fx-border-radius: 5;
    -fx-background-radius: 5;
    -fx-font-family: "Segoe UI Semibold";
    -fx-font-size: 16pt;
    -fx-text-fill: black;
}

#commandTextField:focused {
    -fx-background-color: #fff4de;
}

/* Result Display Box */

#resultDisplay .content {
    -fx-background-color: #f4f4f4;
}

#filterField, #personListPanel, #personWebpage {
    -fx-effect: innershadow(gaussian, black, 10, 0, 0, 0);
}

/* Person List Card Root HBox */

#cardPane {
    -fx-background-color: transparent;
    -fx-border-width: 0;
}

/* Tags */

#tags {
    -fx-hgap: 8;
    -fx-vgap: 4;
}

#tags .label {
    -fx-font-family: "Helvetica";
    -fx-font-size: 11pt;
    -fx-font-weight: bold;
    -fx-text-fill: white;
    -fx-padding: 1 8 1 8;
    -fx-background-color: #fc4465;
    -fx-border-radius: 5;
    -fx-background-radius: 5;
}

#favorite {
    -fx-image: url("../images/fav_icon_red.png");
}

/* Window Buttons */

#closeButton {
    -fx-background-image: url("../images/close.png");
    -fx-background-size: 35px 35px;
    -fx-background-repeat: no-repeat;
    -fx-background-position: center;
    -fx-border-radius: 0 10 0 0;
    -fx-background-radius: 0 10 0 0;
}

#maximiseButton {
    -fx-background-image: url("../images/maximise.png");
    -fx-background-size: 35px 35px;
    -fx-background-repeat: no-repeat;
    -fx-background-position: center;
}

#restoreButton {
    -fx-background-image: url("../images/restore.png");
    -fx-background-size: 35px 35px;
    -fx-background-repeat: no-repeat;
    -fx-background-position: center;
}

#minimiseButton {
    -fx-background-image: url("../images/minimise.png");
    -fx-background-size: 35px 35px;
    -fx-background-repeat: no-repeat;
    -fx-background-position: center;
}
```
###### /main/resources/view/ThemeDayExtensions.css
``` css
.error {
    -fx-text-fill: #ff0000 !important; /* The error class should always override the default text-fill style */
    -fx-background-color: #ffdede !important;
}

.list-cell:empty {
    /* Empty cells will not have alternating colours */
    -fx-background: #ffffff;
}
```
###### /main/resources/view/ThemeNight.css
``` css
/* Begin Styling for Default Web Page (used in default.html file) */

.background {
    background-color: #272822;
    background-image: url("../images/background_night.png");
}

.center {
    display: flex;
    position: fixed;
    align-items: center;
    justify-content: center;
    height: 100%;
    width: 100%;
}

.text {
    font-family: "Segoe UI";
    font-size: 25px;
    color: white;

    display: inline-block;
    text-align: center;

    background-color: rgba(0, 0, 0, 0.4);
    border-radius: 50px;
    padding: 10px 30px 12px 30px;
}

/* Begin Styling for JavaFX components */

/* Round Borders */

#rootVBox, #rootHBox {
    -fx-border-radius: 10;
    -fx-background-radius: 10;
}

#statusBarFooter, #syncStatus, #saveLocationStatus {
    -fx-border-radius: 0 0 10 10;
    -fx-background-radius: 0 10 10 10;
}

/* Tab Pane */

.tab-pane {
    -fx-padding: 0 0 0 1;
}

.tab-pane .tab-header-area {
    -fx-padding: 0 0 0 0;
    -fx-min-height: 0;
    -fx-max-height: 0;
}

/* Table View */

.table-view {
    -fx-base: #d13438;
    -fx-control-inner-background: #d13438;
    -fx-background-color: #d13438;
    -fx-table-cell-border-color: transparent;
    -fx-table-header-border-color: transparent;
    -fx-padding: 5;
}

.table-view .column-header-background {
    -fx-background-color: transparent;
}

.table-view .column-header, .table-view .filler {
    -fx-size: 35;
    -fx-border-width: 0 0 1 0;
    -fx-background-color: transparent;
    -fx-border-color: transparent transparent derive(-fx-base, 80%) transparent;
    -fx-border-insets: 0 10 1 0;
}

.table-view .column-header .label {
    -fx-font-size: 20pt;
    -fx-font-family: "Segoe UI Light";
    -fx-text-fill: white;
    -fx-alignment: center-left;
}

.table-view:focused .table-row-cell:filled:focused:selected {
    -fx-background-color: -fx-focus-color;
}

/* Split Pane Divider */

.split-pane:horizontal .split-pane-divider {
    -fx-background-color: derive(#3d3e35, 20%);
    -fx-border-radius: 10 10 0 0;
    -fx-background-radius: 10 10 0 0;
    -fx-background-insets: 5 0 0 0;
}

/* Split Pane Consisting of Person List Cards + Browser Panel */

.split-pane {
    -fx-border-width: 0 1 0 1;
    -fx-border-color: #272822;
    -fx-background-color: #272822;
}

/* Profile Photo */

.profile-photo-pane {
    -fx-font-family: "Segoe UI Light";
    -fx-font-size: 31px;
}

/* Person List Card Cells */

.list-view {
    -fx-background-color: #272822;
    -fx-background-insets: 0;
    -fx-padding: 0;
}

.list-cell {
    -fx-label-padding: 0 0 0 0;
    -fx-graphic-text-gap: 0;
    -fx-padding: 0 0 0 0;
    -fx-border-radius: 5;
    -fx-background-radius: 5;
}

.list-cell:filled:even {
    -fx-background-color: #272822;
}

.list-cell:filled:odd {
    -fx-background-color: #3d3e35;
}

.list-cell:filled:selected {
    -fx-background-color: #378098;
}

/* Person List Card Fonts */

.cell_big_label {
    -fx-font-family: "Helvetica";
    -fx-font-size: 25px;
    -fx-text-fill: white;
}

.cell_small_label {
    -fx-font-family: "Helvetica";
    -fx-font-size: 16px;
    -fx-text-fill: white;
}

/* Command Box & Result Display Box Background */

.anchor-pane {
    -fx-background-color: #272822;
}

.pane-with-border {
    -fx-background-color: #272822;
    -fx-border-top-width: 1px;
    -fx-border-width: 0 1 0 1;
    -fx-border-color: #272822;
}

/* Result Display Box */

.result-display {
    -fx-background-color: #4a4b40;
    -fx-border-radius: 5;
    -fx-background-radius: 5;
    -fx-font-family: "Segoe UI";
    -fx-font-size: 14pt;
    -fx-text-fill: white;
}

.result-display .label {
    -fx-text-fill: white !important;
}

/* Status Bar */

.status-bar {
    -fx-background-color: #131411;
    -fx-text-fill: white;
}

.status-bar .label {
    -fx-font-family: "Segoe UI";
    -fx-text-fill: white;
}

/* Grid */

.grid-pane {
    -fx-background-color: #131411;
    -fx-border-color: #131411;
    -fx-border-width: 5px;
}

.grid-pane .anchor-pane {
    -fx-background-color: #131411;
}

/* Context Menu */

.context-menu {
    -fx-background-color: derive(#d13438, 10%);
}

.context-menu .label {
    -fx-text-fill: white;
}

/* Menu */

.menu-bar {
    -fx-background-color: #d13438;
    -fx-border-radius: 10 0 0 0;
    -fx-background-radius: 10 0 0 0;
    -fx-border-color: #d13438;
    -fx-border-width: 5px;
}

.menu-bar .label {
    -fx-font-size: 14pt;
    -fx-font-family: "Segoe UI";
    -fx-text-fill: white;
}

.menu:hover, .menu:showing {
    -fx-background-color: #d55b5e;
}

.menu-item:hover, .menu-item:focused {
    -fx-background-color: #d55b5e;
}

.menu-item:pressed {
    -fx-background-color: #f1707a;
}

/* Button */

.button {
    -fx-padding: 12 25 12 35;
    -fx-background-radius: 0;
    -fx-background-color: #d13438;
    -fx-font-family: "Segoe UI", Helvetica, Arial, sans-serif;
    -fx-font-size: 11pt;
    -fx-text-fill: #d8d8d8;
    -fx-background-insets: 0 0 0 0, 0, 1, 2;
}

.button:hover {
    -fx-background-color: #d55b5e;
}

.button:pressed, .button:default:hover:pressed {
    -fx-background-color: #f1707a;
}

.button:disabled, .button:default:disabled {
    -fx-opacity: 0.4;
    -fx-background-color: #d13438;
    -fx-text-fill: white;
}

/* Dialog */

.dialog-pane {
    -fx-background-color: #1d1d1d;
}

.dialog-pane > *.button-bar > *.container {
    -fx-background-color: #1d1d1d;
}

.dialog-pane > *.label.content {
    -fx-font-size: 14px;
    -fx-font-weight: bold;
    -fx-text-fill: black;
}

.dialog-pane:header *.header-panel {
    -fx-background-color: derive(#1d1d1d, 25%);
}

.dialog-pane:header *.header-panel *.label {
    -fx-font-size: 18px;
    -fx-font-style: italic;
    -fx-fill: black;
    -fx-text-fill: black;
}

/* Scroll Bar */

.scroll-bar {
    -fx-background-color: #272822;
}

.scroll-bar .thumb {
    -fx-background-color: #d13438;
    -fx-background-insets: 3;
}

.scroll-bar .increment-button, .scroll-bar .decrement-button {
    -fx-background-color: transparent;
    -fx-padding: 0 0 0 0;
}

.scroll-bar .increment-arrow, .scroll-bar .decrement-arrow {
    -fx-shape: " ";
}

.scroll-bar:vertical .increment-arrow, .scroll-bar:vertical .decrement-arrow {
    -fx-padding: 1 6 1 6;
}

.scroll-bar:horizontal .increment-arrow, .scroll-bar:horizontal .decrement-arrow {
    -fx-padding: 6 1 7 6;
}

/* Command Box */

#commandTextField {
    -fx-padding: 11 11 11 11;
    -fx-background-color: #4a4b40;
    -fx-border-radius: 5;
    -fx-background-radius: 5;
    -fx-font-family: "Segoe UI Semibold";
    -fx-font-size: 16pt;
    -fx-text-fill: white;
}

#commandTextField:focused {
    -fx-background-color: #626359;
}

/* Result Display Box */

#resultDisplay .content {
    -fx-background-color: #4a4b40;
}

#filterField, #personListPanel, #personWebpage {
    -fx-effect: innershadow(gaussian, black, 10, 0, 0, 0);
}

/* Person List Card Root HBox */

#cardPane {
    -fx-background-color: transparent;
    -fx-border-width: 0;
}

/* Tags */

#tags {
    -fx-hgap: 8;
    -fx-vgap: 4;
}

#tags .label {
    -fx-font-family: "Helvetica";
    -fx-font-size: 11pt;
    -fx-font-weight: bold;
    -fx-text-fill: white;
    -fx-padding: 1 8 1 8;
    -fx-background-color: #fc4465;
    -fx-border-radius: 5;
    -fx-background-radius: 5;
}

#favorite {
    -fx-image: url("../images/fav_icon_red.png");
}

/* Window Buttons */

#closeButton {
    -fx-background-image: url("../images/close.png");
    -fx-background-size: 35px 35px;
    -fx-background-repeat: no-repeat;
    -fx-background-position: center;
    -fx-border-radius: 0 10 0 0;
    -fx-background-radius: 0 10 0 0;
}

#maximiseButton {
    -fx-background-image: url("../images/maximise.png");
    -fx-background-size: 35px 35px;
    -fx-background-repeat: no-repeat;
    -fx-background-position: center;
}

#restoreButton {
    -fx-background-image: url("../images/restore.png");
    -fx-background-size: 35px 35px;
    -fx-background-repeat: no-repeat;
    -fx-background-position: center;
}

#minimiseButton {
    -fx-background-image: url("../images/minimise.png");
    -fx-background-size: 35px 35px;
    -fx-background-repeat: no-repeat;
    -fx-background-position: center;
}
```
###### /main/resources/view/ThemeNightExtensions.css
``` css
.error {
    -fx-text-fill: #ff6161 !important; /* The error class should always override the default text-fill style */
}

.list-cell:empty {
    /* Empty cells will not have alternating colours */
    -fx-background: #272822;
}
```
###### /test/java/seedu/address/logic/commands/FavoriteCommandTest.java
``` java
/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code FavoriteCommand}.
 */
public class FavoriteCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() throws Exception {
        ReadOnlyPerson personToFavorite = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        FavoriteCommand favoriteCommand = prepareCommand(Arrays.asList(INDEX_FIRST_PERSON));

        String expectedMessage = String.format(
                FavoriteCommand.MESSAGE_FAVORITE_PERSON_SUCCESS, "\n★ " + personToFavorite.getName().toString());

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.toggleFavoritePerson(personToFavorite, FavoriteCommand.COMMAND_WORD);

        assertCommandSuccess(favoriteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() throws Exception {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        FavoriteCommand favoriteCommand = prepareCommand(Arrays.asList(outOfBoundIndex));

        assertCommandFailure(favoriteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() throws Exception {
        showFirstPersonOnly(model);

        ReadOnlyPerson personToFavorite = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        FavoriteCommand favoriteCommand = prepareCommand(Arrays.asList(INDEX_FIRST_PERSON));

        String expectedMessage = String.format(
                FavoriteCommand.MESSAGE_FAVORITE_PERSON_SUCCESS, "\n★ " + personToFavorite.getName().toString());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.toggleFavoritePerson(personToFavorite, FavoriteCommand.COMMAND_WORD);
        model.updateFilteredPersonList(p -> true);

        assertCommandSuccess(favoriteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showFirstPersonOnly(model);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        FavoriteCommand favoriteCommand = prepareCommand(Arrays.asList(outOfBoundIndex));

        assertCommandFailure(favoriteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        FavoriteCommand favoriteFirstCommand = new FavoriteCommand(Arrays.asList(INDEX_FIRST_PERSON));
        FavoriteCommand favoriteSecondCommand = new FavoriteCommand(Arrays.asList(INDEX_SECOND_PERSON));

        // same object -> returns true
        assertTrue(favoriteFirstCommand.equals(favoriteFirstCommand));

        // same values -> returns true
        FavoriteCommand favoriteFirstCommandCopy = new FavoriteCommand(Arrays.asList(INDEX_FIRST_PERSON));
        assertTrue(favoriteFirstCommand.equals(favoriteFirstCommandCopy));

        // different types -> returns false
        assertFalse(favoriteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(favoriteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(favoriteFirstCommand.equals(favoriteSecondCommand));
    }

    /**
     * Returns a {@code FavoriteCommand} with the parameter {@code index}.
     */
    private FavoriteCommand prepareCommand(List<Index> indexList) {
        FavoriteCommand favoriteCommand = new FavoriteCommand(indexList);
        favoriteCommand.setData(model, getNullStorage(), new CommandHistory(), new UndoRedoStack());
        return favoriteCommand;
    }
}
```
###### /test/java/seedu/address/logic/commands/ListCommandTest.java
``` java
    @Test
    public void execute_noOptionUnfilteredList_showsSameList() {
        assertCommandSuccess(prepareCommand(""), model, ListCommand.MESSAGE_SUCCESS_LIST_ALL, expectedModel);
    }

    @Test
    public void execute_noOptionFilteredList_showsAllPersons() {
        showFirstPersonOnly(model);
        assertCommandSuccess(prepareCommand(""), model, ListCommand.MESSAGE_SUCCESS_LIST_ALL, expectedModel);
    }

    @Test
    public void execute_noOptionExtraArgumentsUnfilteredList_showsSameList() {
        assertCommandSuccess(prepareCommand("abc"),
                model, ListCommand.MESSAGE_SUCCESS_LIST_ALL, expectedModel);

        assertCommandSuccess(prepareCommand("FaV"),
                model, ListCommand.MESSAGE_SUCCESS_LIST_ALL, expectedModel);
    }

    @Test
    public void execute_favOptionUnfilteredList_showsAllFavoritePersons() {
        expectedModel.updateFilteredPersonList(Model.PREDICATE_SHOW_FAV_PERSONS);
        assertCommandSuccess(prepareCommand(ListCommand.COMMAND_OPTION_FAV),
                model, ListCommand.MESSAGE_SUCCESS_LIST_FAV, expectedModel);
    }

    @Test
    public void execute_favOptionFilteredList_showsAllFavoritePersons() {
        showFirstPersonOnly(model);
        expectedModel.updateFilteredPersonList(Model.PREDICATE_SHOW_FAV_PERSONS);
        assertCommandSuccess(prepareCommand(ListCommand.COMMAND_OPTION_FAV),
                model, ListCommand.MESSAGE_SUCCESS_LIST_FAV, expectedModel);
    }

    /**
     * Returns a {@code ListCommand} with the parameter {@code argument}.
     */
    private ListCommand prepareCommand(String argument) {
        ListCommand listCommand = new ListCommand(argument);
        listCommand.setData(model, getNullStorage(), new CommandHistory(), new UndoRedoStack());
        return listCommand;
    }
```
###### /test/java/seedu/address/logic/commands/UnFavoriteCommandTest.java
``` java
/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code UnFavoriteCommand}.
 */
public class UnFavoriteCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() throws Exception {
        ReadOnlyPerson personToUnFavorite = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        UnFavoriteCommand unFavoriteCommand = prepareCommand(Arrays.asList(INDEX_FIRST_PERSON));

        String expectedMessage = String.format(
                UnFavoriteCommand.MESSAGE_UNFAVORITE_PERSON_SUCCESS, "\n" + personToUnFavorite.getName().toString());

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.toggleFavoritePerson(personToUnFavorite, UnFavoriteCommand.COMMAND_WORD);

        assertCommandSuccess(unFavoriteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() throws Exception {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        UnFavoriteCommand unFavoriteCommand = prepareCommand(Arrays.asList(outOfBoundIndex));

        assertCommandFailure(unFavoriteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() throws Exception {
        ReadOnlyPerson personToUnFavorite = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        UnFavoriteCommand unFavoriteCommand = prepareCommand(Arrays.asList(INDEX_FIRST_PERSON));

        String expectedMessage = String.format(
                UnFavoriteCommand.MESSAGE_UNFAVORITE_PERSON_SUCCESS, "\n" + personToUnFavorite.getName().toString());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.toggleFavoritePerson(personToUnFavorite, UnFavoriteCommand.COMMAND_WORD);

        assertCommandSuccess(unFavoriteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showFirstPersonOnly(model);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        UnFavoriteCommand unFavoriteCommand = prepareCommand(Arrays.asList(outOfBoundIndex));

        assertCommandFailure(unFavoriteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        UnFavoriteCommand unFavoriteFirstCommand = new UnFavoriteCommand(Arrays.asList(INDEX_FIRST_PERSON));
        UnFavoriteCommand favoriteSecondCommand = new UnFavoriteCommand(Arrays.asList(INDEX_SECOND_PERSON));

        // same object -> returns true
        assertTrue(unFavoriteFirstCommand.equals(unFavoriteFirstCommand));

        // same values -> returns true
        UnFavoriteCommand unFavoriteFirstCommandCopy = new UnFavoriteCommand(Arrays.asList(INDEX_FIRST_PERSON));
        assertTrue(unFavoriteFirstCommand.equals(unFavoriteFirstCommandCopy));

        // different types -> returns false
        assertFalse(unFavoriteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(unFavoriteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(unFavoriteFirstCommand.equals(favoriteSecondCommand));
    }

    /**
     * Returns a {@code UnFavoriteCommand} with the parameter {@code index}.
     */
    private UnFavoriteCommand prepareCommand(List<Index> indexList) {
        UnFavoriteCommand unFavoriteCommand = new UnFavoriteCommand(indexList);
        unFavoriteCommand.setData(model, getNullStorage(), new CommandHistory(), new UndoRedoStack());
        return unFavoriteCommand;
    }
}
```
###### /test/java/seedu/address/logic/parser/AddressBookParserTest.java
``` java
    @Test
    public void parseCommand_favorite() throws Exception {
        FavoriteCommand command = (FavoriteCommand) parser.parseCommand(
                FavoriteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new FavoriteCommand(Arrays.asList(INDEX_FIRST_PERSON)), command);
    }

    @Test
    public void parseCommand_favorite_multi() throws Exception {
        FavoriteCommand command = (FavoriteCommand) parser.parseCommand(
                FavoriteCommand.COMMAND_WORD + " "
                        + INDEX_FIRST_PERSON.getOneBased() + " "
                        + INDEX_SECOND_PERSON.getOneBased());
        assertEquals(new FavoriteCommand(Arrays.asList(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON)), command);
    }
```
###### /test/java/seedu/address/logic/parser/AddressBookParserTest.java
``` java
    @Test
    public void parseCommand_listFav() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD
                + " " + ListCommand.COMMAND_OPTION_FAV) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD
                + " " + ListCommand.COMMAND_OPTION_FAV + " 3") instanceof ListCommand);
    }
```
###### /test/java/seedu/address/logic/parser/AddressBookParserTest.java
``` java
    @Test
    public void parseCommand_unFavorite() throws Exception {
        UnFavoriteCommand command = (UnFavoriteCommand) parser.parseCommand(
                UnFavoriteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new UnFavoriteCommand(Arrays.asList(INDEX_FIRST_PERSON)), command);
    }

    @Test
    public void parseCommand_unFavorite_multi() throws Exception {
        UnFavoriteCommand command = (UnFavoriteCommand) parser.parseCommand(
                UnFavoriteCommand.COMMAND_WORD + " "
                        + INDEX_FIRST_PERSON.getOneBased() + " "
                        + INDEX_SECOND_PERSON.getOneBased());
        assertEquals(new UnFavoriteCommand(Arrays.asList(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON)), command);
    }
```
###### /test/java/seedu/address/logic/parser/FavoriteCommandParserTest.java
``` java
/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the FavoriteCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the FavoriteCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class FavoriteCommandParserTest {

    private FavoriteCommandParser parser = new FavoriteCommandParser();

    @Test
    public void parse_validArgs_returnsFavoriteCommand() {
        assertParseSuccess(parser, "1", new FavoriteCommand(Arrays.asList(INDEX_FIRST_PERSON)));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FavoriteCommand.MESSAGE_USAGE));
    }
}
```
###### /test/java/seedu/address/logic/parser/ParserUtilTest.java
``` java
    @Test
    public void parseMultiIndex_invalidInput_throwsIllegalValueException() throws Exception {
        thrown.expect(IllegalValueException.class);
        ParserUtil.parseMultipleIndexes("1 2 3 a"); // Two trailing spaces in front
    }
```
###### /test/java/seedu/address/logic/parser/ParserUtilTest.java
``` java
    @Test
    public void parseMultiIndex_validInput_success() throws Exception {
        List<Index> expectedIndexList = Arrays.asList(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON, INDEX_THIRD_PERSON);

        // No whitespaces
        assertEquals(expectedIndexList, ParserUtil.parseMultipleIndexes("1 2 3"));

        // Leading and trailing whitespaces
        assertEquals(expectedIndexList, ParserUtil.parseMultipleIndexes(" 1  2   3    "));
    }
```
###### /test/java/seedu/address/logic/parser/ThemeCommandParserTest.java
``` java
public class ThemeCommandParserTest {

    private ThemeCommandParser parser = new ThemeCommandParser();

    @Test
    public void parse_validArgs_returnsThemeCommand() {
        assertParseSuccess(parser, "-day", new ThemeCommand("-day"));
        assertParseSuccess(parser, "-night", new ThemeCommand("-night"));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, " ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ThemeCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "-",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ThemeCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "day",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ThemeCommand.MESSAGE_USAGE));
    }
}
```
###### /test/java/seedu/address/logic/parser/UnFavoriteCommandParserTest.java
``` java
/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the UnFavoriteCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the UnFavoriteCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class UnFavoriteCommandParserTest {

    private UnFavoriteCommandParser parser = new UnFavoriteCommandParser();

    @Test
    public void parse_validArgs_returnsUnFavoriteCommand() {
        assertParseSuccess(parser, "1", new UnFavoriteCommand(Arrays.asList(INDEX_FIRST_PERSON)));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnFavoriteCommand.MESSAGE_USAGE));
    }
}
```
###### /test/java/seedu/address/testutil/EditPersonDescriptorBuilder.java
``` java
    /**
     * Sets the {@code Favorite} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withFavorite(boolean favorite) {
        descriptor.setFavorite(new Favorite(favorite));
        return this;
    }
```
###### /test/java/seedu/address/testutil/modelstubs/ModelStub.java
``` java
    @Override
    public void toggleFavoritePerson(ReadOnlyPerson target, String type)
            throws DuplicatePersonException, PersonNotFoundException {
        fail("This method should not be called.");
    }
```
###### /test/java/seedu/address/testutil/PersonBuilder.java
``` java
    /**
     * Sets the {@code Favorite} of the {@code Person} that we are building.
     */
    public PersonBuilder withFavorite(boolean favorite) {
        this.person.setFavorite(new Favorite(favorite));
        return this;
    }
```
###### /test/java/seedu/address/testutil/TypicalIndexes.java
``` java
    public static final Index INDEX_FOURTH_PERSON = Index.fromOneBased(4);
    public static final Index INDEX_FIFTH_PERSON = Index.fromOneBased(5);
```
###### /test/java/systemtests/FavoriteCommandSystemTest.java
``` java
public class FavoriteCommandSystemTest extends AddressBookSystemTest {

    private static final String MESSAGE_INVALID_FAVORITE_COMMAND_FORMAT =
            String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FavoriteCommand.MESSAGE_USAGE);

    @Test
    public void favorite() {
        /* -------------- Performing favorite operation while an unfiltered list is being shown ----------------- */

        /*
         * Case: favorites the 3rd, 4th & 5th person in the list,
         * command with leading spaces and trailing spaces -> favorited
         */
        Model expectedModel = getModel();
        String command = "     " + FavoriteCommand.COMMAND_WORD + "      " + INDEX_THIRD_PERSON.getOneBased()
                + "       " + INDEX_FOURTH_PERSON.getOneBased() + "        " + INDEX_FIFTH_PERSON.getOneBased();
        List<Index> indexList = Arrays.asList(INDEX_THIRD_PERSON, INDEX_FOURTH_PERSON, INDEX_FIFTH_PERSON);
        StringBuilder names = new StringBuilder();
        for (Index index : indexList) {
            ReadOnlyPerson favoritedPerson = favoritePerson(expectedModel, index);
            names.append("\n★ ").append(favoritedPerson.getName().toString());
        }
        String expectedResultMessage = String.format(MESSAGE_FAVORITE_PERSON_SUCCESS, names);
        assertCommandSuccess(command, expectedModel, expectedResultMessage);

        /* Case: undo favoriting the 3rd, 4th & 5th person in the list -> 3rd, 4th & 5th person unfavorited */
        command = UndoCommand.COMMAND_WORD;
        expectedResultMessage = UndoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, expectedModel, expectedResultMessage);

        /* Case: redo favoriting the 3rd, 4th & 5th person in the list -> 3rd, 4th & 5th person favorited again */
        command = RedoCommand.COMMAND_WORD;
        for (Index index : indexList) {
            favoritePerson(expectedModel, index);
        }
        expectedResultMessage = RedoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, expectedModel, expectedResultMessage);

        /* --------------- Performing favorite operation while a filtered list is being shown ------------------- */

        /* Case: filtered person list, favorite index within bounds of address book and person list -> favorited */
        showPersonsWithName(KEYWORD_MATCHING_MEIER);
        expectedModel = getModel();
        Index index = INDEX_FIRST_PERSON;
        assertTrue(index.getZeroBased() < getModel().getFilteredPersonList().size());
        command = FavoriteCommand.COMMAND_WORD + " " + index.getOneBased();
        ReadOnlyPerson favoritedPerson = favoritePerson(expectedModel, index);
        expectedResultMessage = String.format(
                MESSAGE_FAVORITE_PERSON_SUCCESS, "\n★ " + favoritedPerson.getName().toString());
        assertCommandSuccess(command, expectedModel, expectedResultMessage);

        /*
         * Case: filtered person list, favorite index within bounds of address book but out of bounds of person list
         * -> rejected
         */
        showPersonsWithName(KEYWORD_MATCHING_MEIER);
        int invalidIndex = getModel().getAddressBook().getPersonList().size();
        command = FavoriteCommand.COMMAND_WORD + " " + invalidIndex;
        assertCommandFailure(command, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);

        /* ------------------ Performing favorite operation while a person card is selected --------------------- */

        /* Case: favorite the selected person -> person list panel selects the person before the favorited person */
        showAllPersons();
        expectedModel = getModel();
        Index selectedIndex = getLastIndex(expectedModel);
        Index expectedIndex = Index.fromZeroBased(selectedIndex.getZeroBased());
        selectPerson(selectedIndex);
        command = FavoriteCommand.COMMAND_WORD + " " + selectedIndex.getOneBased();
        favoritedPerson = favoritePerson(expectedModel, selectedIndex);
        expectedResultMessage = String.format(
                MESSAGE_FAVORITE_PERSON_SUCCESS, "\n★ " + favoritedPerson.getName().toString());
        assertCommandSuccess(command, expectedModel, expectedResultMessage, expectedIndex);

        /* ------------------------------ Performing invalid favorite operation --------------------------------- */

        /* Case: multiple invalid indexes (0 0 0) -> rejected */
        command = FavoriteCommand.COMMAND_WORD + " 0 0 0";
        assertCommandFailure(command, MESSAGE_INVALID_FAVORITE_COMMAND_FORMAT);

        /* Case: multiple indexes with only one valid (1 0 -1 -2 -3) -> rejected */
        command = FavoriteCommand.COMMAND_WORD + " 1 0 -1 -2 -3";
        assertCommandFailure(command, MESSAGE_INVALID_FAVORITE_COMMAND_FORMAT);

        /* Case: invalid index (size + 1) -> rejected */
        Index outOfBoundsIndex = Index.fromOneBased(
                getModel().getAddressBook().getPersonList().size() + 1);
        command = FavoriteCommand.COMMAND_WORD + " " + outOfBoundsIndex.getOneBased();
        assertCommandFailure(command, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);

        /* Case: invalid arguments (alphabets) -> rejected */
        assertCommandFailure(FavoriteCommand.COMMAND_WORD + " 1 2 a", MESSAGE_INVALID_FAVORITE_COMMAND_FORMAT);

        /* Case: mixed case command word -> rejected */
        assertCommandFailure("FaV 1", MESSAGE_UNKNOWN_COMMAND);
    }

    /**
     * Favorites the {@code ReadOnlyPerson} at the specified {@code index} in {@code model}'s address book.
     * @return the favorited person
     */
    private ReadOnlyPerson favoritePerson(Model model, Index index) {
        ReadOnlyPerson targetPerson = model.getFilteredPersonList().get(index.getZeroBased());
        try {
            model.toggleFavoritePerson(targetPerson, FavoriteCommand.COMMAND_WORD);
        } catch (DuplicatePersonException dpe) {
            throw new AssertionError(EditCommand.MESSAGE_DUPLICATE_PERSON);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("targetPerson is retrieved from model.");
        }
        return targetPerson;
    }

    /**
     * Executes {@code command} and in addition,<br>
     * 1. Asserts that the command box displays an empty string.<br>
     * 2. Asserts that the result display box displays {@code expectedResultMessage}.<br>
     * 3. Asserts that the model related components equal to {@code expectedModel}.<br>
     * 4. Asserts that the browser url and selected card remains unchanged.<br>
     * 5. Asserts that the status bar's sync status changes.<br>
     * 6. Asserts that the command box has the default style class.<br>
     * Verifications 1 to 3 are performed by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage) {
        assertCommandSuccess(command, expectedModel, expectedResultMessage, null);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Model, String)} except that the
     * browser url and selected card are expected to update accordingly depending on the card
     * at {@code expectedSelectedCardIndex}.
     * @see FavoriteCommandSystemTest#assertCommandSuccess(String, Model, String)
     * @see AddressBookSystemTest#assertSelectedCardChanged(Index)
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage,
                                      Index expectedSelectedCardIndex) {
        executeCommand(command);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);

        if (expectedSelectedCardIndex != null) {
            assertSelectedCardChanged(expectedSelectedCardIndex);
        } else {
            assertSelectedCardUnchanged();
        }

        assertCommandBoxShowsDefaultStyle();
        assertStatusBarUnchangedExceptSyncStatus();
    }

    /**
     * Executes {@code command} and in addition,<br>
     * 1. Asserts that the command box displays {@code command}.<br>
     * 2. Asserts that result display box displays {@code expectedResultMessage}.<br>
     * 3. Asserts that the model related components equal to the current model.<br>
     * 4. Asserts that the browser url, selected card and status bar remain unchanged.<br>
     * 5. Asserts that the command box has the error style.<br>
     * Verifications 1 to 3 are performed by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandFailure(String command, String expectedResultMessage) {
        Model expectedModel = getModel();

        executeCommand(command);
        assertApplicationDisplaysExpected(command, expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsErrorStyle();
        assertStatusBarUnchanged();
    }
}
```
