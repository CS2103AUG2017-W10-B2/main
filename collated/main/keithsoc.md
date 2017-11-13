# keithsoc
###### \java\seedu\address\commons\core\GuiSettings.java
``` java
    private static final double DEFAULT_HEIGHT = 835;
    private static final double DEFAULT_WIDTH = 1100;
```
###### \java\seedu\address\commons\core\index\Index.java
``` java
    /**
     * Implement comparable for usage such as {@code Collections.max}
     */
    @Override
    public int compareTo(Index idx) {
        return Double.compare(getOneBased(), idx.getOneBased());
    }
```
###### \java\seedu\address\commons\core\Messages.java
``` java
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX_MULTI = "One or more indexes provided is invalid";
```
###### \java\seedu\address\commons\core\ThemeSettings.java
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
###### \java\seedu\address\logic\commands\EditCommand.java
``` java
        public void setFavorite(Favorite favorite) {
            this.favorite = favorite;
        }

        public Optional<Favorite> getFavorite() {
            return Optional.ofNullable(favorite);
        }

        public void setDisplayPhoto(DisplayPhoto displayPhoto) {
            this.displayPhoto = displayPhoto;
        }

        public Optional<DisplayPhoto> getDisplayPhoto() {
            return Optional.ofNullable(displayPhoto);
        }
```
###### \java\seedu\address\logic\commands\FavoriteCommand.java
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

    public static final String MESSAGE_FAVORITE_PERSON_SUCCESS = "Added as favorite contact(s): ";
    public static final String MESSAGE_FAVORITE_PERSON_FAILURE = "These contact(s) has already been "
            + "added as favorites: ";

    private final List<Index> targetIndexList;
    private final Set<ReadOnlyPerson> targetPersonList;
    private final StringBuilder allNameList;
    private final StringBuilder successNameList;
    private final StringBuilder failureNameList;

    public FavoriteCommand(List<Index> targetIndexList) {
        this.targetIndexList = targetIndexList;
        this.targetPersonList = new LinkedHashSet<>();
        this.allNameList = new StringBuilder();
        this.successNameList = new StringBuilder();
        this.failureNameList = new StringBuilder();
    }

    /**
     * Efficiently check whether user input any index larger than address book size with Collections.max
     * This is to avoid the following situation:
     *
     * E.g. AddressBook size is 100
     * Execute "fav 6 7 101 8 9" ->
     * persons at indexes 7 and 8 gets favorited but method halts due to CommandException for index 101 and
     * person at index 9 and beyond does not get favorited.
     */
    private void checkIndexBoundaries(List<ReadOnlyPerson> lastShownList) throws CommandException {
        if (Collections.max(targetIndexList).getOneBased() > lastShownList.size()) {
            if (targetIndexList.size() > 1) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX_MULTI);
            } else {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
        }
    }

    /**
     * Stores all persons to favorite into {@code targetPersonList} found from the index(es) used in last shown list.
     * {@code targetPersonList} uses a LinkedHashSet implementation for the following purposes:
     * 1. Prevent duplicates (of persons) resulting from such an input: fav 1 1 1
     * 2. Preserve insertion order (since {@code targetIndexList} is sorted)
     */
    private void getPersonsToFavorite(List<ReadOnlyPerson> lastShownList) {
        for (Index targetIndex : targetIndexList) {
            ReadOnlyPerson personToFavorite = lastShownList.get(targetIndex.getZeroBased());
            if (!isAlreadyFavorite(personToFavorite)) { // Add person into set
                targetPersonList.add(personToFavorite);
            } else { // Do not add person into set, append into failure name list instead
                failureNameList.append("\n\t- ");
                failureNameList.append(personToFavorite.getName().toString());
            }
        }
    }

    /**
     * Checks if {@code person} is already a favorite contact.
     */
    private boolean isAlreadyFavorite(ReadOnlyPerson person) {
        return person.getFavorite().isFavorite();
    }

    /**
     * Adds all appropriate String messages into {@code allNameList}.
     */
    private String compileAllNames() {
        if (successNameList.length() != 0 && failureNameList.length() != 0) {
            allNameList.append(MESSAGE_FAVORITE_PERSON_SUCCESS);
            allNameList.append(successNameList);
            allNameList.append("\n");
            allNameList.append(MESSAGE_FAVORITE_PERSON_FAILURE);
            allNameList.append(failureNameList);
        } else if (successNameList.length() != 0 && failureNameList.length() == 0) {
            allNameList.append(MESSAGE_FAVORITE_PERSON_SUCCESS);
            allNameList.append(successNameList);
        } else {
            allNameList.append(MESSAGE_FAVORITE_PERSON_FAILURE);
            allNameList.append(failureNameList);
        }
        return allNameList.toString();
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        checkIndexBoundaries(lastShownList);
        /*
         * As long no exception is thrown above i.e. all index are within boundaries,
         * the following codes will run
         */
        getPersonsToFavorite(lastShownList);

        for (ReadOnlyPerson personToFavorite : targetPersonList) {
            try {
                model.toggleFavoritePerson(personToFavorite, COMMAND_WORD);
                successNameList.append("\n\t★ ");
                successNameList.append(personToFavorite.getName().toString());
            } catch (DuplicatePersonException dpe) {
                throw new CommandException(EditCommand.MESSAGE_DUPLICATE_PERSON);
            } catch (PersonNotFoundException pnfe) {
                throw new AssertionError("The target person cannot be missing");
            }
        }

        return new CommandResult(compileAllNames());
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof FavoriteCommand // instanceof handles nulls
                && this.targetIndexList.equals(((FavoriteCommand) other).targetIndexList)); // state check
    }
}
```
###### \java\seedu\address\logic\commands\ListCommand.java
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
###### \java\seedu\address\logic\commands\ThemeCommand.java
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
###### \java\seedu\address\logic\commands\UnFavoriteCommand.java
``` java
/**
 * Unfavorites the person(s) identified using it's last displayed index from the address book.
 */
