# marvinchin
###### \java\seedu\address\logic\commands\DeleteByIndexCommand.java
``` java
/**
 * Deletes {@code Person}s from the address book identified by their indexes in the last displayed person list.
 */
public class DeleteByIndexCommand extends DeleteCommand {
    private Collection<Index> targetIndexes;

    public DeleteByIndexCommand(Collection<Index> indexes) {
        targetIndexes = indexes;
    }

    /**
     * Returns the list of {@code Person}s in the last shown list referenced by indexes provided.
     * @throws CommandException if any of the input indexes are invalid.
     */
    private Collection<ReadOnlyPerson> mapPersonsToIndexes(Collection<Index> indexes) throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();
        ArrayList<ReadOnlyPerson> personSet = new ArrayList<>();
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
    protected Collection<ReadOnlyPerson> getPersonsToDelete() throws CommandException {
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
###### \java\seedu\address\logic\commands\DeleteByTagCommand.java
``` java
/**
 * Deletes {@code Person}s from the list whose {@code Tag}s match the input keywords.
 */
public class DeleteByTagCommand extends DeleteCommand {
    public static final String COMMAND_OPTION = "tag";

    private Set<String> targetTags;

    public DeleteByTagCommand(Set<String> tags) {
        targetTags = tags;
    }

    /**
     * Returns the list of {@code Person}s in the last shown list whose {@code Tag}s match the input keywords.
     */
    private Collection<ReadOnlyPerson> mapPersonsToTags(Collection<String> keywords) {
        TagsContainKeywordsPredicate predicate = new TagsContainKeywordsPredicate(keywords);
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();
        List<ReadOnlyPerson> matchedPersons = lastShownList.stream().filter(predicate).collect(Collectors.toList());
        return matchedPersons;
    }

