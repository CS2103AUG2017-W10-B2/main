# marvinchin
###### /java/systemtests/SortCommandSystemTest.java
``` java
public class SortCommandSystemTest extends AddressBookSystemTest {

    @Test
    public void sort() throws DuplicatePersonException, PersonNotFoundException {
        /* ----------------- Performing sort operations while an unfiltered list is being shown ----------------- */

        /* Case: sort by name -> all persons in the list sorted by name */
        String command = SortCommand.COMMAND_WORD + " -" + SortByNameCommand.COMMAND_OPTION;
        String expectedResultMessage = SortByNameCommand.MESSAGE_SORT_SUCCESS;
        Comparator<ReadOnlyPerson> expectedComparator = new PersonNameComparator();
        assertSortCommandSuccess(command, expectedResultMessage, expectedComparator);

        /* Case: add a person -> persons in the list are still sorted by name */
        command = AddCommand.COMMAND_WORD + " "
                + NAME_DESC_AMY + "  "
                + PHONE_DESC_AMY + " "
                + EMAIL_DESC_AMY + "   "
                + ADDRESS_DESC_AMY + "   "
                + TAG_DESC_FRIEND + " "
                + SOCIAL_DESC_AMY + " ";
        ReadOnlyPerson toAdd = AMY;
        assertAddCommandRetainsSortOrder(command, toAdd, expectedComparator);

        /* Case: sort by recent -> all persons in the list sorted by last access time */
        command = SortCommand.COMMAND_WORD + " -" + SortByRecentCommand.COMMAND_OPTION;
        expectedResultMessage = SortByRecentCommand.MESSAGE_SORT_SUCCESS;
        expectedComparator = new PersonRecentComparator();
        assertSortCommandSuccess(command, expectedResultMessage, expectedComparator);

        /* Case: select a person -> persons in the list are still sorted by recent */
        Index editIndex = Index.fromOneBased(3);
        command = EditCommand.COMMAND_WORD + " " + editIndex.getOneBased() + NAME_DESC_BOB;
        ReadOnlyPerson editTarget = getPersonAtIndex(editIndex);
        ReadOnlyPerson editedPerson = new PersonBuilder(editTarget).withName(VALID_NAME_BOB).build();
        assertEditCommandRetainsSortOrder(command, editTarget, editedPerson, expectedComparator);

        /* Case: sort by default -> all persons in the list sorted based on default ordering */
        command = SortCommand.COMMAND_WORD;
        expectedResultMessage = SortByDefaultCommand.MESSAGE_SORT_SUCCESS;
        expectedComparator = new PersonDefaultComparator();
        assertSortCommandSuccess(command, expectedResultMessage, expectedComparator);

        /* Case: favorite a person -> persons in the list are still sorted by default ordering */
        Index favIndex = Index.fromOneBased(5);
        command = FavoriteCommand.COMMAND_WORD + " " + favIndex.getOneBased();
        ReadOnlyPerson favTarget = getPersonAtIndex(favIndex);
        assertFavoriteCommandRetainsSortOrder(command, favTarget, expectedComparator);

        /* ----------------- Performing sort operations while an filtered list is being shown ----------------- */

        showPersonsWithName("Meier");

        /* Case: sort by name -> all persons in the list sorted by name */
        command = SortCommand.COMMAND_WORD + " -" + SortByNameCommand.COMMAND_OPTION;
        expectedResultMessage = SortByNameCommand.MESSAGE_SORT_SUCCESS;
        expectedComparator = new PersonNameComparator();
        assertSortCommandSuccess(command, expectedResultMessage, expectedComparator);

        /* Case: sort by recent -> all persons in the list sorted by last access time */
        command = SortCommand.COMMAND_WORD + " -" + SortByRecentCommand.COMMAND_OPTION;
        expectedResultMessage = SortByRecentCommand.MESSAGE_SORT_SUCCESS;
        expectedComparator = new PersonRecentComparator();
        assertSortCommandSuccess(command, expectedResultMessage, expectedComparator);

        /* Case: sort by default -> all persons in the list sorted based on default ordering */
        command = SortCommand.COMMAND_WORD;
        expectedResultMessage = SortByDefaultCommand.MESSAGE_SORT_SUCCESS;
        expectedComparator = new PersonDefaultComparator();
        assertSortCommandSuccess(command, expectedResultMessage, expectedComparator);

        /* ----------------- Performing invalid sort operations ----------------- */
        // sort command with args
        command = SortCommand.COMMAND_WORD + " hello world";
        assertSortCommandFailure(command, expectedComparator);

        // sort command with invalid options
        command = SortCommand.COMMAND_WORD + " -somethinginvalid1234";
        assertSortCommandFailure(command, expectedComparator);

        // sort command with multiple options
        command = SortCommand.COMMAND_WORD + " "
            + "-" + SortByRecentCommand.COMMAND_OPTION + " "
            + "-" + SortByNameCommand.COMMAND_OPTION;
        assertSortCommandFailure(command, expectedComparator);
    }

    /**
     * Utility method to get the person at the given index in the model's filtered person list
     */
    private ReadOnlyPerson getPersonAtIndex(Index index) {
        Model model = getModel();
        return model.getFilteredPersonList().get(index.getZeroBased());
    }

    /**
     * Verifies that the add command succeeds and that the ordering of persons in the model's filtered person list
     * set by the last sort function still holds true
     */
    private void assertAddCommandRetainsSortOrder(String command, ReadOnlyPerson toAdd,
            Comparator<ReadOnlyPerson> expectedComparator) throws DuplicatePersonException {
        Model expectedModel = getModel();
        expectedModel.addPerson(toAdd);
        // expect the persons in the model to be sorted based on the input comparator
        expectedModel.sortPersons(expectedComparator);
        String expectedResultMessage = String.format(AddCommand.MESSAGE_SUCCESS, toAdd);
        assertCommandSuccessWithSyncStatusChanged(command, expectedModel, expectedResultMessage);
    }

    /**
     * Verifies that the edit command succeeds and that the ordering of persons in the model's filtered person list
     * set by the last sort function still holds true
     */
    private void assertEditCommandRetainsSortOrder(String command, ReadOnlyPerson target, ReadOnlyPerson editedPerson,
            Comparator<ReadOnlyPerson> expectedComparator) throws DuplicatePersonException, PersonNotFoundException {
        Model expectedModel = getModel();
        expectedModel.updatePerson(target, editedPerson);
        // expect the persons in the model to be sorted based on the input comparator
        expectedModel.sortPersons(expectedComparator);
        String expectedResultMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, editedPerson);
        assertCommandSuccessWithSyncStatusChanged(command, expectedModel, expectedResultMessage);
    }

    /**
     * Verifies that the favorite command succeeds and that the ordering of persons in the model's filtered person list
     * set by the last sort function still holds true
     */
    private void assertFavoriteCommandRetainsSortOrder(String command, ReadOnlyPerson toFav,
            Comparator<ReadOnlyPerson> expectedComparator) throws DuplicatePersonException, PersonNotFoundException {
        Model expectedModel = getModel();
        expectedModel.toggleFavoritePerson(toFav, FavoriteCommand.COMMAND_WORD);
        // expect the persons in the model to be sorted based on the input comparator
        expectedModel.sortPersons(expectedComparator);
        System.out.println(toFav.getName());
        String favoritePersonMessage = "\n\t★ " + toFav.getName().toString();
        System.out.println(favoritePersonMessage);
        String expectedResultMessage = FavoriteCommand.MESSAGE_FAVORITE_PERSON_SUCCESS + favoritePersonMessage;
        assertCommandSuccessWithSyncStatusChanged(command, expectedModel, expectedResultMessage);
    }

    /**
     * Verifies that the sort command succeeds and that the ordering of persons in the model's filtered person list is
     * correctly set by the sort function
     */
    private void assertSortCommandSuccess(String command, String expectedResultMessage,
            Comparator<ReadOnlyPerson> expectedComparator) {
        Model expectedModel = getModel();
        expectedModel.sortPersons(expectedComparator);
        assertCommandSuccessWithStatusBarUnchanged(command, expectedModel, expectedResultMessage);
    }

    /**
     * Executes {@code command} and in addition,<br>
     * 1. Asserts that the command box displays an empty string.<br>
     * 2. Asserts that the result display box displays {@code expectedResultMessage}.<br>
     * 3. Asserts that the model related components equal to {@code expectedModel}.<br>
     * 4. Asserts that no card is selected.<br>
     * 5. Asserts that the status bar's sync status does not change.<br>
     * 6. Asserts that the command box has the default style class.<br>
     * Verifications 1 to 3 are performed by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     * @see AddressBookSystemTest#assertSelectedCardChanged(Index)
     */
    private void assertCommandSuccessWithStatusBarUnchanged(String command, Model expectedModel,
            String expectedResultMessage) {
        executeCommand(command);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);
        assertSelectedCardDeselected();
        assertCommandBoxShowsDefaultStyle();
        assertStatusBarUnchanged();
    }

    /**
     * Performs the same verification as {@code assertCommandSuccessWithStatusBarUnchanged(String, Model, String)}
     * except that the sync status in the status bar changes.
     * @see SortCommandSystemTest#assertCommandSuccessWithStatusBarUnchanged(String, Model, String)
     */
    private void assertCommandSuccessWithSyncStatusChanged(String command, Model expectedModel,
            String expectedResultMessage) {
        executeCommand(command);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);
        assertSelectedCardDeselected();
        assertCommandBoxShowsDefaultStyle();
        assertStatusBarUnchangedExceptSyncStatus();
    }

    /**
     * Executes {@code command} and in addition,<br>
     * 1. Asserts that the command box displays {@code command}.<br>
     * 2. Asserts that result display box displays {@code expectedResultMessage}.<br>
     * 3. Asserts that the model related components equal to the current model, and that the ordering of the persons in
     * the model is correct.<br>
     * 4. Asserts that the browser url, selected card and status bar remain unchanged.<br>
     * 5. Asserts that the command box has the error style.<br>
     * Verifications 1 to 3 are performed by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertSortCommandFailure(String command, Comparator<ReadOnlyPerson> lastValidSortComparator) {
        Model expectedModel = getModel();
        // model should remain sorted with the last valid sort even if command fails
        expectedModel.sortPersons(lastValidSortComparator);
        String expectedResultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);
        executeCommand(command);
        assertApplicationDisplaysExpected(command, expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsErrorStyle();
        assertStatusBarUnchanged();
    }
}
```
###### /java/seedu/address/logic/parser/ImportCommandParserTest.java
``` java
public class ImportCommandParserTest {

    private ImportCommandParser parser = new ImportCommandParser();

    @Test
    public void parse_validArgs_returnsImportCommand() {
        String args = " some/path/somewhere  ";
        assertParseSuccess(parser, args, new ImportCommand("some/path/somewhere"));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String args = "";
        assertParseFailure(parser, args, String.format(MESSAGE_INVALID_COMMAND_FORMAT, ImportCommand.MESSAGE_USAGE));
    }
}
```
###### /java/seedu/address/logic/parser/SortCommandParserTest.java
``` java
/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the SortCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the SortCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class SortCommandParserTest {

    private SortCommandParser parser = new SortCommandParser();

    @Test
    public void parse_noOptionsAndHasParams_throwsParseException() {
        String input = "param";
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);
        assertParseFailure(parser, input, expectedMessage);
    }

    @Test
    public void parse_validOptionsAndHasParams_throwsParseException() {
        String input = "-" + SortByNameCommand.COMMAND_OPTION + " param";
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);
        assertParseFailure(parser, input, expectedMessage);
    }

    @Test
    public void parse_invalidOption_throwsParseException() {
        String input = "-someinvalidoption123";
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);
        assertParseFailure(parser, input, expectedMessage);
    }

    @Test
    public void parse_multipleOptions_throwsParseException() {
        String input = "-" + SortByNameCommand.COMMAND_OPTION + " "
                + "-" + SortByRecentCommand.COMMAND_OPTION;
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);
        assertParseFailure(parser, input, expectedMessage);
    }

    @Test
    public void parse_noOptionsAndNoParams_returnsFindByDefaultCommand() {
        String input = "";
        SortCommand expectedSortCommand = new SortByDefaultCommand();
        assertParseSuccess(parser, input, expectedSortCommand);
    }

    @Test
    public void parse_nameOptionAndNoParams_returnsFindByDefaultCommand() {
        String input = "-" + SortByNameCommand.COMMAND_OPTION;
        SortCommand expectedSortCommand = new SortByNameCommand();
        assertParseSuccess(parser, input, expectedSortCommand);
    }

    @Test
    public void parse_recentOptionAndNoParams_returnsFindByRecentCommand() {
        String input = "-" + SortByRecentCommand.COMMAND_OPTION;
        SortCommand expectedSortCommand = new SortByRecentCommand();
        assertParseSuccess(parser, input, expectedSortCommand);
    }
}
```
###### /java/seedu/address/logic/parser/FindCommandParserTest.java
``` java
    @Test
    public void parse_moreThanOneOption_throwsParseException() {
        String input = "-tag -fav";
        assertParseFailure(parser, input, FindCommandParser.INVALID_FIND_COMMAND_FORMAT_MESSAGE);
    }

    @Test
    public void parse_invalidOption_throwsParseException() {
        String input = "-someinvalidoption123";
        assertParseFailure(parser, input, FindCommandParser.INVALID_FIND_COMMAND_FORMAT_MESSAGE);
    }

    @Test
    public void parse_tagOptionNoArgs_throwsParseException() {
        String input = "-tag     ";
        assertParseFailure(parser, input, FindCommandParser.INVALID_FIND_COMMAND_FORMAT_MESSAGE);
    }

    @Test
    public void parse_tagOptionValidArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindCommand expectedFindCommand =
                new FindByTagsCommand(new TagsContainKeywordsPredicate(Arrays.asList("colleagues", "friends")));
        assertParseSuccess(parser, "-tag colleagues friends", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, "-tag   \n colleagues \t friends \n", expectedFindCommand);
    }
```
###### /java/seedu/address/logic/parser/ExportCommandParserTest.java
``` java
public class ExportCommandParserTest {

    private ExportCommandParser parser = new ExportCommandParser();

    @Test
    public void parse_validArgs_returnsExportCommand() {
        String args = " some/path/somewhere  ";
        assertParseSuccess(parser, args, new ExportCommand("some/path/somewhere"));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String args = "";
        assertParseFailure(parser, args, String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExportCommand.MESSAGE_USAGE));
    }
}
```
###### /java/seedu/address/logic/parser/DeleteCommandParserTest.java
``` java
    @Test
    public void parse_moreThanOneOption_throwsParseException() {
        String input = "-tag -hello";
        assertParseFailure(parser, input, DeleteCommandParser.INVALID_DELETE_COMMAND_FORMAT_MESSAGE);
    }

    @Test
    public void parse_invalidOption_throwsParseException() {
        String input = "-someinvalidoption123";
        assertParseFailure(parser, input, DeleteCommandParser.INVALID_DELETE_COMMAND_FORMAT_MESSAGE);
    }

    @Test
    public void parse_tagOptionNoArgs_throwsParseException() {
        String input = "-tag    ";
        assertParseFailure(parser, input, DeleteCommandParser.INVALID_DELETE_COMMAND_FORMAT_MESSAGE);
    }

    @Test
    public void parse_validTagArgs_returnsDeleteCommand() {
        HashSet<String> keys = new HashSet<>(Arrays.asList("friends", "colleagues"));
        DeleteCommand expectedDeleteCommand = new DeleteByTagCommand(keys);
        String input = "-tag colleagues friends";
        String inputWithWhitespace = "-tag   \t friends \t\t\n colleagues";
        assertParseSuccess(parser, input, expectedDeleteCommand);
        assertParseSuccess(parser, inputWithWhitespace, expectedDeleteCommand);
    }
```
###### /java/seedu/address/logic/parser/OptionBearingArgumentTest.java
``` java
public class OptionBearingArgumentTest {

    @Test
    public void optionBearingArgument_argsWithoutOptions_success() {
        String args = "a/123 some building #01-01 e/hello@world.com";
        OptionBearingArgument opArg = new OptionBearingArgument(args);

        assertEquals(args, opArg.getRawArgs());
        assertEquals(args, opArg.getFilteredArgs());
        assertTrue(opArg.getOptions().isEmpty());
    }

    @Test
    public void optionBearingArgument_argsWithSingleLeadingOption_success() {
        String args = "-opt a/123 some building #01-01 e/hello@world.com";
        OptionBearingArgument opArg = new OptionBearingArgument(args);

        String expectedFilteredArgs = "a/123 some building #01-01 e/hello@world.com";
        Set<String> expectedOptions = new HashSet<>(Arrays.asList("opt"));
        assertEquals(args, opArg.getRawArgs());
        assertEquals(expectedFilteredArgs, opArg.getFilteredArgs());
        assertEquals(expectedOptions, opArg.getOptions());
    }

    @Test
    public void optionBearingArgument_argsWithSingleEmbeddedOption_success() {
        String args = "a/123 some building -opt #01-01 e/hello@world.com";
        OptionBearingArgument opArg = new OptionBearingArgument(args);

        String expectedFilteredArgs = "a/123 some building #01-01 e/hello@world.com";
        Set<String> expectedOptions = new HashSet<>(Arrays.asList("opt"));
        assertEquals(args, opArg.getRawArgs());
        assertEquals(expectedFilteredArgs, opArg.getFilteredArgs());
        assertEquals(expectedOptions, opArg.getOptions());
    }

    @Test
    public void optionBearingArgument_argsWithMultipleOptions_success() {
        String args = "-tag a/123 some building -opt #01-01 e/hello@world.com";
        OptionBearingArgument opArg = new OptionBearingArgument(args);

        String expectedFilteredArgs = "a/123 some building #01-01 e/hello@world.com";
        Set<String> expectedOptions = new HashSet<>(Arrays.asList("opt", "tag"));
        assertEquals(args, opArg.getRawArgs());
        assertEquals(expectedFilteredArgs, opArg.getFilteredArgs());
        assertEquals(expectedOptions, opArg.getOptions());
    }
}
```
###### /java/seedu/address/logic/commands/ImportCommandTest.java
``` java
public class ImportCommandTest {
    private static final String TEST_DATA_FOLDER = Paths.get("src/test/data/ImportCommandTest")
            .toAbsolutePath().toString() + File.separator;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // we can use null as a file path as we will not be using the instance file path
    private AddressBookStorage addressBookStorage = new XmlAddressBookStorage(null);
    private Storage storage = new StorageManager(addressBookStorage, null);

    @Test
    public void execute_validExportData_success() throws Exception {
        ModelStubAcceptingPersonAdded model = new ModelStubAcceptingPersonAdded();
        String absoluteImportFilePath = TEST_DATA_FOLDER + "validXmlExportData.xml";
        ImportCommand importCommand = prepareCommand(model, absoluteImportFilePath);

        CommandResult commandResult = importCommand.execute();

        String expectedMessage = String.format(ImportCommand.MESSAGE_IMPORT_CONTACTS_SUCCESS, absoluteImportFilePath);
        assertEquals(expectedMessage, commandResult.feedbackToUser);

        ArrayList<Person> expectedPersonsAdded = new ArrayList<>(Arrays.asList(new Person(HOON), new Person(IDA)));
        assertEquals(expectedPersonsAdded, model.personsAdded);
    }

    @Test
    public void execute_invalidExportData_throwsCommandException() throws Exception {
        ModelStub model = new ModelStub();
        String absoluteImportFilePath = TEST_DATA_FOLDER + "invalidExportData.txt";
        ImportCommand importCommand = prepareCommand(model, absoluteImportFilePath);

        thrown.expect(CommandException.class);
        thrown.expectMessage(String.format(ImportCommand.MESSAGE_IMPORT_CONTACTS_DCE_FAILURE, absoluteImportFilePath));

        importCommand.execute();
    }

    @Test
    public void execute_wrongFilePath_throwsCommandException() throws Exception {
        ModelStub model = new ModelStub();
        String absoluteImportFilePath = TEST_DATA_FOLDER + "nonexistentExportData.txt";
        ImportCommand importCommand = prepareCommand(model, absoluteImportFilePath);

        thrown.expect(CommandException.class);
        thrown.expectMessage(String.format(ImportCommand.MESSAGE_IMPORT_CONTACTS_FNF_FAILURE, absoluteImportFilePath));

        importCommand.execute();
    }

    @Test
    public void equals() {
        String someValidFilePath = TEST_DATA_FOLDER + "exported-data.xml";
        String anotherValidFilePath = TEST_DATA_FOLDER  + "more-exported-data.xml";
        ImportCommand importToSomeValidFilePathCommand = new ImportCommand(someValidFilePath);
        ImportCommand importToAnotherValidFilePathCommand = new ImportCommand(anotherValidFilePath);

        // same object -> returns true
        assertTrue(importToSomeValidFilePathCommand.equals(importToSomeValidFilePathCommand));

        // same values -> returns true
        ImportCommand importToSomeValidFilePathCommandCopy = new ImportCommand(someValidFilePath);
        assertTrue(importToSomeValidFilePathCommand.equals(importToSomeValidFilePathCommandCopy));

        // different types -> returns false
        assertFalse(importToSomeValidFilePathCommand.equals(1));

        // null -> returns false
        assertFalse(importToSomeValidFilePathCommand.equals(null));

        //different value -> returns false
        assertFalse(importToSomeValidFilePathCommand.equals(importToAnotherValidFilePathCommand));
    }

    private ImportCommand prepareCommand(Model model, String filePath) {
        ImportCommand importCommand = new ImportCommand(filePath);
        importCommand.setData(model, storage, new CommandHistory(), new UndoRedoStack());
        return importCommand;
    }

}
```
###### /java/seedu/address/logic/commands/SortByNameCommandTest.java
``` java
/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code SortByNameCommand}.
 */
public class SortByNameCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_unfilteredList_success() throws Exception {
        SortByNameCommand sortCommand = prepareCommand();
        String expectedMessage = SortByNameCommand.MESSAGE_SORT_SUCCESS;

        assertUnfilteredSortCommandSuccess(sortCommand, model, new PersonNameComparator(), expectedMessage);
    }

    @Test
    public void execute_filteredList_success() throws Exception {
        SortByNameCommand sortCommand = prepareCommand();
        String expectedMessage = SortByNameCommand.MESSAGE_SORT_SUCCESS;

        assertFilteredSortCommandSuccess(sortCommand, model, new PersonNameComparator(), expectedMessage);
    }

    @Test
    public void equals() {
        SortByNameCommand sortByNameCommandOne = new SortByNameCommand();
        SortByNameCommand sortByNameCommandTwo = new SortByNameCommand();

        // same object -> returns true
        assertTrue(sortByNameCommandOne.equals(sortByNameCommandOne));

        // same type -> returns true
        assertTrue(sortByNameCommandOne.equals(sortByNameCommandTwo));

        // different types -> returns false
        assertFalse(sortByNameCommandOne.equals(1));

        // null -> returns false
        assertFalse(sortByNameCommandOne.equals(null));
    }

    /**
     * Returns a {@code SortByNameCommand}.
     */
    private SortByNameCommand prepareCommand() {
        SortByNameCommand sortCommand = new SortByNameCommand();
        sortCommand.setData(model, getDummyStorage(), new CommandHistory(), new UndoRedoStack());
        return sortCommand;
    }
}
```
###### /java/seedu/address/logic/commands/DeleteByTagCommandTest.java
``` java
/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code DeleteByTagCommand}.
 */
public class DeleteByTagCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_unfilteredList_success() throws Exception {
        List<String> tagsToDelete = Arrays.asList("friends");
        TagsContainKeywordsPredicate predicate = new TagsContainKeywordsPredicate(tagsToDelete);
        List<ReadOnlyPerson> personsToDelete = model.getFilteredPersonList().stream()
                .filter(predicate).collect(Collectors.toList());
        StringBuilder deletedPersons = new StringBuilder();
        for (ReadOnlyPerson person : personsToDelete) {
            deletedPersons.append("\n" + person.toString());
        }

        DeleteByTagCommand deleteCommand = prepareCommand(tagsToDelete);

        String expectedMessage = String.format(DeleteByTagCommand.MESSAGE_DELETE_PERSON_SUCCESS, deletedPersons);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        for (ReadOnlyPerson person : personsToDelete) {
            expectedModel.deletePerson(person);
        }

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() throws Exception {
        NameContainsKeywordsPredicate filterPredicate = new NameContainsKeywordsPredicate(Arrays.asList("Alice"));
        model.updateFilteredPersonList(filterPredicate);
        List<ReadOnlyPerson> filteredPersons = model.getFilteredPersonList();

        List<String> tagsToDelete = Arrays.asList("friends");
        TagsContainKeywordsPredicate deletePredicate = new TagsContainKeywordsPredicate(tagsToDelete);

        List<ReadOnlyPerson> personsToDelete = filteredPersons.stream()
                .filter(deletePredicate).collect(Collectors.toList());
        StringBuilder deletedPersons = new StringBuilder();
        for (ReadOnlyPerson person : personsToDelete) {
            deletedPersons.append("\n" + person.toString());
        }

        DeleteByTagCommand deleteCommand = prepareCommand(tagsToDelete);

        String expectedMessage = String.format(DeleteByTagCommand.MESSAGE_DELETE_PERSON_SUCCESS, deletedPersons);
        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        for (ReadOnlyPerson person : personsToDelete) {
            expectedModel.deletePerson(person);
        }
        expectedModel.updateFilteredPersonList(filterPredicate);
        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void equals() {
        Set<String> firstDeleteTagSet = new HashSet<>(Arrays.asList("friends"));
        Set<String> secondDeleteTagSet = new HashSet<>(Arrays.asList("family"));
        DeleteByTagCommand deleteFirstCommand = new DeleteByTagCommand(firstDeleteTagSet);
        DeleteByTagCommand deleteSecondCommand = new DeleteByTagCommand(secondDeleteTagSet);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteByTagCommand deleteFirstCommandCopy = new DeleteByTagCommand(firstDeleteTagSet);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    /**
     * Returns a {@code DeleteByTagCommand} with the parameter {@code index}.
     */
    private DeleteByTagCommand prepareCommand(List<String> tags) {
        HashSet<String> tagSet = new HashSet<>(tags);
        DeleteByTagCommand deleteCommand = new DeleteByTagCommand(tagSet);
        deleteCommand.setData(model, getDummyStorage(), new CommandHistory(), new UndoRedoStack());
        return deleteCommand;
    }
}
```
###### /java/seedu/address/logic/commands/SortByRecentCommandTest.java
``` java
/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code SortByRecentCommand}.
 */
public class SortByRecentCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_unfilteredList_success() throws Exception {
        SortByRecentCommand sortCommand = prepareCommand();
        String expectedMessage = SortByRecentCommand.MESSAGE_SORT_SUCCESS;

        assertUnfilteredSortCommandSuccess(sortCommand, model, new PersonRecentComparator(), expectedMessage);
    }

    @Test
    public void execute_filteredList_success() throws Exception {
        SortByRecentCommand sortCommand = prepareCommand();
        String expectedMessage = SortByRecentCommand.MESSAGE_SORT_SUCCESS;

        assertFilteredSortCommandSuccess(sortCommand, model, new PersonRecentComparator(), expectedMessage);
    }

    @Test
    public void equals() {
        SortByRecentCommand sortByRecentCommandOne = new SortByRecentCommand();
        SortByRecentCommand sortByRecentCommandTwo = new SortByRecentCommand();

        // same object -> returns true
        assertTrue(sortByRecentCommandOne.equals(sortByRecentCommandOne));

        // same type -> returns true
        assertTrue(sortByRecentCommandOne.equals(sortByRecentCommandTwo));

        // different types -> returns false
        assertFalse(sortByRecentCommandOne.equals(1));

        // null -> returns false
        assertFalse(sortByRecentCommandOne.equals(null));
    }

    /**
     * Returns a {@code SortByRecentCommand}.
     */
    private SortByRecentCommand prepareCommand() {
        SortByRecentCommand sortCommand = new SortByRecentCommand();
        sortCommand.setData(model, getDummyStorage(), new CommandHistory(), new UndoRedoStack());
        return sortCommand;
    }
}
```
###### /java/seedu/address/logic/commands/SortByDefaultCommandTest.java
``` java
/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code SortByDefaultCommand}.
 */
public class SortByDefaultCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_unfilteredList_success() throws Exception {
        SortByDefaultCommand sortCommand = prepareCommand();
        String expectedMessage = SortByDefaultCommand.MESSAGE_SORT_SUCCESS;

        assertUnfilteredSortCommandSuccess(sortCommand, model, new PersonDefaultComparator(), expectedMessage);
    }

    @Test
    public void execute_filteredList_success() throws Exception {
        SortByDefaultCommand sortCommand = prepareCommand();
        String expectedMessage = SortByDefaultCommand.MESSAGE_SORT_SUCCESS;

        assertFilteredSortCommandSuccess(sortCommand, model, new PersonDefaultComparator(), expectedMessage);
    }

    @Test
    public void equals() {
        SortByDefaultCommand sortByDefaultCommandOne = new SortByDefaultCommand();
        SortByDefaultCommand sortByDefaultCommandTwo = new SortByDefaultCommand();

        // same object -> returns true
        assertTrue(sortByDefaultCommandOne.equals(sortByDefaultCommandOne));

        // same type -> returns true
        assertTrue(sortByDefaultCommandOne.equals(sortByDefaultCommandTwo));

        // different types -> returns false
        assertFalse(sortByDefaultCommandOne.equals(1));

        // null -> returns false
        assertFalse(sortByDefaultCommandOne.equals(null));
    }

    /**
     * Returns a {@code SortByDefaultCommand}.
     */
    private SortByDefaultCommand prepareCommand() {
        SortByDefaultCommand sortCommand = new SortByDefaultCommand();
        sortCommand.setData(model, getDummyStorage(), new CommandHistory(), new UndoRedoStack());
        return sortCommand;
    }
}
```
###### /java/seedu/address/logic/commands/ExportCommandTest.java
``` java
public class ExportCommandTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private AddressBook addressBook = getTypicalAddressBook();
    private Model model = new ModelManager(addressBook, new UserPrefs());
    // we can use null as a file path as we will not be using the instance file path
    private AddressBookStorage addressBookStorage = new XmlAddressBookStorage(null);
    private Storage storage = new StorageManager(addressBookStorage, null);

    @Test
    public void execute_validFilePath_success() throws Exception {
        String validFilePath = testFolder.getRoot().getPath() + "exportedData.xml";
        // if the file already exists, delete it
        Files.deleteIfExists(Paths.get(validFilePath));

        ExportCommand exportCommand = prepareCommand(validFilePath);

        String expectedMessage = String.format(ExportCommand.MESSAGE_EXPORT_CONTACTS_SUCCESS, validFilePath);
        Model expectedModel = model;
        assertCommandSuccess(exportCommand, model, expectedMessage, expectedModel);

        // check that the written file is correct
        ReadOnlyAddressBook readBack = storage.readAddressBook(validFilePath).get();
        ReadOnlyAddressBook readBackAddressBook = new AddressBook(readBack);
        assertEquals(addressBook, readBackAddressBook);
    }

    @Test
    public void equals() {
        String someValidFilePath = testFolder.getRoot().getPath() + "exported-data.xml";
        String anotherValidFilePath = testFolder.getRoot().getPath()  + "more-exported-data.xml";
        ExportCommand exportToSomeValidFilePathCommand = new ExportCommand(someValidFilePath);
        ExportCommand exportToAnotherValidFilePathCommand = new ExportCommand(anotherValidFilePath);

        // same object -> returns true
        assertTrue(exportToSomeValidFilePathCommand.equals(exportToSomeValidFilePathCommand));

        // same values -> returns true
        ExportCommand exportToSomeValidFilePathCommandCopy = new ExportCommand(someValidFilePath);
        assertTrue(exportToSomeValidFilePathCommand.equals(exportToSomeValidFilePathCommandCopy));

        // different types -> returns false
        assertFalse(exportToSomeValidFilePathCommand.equals(1));

        // null -> returns false
        assertFalse(exportToSomeValidFilePathCommand.equals(null));

        //different value -> returns false
        assertFalse(exportToSomeValidFilePathCommand.equals(exportToAnotherValidFilePathCommand));
    }

    private ExportCommand prepareCommand(String filePath) {
        ExportCommand exportCommand = new ExportCommand(filePath);
        exportCommand.setData(model, storage, new CommandHistory(), new UndoRedoStack());
        return exportCommand;
    }
}
```
###### /java/seedu/address/model/comparator/ComparatorUtilTest.java
``` java
public class ComparatorUtilTest {

    @Test
    public void compareFavorite_sameFavorite_returnZero() {
        ReadOnlyPerson favoritePersonOne = new PersonBuilder(AMY).withFavorite(true).build();
        ReadOnlyPerson favoritePersonTwo = new PersonBuilder(BOB).withFavorite(true).build();
        assertEquals(0, compareFavorite(favoritePersonOne, favoritePersonTwo));

        ReadOnlyPerson unfavoritePersonOne = new PersonBuilder(AMY).withFavorite(false).build();
        ReadOnlyPerson unfavoritePersonTwo = new PersonBuilder(BOB).withFavorite(false).build();
        assertEquals(0, compareFavorite(unfavoritePersonOne, unfavoritePersonTwo));
    }

    @Test
    public void compareFavorite_differentFavorite_returnCorrectOrder() {
        ReadOnlyPerson favoritePersonOne = new PersonBuilder(AMY).withFavorite(true).build();
        ReadOnlyPerson unfavoritePersonTwo = new PersonBuilder(BOB).withFavorite(false).build();
        assertEquals(-1, compareFavorite(favoritePersonOne, unfavoritePersonTwo));

        ReadOnlyPerson unfavoritePersonOne = new PersonBuilder(AMY).withFavorite(false).build();
        ReadOnlyPerson favoritePersonTwo = new PersonBuilder(BOB).withFavorite(true).build();
        assertEquals(1, compareFavorite(unfavoritePersonOne, favoritePersonTwo));
    }

    @Test
    public void compareName_sameName_returnZero() {
        ReadOnlyPerson personOne = new PersonBuilder(AMY).withName("Bob").build();
        ReadOnlyPerson personTwo = new PersonBuilder(BOB).withName("Bob").build();
        assertEquals(0, compareName(personOne, personTwo));
    }

    @Test
    public void compareName_differentName_returnCorrectOrder() {
        ReadOnlyPerson personOne = new PersonBuilder(AMY).withName("Amy").build();
        ReadOnlyPerson personTwo = new PersonBuilder(BOB).withName("Bob").build();
        assertEquals(-1, compareName(personOne, personTwo));
        assertEquals(1, compareName(personTwo, personOne));
    }

    @Test
    public void comparePhone_samePhone_returnZero() {
        ReadOnlyPerson personOne = new PersonBuilder(AMY).withPhone("11111111").build();
        ReadOnlyPerson personTwo = new PersonBuilder(BOB).withPhone("11111111").build();
        assertEquals(0, comparePhone(personOne, personTwo));
    }

    @Test
    public void comparePhone_differentPhone_returnCorrectOrder() {
        ReadOnlyPerson personOne = new PersonBuilder(AMY).withPhone("11111111").build();
        ReadOnlyPerson personTwo = new PersonBuilder(BOB).withPhone("22222222").build();
        assertEquals(-1, comparePhone(personOne, personTwo));
        assertEquals(1, comparePhone(personTwo, personOne));
    }

    @Test
    public void compareAddress_sameAddress_returnZero() {
        ReadOnlyPerson personOne = new PersonBuilder(AMY).withAddress("Address 1").build();
        ReadOnlyPerson personTwo = new PersonBuilder(BOB).withAddress("Address 1").build();
        assertEquals(0, compareAddress(personOne, personTwo));
    }

    @Test
    public void compareAddress_differentAddress_returnCorrectOrder() {
        ReadOnlyPerson personOne = new PersonBuilder(AMY).withAddress("Address 1").build();
        ReadOnlyPerson personTwo = new PersonBuilder(BOB).withAddress("Address 2").build();
        assertEquals(-1, compareAddress(personOne, personTwo));
        assertEquals(1, compareAddress(personTwo, personOne));
    }

    @Test
    public void compareEmail_sameEmail_returnZero() {
        ReadOnlyPerson personOne = new PersonBuilder(AMY).withEmail("amy@example.com").build();
        ReadOnlyPerson personTwo = new PersonBuilder(BOB).withEmail("amy@example.com").build();
        assertEquals(0, compareEmail(personOne, personTwo));
    }

    @Test
    public void compareEmail_differentEmail_returnCorrectOrder() {
        ReadOnlyPerson personOne = new PersonBuilder(AMY).withEmail("amy@example.com").build();
        ReadOnlyPerson personTwo = new PersonBuilder(BOB).withEmail("bob@example.com").build();
        assertEquals(-1, compareEmail(personOne, personTwo));
        assertEquals(1, compareEmail(personTwo, personOne));
    }

    @Test
    public void compareLastAccessDate_sameLastAccessDate_returnZero() {
        ReadOnlyPerson personOne = new PersonBuilder(AMY).withLastAccessDate(new Date(1000)).build();
        ReadOnlyPerson personTwo = new PersonBuilder(BOB).withLastAccessDate(new Date(1000)).build();
        assertEquals(0, compareLastAccessDate(personOne, personTwo));
    }

    @Test
    public void compareLastAccessDate_differentLastAccessDate_returnCorrectOrder() {
        ReadOnlyPerson personOne = new PersonBuilder(AMY).withLastAccessDate(new Date(2000)).build();
        ReadOnlyPerson personTwo = new PersonBuilder(BOB).withLastAccessDate(new Date(1000)).build();
        // expect the person with later last access date to come first in the ordering
        assertEquals(-1, compareLastAccessDate(personOne, personTwo));
        assertEquals(1, compareLastAccessDate(personTwo, personOne));
    }
}
```
###### /java/seedu/address/model/person/TagsContainKeywordsPredicateTest.java
``` java
public class TagsContainKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("first");
        List<String> secondPredicateKeywordList = Arrays.asList("first", "second");

        TagsContainKeywordsPredicate firstPredicate = new TagsContainKeywordsPredicate(firstPredicateKeywordList);
        TagsContainKeywordsPredicate secondPredicate = new TagsContainKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        TagsContainKeywordsPredicate firstPredicateCopy = new TagsContainKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different person -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_tagsContainKeywords_returnsTrue() {
        // One keyword
        TagsContainKeywordsPredicate predicate = new TagsContainKeywordsPredicate(Collections.singletonList("friend"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").withTags("friend").build()));

        // Multiple keywords
        predicate = new TagsContainKeywordsPredicate(Arrays.asList("friend", "colleague"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").withTags("friend", "colleague").build()));

        // Only one matching keyword
        predicate = new TagsContainKeywordsPredicate(Arrays.asList("friend", "colleague"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").withTags("friend").build()));

        // Mixed-case keywords
        predicate = new TagsContainKeywordsPredicate(Arrays.asList("fRiEnD", "coLleaGue"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").withTags("friend", "colleague").build()));
    }

    @Test
    public void test_tagsDoNotContainKeywords_returnsFalse() {
        // Zero keywords
        TagsContainKeywordsPredicate predicate = new TagsContainKeywordsPredicate(Collections.emptyList());
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").build()));

        // Non-matching keyword
        predicate = new TagsContainKeywordsPredicate(Arrays.asList("family"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withTags("friend").build()));
    }
}
```
###### /java/seedu/address/model/person/LastAccessDateTest.java
``` java
public class LastAccessDateTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void constructor_nullDate_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        new LastAccessDate(null);
    }

    @Test
    public void constructor_validDate_success() {
        Date date = new Date(1000);
        LastAccessDate lastAccessDate = new LastAccessDate(date);
        // string value of last access date should be equal to string value of date
        String originalDateString = date.toString();
        assertEquals(originalDateString, lastAccessDate.toString());

        // last access date should not be mutated when the original date is changed
        date.setTime(2000);
        assertEquals(originalDateString, lastAccessDate.toString());
    }

    @Test
    public void getDate_mutateReturnedDate_isNotMutated() {
        Date originalDate = new Date(1000);
        LastAccessDate lastAccessDate = new LastAccessDate(originalDate);
        Date date = lastAccessDate.getDate();
        // returned date value should be same as original date value
        assertEquals(originalDate, lastAccessDate.getDate());

        date.setTime(2000);
        // date value stored in get access date should not have been mutated
        assertEquals(originalDate, lastAccessDate.getDate());
    }

    @Test
    public void equals() {
        LastAccessDate lastAccessDateOne = new LastAccessDate(new Date(1000));
        LastAccessDate lastAccessDateTwo = new LastAccessDate(new Date(2000));

        // same object -> returns true
        assertTrue(lastAccessDateOne.equals(lastAccessDateOne));

        // same value -> returns true
        LastAccessDate lastAccessDateOneCopy = new LastAccessDate(new Date(1000));
        assertTrue(lastAccessDateOneCopy.equals(lastAccessDateOne));

        // different types -> returns false
        assertFalse(lastAccessDateOne.equals(new Date(1000)));

        // null -> returns false
        assertFalse(lastAccessDateOne.equals(null));

        // different last access date -> returns false
        assertFalse(lastAccessDateOne.equals(lastAccessDateTwo));
    }
}
```
###### /java/seedu/address/model/social/UniqueSocialInfoListTest.java
``` java
public class UniqueSocialInfoListTest {
    private static SocialInfo aliceFacebook = new SocialInfo("facebook", "alice", "facebook.com/alice");
    private static SocialInfo aliceTwitter = new SocialInfo("twitter", "alice", "instagram.com/alice");
    private static SocialInfo bobFacebook = new SocialInfo("facebook", "bob", "facebook.com/bob");
    private static SocialInfo bobTwitter = new SocialInfo("twitter", "bob", "instagram.com/bob");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void uniqueSocialInfoList_toSet_success() {
        UniqueSocialInfoList uniqueSocialInfoList = prepareUniqueSocialInfoList(aliceFacebook, aliceTwitter);
        HashSet<SocialInfo> expectedSet = new HashSet<>(Arrays.asList(aliceFacebook, aliceTwitter));
        assertEquals(expectedSet, uniqueSocialInfoList.toSet());
    }

    @Test
    public void uniqueSocialInfoList_addUnique_success() throws UniqueSocialInfoList.DuplicateSocialTypeException {
        UniqueSocialInfoList uniqueSocialInfoList = prepareUniqueSocialInfoList(aliceFacebook);
        uniqueSocialInfoList.add(aliceTwitter);
        HashSet<SocialInfo> expectedSet = new HashSet<>(Arrays.asList(aliceFacebook, aliceTwitter));
        assertEquals(expectedSet, uniqueSocialInfoList.toSet());
    }

    @Test
    public void uniqueSocialInfoList_addDuplicateSocialType_throwsDuplicateSocialTypeException()
            throws UniqueSocialInfoList.DuplicateSocialTypeException {
        UniqueSocialInfoList uniqueSocialInfoList = prepareUniqueSocialInfoList(aliceFacebook);
        thrown.expect(UniqueSocialInfoList.DuplicateSocialTypeException.class);
        uniqueSocialInfoList.add(bobFacebook);
    }

    @Test
    public void uniqueSocialInfoList_setSocialInfos_success() {
        // Should work for an empty list
        UniqueSocialInfoList uniqueSocialInfoList = new UniqueSocialInfoList();
        HashSet<SocialInfo> toSet = new HashSet<>(Arrays.asList(bobFacebook, bobTwitter));
        uniqueSocialInfoList.setSocialInfos(toSet);
        assertEquals(toSet, uniqueSocialInfoList.toSet());
        // Should work for list with existing social infos
        HashSet<SocialInfo> nextToSet = new HashSet<>(Arrays.asList(aliceFacebook, aliceTwitter));
        uniqueSocialInfoList.setSocialInfos(nextToSet);
        assertEquals(nextToSet, uniqueSocialInfoList.toSet());
    }

    @Test
    public void uniqueSocialInfoList_equals_success() throws UniqueSocialInfoList.DuplicateSocialTypeException {
        UniqueSocialInfoList aliceList = prepareUniqueSocialInfoList(aliceFacebook, aliceTwitter);
        UniqueSocialInfoList bobList = prepareUniqueSocialInfoList(bobFacebook, bobTwitter);
        assertFalse(aliceList.equals(bobList));
        UniqueSocialInfoList aliceListCopy = prepareUniqueSocialInfoList(aliceFacebook, aliceTwitter);
        assertTrue(aliceList.equals(aliceListCopy));

        UniqueSocialInfoList aliceListOrdered = new UniqueSocialInfoList();
        aliceListOrdered.add(aliceFacebook);
        aliceListOrdered.add(aliceTwitter);
        UniqueSocialInfoList aliceListReversed = new UniqueSocialInfoList();
        aliceListReversed.add(aliceTwitter);
        aliceListReversed.add(aliceFacebook);
        assertFalse(aliceListOrdered.equals(aliceListReversed));
    }

    @Test
    public void uniqueSocialInfoList_equalsOrderInsensitive_success()
            throws UniqueSocialInfoList.DuplicateSocialTypeException {
        UniqueSocialInfoList aliceList = prepareUniqueSocialInfoList(aliceFacebook, aliceTwitter);
        UniqueSocialInfoList bobList = prepareUniqueSocialInfoList(bobFacebook, bobTwitter);
        assertFalse(aliceList.equalsOrderInsensitive(bobList));
        UniqueSocialInfoList aliceListCopy = prepareUniqueSocialInfoList(aliceFacebook, aliceTwitter);
        assertTrue(aliceList.equalsOrderInsensitive(aliceListCopy));

        UniqueSocialInfoList aliceListOrdered = new UniqueSocialInfoList();
        aliceListOrdered.add(aliceFacebook);
        aliceListOrdered.add(aliceTwitter);
        UniqueSocialInfoList aliceListReversed = new UniqueSocialInfoList();
        aliceListReversed.add(aliceTwitter);
        aliceListReversed.add(aliceFacebook);
        assertTrue(aliceListOrdered.equalsOrderInsensitive(aliceListReversed));
    }

    private static UniqueSocialInfoList prepareUniqueSocialInfoList(SocialInfo... socialInfos) {
        return new UniqueSocialInfoList(
                new HashSet<>(Arrays.asList(socialInfos))
        );
    }
}
```
###### /java/seedu/address/model/ModelManagerTest.java
``` java
    @Test
    public void addPersons_noDuplicates_success() throws DuplicatePersonException {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        UserPrefs userPrefs = new UserPrefs();
        ModelManager modelManager = new ModelManager(addressBook, userPrefs);

        UniquePersonList personsToAdd = new UniquePersonList();
        personsToAdd.add(CARL);
        personsToAdd.add(DANIEL);

        AddressBook expectedAddressBook = new AddressBookBuilder()
                .withPerson(ALICE).withPerson(BENSON).withPerson(CARL).withPerson(DANIEL).build();

        modelManager.addPersons(personsToAdd.asObservableList());
        assertEquals(expectedAddressBook, modelManager.getAddressBook());
    }

    @Test
    public void addPersons_withDuplicates_success() throws DuplicatePersonException {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        UserPrefs userPrefs = new UserPrefs();
        ModelManager modelManager = new ModelManager(addressBook, userPrefs);

        UniquePersonList personsToAdd = new UniquePersonList();
        personsToAdd.add(ALICE);
        personsToAdd.add(DANIEL);

        AddressBook expectedAddressBook = new AddressBookBuilder()
                .withPerson(ALICE).withPerson(BENSON).withPerson(DANIEL).build();

        modelManager.addPersons(personsToAdd.asObservableList());
        assertEquals(expectedAddressBook, modelManager.getAddressBook());
    }
```
###### /java/seedu/address/testutil/StorageUtil.java
``` java
/**
 * Utility class for storage utilities
 */
public class StorageUtil {

    /**
     * Returns a dummy storage for tests where a real storage does not need to be used
     */
    public static Storage getDummyStorage() {
        return new StorageManager(null, null);
    }
}
```
###### /java/seedu/address/testutil/PersonBuilder.java
``` java
    /**
     * Parses the {@code socialInfos} into a {@code Set<SocialInfo} and set it to the {@code Person}
     * that we are building.
     */
    public PersonBuilder withSocialInfos(String... rawSocialInfos) {
        try {
            ArrayList<SocialInfo> socialInfos = new ArrayList<>();
            for (String rawSocialInfo : rawSocialInfos) {
                socialInfos.add(parseSocialInfo(rawSocialInfo));
            }
            // convert to array to be passed as varargs in getSocialInfoSet
            SocialInfo[] socialInfosArray = socialInfos.toArray(new SocialInfo[rawSocialInfos.length]);
            this.person.setSocialInfos(SampleDataUtil.getSocialInfoSet(socialInfosArray));
        } catch (IllegalValueException ive) {
            throw new IllegalArgumentException("raw social infos must be valid.");
        }
        return this;
    }

    /**
     * Sets the {@code LastAccessDate} of the {@code Person} that we are building.
     */
    public PersonBuilder withLastAccessDate(Date date) {
        LastAccessDate lastAccessDate = new LastAccessDate(date);
        this.person.setLastAccessDate(lastAccessDate);

        return this;

    }
```
###### /java/seedu/address/testutil/modelstubs/ModelStubThrowingDuplicatePersonException.java
``` java
/**
 * A Model stub that always throw a DuplicatePersonException when trying to add a person.
 */
public class ModelStubThrowingDuplicatePersonException extends ModelStub {
    @Override
    public void addPerson(ReadOnlyPerson person) throws DuplicatePersonException {
        throw new DuplicatePersonException();
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return new AddressBook();
    }
}
```
###### /java/seedu/address/testutil/modelstubs/ModelStubAcceptingPersonAdded.java
``` java
/**
 * A Model stub that always accept the person being added.
 */
public class ModelStubAcceptingPersonAdded extends ModelStub {
    public final ArrayList<Person> personsAdded = new ArrayList<>();

    @Override
    public void addPerson(ReadOnlyPerson person) throws DuplicatePersonException {
        personsAdded.add(new Person(person));
    }

    @Override
    public void addPersons(Collection<ReadOnlyPerson> persons) {
        for (ReadOnlyPerson person : persons) {
            personsAdded.add(new Person(person));
        }
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return new AddressBook();
    }
}
```
###### /java/seedu/address/testutil/modelstubs/ModelStub.java
``` java
/**
 * A default model stub that have all of the methods failing.
 */
public class ModelStub implements Model {
    @Override
    public void addPerson(ReadOnlyPerson person) throws DuplicatePersonException {
        fail("This method should not be called.");
    }

    @Override
    public void addPersons(Collection<ReadOnlyPerson> persons) {
        fail("This method should not be called.");
    }

    @Override
    public void sortPersons(Comparator<ReadOnlyPerson> comparator) {
        fail("This method should not be called.");
    }

    @Override
    public void resetData(ReadOnlyAddressBook newData) {
        fail("This method should not be called.");
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        fail("This method should not be called.");
        return null;
    }

    @Override
    public void deletePerson(ReadOnlyPerson target) throws PersonNotFoundException {
        fail("This method should not be called.");
    }

    @Override
    public void updatePerson(ReadOnlyPerson target, ReadOnlyPerson editedPerson)
            throws DuplicatePersonException {
        fail("This method should not be called.");
    }

    @Override
    public void selectPerson(ReadOnlyPerson target) throws PersonNotFoundException {
        fail("This method should not be called.");
    }

    @Override
    public Index getPersonIndex(ReadOnlyPerson target) throws PersonNotFoundException {
        fail("This method should not be called.");
        return null;
    }

```
###### /java/seedu/address/testutil/modelstubs/ModelStub.java
``` java

    @Override
    public ObservableList<ReadOnlyPerson> getFilteredPersonList() {
        fail("This method should not be called.");
        return null;
    }

    @Override
    public void updateFilteredPersonList(Predicate<ReadOnlyPerson> predicate) {
        fail("This method should not be called.");
    }

    @Override
    public void removeTag(Tag tag) throws PersonNotFoundException, DuplicatePersonException {
        fail("This method should not be called.");
    }

    @Override
    public Model makeCopy() {
        fail("This method should not be called.");
        return null;
    }
}
```