public class UnFavoriteCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "unfav";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Unfavorites the person(s) identified by the index number used in the last person listing.\n"
            + "Parameters: INDEX [ADDITIONAL INDEXES] (INDEX must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1\n"
            + "Example: " + COMMAND_WORD + " 1 2 3";

    public static final String MESSAGE_UNFAVORITE_PERSON_SUCCESS = "Removed from favorite contact(s): ";
    public static final String MESSAGE_UNFAVORITE_PERSON_FAILURE = "These contact(s) has not been "
            + "added as favorites for this operation: ";

    private final List<Index> targetIndexList;
    private final Set<ReadOnlyPerson> targetPersonList;
    private final StringBuilder allNameList;
    private final StringBuilder successNameList;
    private final StringBuilder failureNameList;

    public UnFavoriteCommand(List<Index> targetIndexList) {
        this.targetIndexList = targetIndexList;
        this.targetPersonList = new LinkedHashSet<>();
        this.allNameList = new StringBuilder();
        this.successNameList = new StringBuilder();
        this.failureNameList = new StringBuilder();
    }

    /**
     * Efficiently check whether user input any index larger than address book size with Collections.max.
     * This is to avoid the following situation:
     *
     * E.g. AddressBook size is 100
     * Execute "unfav 6 7 101 8 9" ->
     * persons at indexes 7 and 8 gets unfavorited but method halts due to CommandException for index 101 and
     * person at index 9 and beyond does not get unfavorited.
     */
    private void checkIndexBoundaries(List<ReadOnlyPerson> lastShownList) throws CommandException {
        if (Collections.max(targetIndexList).getOneBased() > lastShownList.size()) {
            if (targetIndexList.size() > 1) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX_MULTI);
            } else {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
        }
    }

    /**
     * Stores all persons to unfavorite into {@code targetPersonList} found from the index(es) used in last shown list.
     * {@code targetPersonList} uses a LinkedHashSet implementation for the following purposes:
     * 1. Prevent duplicates (of persons) resulting from such an input: unfav 1 1 1
     * 2. Preserve insertion order (since {@code targetIndexList} is sorted)
     */
    private void getPersonsToUnFavorite(List<ReadOnlyPerson> lastShownList) {
        for (Index targetIndex : targetIndexList) {
            ReadOnlyPerson personToUnFavorite = lastShownList.get(targetIndex.getZeroBased());
            if (isAlreadyFavorite(personToUnFavorite)) { // Add person into set
                targetPersonList.add(personToUnFavorite);
            } else { // Do not add person into set, append into failure name list instead
                failureNameList.append("\n\t- ");
                failureNameList.append(personToUnFavorite.getName().toString());
            }
        }
    }

    /**
     * Checks if {@code person} is already a favorite contact.
     */
    private boolean isAlreadyFavorite(ReadOnlyPerson person) {
        return person.getFavorite().isFavorite();
    }

    /**
     * Adds all appropriate String messages into {@code allNameList}.
     */
    private String compileAllNames() {
        if (successNameList.length() != 0 && failureNameList.length() != 0) {
            allNameList.append(MESSAGE_UNFAVORITE_PERSON_SUCCESS);
            allNameList.append(successNameList);
            allNameList.append("\n");
            allNameList.append(MESSAGE_UNFAVORITE_PERSON_FAILURE);
            allNameList.append(failureNameList);
        } else if (successNameList.length() != 0 && failureNameList.length() == 0) {
            allNameList.append(MESSAGE_UNFAVORITE_PERSON_SUCCESS);
            allNameList.append(successNameList);
        } else {
            allNameList.append(MESSAGE_UNFAVORITE_PERSON_FAILURE);
            allNameList.append(failureNameList);
        }
        return allNameList.toString();
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        checkIndexBoundaries(lastShownList);
        /*
         * As long no exception is thrown above i.e. all index are within boundaries,
         * the following codes will run
         */
        getPersonsToUnFavorite(lastShownList);

        for (ReadOnlyPerson personToUnFavorite : targetPersonList) {
            try {
                model.toggleFavoritePerson(personToUnFavorite, COMMAND_WORD);
                successNameList.append("\n\t- ");
                successNameList.append(personToUnFavorite.getName().toString());
            } catch (DuplicatePersonException dpe) {
                throw new CommandException(EditCommand.MESSAGE_DUPLICATE_PERSON);
            } catch (PersonNotFoundException pnfe) {
                throw new AssertionError("The target person cannot be missing");
            }
        }

        return new CommandResult(compileAllNames());
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UnFavoriteCommand // instanceof handles nulls
                && this.targetIndexList.equals(((UnFavoriteCommand) other).targetIndexList)); // state check
    }
}
```
###### \java\seedu\address\logic\parser\AddressBookParser.java
``` java
        case FavoriteCommand.COMMAND_WORD:
            return new FavoriteCommandParser().parse(arguments);

        case UnFavoriteCommand.COMMAND_WORD:
            return new UnFavoriteCommandParser().parse(arguments);