    @Override
    protected Collection<ReadOnlyPerson> getPersonsToDelete() {
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
###### \java\seedu\address\logic\commands\DeleteCommand.java
``` java
    /**
     * Returns the collection of {@code Person}s to be deleted.
     */
    protected abstract Collection<ReadOnlyPerson> getPersonsToDelete() throws CommandException;
```
###### \java\seedu\address\logic\commands\EditCommand.java
``` java
        public Optional<Set<SocialInfo>> getSocialInfos() {
            return Optional.ofNullable(socialInfos);
        }

        public void setSocialInfos(Set<SocialInfo> socialInfos) {
            this.socialInfos = socialInfos;
        }
```
###### \java\seedu\address\logic\commands\ExportCommand.java
``` java
/**
 * Exports existing {@code Person}s to an external XML file.
 */
public class ExportCommand extends Command {

    public static final String COMMAND_WORD = "export";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Exports existing contacts.\n"
            + "Parameters: FILE PATH (must be a valid file path where the current user has write access\n"
            + "Example: " + COMMAND_WORD + " /Users/seedu/Documents/exportedData.xml";

    public static final String MESSAGE_EXPORT_CONTACTS_SUCCESS = "Contacts exported to: %1$s";
    public static final String MESSAGE_EXPORT_CONTACTS_FAILURE = "Unable to export contacts to: %1$s";

    private static final Logger logger = LogsCenter.getLogger(ExportCommand.class);

    private final Path exportFilePath;

    public ExportCommand(String filePath) {
        exportFilePath = Paths.get(filePath);
    }

    @Override
    public CommandResult execute() throws CommandException {
        ReadOnlyAddressBook currentAddressBook = model.getAddressBook();
        ReadOnlyAddressBook exportAddressBook = generateExportAddressBook(currentAddressBook);
        String absoluteExportFilePathString = exportFilePath.toAbsolutePath().toString();
        try {
            storage.saveAddressBook(exportAddressBook, absoluteExportFilePathString);
        } catch (IOException ioe) {
            logger.warning("Error writing to file at: " + absoluteExportFilePathString);
            throw new CommandException(String.format(MESSAGE_EXPORT_CONTACTS_FAILURE, absoluteExportFilePathString));
        }
        return new CommandResult(String.format(MESSAGE_EXPORT_CONTACTS_SUCCESS, absoluteExportFilePathString));
    }

    /**
     * Generates an address book for exporting that is equivalent to the input address book, but with all display
     * pictures removed.
     */
    private ReadOnlyAddressBook generateExportAddressBook(ReadOnlyAddressBook currentAddressBook) {
        AddressBook exportAddressBook = new AddressBook(currentAddressBook);

        for (ReadOnlyPerson person : exportAddressBook.getPersonList()) {
            Person personWithoutDisplayPicture = new Person(person);
            try {
                personWithoutDisplayPicture.setDisplayPhoto(new DisplayPhoto(null));
                exportAddressBook.updatePerson(person, personWithoutDisplayPicture);
            } catch (IllegalValueException ive) {
                assert false : "Display photo should not be invalid";
            } catch (PersonNotFoundException pnfe) {
                assert false : "Person should not be missing";
            }
        }

        return exportAddressBook;
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
###### \java\seedu\address\logic\commands\FindByNameCommand.java
``` java
/**
 * Finds and lists all {@code Person} in address book whose {@code Name} contains any of the input keywords.
 * Keyword matching is case sensitive.
 */
public class FindByNameCommand extends FindCommand {
    private NameContainsKeywordsPredicate predicate;

    public FindByNameCommand(NameContainsKeywordsPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    protected Predicate<ReadOnlyPerson> getPredicate() {
        return predicate;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof FindByNameCommand // instanceof handles nulls
                && this.predicate.equals(((FindByNameCommand) other).predicate)); // state check
    }
}
```
###### \java\seedu\address\logic\commands\FindByTagsCommand.java
``` java
/**
 * Finds and lists all {@code Person}s in address book whose {@code Tag}s contains any of the input keywords.
 * Keyword matching is case sensitive.
 */
public class FindByTagsCommand extends FindCommand {
    public static final String COMMAND_OPTION = "tag";

    private TagsContainKeywordsPredicate predicate;

    public FindByTagsCommand(TagsContainKeywordsPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    protected Predicate<ReadOnlyPerson> getPredicate() {
        return predicate;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof FindByTagsCommand // instanceof handles nulls
                && this.predicate.equals(((FindByTagsCommand) other).predicate)); // state check
    }
}
```
###### \java\seedu\address\logic\commands\FindCommand.java
``` java
/**
 * Finds and lists all {@code Person}s in address book who meet the specified criteria.
 */
public abstract class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";
    public static final String COMMAND_ALIAS = "f";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all persons who meet the specified criteria"
            + "and displays them as a list with index numbers.\n"
            + "Alias: " + COMMAND_ALIAS + "\n"
            + "Options: \n"
            + "\tdefault - Find contacts whose names contain any of the specified keywords (case-sensitive)\n"
            + "\t" + FindByTagsCommand.COMMAND_OPTION
            + " - Find contacts whose tags contain any of the specified keywords (case-sensitive)\n"
            + "Parameters: [OPTION] KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: \n"
            + COMMAND_WORD + " bob alice\n"
            + COMMAND_WORD + " -" + FindByTagsCommand.COMMAND_OPTION + " friends colleagues";

    @Override
    public CommandResult execute() {
        model.updateFilteredPersonList(getPredicate());
        return new CommandResult(getMessageForPersonListShownSummary(model.getFilteredPersonList().size()));
    }


    /**
     * Returns the predicate used to determine which {@code Person}s should be shown.
     */
    protected abstract Predicate<ReadOnlyPerson> getPredicate();
}
```
###### \java\seedu\address\logic\commands\ImportCommand.java
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

    private static final Logger logger = LogsCenter.getLogger(ImportCommand.class);

    private final Path importFilePath;

    public ImportCommand(String filePath) {
        importFilePath = Paths.get(filePath);
    }

    @Override
    public CommandResult execute() throws CommandException {
        String absoluteImportFilePathString = importFilePath.toAbsolutePath().toString();

        Optional<ReadOnlyAddressBook> optionalImportedAddressBook;
        try {
            optionalImportedAddressBook = storage.readAddressBook(absoluteImportFilePathString);
        } catch (DataConversionException dce) {
            logger.warning("Error converting file at: " + absoluteImportFilePathString);
            throw new CommandException(
                    String.format(MESSAGE_IMPORT_CONTACTS_DCE_FAILURE, absoluteImportFilePathString));
        } catch (IOException ioe) {
            logger.warning("Error reading file at: " + absoluteImportFilePathString);
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
###### \java\seedu\address\logic\commands\SelectCommand.java
``` java
    @Override
    public CommandResult execute() throws CommandException {

        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        ReadOnlyPerson selectedPerson = lastShownList.get(targetIndex.getZeroBased());

        if (socialType != null && !checkPersonHasSocialType(selectedPerson, socialType)) {
            // check to see if the social type matches any of the selected person's social media accounts
            // if the selected person does not have the requested social type, throw a command exception
            throw new CommandException(MESSAGE_SOCIAL_TYPE_NOT_FOUND);
        }

        try {
            model.selectPerson(selectedPerson);
            // index of person might have shifted because of the select operation
            // so we need to find the new index
            Index newIndex = model.getPersonIndex(selectedPerson);
            EventsCenter.getInstance().post(new JumpToListRequestEvent(newIndex, socialType));
        } catch (PersonNotFoundException e) {
            assert false : "The selected person should be in the last shown list";
        }

        return new CommandResult(String.format(MESSAGE_SELECT_PERSON_SUCCESS, targetIndex.getOneBased()));
    }


```
###### \java\seedu\address\logic\commands\SortByDefaultCommand.java
``` java
/**
 * Sorts the {@code Person}s in the address book first by favorite status, then by name in lexicographic order.
 * @see PersonDefaultComparator
 */
public class SortByDefaultCommand extends SortCommand {
    @Override
    protected Comparator<ReadOnlyPerson> getComparator() {
        return new PersonDefaultComparator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || other instanceof SortByDefaultCommand; // instanceof handles nulls
    }
}
```
###### \java\seedu\address\logic\commands\SortByNameCommand.java
``` java
/**
 * Sorts the {@code Person}s in the address book by their names in lexicographic order order.
 * @see PersonNameComparator
 */
public class SortByNameCommand extends SortCommand {

