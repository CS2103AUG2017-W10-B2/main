# marvinchin
###### /java/seedu/address/ui/PersonCard.java
``` java
    /**
     * Creates a social info label for each {@code Person}
     */
    private void initSocialInfos(ReadOnlyPerson person) {
        person.getSocialInfos().forEach(socialInfo -> {
            String labelText = socialInfo.getSocialType() + ": " + socialInfo.getUsername();
            Label socialLabel = new Label(labelText);
            socialLabel.getStyleClass().add("cell_small_label");
            socialInfos.getChildren().add(socialLabel);
        });
    }
```
###### /java/seedu/address/logic/parser/OptionBearingArgument.java
``` java
/**
 * The OptionBearingArgument class encapsulates an argument that contains options, and handles the parsing and filtering
 * of these options from the argument.
 */
public class OptionBearingArgument {

    private String rawArgs;
    private Set<String> optionsList;
    private String filteredArgs;

    /**
     * Constructs an OptionBearingArgument for the input argument string.
     */
    public OptionBearingArgument(String args) {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        rawArgs = trimmedArgs;
        parse(rawArgs);
    }

    /**
     * Parses the string to get the list of options, and a filtered argument string with the options removed.
     */
    private void parse(String args) {
        String[] splitArgs = args.split("\\s+");
        optionsList = Arrays.stream(splitArgs)
                .filter(arg -> arg.startsWith(PREFIX_OPTION.getPrefix()))
                .map(optionArg -> optionArg.substring(PREFIX_OPTION.getPrefix().length())) // drop the leading prefix
                .collect(Collectors.toCollection(HashSet::new));

        filteredArgs = Arrays.stream(splitArgs)
                .filter(arg -> !arg.startsWith(PREFIX_OPTION.getPrefix()))
                .collect(Collectors.joining(" "));
    }

    public String getRawArgs() {
        return rawArgs;
    }

    public Set<String> getOptions() {
        return optionsList;
    }

    public String getFilteredArgs() {
        return filteredArgs;
    }

}
```
###### /java/seedu/address/logic/parser/ImportCommandParser.java
``` java
/**
 * Parses input arguments and creates a new ImportCommand object
 */
public class ImportCommandParser implements Parser {

    /**
     * Parses the given {@code String} of arguments in the context of the ImportCommand
     * and returns an ImportCommand object for execution.
     */
    public ImportCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ImportCommand.MESSAGE_USAGE));
        }

        return new ImportCommand(trimmedArgs);
    }
}
```
###### /java/seedu/address/logic/parser/ExportCommandParser.java
``` java
/**
 * Parses input arguments and creates a new ExportCommand object
 */
public class ExportCommandParser implements Parser {

    /**
     * Parses the given {@code String} of arguments in the context of the ExportCommand
     * and returns an ExportCommand object for execution.
     */
    public ExportCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExportCommand.MESSAGE_USAGE));
        }

        return new ExportCommand(trimmedArgs);
    }
}
```
###### /java/seedu/address/logic/parser/SocialInfoMapping.java
``` java
/**
 * Handles mappings of social related identifiers when parsing SocialInfo
 */
public class SocialInfoMapping {

    private static final int SOCIAL_TYPE_INDEX = 0;
    private static final int SOCIAL_USERNAME_INDEX = 1;

    private static final String FACEBOOK_IDENTIFIER = "facebook";
    private static final String FACEBOOK_IDENTIFIER_ALIAS = "fb";
    private static final String INSTAGRAM_IDENTIFIER = "instagram";
    private static final String INSTAGRAM_IDENTIFIER_ALIAS = "ig";

    private static final String INVALID_SYNTAX_EXCEPTION_MESSAGE = "Invalid syntax for social info";
    private static final String UNRECOGNIZED_SOCIAL_TYPE_MESSAGE = "Unrecognized social type.\n"
        + "Currently supported platforms: "
        + FACEBOOK_IDENTIFIER + "(aliases: " + FACEBOOK_IDENTIFIER_ALIAS + "), "
        + INSTAGRAM_IDENTIFIER + "(aliases: " + INSTAGRAM_IDENTIFIER_ALIAS + ")\n";


    /**
     * Returns the SocialInfo object represented by the input String.
     * @throws IllegalValueException if the input does not represent a valid SocialInfo recognized
     * by the defined mappings
     */
    public static SocialInfo parseSocialInfo(String rawSocialInfo) throws IllegalValueException {
        String[] splitRawSocialInfo = rawSocialInfo.split(" ", 2);
        if (splitRawSocialInfo.length != 2) {
            throw new IllegalValueException(INVALID_SYNTAX_EXCEPTION_MESSAGE);
        }

        if (isFacebookInfo(splitRawSocialInfo)) {
            return buildFacebookInfo(splitRawSocialInfo);
        } else if (isInstagramInfo(splitRawSocialInfo)) {
            return buildInstagramInfo(splitRawSocialInfo);
        } else {
            throw new IllegalValueException(UNRECOGNIZED_SOCIAL_TYPE_MESSAGE);
        }

    }

    private static boolean isFacebookInfo(String[] splitRawSocialInfo) {
        String trimmedSocialType = splitRawSocialInfo[SOCIAL_TYPE_INDEX].trim();
        return trimmedSocialType.equals(FACEBOOK_IDENTIFIER) || trimmedSocialType.equals(FACEBOOK_IDENTIFIER_ALIAS);
    }

    private static SocialInfo buildFacebookInfo(String[] splitRawSocialInfo) {
        String trimmedSocialUsername = splitRawSocialInfo[SOCIAL_USERNAME_INDEX].trim();
        String socialUrl = "https://facebook.com/" + trimmedSocialUsername;
        return new SocialInfo(FACEBOOK_IDENTIFIER, trimmedSocialUsername, socialUrl);
    }

    private static boolean isInstagramInfo(String[] splitRawSocialInfo) {
        String trimmedSocialType = splitRawSocialInfo[SOCIAL_TYPE_INDEX].trim();
        return trimmedSocialType.equals(INSTAGRAM_IDENTIFIER) || trimmedSocialType.equals(INSTAGRAM_IDENTIFIER_ALIAS);
    }

    private static SocialInfo buildInstagramInfo(String[] splitRawSocialInfo) {
        String trimmedSocialUsername = splitRawSocialInfo[SOCIAL_USERNAME_INDEX].trim();
        String socialUrl = "https://instagram.com/" + trimmedSocialUsername;
        return new SocialInfo(INSTAGRAM_IDENTIFIER, trimmedSocialUsername, socialUrl);
    }
}
```
###### /java/seedu/address/logic/parser/DeleteCommandParser.java
``` java
    /**
     * Utility function to check that the input arguments is not empty.
     * Throws a parse exception if it is empty.
     */
    private void checkArgsNotEmpty(String args) throws ParseException {
        if (args == null || args.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }
    }
    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns an DeleteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteCommand parse(String args) throws ParseException {
        // check that the raw args are not empty before processing
        checkArgsNotEmpty(args);
        OptionBearingArgument opArgs = new OptionBearingArgument(args);
        String filteredArgs = opArgs.getFilteredArgs();
        // check that the filtered args are not empty
        checkArgsNotEmpty(filteredArgs);

        if (opArgs.getOptions().contains(DeleteByTagCommand.COMMAND_OPTION)) {
            List<String> tags = parseWhitespaceSeparatedStrings(filteredArgs);
            HashSet<String> tagSet = new HashSet<>(tags);
            return new DeleteByTagCommand(tagSet);
        } else {
            try {
                List<Index> indexes = ParserUtil.parseMultipleIndexes(filteredArgs);
                return new DeleteByIndexCommand(indexes);
            } catch (IllegalValueException ive) {
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
            }
        }
    }
```
###### /java/seedu/address/logic/parser/FindCommandParser.java
``` java
    /**
     * Utility function to check that the input arguments is not empty.
     * Throws a parse exception if it is empty.
     */
    private void checkArgsNotEmpty(String args) throws ParseException {
        if (args == null || args.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns an FindCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        // check that the raw args are not empty before processing
        checkArgsNotEmpty(args);
        OptionBearingArgument opArgs = new OptionBearingArgument(args);
        String filteredArgs = opArgs.getFilteredArgs();
        // check that the filtered args are not empty
        checkArgsNotEmpty(filteredArgs);

        if (opArgs.getOptions().contains(FindByTagsCommand.COMMAND_OPTION)) {
            List<String> tagKeywords = parseWhitespaceSeparatedStrings(filteredArgs);
            TagsContainKeywordsPredicate predicate = new TagsContainKeywordsPredicate(tagKeywords);
            return new FindByTagsCommand(predicate);
        } else {
            checkArgsNotEmpty(opArgs.getFilteredArgs());
            List<String> nameKeywords = parseWhitespaceSeparatedStrings(filteredArgs);
            NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(nameKeywords);
            return new FindByNameCommand(predicate);
        }
    }
```
###### /java/seedu/address/logic/parser/ParserUtil.java
``` java
    /**
     * Splits {@code args} by whitespace and returns it
     */
    public static List<String> parseWhitespaceSeparatedStrings(String args) {
        requireNonNull(args);
        String[] splitArgs = args.split("\\s+");
        return Arrays.asList(splitArgs);
    }
```
###### /java/seedu/address/logic/parser/ParserUtil.java
``` java
    /**
     * Parses {@code Collection<String> rawSocialInfos} into {@code Set<SocialInfo}.
     * @param rawSocialInfos
     * @return
     */
    public static Set<SocialInfo> parseSocialInfos(Collection<String> rawSocialInfos) throws IllegalValueException {
        requireNonNull(rawSocialInfos);
        final Set<SocialInfo> socialInfoSet = new HashSet<>();
        for (String rawSocialInfo : rawSocialInfos) {
            socialInfoSet.add(parseSocialInfo(rawSocialInfo));
        }
        return socialInfoSet;
    }
```
###### /java/seedu/address/logic/parser/SortCommandParser.java
``` java
/**
 * Parses input arguments and creates a new SortCommand object.
 */
public class SortCommandParser implements Parser<SortCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the SortCommand
     * and returns an SortCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public SortCommand parse(String args) throws ParseException {
        OptionBearingArgument opArgs = new OptionBearingArgument(args);
        Set<String> options = opArgs.getOptions();

        if (!opArgs.getFilteredArgs().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
        }

        if (options.contains(SortByNameCommand.COMMAND_OPTION)) {
            return new SortByNameCommand();
        } else {
            return new SortByDefaultCommand();
        }
    }
}
```
###### /java/seedu/address/logic/parser/EditCommandParser.java
``` java
    /**
     * Parses {@code Collection<String> socialInfos} into a {@code Set<SocialInfo>} if {@code tags} is non-empty.
     * If {@code socialInfos} contain only one element which is an empty string, it will be parsed into a
     * {@code Set<SocialInfo>} containing zero elements.
     */
    private Optional<Set<SocialInfo>> parseSocialInfosForEdit(Collection<String> socialInfos)
            throws IllegalValueException {
        assert socialInfos != null;

        if (socialInfos.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(ParserUtil.parseSocialInfos(getCollectionToParse(socialInfos)));
    }

    /**
     * Checks if the input collection represents an empty collection. Returns an empty collection if it is.
     */
    private Collection<String> getCollectionToParse(Collection<String> collection) {
        return collection.size() == 1 && collection.contains("")
                ? Collections.emptySet()
                : collection;
    }
```
###### /java/seedu/address/logic/commands/DeleteByIndexCommand.java
``` java
/**
 * Deletes persons from the list identified by their indexes in the last displayed list in the address book.
 */
public class DeleteByIndexCommand extends DeleteCommand {
    private Collection<Index> targetIndexes;

    public DeleteByIndexCommand(Collection<Index> indexes) {
        targetIndexes = indexes;
    }

    /**
     * Returns the list of persons in the last shown list referenced by the collection of indexes provided.
     */
    private Collection<ReadOnlyPerson> mapPersonsToIndexes(Collection<Index> indexes) throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();
        HashSet<ReadOnlyPerson> personSet = new HashSet<>();
        for (Index index : indexes) {
            if (index.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            ReadOnlyPerson person = lastShownList.get(index.getZeroBased());
            personSet.add(person);
        }

        return personSet;
    }

    @Override
    public Collection<ReadOnlyPerson> getPersonsToDelete() throws CommandException {
        return mapPersonsToIndexes(targetIndexes);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteByIndexCommand // instanceof handles nulls
                && this.targetIndexes.equals(((DeleteByIndexCommand) other).targetIndexes)); // state check
    }
}
```
###### /java/seedu/address/logic/commands/DeleteCommand.java
``` java
    /**
     * Returns the collection of persons to be deleted.
     * To be implemented by the classes inheriting this class.
     */
    public abstract Collection<ReadOnlyPerson> getPersonsToDelete() throws CommandException;
```
###### /java/seedu/address/logic/commands/ExportCommand.java
``` java
/**
 * Exports existing contacts to external XML file
 */
public class ExportCommand extends Command {

    public static final String COMMAND_WORD = "export";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Exports existing contacts.\n"
            + "Parameters: FILE PATH (must be a valid file path where the current user has write access\n"
            + "Example: " + COMMAND_WORD + " /Users/seedu/Documents/exportedData.xml";

    public static final String MESSAGE_EXPORT_CONTACTS_SUCCESS = "Contacts exported to: %1$s";
    public static final String MESSAGE_EXPORT_CONTACTS_FAILURE = "Unable to export contacts to: %1$s";

    private final Path exportFilePath;

    public ExportCommand(String filePath) {
        // we store it as a Path rather than a String so that we can get the absolute file path
        // this makes it clearer to the user where the file is saved
        exportFilePath = Paths.get(filePath);
    }

    @Override
    public CommandResult execute() throws CommandException {
        ReadOnlyAddressBook readOnlyAddressBook = model.getAddressBook();
        String absoluteExportFilePathString = exportFilePath.toAbsolutePath().toString();
        try {
            storage.saveAddressBook(readOnlyAddressBook, absoluteExportFilePathString);
        } catch (IOException ioe) {
            throw new CommandException(String.format(MESSAGE_EXPORT_CONTACTS_FAILURE, absoluteExportFilePathString));
        }
        return new CommandResult(String.format(MESSAGE_EXPORT_CONTACTS_SUCCESS, absoluteExportFilePathString));
    }

    @Override
    public void setData(Model model, Storage storage, CommandHistory commandHistory, UndoRedoStack undoRedoStack) {
        this.model = model;
        this.storage = storage;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ExportCommand // instanceof handles nulls
                && this.exportFilePath.equals(((ExportCommand) other).exportFilePath)); // state check
    }
}
```
###### /java/seedu/address/logic/commands/SortByDefaultCommand.java
``` java
/**
 * Sorts the displayed person list by their names in alphabetical order.
 */
public class SortByDefaultCommand extends SortCommand {
    @Override
    public Comparator<ReadOnlyPerson> getComparator() {
        return new PersonDefaultComparator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || other instanceof SortByDefaultCommand; // instanceof handles nulls
    }
}
```
###### /java/seedu/address/logic/commands/SortCommand.java
``` java
/**
 * Sorts the displayed person list.
 */
public abstract class SortCommand extends Command {

    public static final String COMMAND_WORD = "sort";
    public static final String COMMAND_ALIAS = "sr";

    public static final String MESSAGE_SORT_SUCCESS = "Person list sorted!\n";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Sorts the displayed person list.\n"
            + "Alias: " + COMMAND_ALIAS + "\n"
            + "Parameters: [OPTION]\n"
            + "Options: \n"
            + "\tdefault - Sorts first by favorite status of a contact, then by name in alphabetical order.\n"
            + "\t" + SortByNameCommand.COMMAND_OPTION + " - Sorts by name in alphabetical order\n"
            + "Example: \n"
            + COMMAND_WORD + "\n";

    @Override
    public CommandResult execute() {
        Comparator<ReadOnlyPerson> comparator = getComparator();
        model.sortPersons(comparator);
        return new CommandResult(MESSAGE_SORT_SUCCESS);
    }

    /**
     * Gets the comparator used to order the person list
     */
    public abstract Comparator<ReadOnlyPerson> getComparator();
}
```
###### /java/seedu/address/logic/commands/SortByNameCommand.java
``` java
/**
 * Sorts the displayed person list by their names in alphabetical order.
 */
public class SortByNameCommand extends SortCommand {

    public static final String COMMAND_OPTION = "name";

    @Override
    public Comparator<ReadOnlyPerson> getComparator() {
        return new PersonNameComparator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || other instanceof SortByNameCommand; // instanceof handles nulls
    }
}
```
###### /java/seedu/address/logic/commands/DeleteByTagCommand.java
``` java
/**
 * Deletes persons from the list identified by their indexes in the last displayed list in the address book.
 */
public class DeleteByTagCommand extends DeleteCommand {
    public static final String COMMAND_OPTION = "tag";

    private Set<String> targetTags;

    public DeleteByTagCommand(Set<String> tags) {
        targetTags = tags;
    }

    /**
     * Returns the list of persons in the last shown list referenced by the collection of tags provided.
     */
    private Collection<ReadOnlyPerson> mapPersonsToTags(Collection<String> tags) {
        TagsContainKeywordsPredicate predicate = new TagsContainKeywordsPredicate(tags);
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();
        List<ReadOnlyPerson> persons = lastShownList.stream().filter(predicate).collect(Collectors.toList());
        return persons;
    }

    @Override
    public Collection<ReadOnlyPerson> getPersonsToDelete() {
        return mapPersonsToTags(targetTags);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteByTagCommand // instanceof handles nulls
                && this.targetTags.equals(((DeleteByTagCommand) other).targetTags)); // state check
    }
}
```
###### /java/seedu/address/logic/commands/FindByTagsCommand.java
``` java
/**
 * Finds and lists all persons in address book whose tags contains any of the argument keywords.
 * Keyword matching is case sensitive.
 */
public class FindByTagsCommand extends FindCommand {
    public static final String COMMAND_OPTION = "tag";

    public FindByTagsCommand(TagsContainKeywordsPredicate predicate) {
        super(predicate);
    }
}
```
###### /java/seedu/address/logic/commands/EditCommand.java
``` java
        public Optional<Set<SocialInfo>> getSocialInfos() {
            return Optional.ofNullable(socialInfos);
        }

        public void setSocialInfos(Set<SocialInfo> socialInfos) {
            this.socialInfos = socialInfos;
        }
```
###### /java/seedu/address/logic/commands/ImportCommand.java
``` java
/**
 * Imports contacts from an external XML file
 */
public class ImportCommand extends Command {

    public static final String COMMAND_WORD = "import";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Imports contacts from external file.\n"
            + "Parameters: FILE PATH (must be a path to a valid exported contacts data file\n"
            + "Example: " + COMMAND_WORD + " /Users/seedu/Documents/exportedData.xml";

    public static final String MESSAGE_IMPORT_CONTACTS_SUCCESS = "Contacts imported from: %1$s";
    public static final String MESSAGE_IMPORT_CONTACTS_DCE_FAILURE = "Unable to parse file at: %1$s";
    public static final String MESSAGE_IMPORT_CONTACTS_IO_FAILURE = "Unable to import contacts from: %1$s";
    public static final String MESSAGE_IMPORT_CONTACTS_FNF_FAILURE = "File at %1$s not found";

    private final Path importFilePath;

    public ImportCommand(String filePath) {
        // we store it as a Path rather than a String so that we can get the absolute file path
        // this makes it clearer to the user where the file is imported from
        importFilePath = Paths.get(filePath);
    }

    @Override
    public CommandResult execute() throws CommandException {
        String absoluteImportFilePathString = importFilePath.toAbsolutePath().toString();

        Optional<ReadOnlyAddressBook> optionalImportedAddressBook;
        try {
            optionalImportedAddressBook = storage.readAddressBook(absoluteImportFilePathString);
        } catch (DataConversionException dce) {
            throw new CommandException(
                    String.format(MESSAGE_IMPORT_CONTACTS_DCE_FAILURE, absoluteImportFilePathString));
        } catch (IOException ioe) {
            throw new CommandException(
                    String.format(MESSAGE_IMPORT_CONTACTS_IO_FAILURE, absoluteImportFilePathString));
        }

        ReadOnlyAddressBook importedAddressBook;
        if (optionalImportedAddressBook.isPresent()) {
            importedAddressBook = optionalImportedAddressBook.get();
        } else {
            // no address book returned, so we know that file was not found
            throw new CommandException(
                    String.format(MESSAGE_IMPORT_CONTACTS_FNF_FAILURE, absoluteImportFilePathString));
        }

        model.addPersons(importedAddressBook.getPersonList());
        return new CommandResult(String.format(MESSAGE_IMPORT_CONTACTS_SUCCESS, absoluteImportFilePathString));
    }

    @Override
    public void setData(Model model, Storage storage, CommandHistory commandHistory, UndoRedoStack undoRedoStack) {
        this.model = model;
        this.storage = storage;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ImportCommand // instanceof handles nulls
                && this.importFilePath.equals(((ImportCommand) other).importFilePath)); // state check
    }
}
```
###### /java/seedu/address/logic/commands/FindByNameCommand.java
``` java
/**
 * Finds and lists all persons in address book whose name contains any of the argument keywords.
 * Keyword matching is case sensitive.
 */
public class FindByNameCommand extends FindCommand {

    public FindByNameCommand(NameContainsKeywordsPredicate predicate) {
        super(predicate);
    }
}
```
###### /java/seedu/address/storage/XmlAdaptedSocialInfo.java
``` java
/**
 * JAXB-friendly adapted version of the SocialInfo.
 */
public class XmlAdaptedSocialInfo {

    @XmlElement(required = true)
    private String username;
    @XmlElement(required = true)
    private String socialType;
    @XmlElement(required = true)
    private String socialUrl;

    /**
     * Constructs an XmlAdaptedSocialInfo.
     * This is the no-arg constructor that is required by JAXB.
     */
    public XmlAdaptedSocialInfo() {}

    /**
     * Converts a given SocialInfo into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created
     */
    public XmlAdaptedSocialInfo(SocialInfo source) {
        socialType = source.getSocialType();
        username = source.getUsername();
        socialUrl = source.getSocialUrl();
    }

    /**
     * Converts this jaxb-friendly adapted social info object into the model's SocialInfo object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person
     */
    public SocialInfo toModelType() throws IllegalValueException {
        return new SocialInfo(socialType, username, socialUrl);
    }

}
```
###### /java/seedu/address/model/util/SampleDataUtil.java
``` java
    /**
     * Returns a set containing the list of SocialInfo given
     */
    public static Set<SocialInfo> getSocialInfoSet(SocialInfo... socialInfos) {
        HashSet<SocialInfo> socialInfoSet = new HashSet<>();
        for (SocialInfo socialInfo : socialInfos) {
            socialInfoSet.add(socialInfo);
        }

        return socialInfoSet;
    }
```
###### /java/seedu/address/model/person/PersonNameComparator.java
``` java
/**
 * Default comparator for persons. Sorts first by name in alphabetical order, then by favorite
 * then by phone in numeric order, then by address in alphabetical order, then by email in alphabetical order
 */
public class PersonNameComparator implements Comparator<ReadOnlyPerson> {
    @Override
    public int compare(ReadOnlyPerson thisPerson, ReadOnlyPerson otherPerson) {
        if (!thisPerson.getName().equals(otherPerson.getName())) {
            return compareName(thisPerson, otherPerson);
        } else if (!thisPerson.getFavorite().equals(otherPerson.getFavorite())) {
            return compareFavorite(thisPerson, otherPerson);
        } else if (!thisPerson.getPhone().equals(otherPerson.getPhone())) {
            return comparePhone(thisPerson, otherPerson);
        } else if (!thisPerson.getAddress().equals(otherPerson.getAddress())) {
            return compareAddress(thisPerson, otherPerson);
        } else {
            return compareEmail(thisPerson, otherPerson);
        }
    }
}

```
###### /java/seedu/address/model/person/PersonComparatorUtil.java
``` java
/**
 * Utility class with useful methods for writing person comparators
 */
public class PersonComparatorUtil {

    /**
     * Compares two persons based on whether or not they are favorited. The favorited person will be ordered first.
     * If both persons have the same favorite status (yes/no), they are considered equal.
     */
    public static int compareFavorite(ReadOnlyPerson thisPerson, ReadOnlyPerson otherPerson) {
        boolean isThisPersonFavorite = thisPerson.getFavorite().isFavorite();
        boolean isOtherPersonFavorite = otherPerson.getFavorite().isFavorite();
        if (isThisPersonFavorite && !isOtherPersonFavorite) {
            return -1;
        } else if (!isThisPersonFavorite && isOtherPersonFavorite) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Compares two persons based on their names
     */
    public static int compareName(ReadOnlyPerson thisPerson, ReadOnlyPerson otherPerson) {
        String thisPersonName = thisPerson.getName().toString();
        String otherPersonName = otherPerson.getName().toString();
        return thisPersonName.compareToIgnoreCase(otherPersonName);
    }

    /**
     * Compares two persons based on their phones
     */
    public static int comparePhone(ReadOnlyPerson thisPerson, ReadOnlyPerson otherPerson) {
        String thisPersonPhone = thisPerson.getPhone().toString();
        String otherPersonPhone = otherPerson.getPhone().toString();
        return thisPersonPhone.compareToIgnoreCase(otherPersonPhone);
    }

    /**
     * Compares two persons based on their addresses
     */
    public static int compareAddress(ReadOnlyPerson thisPerson, ReadOnlyPerson otherPerson) {
        String thisPersonAddress = thisPerson.getAddress().toString();
        String otherPersonAddress = otherPerson.getAddress().toString();
        return thisPersonAddress.compareToIgnoreCase(otherPersonAddress);
    }

    /**
     * Compares two persons based on their emails
     */
    public static int compareEmail(ReadOnlyPerson thisPerson, ReadOnlyPerson otherPerson) {
        String thisPersonEmail = thisPerson.getEmail().toString();
        String otherPersonEmail = otherPerson.getEmail().toString();
        return thisPersonEmail.compareToIgnoreCase(otherPersonEmail);
    }
}
```
###### /java/seedu/address/model/person/PersonDefaultComparator.java
``` java
/**
 * Default comparator for persons. Sorts first by favorites, then by name in alphabetical order,
 * then by phone in numeric order, then by address in alphabetical order, then by email in alphabetical order
 */
public class PersonDefaultComparator implements Comparator<ReadOnlyPerson> {
    @Override
    public int compare(ReadOnlyPerson thisPerson, ReadOnlyPerson otherPerson) {
        if (!thisPerson.getFavorite().equals(otherPerson.getFavorite())) {
            return compareFavorite(thisPerson, otherPerson);
        } else if (!thisPerson.getName().equals(otherPerson.getName())) {
            return compareName(thisPerson, otherPerson);
        } else if (!thisPerson.getPhone().equals(otherPerson.getPhone())) {
            return comparePhone(thisPerson, otherPerson);
        } else if (!thisPerson.getAddress().equals(otherPerson.getAddress())) {
            return compareAddress(thisPerson, otherPerson);
        } else {
            return compareEmail(thisPerson, otherPerson);
        }
    }
}
```
###### /java/seedu/address/model/person/Person.java
``` java
    @Override
    public ObjectProperty<UniqueSocialInfoList> socialInfoProperty() {
        return socialInfos;
    }

    @Override
    public Set<SocialInfo> getSocialInfos() {
        return Collections.unmodifiableSet(socialInfos.get().toSet());
    }

    public void setSocialInfos(Set<SocialInfo> replacement) {
        socialInfos.set(new UniqueSocialInfoList(replacement));
    }
```
###### /java/seedu/address/model/person/TagsContainKeywordsPredicate.java
``` java
/**
 * Tests that a {@code ReadOnlyPerson}'s {@code Name} matches any of the keywords given.
 */
public class TagsContainKeywordsPredicate implements Predicate<ReadOnlyPerson> {
    private final Collection<String> keywords;

    public TagsContainKeywordsPredicate(Collection<String> keywords) {
        this.keywords = keywords;
    }

    private boolean personTagsMatchKeyword(ReadOnlyPerson person, String keyword) {
        Set<Tag> tags = person.getTags();
        return tags.stream().anyMatch(tag -> StringUtil.containsWordIgnoreCase(tag.tagName, keyword));
    }

    @Override
    public boolean test(ReadOnlyPerson person) {
        return keywords.stream()
                .anyMatch(keyword -> personTagsMatchKeyword(person, keyword));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof TagsContainKeywordsPredicate // instanceof handles nulls
                && this.keywords.equals(((TagsContainKeywordsPredicate) other).keywords)); // state check
    }

}
```
###### /java/seedu/address/model/social/UniqueSocialInfoList.java
``` java
/**
 * A list of social media information that enforces no nulls and
 * no duplicate social info types between its elements.
 */
public class UniqueSocialInfoList implements Iterable<SocialInfo> {

    private final ObservableList<SocialInfo> internalList = FXCollections.observableArrayList();

    /**
     * Constructs empty SocialInfoList.
     */
    public UniqueSocialInfoList() {}

    /**
     * Creates a UniqueSocialInfoList using given socialInfos.
     * Enforces no nulls.
     */
    public UniqueSocialInfoList(Set<SocialInfo> socialInfos) {
        requireAllNonNull(socialInfos);
        internalList.addAll(socialInfos);

        assert internalListSocialTypesUnique();
    }


    /**
     * Returns all the social media information in this list as a Set.
     */
    public Set<SocialInfo> toSet() {
        assert internalListSocialTypesUnique();
        return new HashSet<>(internalList);
    }

    /**
     * Replaces the SocialInfo in this list with those in the argument SocialInfo list.
     * @param socialInfos
     */
    public void setSocialInfos(Set<SocialInfo> socialInfos) {
        requireAllNonNull(socialInfos);
        internalList.setAll(socialInfos);

        assert internalListSocialTypesUnique();
    }

    /**
     * Returns true if the list contains a SocialInfo with the same type
     */
    public boolean containsSameType(SocialInfo toCheck) {
        requireNonNull(toCheck);

        String toCheckType = toCheck.getSocialType();
        for (SocialInfo socialInfo : internalList) {
            String thisSocialType = socialInfo.getSocialType();
            if (toCheckType.equals(thisSocialType)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Adds a SocialInfo to the list
     * @throws DuplicateSocialTypeException if the SocialInfo to add is a duplicate of an existing
     * SocialInfo in the list.
     */
    public void add(SocialInfo toAdd) throws DuplicateSocialTypeException {
        requireNonNull(toAdd);

        if (containsSameType(toAdd)) {
            throw new DuplicateSocialTypeException();
        }
        internalList.add(toAdd);

        assert internalListSocialTypesUnique();
    }

    /**
     * Checks that there are no SocialInfos of the same type in the collection
     */
    private boolean internalListSocialTypesUnique() {
        HashSet<String> socialTypes = new HashSet<>();

        for (SocialInfo socialInfo : internalList) {
            String socialType = socialInfo.getSocialType();
            if (socialTypes.contains(socialType)) {
                return false;
            } else {
                socialTypes.add(socialType);
            }
        }

        return true;
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     * @return
     */
    public ObservableList<SocialInfo> asObservableList() {
        assert internalListSocialTypesUnique();

        return FXCollections.unmodifiableObservableList(internalList);
    }

    @Override
    public Iterator<SocialInfo> iterator() {
        assert internalListSocialTypesUnique();
        return internalList.iterator();

    }

    @Override
    public boolean equals(Object other) {
        assert internalListSocialTypesUnique();

        return other == this
                || (other instanceof UniqueSocialInfoList
                && this.internalList.equals(((UniqueSocialInfoList) other).internalList));
    }

    /**
     * Returns true if the elements in this list is equal to the elements in {@code other}.
     * The elements do not have to be in the same order.
     */
    public boolean equalsOrderInsensitive(UniqueSocialInfoList other) {
        assert internalListSocialTypesUnique();
        assert other.internalListSocialTypesUnique();
        return other == this
                || new HashSet<>(internalList).equals(new HashSet<>(other.internalList));
    }

    /**
     * Signals that an operation would have violated the 'no duplicate social type' property of the list.
     */
    public static class DuplicateSocialTypeException extends DuplicateDataException {
        protected DuplicateSocialTypeException() {
            super("Operation would result in more than one SocialInfo of the same type");
        }
    }
}
```
###### /java/seedu/address/model/social/SocialInfo.java
``` java
/**
 * Represents information about a social media account in the address book.
 */
public class SocialInfo {

    private final String username;
    private final String socialType;
    private final String socialUrl;

    public SocialInfo(String socialType, String username, String socialUrl) {
        requireAllNonNull(username, socialType, socialUrl);
        String trimmedUsername = username.trim();
        String trimmedSocialType = socialType.trim();
        String trimmedSocialUrl = socialUrl.trim();
        this.username = trimmedUsername;
        this.socialType = trimmedSocialType;
        this.socialUrl = trimmedSocialUrl;
    }

    /**
     * Returns the username for the represented account
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Returns the type (usually platform) of this social media information
     */
    public String getSocialType() {
        return this.socialType;
    }

    /**
     * Returns the link to the social media feed for the represented account
     */
    public String getSocialUrl() {
        return this.socialUrl;
    }

    /**
     * Format state as text for viewing.
     */
    public String toString() {
        return "["
            + "Type: " + getSocialType() + ", "
            + "Username: " + getUsername() + ", "
            + "Link: " + getSocialUrl() + "]";
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof SocialInfo
                && this.getUsername().equals(((SocialInfo) other).getUsername())
                && this.getSocialType().equals(((SocialInfo) other).getSocialType())
                && this.getSocialUrl().equals(((SocialInfo) other).getSocialUrl()));

    }
}
```
###### /java/seedu/address/model/ModelManager.java
``` java
    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, UserPrefs userPrefs) {
        super();
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        // To avoid having to re-sort upon every change in filter, we first sort the list before applying the filter
        // This was we only need to re-sort when there is a change in the backing person list
        sortedPersons = new SortedList<>(this.addressBook.getPersonList(), new PersonDefaultComparator());
        filteredPersons = new FilteredList<>(sortedPersons);
    }
```
###### /java/seedu/address/model/ModelManager.java
``` java
    @Override
    public synchronized void addPersons(Collection<ReadOnlyPerson> persons) {
        for (ReadOnlyPerson person : persons) {
            try {
                addressBook.addPerson(person);
            } catch (DuplicatePersonException e) {
                logger.info("Person already in address book: " + person.toString());
                continue;
            }
        }
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        indicateAddressBookChanged();
    }
```
###### /java/seedu/address/model/ModelManager.java
``` java
    @Override
    public void sortPersons(Comparator<ReadOnlyPerson> comparator) {
        sortedPersons.setComparator(comparator);
    }
```
###### /java/seedu/address/model/Model.java
``` java
    /** Sorts the persons in the address book based on the input {@code comparator} */
    void sortPersons(Comparator<ReadOnlyPerson> comparator);

```