```
###### \java\seedu\address\logic\parser\AddressBookParser.java
``` java
        case ListCommand.COMMAND_WORD:
        case ListCommand.COMMAND_ALIAS:
            return new ListCommand(arguments);
```
###### \java\seedu\address\logic\parser\AddressBookParser.java
``` java
        case ThemeCommand.COMMAND_WORD:
        case ThemeCommand.COMMAND_ALIAS:
            return new ThemeCommandParser().parse(arguments);
```
###### \java\seedu\address\logic\parser\ArgumentMultimap.java
``` java
    /**
     * Returns a boolean value that indicates whether a prefix is present in user input
     */
    public boolean isPrefixPresent(Prefix prefix) {
        return argMultimap.containsKey(prefix);
    }
```
###### \java\seedu\address\logic\parser\CliSyntax.java
``` java
    public static final Prefix PREFIX_FAV = new Prefix("f/");
    public static final Prefix PREFIX_UNFAV = new Prefix("uf/");
    public static final Prefix PREFIX_DISPLAY_PHOTO = new Prefix("dp/");
```
###### \java\seedu\address\logic\parser\CliSyntax.java
``` java
    public static final Prefix PREFIX_OPTION = new Prefix("-");
```
###### \java\seedu\address\logic\parser\FavoriteCommandParser.java
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
            // Sorts indexes in ascending order
            Collections.sort(indexList);
            return new FavoriteCommand(indexList);
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FavoriteCommand.MESSAGE_USAGE));
        }
    }
}
```
###### \java\seedu\address\logic\parser\ParserUtil.java
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
###### \java\seedu\address\logic\parser\ParserUtil.java
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

    /**
     * Parses a {@code Optional<String> displayPhoto} into an {@code Optional<DisplayPhoto>}
     * if {@code displayPhoto} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<DisplayPhoto> parseDisplayPhoto(Optional<String> displayPhoto) throws IllegalValueException {
        return displayPhoto.isPresent()
                ? Optional.of(new DisplayPhoto(displayPhoto.get())) : Optional.empty();
    }
```
###### \java\seedu\address\logic\parser\ThemeCommandParser.java
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

        if (opArgs.getOptions().size() != 1) {
            // theme command must have one option specified
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ThemeCommand.MESSAGE_USAGE));
        }

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
###### \java\seedu\address\logic\parser\UnFavoriteCommandParser.java
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
            // Sorts indexes in descending order
            Collections.sort(indexList, Collections.reverseOrder());
            return new UnFavoriteCommand(indexList);
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnFavoriteCommand.MESSAGE_USAGE));
        }
    }
}
```
###### \java\seedu\address\MainApp.java
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
###### \java\seedu\address\model\AddressBook.java
``` java
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
            indicatePersonAccessed(personToFav);
        } else {
            throw new PersonNotFoundException();
        }
    }
```
###### \java\seedu\address\model\Model.java
``` java
    /** {@code Predicate} that consists of all ReadOnlyPerson who has been favorited */
    Predicate<ReadOnlyPerson> PREDICATE_SHOW_FAV_PERSONS = p -> p.getFavorite().isFavorite();