    public static final String COMMAND_OPTION = "name";

    @Override
    protected Comparator<ReadOnlyPerson> getComparator() {
        return new PersonNameComparator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || other instanceof SortByNameCommand; // instanceof handles nulls
    }
}
```
###### \java\seedu\address\logic\commands\SortByRecentCommand.java
``` java
/**
 * Sorts the {@code Person}s in the address book by the last time they were added, updated, or selected.
 * @see PersonRecentComparator
 */
public class SortByRecentCommand extends SortCommand {

    public static final String COMMAND_OPTION = "recent";

    @Override
    protected Comparator<ReadOnlyPerson> getComparator() {
        return new PersonRecentComparator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || other instanceof SortByRecentCommand; // instanceof handles nulls
    }
}
```
###### \java\seedu\address\logic\commands\SortCommand.java
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
     * Gets the comparator that defines the ordering in the person list.
     */
    protected abstract Comparator<ReadOnlyPerson> getComparator();
}
```
###### \java\seedu\address\logic\parser\DeleteCommandParser.java
``` java
/**
 * Parses input arguments and creates a new {@code DeleteCommand}.
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {
    public static final String INVALID_DELETE_COMMAND_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);

    /**
     * Checks that the input {@code String} is not empty.
     * @throws ParseException if it is empty.
     */
    private void checkArgsNotEmpty(String args) throws ParseException {
        if (args == null || args.isEmpty()) {
            throw new ParseDeleteCommandException();
        }
    }
    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns an DeleteCommand object for execution.
     * @throws ParseException if the input arguments does not conform the expected format.
     */
    public DeleteCommand parse(String args) throws ParseException {
        // check that the raw args are not empty before processing
        checkArgsNotEmpty(args);
        OptionBearingArgument opArgs = new OptionBearingArgument(args);
        String filteredArgs = opArgs.getFilteredArgs();
        // check that the filtered args are not empty
        checkArgsNotEmpty(filteredArgs);

        if (opArgs.getOptions().size() > 1) {
            // args should only have at most 1 option
            throw new ParseDeleteCommandException();
        }

        if (opArgs.getOptions().contains(DeleteByTagCommand.COMMAND_OPTION)) {
            List<String> tags = parseWhitespaceSeparatedStrings(filteredArgs);
            HashSet<String> tagSet = new HashSet<>(tags);
            return new DeleteByTagCommand(tagSet);
        } else if (opArgs.getOptions().isEmpty()) {
            try {
                List<Index> indexes = ParserUtil.parseMultipleIndexes(filteredArgs);
                return new DeleteByIndexCommand(indexes);
            } catch (IllegalValueException ive) {
                throw new ParseDeleteCommandException();
            }
        } else {
            // option is not a valid option
            throw new ParseDeleteCommandException();
        }
    }

    /**
     * Represents a {@code ParseException} encountered when parsing arguments for a {@code DeleteCommand}.
     */
    private class ParseDeleteCommandException extends ParseException {
        public ParseDeleteCommandException() {
            super(INVALID_DELETE_COMMAND_FORMAT_MESSAGE);
        }
    }
}
```
###### \java\seedu\address\logic\parser\EditCommandParser.java
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
     * Returns the input collection, or an empty collection if the input collection contains
     * only a single element which is an empty string.
     */
    private Collection<String> getCollectionToParse(Collection<String> collection) {
        return collection.size() == 1 && collection.contains("")
                ? Collections.emptySet()
                : collection;
    }
```
###### \java\seedu\address\logic\parser\ExportCommandParser.java
``` java
/**
 * Parses input arguments and creates a new {@code ExportCommand}.
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
###### \java\seedu\address\logic\parser\FindCommandParser.java
``` java
/**
 * Parses input arguments and creates a new {@code FindCommand}.
 */
public class FindCommandParser implements Parser<FindCommand> {
    public static final String INVALID_FIND_COMMAND_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
    /**
     * Checks that the input {@code String} is not empty.
     * @throws ParseException if it is empty.
     */
    private void checkArgsNotEmpty(String args) throws ParseException {
        if (args == null || args.isEmpty()) {
            throw new ParseFindCommandException();
        }
    }

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns an FindCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format.
     */
    public FindCommand parse(String args) throws ParseException {
        // check that the raw args are not empty before processing
        checkArgsNotEmpty(args);
        OptionBearingArgument opArgs = new OptionBearingArgument(args);
        String filteredArgs = opArgs.getFilteredArgs();
        // check that the filtered args are not empty
        checkArgsNotEmpty(filteredArgs);

        if (opArgs.getOptions().size() > 1) {
            // args should have at most 1 option
            throw new ParseFindCommandException();
        }

        if (opArgs.getOptions().contains(FindByTagsCommand.COMMAND_OPTION)) {
            List<String> tagKeywords = parseWhitespaceSeparatedStrings(filteredArgs);
            TagsContainKeywordsPredicate predicate = new TagsContainKeywordsPredicate(tagKeywords);
            return new FindByTagsCommand(predicate);
        } else if (opArgs.getOptions().isEmpty()) {
            checkArgsNotEmpty(opArgs.getFilteredArgs());
            List<String> nameKeywords = parseWhitespaceSeparatedStrings(filteredArgs);
            NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(nameKeywords);
            return new FindByNameCommand(predicate);
        } else {
            // option is not a valid option
            throw new ParseFindCommandException();
        }
    }