```
###### \java\seedu\address\model\Model.java
``` java
    /** Favorites or unfavorites the given person. Should update the last accessed time of the person. */
    void toggleFavoritePerson(ReadOnlyPerson target, String type)
            throws DuplicatePersonException, PersonNotFoundException;

```
###### \java\seedu\address\model\ModelManager.java
``` java
    @Override
    public void toggleFavoritePerson(ReadOnlyPerson target, String type)
            throws DuplicatePersonException, PersonNotFoundException {
        requireAllNonNull(target, type);
        addressBook.toggleFavoritePerson(target, type);
        indicateAddressBookChanged();
    }

```
###### \java\seedu\address\model\person\DisplayPhoto.java
``` java
/**
 * Represents a Person's Display Photo in the address book.
 * Guarantees: immutability and validity.
 */
public class DisplayPhoto {
    // Display photo for sample persons in SampleDataUtil
    public static final String SAMPLE_PHOTO = "/images/sample_dp_one.png";

    private static final Logger logger = LogsCenter.getLogger(DisplayPhoto.class);
    private static final String MESSAGE_PHOTO_CONSTRAINTS = "Display photo: "
            + "specified file does not exist or it exceeded maximum size of 1MB.";
    private static final String MESSAGE_PHOTO_COPY_ERROR = "Error copying photo to application's data directory";
    private static final String DEFAULT_SAVE_DIR = "data" + File.separator;
    private static final String ALLOWED_TYPE_JPG = ".jpg";
    private static final String ALLOWED_TYPE_JPEG = ".jpeg";
    private static final String ALLOWED_TYPE_PNG = ".png";
    private static final int MAX_SIZE = 1000000; // Sets allowable maximum display photo size to 1MB

    public final String value;

    /**
     * Validates given Display Photo.
     *
     * @throws IllegalValueException if given display photo string is invalid.
     */
    public DisplayPhoto(String photoPath) throws IllegalValueException {
        // Allow null values
        if (photoPath == null || photoPath.isEmpty()) {
            this.value = null;
        } else if (isSamplePhoto(photoPath)) {
            this.value = photoPath;
        } else {
            String trimmedPhotoPath = photoPath.trim();
            if (!isValidPhoto(trimmedPhotoPath)) {
                throw new IllegalValueException(MESSAGE_PHOTO_CONSTRAINTS);
            } else {
                File from = new File(trimmedPhotoPath);
                this.value = DEFAULT_SAVE_DIR + from.getName();
                Path to = Paths.get(this.value);
                copyPhotoToDefaultDir(from.toPath(), to);
            }
        }
    }

    /**
     * Returns if a given string is a valid person display photo file and of correct size.
     */
    private static boolean isValidPhoto(String test) {
        File file = new File(test);
        return file.exists()
                && file.length() <= MAX_SIZE
                && (test.endsWith(ALLOWED_TYPE_JPG)
                || test.endsWith(ALLOWED_TYPE_JPEG)
                || test.endsWith(ALLOWED_TYPE_PNG));
    }