    /**
     * Represents a {@code ParseException} encountered when parsing arguments for a {@code FindCommand}.
     */
    private class ParseFindCommandException extends ParseException {
        public ParseFindCommandException() {
            super(INVALID_FIND_COMMAND_FORMAT_MESSAGE);
        }
    }
}
```
###### \java\seedu\address\logic\parser\ImportCommandParser.java
``` java
/**
 * Parses input arguments and creates a new {@code ImportCommand}.
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
###### \java\seedu\address\logic\parser\OptionBearingArgument.java
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
     * Constructs an OptionBearingArgument for the input {@code String}.
     */
    public OptionBearingArgument(String args) {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        rawArgs = trimmedArgs;
        parse(rawArgs);
    }

    /**
     * Parses the {@code String} to get the list of options, and a filtered argument string with the options removed.
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
###### \java\seedu\address\logic\parser\ParserUtil.java
``` java
    /**
     * Returns the input {@code String} splitted by whitespace.
     */
    public static List<String> parseWhitespaceSeparatedStrings(String args) {
        requireNonNull(args);
        String[] splitArgs = args.split("\\s+");
        return Arrays.asList(splitArgs);
    }
```
###### \java\seedu\address\logic\parser\ParserUtil.java
``` java
    /**
     * Parses {@code Collection<String> rawSocialInfos} into {@code Set<SocialInfo}.
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
###### \java\seedu\address\logic\parser\SocialInfoMapping.java
``` java
/**
 * Handles mappings of social related identifiers when parsing {@code SocialInfo}.
 */
public class SocialInfoMapping {
    public static final String FACEBOOK_IDENTIFIER = "facebook";
    public static final String INSTAGRAM_IDENTIFIER = "instagram";
    public static final String FACEBOOK_IDENTIFIER_ALIAS = "fb";
    public static final String INSTAGRAM_IDENTIFIER_ALIAS = "ig";

    private static final int SOCIAL_TYPE_INDEX = 0;
    private static final int SOCIAL_USERNAME_INDEX = 1;

    private static final String INVALID_SYNTAX_EXCEPTION_MESSAGE = "Invalid syntax for social info";
    private static final String UNRECOGNIZED_SOCIAL_TYPE_MESSAGE = "Unrecognized social type.\n"
        + "Currently supported platforms: "
        + FACEBOOK_IDENTIFIER + "(aliases: " + FACEBOOK_IDENTIFIER_ALIAS + "), "
        + INSTAGRAM_IDENTIFIER + "(aliases: " + INSTAGRAM_IDENTIFIER_ALIAS + ")\n";

    private static final Logger logger = LogsCenter.getLogger(SocialInfoMapping.class);

    /**
     * Returns the SocialInfo object represented by the input {@code String}.
     * @throws IllegalValueException if the input does not represent a valid {@code SocialInfo} recognized
     * by the defined mappings.
     */
    public static SocialInfo parseSocialInfo(String rawSocialInfo) throws IllegalValueException {
        String[] splitRawSocialInfo = rawSocialInfo.split(" ", 2);
        if (splitRawSocialInfo.length != 2) {
            logger.warning("Incorrect number of parts: " + rawSocialInfo);
            throw new IllegalValueException(INVALID_SYNTAX_EXCEPTION_MESSAGE);
        }

        if (isFacebookInfo(splitRawSocialInfo)) {
            return buildFacebookInfo(splitRawSocialInfo);
        } else if (isInstagramInfo(splitRawSocialInfo)) {
            return buildInstagramInfo(splitRawSocialInfo);
        } else {
            logger.warning("Unrecognised social type: " + rawSocialInfo);
            throw new IllegalValueException(UNRECOGNIZED_SOCIAL_TYPE_MESSAGE);
        }

    }

```
###### \java\seedu\address\logic\parser\SocialInfoMapping.java
``` java
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
###### \java\seedu\address\logic\parser\SortCommandParser.java
``` java
/**
 * Parses input arguments and creates a new {@code SortCommand}.
 */
public class SortCommandParser implements Parser<SortCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the SortCommand
     * and returns an SortCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format.
     */
    public SortCommand parse(String args) throws ParseException {
        OptionBearingArgument opArgs = new OptionBearingArgument(args);
        Set<String> options = opArgs.getOptions();

        if (!opArgs.getFilteredArgs().isEmpty() || options.size() > 1) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
        }

        if (options.contains(SortByNameCommand.COMMAND_OPTION)) {
            return new SortByNameCommand();
        } else if (options.contains(SortByRecentCommand.COMMAND_OPTION)) {
            return new SortByRecentCommand();
        } else if (options.size() == 0) {
            // no options, so return sort by default command
            return new SortByDefaultCommand();
        } else {
            // invalid option, throw exception
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
        }
    }
}
```
###### \java\seedu\address\model\AddressBook.java
``` java
    /**
     * Indicates that a {@code Person} in the address book has been accessed.
     */
    public void indicatePersonAccessed(ReadOnlyPerson target) throws PersonNotFoundException {
        Person updatedPerson = new Person(target);
        updatedPerson.setLastAccessDateToNow();
        try {
            persons.setPerson(target, updatedPerson);
        } catch (DuplicatePersonException dpe) {
            assert false : "Person should be unique";
        }
    }

```
###### \java\seedu\address\model\Model.java
``` java
    /** Sorts the {@code Person}s in the address book based on the input {@code comparator}. */
    void sortPersons(Comparator<ReadOnlyPerson> comparator);
```
###### \java\seedu\address\model\Model.java
``` java
    /** Selects the given {@code Person}. Should update the {@code LastAccessDate} of the person. */
    void selectPerson(ReadOnlyPerson target) throws PersonNotFoundException;

    /**
     * Returns the {@code Index} of the {@code target} in the filtered person list.
     * @throws PersonNotFoundException if {@code target} could not be found in the list.
     */
    Index getPersonIndex(ReadOnlyPerson target) throws PersonNotFoundException;

```
###### \java\seedu\address\model\Model.java
``` java
    /**
     * Returns a defensive copy of the {@code model}.
     */
    Model makeCopy();
}
```
###### \java\seedu\address\model\ModelManager.java
``` java
    /**
     * Initializes a {@code ModelManager} with the given {@code addressBook} and {@code userPrefs}.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, UserPrefs userPrefs) {
        super();
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        sortedPersons = new SortedList<>(this.addressBook.getPersonList());
        filteredPersons = new FilteredList<>(sortedPersons);
        // To avoid having to re-sort upon every change in filter, we first sort the list before applying the filter
        // This was we only need to re-sort when there is a change in the backing person list
        sortPersons(new PersonDefaultComparator());
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }
```
###### \java\seedu\address\model\ModelManager.java
``` java
    @Override
    public synchronized void addPersons(Collection<ReadOnlyPerson> persons) {
        for (ReadOnlyPerson person : persons) {
            try {
                addressBook.addPerson(person);
                logger.info("Added person: " + person);
            } catch (DuplicatePersonException e) {
                logger.info("Person already in address book: " + person);
                continue;
            }
        }

        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        indicateAddressBookChanged();
    }

```
###### \java\seedu\address\model\ModelManager.java
``` java
    @Override
    public Index getPersonIndex(ReadOnlyPerson target) throws PersonNotFoundException {
        int zeroBasedIndex = filteredPersons.indexOf(target);
        if (zeroBasedIndex == -1) {
            throw new PersonNotFoundException();
        }
        return Index.fromZeroBased(zeroBasedIndex);
    }

    @Override
    public void sortPersons(Comparator<ReadOnlyPerson> comparator) {
        logger.info("Sorting persons based using: " + comparator.getClass());
        sortedPersons.setComparator(comparator);
        lastSortComparator = comparator;
    }

    @Override
    public void selectPerson(ReadOnlyPerson target) throws PersonNotFoundException {
        indicatePersonAccessed(target);
        // TODO(Marvin): Since IO operations are expensive, consider if we can defer this operation instead of saving
        // on every access (which includes select)
        indicateAddressBookChanged();
    }

    private void indicatePersonAccessed(ReadOnlyPerson target) throws PersonNotFoundException {
        logger.info("Indicated that " + target + "has been accessed.");
        addressBook.indicatePersonAccessed(target);
    }

```
###### \java\seedu\address\model\ModelManager.java
``` java
    @Override
    public Model makeCopy() {
        // initialize new UserPrefs for now as address book doesn't make use of it
        ModelManager copy = new ModelManager(this.getAddressBook(), new UserPrefs());
        copy.sortPersons(lastSortComparator);
        copy.updateFilteredPersonList(lastFilterPredicate);

        return copy;
    }

```
###### \java\seedu\address\model\person\LastAccessDate.java
``` java
/**
 * Represents the last date a {@code Person} is accessed.
 * Guarantees immutability.
 */
public class LastAccessDate implements Comparable<LastAccessDate> {
    private Date lastAccessDate;

    /**
     * Constructs a new LastAccessDate with the date set to the current date.
     */
    public LastAccessDate() {
        lastAccessDate = new Date();
    }

    /**
     * Constructs a new LastAccessDate with the date equivalent to the input {@code Date}.
     */
    public LastAccessDate(Date date) {
        requireNonNull(date);
        // store a copy instead of using input date directly to avoid reference to external objects that can be mutated
        lastAccessDate = copyDate(date);
    }

    public Date getDate() {
        // returns a copy of the date so that the internal date cannot be mutated by external methods
        return copyDate(lastAccessDate);
    }

    @Override
    public String toString() {
        return lastAccessDate.toString();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof LastAccessDate // instanceof handles nulls
                && this.lastAccessDate.equals(((LastAccessDate) other).lastAccessDate));
    }

    /**
     * Utility method to create a copy of the input {@code Date}s.
     */
    private Date copyDate(Date originalDate) {
        // clone using constructor instead of clone method due to vulnerabilities in the clone method
        // see https://stackoverflow.com/questions/7082553/java-util-date-clone-or-copy-to-not-expose-internal-reference
        Date copiedDate = new Date(originalDate.getTime());
        return copiedDate;
    }

    @Override
    public int compareTo(LastAccessDate other) {
        return this.lastAccessDate.compareTo(other.lastAccessDate);
    }
}
```
###### \java\seedu\address\model\person\Person.java
``` java
    @Override
    public ObjectProperty<UniqueSocialInfoList> socialInfoProperty() {
        return socialInfos;
    }

    @Override
    public Set<SocialInfo> getSocialInfos() {
        return socialInfos.get().toSet();
    }

    public void setSocialInfos(Set<SocialInfo> replacement) throws DuplicateDataException {
        socialInfos.set(new UniqueSocialInfoList(replacement));
    }

    @Override
    public ObjectProperty<LastAccessDate> lastAccessDateProperty() {
        return lastAccessDate;
    }

    @Override
    public LastAccessDate getLastAccessDate() {
        return lastAccessDate.get();
    }

    public void setLastAccessDate(LastAccessDate replacement) {
        lastAccessDate.set(replacement);
    }

    public void setLastAccessDateToNow() {
        setLastAccessDate(new LastAccessDate());
    }
```
###### \java\seedu\address\model\person\PersonComparatorUtil.java
``` java

/**
 * Utility class with useful methods for writing {@code Person} comparators.
 */
public class PersonComparatorUtil {

    /**
     * Compares two {@code Person} based on their {@code Favorite} status.
     * The favorited person will be ordered first.
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
     * Compares two {@code Person}s based on their {@code Name}.
     */
    public static int compareName(ReadOnlyPerson thisPerson, ReadOnlyPerson otherPerson) {
        String thisPersonName = thisPerson.getName().toString();
        String otherPersonName = otherPerson.getName().toString();
        return thisPersonName.compareToIgnoreCase(otherPersonName);
    }

    /**
     * Compares two {@code Person}s based on their {@code Phone}.
     */
    public static int comparePhone(ReadOnlyPerson thisPerson, ReadOnlyPerson otherPerson) {
        String thisPersonPhone = thisPerson.getPhone().toString();
        String otherPersonPhone = otherPerson.getPhone().toString();
        return thisPersonPhone.compareToIgnoreCase(otherPersonPhone);
    }

    /**
     * Compares two {@code Person}s based on their {@code Address}.
     */
    public static int compareAddress(ReadOnlyPerson thisPerson, ReadOnlyPerson otherPerson) {
        String thisPersonAddress = thisPerson.getAddress().toString();
        String otherPersonAddress = otherPerson.getAddress().toString();
        return thisPersonAddress.compareToIgnoreCase(otherPersonAddress);
    }

    /**
     * Compares two {@code Person}s based on their {@code Email}.
     */
    public static int compareEmail(ReadOnlyPerson thisPerson, ReadOnlyPerson otherPerson) {
        String thisPersonEmail = thisPerson.getEmail().toString();
        String otherPersonEmail = otherPerson.getEmail().toString();
        return thisPersonEmail.compareToIgnoreCase(otherPersonEmail);
    }

    /**
     * Compares two {@code Person}s based on their {@code LastAccessDate}.
     * The person which is most recently accessed person will be ordered first.
     */
    public static int compareLastAccessDate(ReadOnlyPerson thisPerson, ReadOnlyPerson otherPerson) {
        LastAccessDate thisPersonLastAccessDate = thisPerson.getLastAccessDate();
        LastAccessDate otherPersonLastAccessDate = otherPerson.getLastAccessDate();
        int dateCompare = thisPersonLastAccessDate.compareTo(otherPersonLastAccessDate);
        // Date comparison puts earlier dates first, so we need to reverse it
        return -dateCompare;
    }
}
```
###### \java\seedu\address\model\person\PersonDefaultComparator.java
``` java
/**
 * Default comparator for {@Person}s.
 * Sorts in the order:
 * 1. {@code Favorite} status
 * 2. {@code Name} in lexicographic order
 * 3. {@code Phone} in numeric order
 * 4. {@code Address} in lexicographic order
 * 5. {@code Email} in lexicographic order
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
###### \java\seedu\address\model\person\PersonNameComparator.java
``` java
/**
 * Comparator for {@Person}s when sorting by {@code Name}.
 * Sorts in the order:
 * 1. {@code Name} in lexicographic order
 * 2. {@code Favorite} status
 * 3. {@code Phone} in numeric order
 * 4. {@code Address} in lexicographic order
 * 5. {@code Email} in lexicographic order
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
###### \java\seedu\address\model\person\PersonRecentComparator.java
``` java
/**
 * Comparator for {@Person}s when sorting by {@code LastAccessDate}.
 * Sorts in the order:
 * 1. {@code LastAccessDate} in order of recency
 * 2. {@code Name} in lexicographic order
 * 3. {@code Favorite} status
 * 4. {@code Phone} in numeric order
 * 5. {@code Address} in lexicographic order
 * 6. {@code Email} in lexicographic order
 */
public class PersonRecentComparator implements Comparator<ReadOnlyPerson> {
    @Override
    public int compare(ReadOnlyPerson thisPerson, ReadOnlyPerson otherPerson) {
        if (!thisPerson.getLastAccessDate().equals(otherPerson.getLastAccessDate())) {
            return compareLastAccessDate(thisPerson, otherPerson);
        } else if (!thisPerson.getFavorite().equals(otherPerson.getFavorite())) {
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
###### \java\seedu\address\model\person\TagsContainKeywordsPredicate.java
``` java
/**
 * Tests that a {@code ReadOnlyPerson}'s {@code Tag} matches any of the keywords given.
 */
public class TagsContainKeywordsPredicate implements Predicate<ReadOnlyPerson> {
    private final Collection<String> keywords;

    public TagsContainKeywordsPredicate(Collection<String> keywords) {
        this.keywords = keywords;
    }

    private boolean doesPersonTagsMatchKeyword(ReadOnlyPerson person, String keyword) {
        Set<Tag> tags = person.getTags();
        return tags.stream().anyMatch(tag -> StringUtil.containsWordIgnoreCase(tag.tagName, keyword));
    }

    @Override
    public boolean test(ReadOnlyPerson person) {
        return keywords.stream()
                .anyMatch(keyword -> doesPersonTagsMatchKeyword(person, keyword));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof TagsContainKeywordsPredicate // instanceof handles nulls
                && this.keywords.equals(((TagsContainKeywordsPredicate) other).keywords)); // state check
    }

}
```
###### \java\seedu\address\model\social\SocialInfo.java
``` java
/**
 * Represents information about a {@code Person}'s social media account.
 * Guarantees immutability.
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
     * Returns the username for the represented account.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Returns the platform of this social media information.
     */
    public String getSocialType() {
        return this.socialType;
    }

    /**
     * Returns the link to the social media feed for the represented account.
     */
    public String getSocialUrl() {
        return this.socialUrl;
    }

    /**
     * Formats state as text for viewing.
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
###### \java\seedu\address\model\social\UniqueSocialInfoList.java
``` java
/**
 * A list of {@code SocialInfo}s that enforces no nulls and no duplicate social platforms between its elements.
 */
public class UniqueSocialInfoList implements Iterable<SocialInfo> {

    private final ObservableList<SocialInfo> internalList = FXCollections.observableArrayList();

    /**
     * Constructs empty SocialInfoList.
     */
    public UniqueSocialInfoList() {}

    /**
     * Creates a UniqueSocialInfoList using given {@code socialInfos}.
     * Enforces no nulls.
     */
    public UniqueSocialInfoList(Set<SocialInfo> socialInfos) throws DuplicateSocialTypeException {
        requireAllNonNull(socialInfos);
        if (containsDuplicateSocialType(socialInfos)) {
            throw new DuplicateSocialTypeException();
        }
        internalList.addAll(socialInfos);

        assert areInternalListSocialTypesUnique();
    }


    /**
     * Returns all the social media information in this list as a {@code Set}.
     */
    public Set<SocialInfo> toSet() {
        assert areInternalListSocialTypesUnique();
        return new HashSet<>(internalList);
    }

    /**
     * Replaces the {@code SocialInfo} in the internal list with those in the argument {@code SocialInfo} set.
     */
    public void setSocialInfos(Set<SocialInfo> socialInfos) {
        requireAllNonNull(socialInfos);
        internalList.setAll(socialInfos);

        assert areInternalListSocialTypesUnique();
    }

    /**
     * Returns true if the internal list contains a {@code SocialInfo} with the same social platform.
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
     * Adds a {@code SocialInfo} to the internal list.
     * @throws DuplicateSocialTypeException if the platform represented by the {@code SocialInfo} to add is a duplicate
     * of another element in the list.
     */
    public void add(SocialInfo toAdd) throws DuplicateSocialTypeException {
        requireNonNull(toAdd);

        if (containsSameType(toAdd)) {
            throw new DuplicateSocialTypeException();
        }
        internalList.add(toAdd);

        assert areInternalListSocialTypesUnique();
    }

    /**
     * Checks that there are no {@code SocialInfo} of the same platform in the internal list.
     */
    private boolean areInternalListSocialTypesUnique() {
        return !containsDuplicateSocialType(internalList);
    }

    /**
     * Checks that there are no {@code SocialInfo} of the same platform in the {@code Collection<SocialInfo>}.
     */
    private boolean containsDuplicateSocialType(Collection<SocialInfo> socialInfos) {
        HashSet<String> socialTypes = new HashSet<>();

        for (SocialInfo socialInfo : socialInfos) {
            String socialType = socialInfo.getSocialType();
            if (socialTypes.contains(socialType)) {
                return true;
            } else {
                socialTypes.add(socialType);
            }
        }
        return false;
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<SocialInfo> asObservableList() {
        assert areInternalListSocialTypesUnique();

        return FXCollections.unmodifiableObservableList(internalList);
    }

    @Override
    public Iterator<SocialInfo> iterator() {
        assert areInternalListSocialTypesUnique();
        return internalList.iterator();

    }

    @Override
    public boolean equals(Object other) {
        assert areInternalListSocialTypesUnique();

        return other == this
                || (other instanceof UniqueSocialInfoList
                && this.internalList.equals(((UniqueSocialInfoList) other).internalList));
    }

    /**
     * Returns true if the elements in this list is equal to the elements in {@code other}.
     * The elements do not have to be in the same order.
     */
    public boolean equalsOrderInsensitive(UniqueSocialInfoList other) {
        assert areInternalListSocialTypesUnique();
        assert other.areInternalListSocialTypesUnique();
        return other == this
                || new HashSet<>(internalList).equals(new HashSet<>(other.internalList));
    }

    /**
     * Signals that an operation would have violated the 'no duplicate social platform' property of the list.
     */
    public static class DuplicateSocialTypeException extends DuplicateDataException {
        protected DuplicateSocialTypeException() {
            super("Person cannot have more than one social media account of the same social type.");
        }
    }
}
```
###### \java\seedu\address\model\util\SampleDataUtil.java
``` java
    /**
     * Returns a {@code UniqueSocialInfoList} containing the {@code SocialInfo}s given.
     */
    public static UniqueSocialInfoList getSocialInfos(SocialInfo... socialInfos) throws DuplicateDataException {
        HashSet<SocialInfo> socialInfoSet = new HashSet<>();
        for (SocialInfo socialInfo : socialInfos) {
            socialInfoSet.add(socialInfo);
        }

        UniqueSocialInfoList uniqueSocialInfoList = new UniqueSocialInfoList(socialInfoSet);

        return uniqueSocialInfoList;
    }
```
###### \java\seedu\address\storage\XmlAdaptedSocialInfo.java
``` java
/**
 * JAXB-friendly adapted version of the {@code SocialInfo}.
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
     * Converts a given {@code SocialInfo} into this class for JAXB use.
     * @param source future changes to this will not affect the created
     */
    public XmlAdaptedSocialInfo(SocialInfo source) {
        socialType = source.getSocialType();
        username = source.getUsername();
        socialUrl = source.getSocialUrl();
    }

    /**
     * Converts this JAXB-friendly adapted {@code SocialInfo} object into the model's {@code SocialInfo} object.
     * @throws IllegalValueException if there were any data constraints violated in the adapted {@code SocialInfo}
     */
    public SocialInfo toModelType() throws IllegalValueException {
        return new SocialInfo(socialType, username, socialUrl);
    }

}
```
###### \java\seedu\address\ui\PersonCard.java
``` java
    /**
     * Creates labels for each {@code SocialInfo} belonging to a {@code Person}.
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