    /**
     * Returns if a given string is a path to one of the sample photos
     */
    private static boolean isSamplePhoto(String path) {
        return path.equals(SAMPLE_PHOTO);
    }

    /**
     * Copies the photo from Path {@code from} to Path {@code to}
     * @throws IllegalValueException
     */
    private static void copyPhotoToDefaultDir(Path from, Path to) throws IllegalValueException {
        try {
            // Create the default data directory to store the display photos if it doesn't already exist
            Files.createDirectories(Paths.get(DEFAULT_SAVE_DIR));

            // Copy file to user's "data" directory.
            // If the target file exists, then the target file is replaced if it is not a non-empty directory.
            Files.copy(from, to, REPLACE_EXISTING);
        } catch (IOException io) {
            logger.info("Display photo error: " + io.toString());
            throw new IllegalValueException(MESSAGE_PHOTO_COPY_ERROR);
        }
    }

    /**
     * Returns the absolute file path for user-specified display photos.
     */
    public String getAbsoluteFilePath() {
        if (isSamplePhoto(value)) {
            return value;
        } else {
            return "file://" + Paths.get(value).toAbsolutePath().toUri().getPath();
        }
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DisplayPhoto // instanceof handles nulls
                && this.value.equals(((DisplayPhoto) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
```
###### \java\seedu\address\model\person\Favorite.java
``` java
/**
 * Represents a Person's favorite status in the address book.
 * Guarantees: immutable.
 */
public class Favorite {

    public static final String MESSAGE_FAVORITE_CONSTRAINTS = "Only prefix is required for favorite status.";
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
###### \java\seedu\address\model\person\Person.java
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

    public void setDisplayPhoto(DisplayPhoto displayPhoto) {
        this.displayPhoto.set(requireNonNull(displayPhoto));
    }

    @Override
    public ObjectProperty<DisplayPhoto> displayPhotoProperty() {
        return displayPhoto;
    }

    @Override
    public DisplayPhoto getDisplayPhoto() {
        return displayPhoto.get();
    }
```
###### \java\seedu\address\model\person\ReadOnlyPerson.java
``` java
    ObjectProperty<Favorite> favoriteProperty();
    Favorite getFavorite();
    ObjectProperty<DisplayPhoto> displayPhotoProperty();
    DisplayPhoto getDisplayPhoto();
```
###### \java\seedu\address\model\UserPrefs.java
``` java
    private ThemeSettings themeSettings;
```
###### \java\seedu\address\model\UserPrefs.java
``` java
    private String addressBookName = "KayPoh";
```
###### \java\seedu\address\model\UserPrefs.java
``` java
    public UserPrefs() {
        this.setGuiSettings(1100, 835, 0, 0);
        this.setThemeSettings("view/ThemeDay.css", "view/ThemeDayExtensions.css");
    }
```
###### \java\seedu\address\model\UserPrefs.java
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
###### \java\seedu\address\ui\BrowserPanel.java
``` java
    public static final String DEFAULT_PAGE_DAY = "defaultDay.html";
    public static final String DEFAULT_PAGE_NIGHT = "defaultNight.html";
```
###### \java\seedu\address\ui\BrowserPanel.java
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
###### \java\seedu\address\ui\CommandBox.java
``` java
        // sets focus on TextField upon app launch
        Platform.runLater(() -> commandTextField.requestFocus());
```
###### \java\seedu\address\ui\MainWindow.java
``` java
    private static final int MIN_HEIGHT = 700;
    private static final int MIN_WIDTH = 600;
```
###### \java\seedu\address\ui\MainWindow.java
``` java
    private Scene scene;
```
###### \java\seedu\address\ui\MainWindow.java
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
###### \java\seedu\address\ui\MainWindow.java
``` java
        scene.setFill(Color.TRANSPARENT);
        setDefaultTheme(prefs, scene);
        UiTheme.getInstance().setScene(scene);
```
###### \java\seedu\address\ui\MainWindow.java
``` java
        // Enable window navigation
        enableMovableWindow();
        enableMinimiseWindow();
        enableMaximiseWindow();
        UiResize.enableResizableWindow(primaryStage, MIN_WIDTH, MIN_HEIGHT, Double.MAX_VALUE, Double.MAX_VALUE);
```
###### \java\seedu\address\ui\MainWindow.java
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
###### \java\seedu\address\ui\MainWindow.java
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
###### \java\seedu\address\ui\PersonCard.java
``` java
    private static HashMap<String, String> personColors = new HashMap<>();
    private static HashMap<String, String> tagColors = new HashMap<>();
    private static Random random = new Random();
    private static final String defaultThemeTagColor = "#fc4465";
    private static final double GOLDEN_RATIO = 0.618033988749895;
```
###### \java\seedu\address\ui\PersonCard.java
``` java
    @FXML
    private StackPane displayPhotoStackPane;
    @FXML
    private ImageView displayPhotoImageView;
```
###### \java\seedu\address\ui\PersonCard.java
``` java
    /**
     * Generates a random pastel color for display photos.
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
     * Binds a display photo background with a random pastel color and store it into personColors HashMap.
     */
    private String getColorForPerson(String name) {
        if (!personColors.containsKey(name)) {
            personColors.put(name, generateRandomPastelColor());
        }
        return personColors.get(name);
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
###### \java\seedu\address\ui\PersonCard.java
``` java
    /**
     * Adds a display photo for each {@code person}.
     * If {@code person} has a non-null display photo field, set ImageView to an image of user's choice.
     * If {@code person} has a null display photo field, set ImageView to a colored thumbnail with name initials.
     */
    private void initDisplayPhoto(ReadOnlyPerson person) {
        // Round display photo
        double value = displayPhotoImageView.getFitWidth() / 2;
        Circle clip = new Circle(value, value, value);
        displayPhotoImageView.setClip(clip);

        if (person.getDisplayPhoto().value != null) {
            // Set image to user's choice
            displayPhotoImageView.setImage(new Image(person.getDisplayPhoto().getAbsoluteFilePath()));
        } else {
            // Add background circle with a random pastel color
            String nameOfPerson = person.getName().toString().trim();
            Circle backgroundCircle = new Circle(value);
            backgroundCircle.setFill(Paint.valueOf(getColorForPerson(nameOfPerson)));

            // Add text
            Text personInitialsText = new Text(extractInitials(nameOfPerson));
            personInitialsText.setFill(Paint.valueOf("white"));
            displayPhotoStackPane.getChildren().addAll(backgroundCircle, personInitialsText);
        }
    }

    /**
     * Extracts the initials from the name of the given {@code name}.
     * Extract only one initial if the name contains a single word;
     * Extract two initials if the name contains more than one word.
     */
    private String extractInitials (String name) {
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
###### \java\seedu\address\ui\UiFont.java
``` java
/**
 * A singleton class that manages the loading of custom fonts and embedding it into the application
 * so that typography will be consistent on different platforms e.g. Windows, macOS, etc.
 */
public class UiFont {

    /** Resource folder where font files are stored. */
    private static final String FONTS_FILE_FOLDER = "/fonts/";

    /** List of all the custom fonts */
    private static final String[] fontList = {
        "OpenSans-Light.ttf", "SegoeUI.ttf", "SegoeUI-Bold.ttf", "SegoeUI-Light.ttf", "SegoeUI-Semibold.ttf"
    };

    private static UiFont instance;

    private UiFont() {
        // Prevents any other class from instantiating
    }

    /**
     * @return instance of UiFont
     */
    public static UiFont getInstance() {
        if (instance == null) {
            instance = new UiFont();
        }
        return instance;
    }

    /**
     * Load in all the fonts specified in fontList String array.
     */
    public void embedFonts() {
        for (String font : fontList) {
            Font.loadFont(getClass().getResourceAsStream(FONTS_FILE_FOLDER + font), 10);
        }
    }

}
```
###### \java\seedu\address\ui\UiTheme.java
``` java
/**
 * A singleton class that manages the changing of scene graph's stylesheets at runtime.
 */
public class UiTheme {
    public static final String THEME_DAY = "view/ThemeDay.css";
    public static final String THEME_NIGHT = "view/ThemeNight.css";
    public static final String THEME_DAY_EXTENSIONS = "view/ThemeDayExtensions.css";
    public static final String THEME_NIGHT_EXTENSIONS = "view/ThemeNightExtensions.css";

    private static UiTheme instance;
    private Scene scene;
    private BrowserPanel browserPanel;

    private UiTheme() {
        // Prevents any other class from instantiating
    }

    /**
     * @return instance of UiTheme
     */
    public static UiTheme getInstance() {
        if (instance == null) {
            instance = new UiTheme();
        }
        return instance;
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
###### \resources\view\defaultDay.html
``` html
<html>
<head>
    <link rel="stylesheet" href="ThemeDay.css">
</head>

<body class="background">
    <div class="center">
        <div class="text">Please select a contact to start stalking</div>
    </div>
</body>
</html>
```
###### \resources\view\defaultNight.html
``` html
<html>
<head>
    <link rel="stylesheet" href="ThemeNight.css">
</head>

<body class="background">
    <div class="center">
        <div class="text">Please select a contact to start stalking</div>
    </div>
</body>
</html>
```
###### \resources\view\MainWindow.fxml
``` fxml

<VBox fx:id="rootVBox" xmlns="http://javafx.com/javafx/8.0.121" xmlns:fx="http://javafx.com/fxml/1">
    <HBox fx:id="rootHBox" alignment="CENTER_LEFT" minHeight="45" prefHeight="45">
        <MenuBar fx:id="menuBar" HBox.hgrow="ALWAYS">
            <Menu mnemonicParsing="false" text="File">
                <MenuItem mnemonicParsing="false" onAction="#handleExit" text="Exit" />
            </Menu>
            <Menu mnemonicParsing="false" text="Help">
                <MenuItem fx:id="helpMenuItem" mnemonicParsing="false" onAction="#handleHelp" text="Help" />
            </Menu>
        </MenuBar>
        <Button fx:id="minimiseButton" mnemonicParsing="false" prefHeight="45" prefWidth="60" />
        <Button fx:id="maximiseButton" mnemonicParsing="false" prefHeight="45" prefWidth="60" />
        <Button fx:id="closeButton" mnemonicParsing="false" prefHeight="45" prefWidth="60" onAction="#handleExit" />
    </HBox>
```
###### \resources\view\PersonListCard.fxml
``` fxml
    <VBox alignment="CENTER" minHeight="105" prefHeight="105" prefWidth="120">
        <StackPane fx:id="displayPhotoStackPane" styleClass="display-photo-pane">
            <ImageView fx:id="displayPhotoImageView" fitHeight="85" fitWidth="85"
                       preserveRatio="true" styleClass="profile-photo" />
        </StackPane>
    </VBox>
```
###### \resources\view\PersonListCard.fxml
``` fxml
    <VBox alignment="TOP_RIGHT" minHeight="105" prefWidth="40">
        <padding>
            <Insets bottom="5" left="5" right="10" top="8" />
        </padding>
        <ImageView fx:id="favoriteImageView" fitHeight="32" fitWidth="32" preserveRatio="true" />
    </VBox>
```
###### \resources\view\ThemeDay.css
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

#rootVBox {
    -fx-border-radius: 10;
    -fx-background-radius: 10;
}

#statusBarFooter, #syncStatus, #saveLocationStatus {
    -fx-border-radius: 0 0 10 10;
    -fx-background-radius: 0 0 10 10;
}

/* Tab Pane */

.tab-pane {
    -fx-padding: 0 0 0 1;
}

.tab-pane .tab-header-area {
    -fx-padding: 0;
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

/* Display Photo */

.display-photo-pane {
    -fx-font-family: "Open Sans Light";
    -fx-font-size: 30px;
}

/* Person List Card Cells */

.list-view {
    -fx-background-color: #ffffff;
    -fx-background-insets: 0;
    -fx-padding: 0;
}

.list-cell {
    -fx-label-padding: 0;
    -fx-graphic-text-gap: 0;
    -fx-padding: 0;
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
    -fx-font-family: "Segoe UI";
    -fx-font-size: 25px;
    -fx-text-fill: black;
    -fx-padding: 3 0 3 0;
    -fx-effect: dropshadow(one-pass-box, rgba(0, 0, 0, 0.5), 1, 0, 0, 0); /* Add extra darkness to font albeit subtly */
}

.cell_small_label {
    -fx-font-family: "Segoe UI";
    -fx-font-size: 16px;
    -fx-text-fill: #1f1f1f;
    -fx-padding: 3 0 3 0;
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

/* Grid Pane used by Status Bar Footer and Command Box */

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

/* HBox containing Menu Bar and Window Buttons */

#rootHBox {
    -fx-border-radius: 10 10 0 0;
    -fx-background-radius: 10 10 0 0;
    -fx-background-color: #d13438;
}

/* Menu Bar */

.menu-bar {
    -fx-background-color: #d13438;
    -fx-border-radius: 10 0 0 0;
    -fx-background-radius: 10 0 0 0;
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
    -fx-background-color: #d13438;
    -fx-font-family: "Segoe UI";
    -fx-font-size: 15pt;
    -fx-text-fill: white;
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
    -fx-background-color: #ffffff;
}

.dialog-pane > *.button-bar > *.container {
    -fx-background-color: #ffffff;
}

.dialog-pane > *.label.content {
    -fx-font-size: 14px;
    -fx-font-weight: bold;
    -fx-text-fill: black;
}

.dialog-pane:header *.header-panel {
    -fx-background-color: #ffffff;
}

.dialog-pane:header *.header-panel *.label {
    -fx-font-size: 18px;
    -fx-font-style: italic;
    -fx-fill: black;
    -fx-text-fill: black;
}

/* Scroll Bar */

.scroll-bar {
    -fx-background-color: #ffffff;
}

.scroll-bar .thumb {
    -fx-background-color: derive(#d13438, 50%);
    -fx-background-insets: 3;
}

.scroll-bar .increment-button, .scroll-bar .decrement-button {
    -fx-background-color: transparent;
    -fx-padding: 0;
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
    -fx-font-family: "Segoe UI Bold";
    -fx-font-size: 11pt;
    -fx-text-fill: white;
    -fx-padding: 2 8 3 8;
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
###### \resources\view\ThemeDayExtensions.css
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
###### \resources\view\ThemeNight.css
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

#rootVBox {
    -fx-border-radius: 10;
    -fx-background-radius: 10;
}

#statusBarFooter, #syncStatus, #saveLocationStatus {
    -fx-border-radius: 0 0 10 10;
    -fx-background-radius: 0 0 10 10;
}

/* Tab Pane */

.tab-pane {
    -fx-padding: 0 0 0 1;
}

.tab-pane .tab-header-area {
    -fx-padding: 0;
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

/* Display Photo */

.display-photo-pane {
    -fx-font-family: "Open Sans Light";
    -fx-font-size: 31px;
}

/* Person List Card Cells */

.list-view {
    -fx-background-color: #272822;
    -fx-background-insets: 0;
    -fx-padding: 0;
}

.list-cell {
    -fx-label-padding: 0;
    -fx-graphic-text-gap: 0;
    -fx-padding: 0;
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
    -fx-font-family: "Segoe UI";
    -fx-font-size: 25px;
    -fx-text-fill: white;
    -fx-padding: 3 0 3 0;
}

.cell_small_label {
    -fx-font-family: "Segoe UI";
    -fx-font-size: 16px;
    -fx-text-fill: white;
    -fx-padding: 3 0 3 0;
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

/* Grid Pane used by Status Bar Footer and Command Box */

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

/* HBox containing Menu Bar and Window Buttons */

#rootHBox {
    -fx-border-radius: 10 10 0 0;
    -fx-background-radius: 10 10 0 0;
    -fx-background-color: #d13438;
}

/* Menu Bar */

.menu-bar {
    -fx-background-color: #d13438;
    -fx-border-radius: 10 0 0 0;
    -fx-background-radius: 10 0 0 0;
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
    -fx-background-color: #d13438;
    -fx-font-family: "Segoe UI";
    -fx-font-size: 15pt;
    -fx-text-fill: white;
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
    -fx-text-fill: white;
}

.dialog-pane:header *.header-panel {
    -fx-background-color: derive(#1d1d1d, 25%);
}

.dialog-pane:header *.header-panel *.label {
    -fx-font-size: 18px;
    -fx-font-style: italic;
    -fx-fill: white;
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
    -fx-padding: 0;
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
    -fx-font-family: "Segoe UI Bold";
    -fx-font-size: 11pt;
    -fx-text-fill: white;
    -fx-padding: 2 8 3 8;
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
###### \resources\view\ThemeNightExtensions.css
``` css
.error {
    -fx-text-fill: #ff6161 !important; /* The error class should always override the default text-fill style */
}

.list-cell:empty {
    /* Empty cells will not have alternating colours */
    -fx-background: #272822;
}
```
