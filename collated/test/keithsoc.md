# keithsoc
###### \java\seedu\address\logic\commands\FavoriteCommandTest.java
``` java
/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code FavoriteCommand}.
 */
public class FavoriteCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() throws Exception {
        ReadOnlyPerson personToFavorite = model.getFilteredPersonList().get(INDEX_THIRD_PERSON.getZeroBased());
        FavoriteCommand favoriteCommand = prepareCommand(Arrays.asList(INDEX_THIRD_PERSON));

        String expectedMessage = FavoriteCommand.MESSAGE_FAVORITE_PERSON_SUCCESS
                + "\n\t★ " + personToFavorite.getName().toString();

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.toggleFavoritePerson(personToFavorite, FavoriteCommand.COMMAND_WORD);

        assertCommandSuccess(favoriteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validMultiIndexesUnfilteredList_success() throws Exception {
        ReadOnlyPerson personAlice = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        ReadOnlyPerson personDaniel = model.getFilteredPersonList().get(INDEX_FOURTH_PERSON.getZeroBased());

        Set<ReadOnlyPerson> targetPersonList = new LinkedHashSet<>();
        targetPersonList.add(personAlice);
        targetPersonList.add(personDaniel);

        FavoriteCommand favoriteCommand = prepareCommand(Arrays.asList(INDEX_FIRST_PERSON, INDEX_FOURTH_PERSON));

        // In TypicalPersons, Alice is already a favorite contact while Daniel is not
        String expectedMessage = FavoriteCommand.MESSAGE_FAVORITE_PERSON_SUCCESS
                + "\n\t★ " + personDaniel.getName().toString()
                + "\n" + FavoriteCommand.MESSAGE_FAVORITE_PERSON_FAILURE
                + "\n\t- " + personAlice.getName().toString();

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        for (ReadOnlyPerson personToFavorite : targetPersonList) {
            expectedModel.toggleFavoritePerson(personToFavorite, FavoriteCommand.COMMAND_WORD);
        }

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

        // In TypicalPersons, first person (Alice) is already a favorite.
        // Assert command success as this is not a command failure,
        // it's because we disallow favoriting of an already favorited person.
        String expectedMessage = FavoriteCommand.MESSAGE_FAVORITE_PERSON_FAILURE
                + "\n\t- " + personToFavorite.getName().toString();

        assertCommandSuccess(favoriteCommand, model, expectedMessage, model);
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
        favoriteCommand.setData(model, getDummyStorage(), new CommandHistory(), new UndoRedoStack());
        return favoriteCommand;
    }
}
```
###### \java\seedu\address\logic\commands\ListCommandTest.java
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
        listCommand.setData(model, getDummyStorage(), new CommandHistory(), new UndoRedoStack());
        return listCommand;
    }
```
###### \java\seedu\address\logic\commands\UnFavoriteCommandTest.java
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

        String expectedMessage = UnFavoriteCommand.MESSAGE_UNFAVORITE_PERSON_SUCCESS
                + "\n\t- " + personToUnFavorite.getName().toString();

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.toggleFavoritePerson(personToUnFavorite, UnFavoriteCommand.COMMAND_WORD);

        assertCommandSuccess(unFavoriteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validMultiIndexesUnfilteredList_success() throws Exception {
        ReadOnlyPerson personAlice = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        ReadOnlyPerson personDaniel = model.getFilteredPersonList().get(INDEX_FOURTH_PERSON.getZeroBased());

        Set<ReadOnlyPerson> targetPersonList = new LinkedHashSet<>();
        targetPersonList.add(personAlice);
        targetPersonList.add(personDaniel);

        UnFavoriteCommand unFavoriteCommand = prepareCommand(Arrays.asList(INDEX_FIRST_PERSON, INDEX_FOURTH_PERSON));

        // In TypicalPersons, Alice is already a favorite contact while Daniel is not
        String expectedMessage = UnFavoriteCommand.MESSAGE_UNFAVORITE_PERSON_SUCCESS
                + "\n\t- " + personAlice.getName().toString()
                + "\n" + UnFavoriteCommand.MESSAGE_UNFAVORITE_PERSON_FAILURE
                + "\n\t- " + personDaniel.getName().toString();

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        for (ReadOnlyPerson personToUnFavorite : targetPersonList) {
            expectedModel.toggleFavoritePerson(personToUnFavorite, UnFavoriteCommand.COMMAND_WORD);
        }

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

        String expectedMessage = UnFavoriteCommand.MESSAGE_UNFAVORITE_PERSON_SUCCESS
                + "\n\t- " + personToUnFavorite.getName().toString();

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
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
        unFavoriteCommand.setData(model, getDummyStorage(), new CommandHistory(), new UndoRedoStack());
        return unFavoriteCommand;
    }
}
```
###### \java\seedu\address\logic\parser\AddressBookParserTest.java
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
###### \java\seedu\address\logic\parser\AddressBookParserTest.java
``` java
    @Test
    public void parseCommand_listFav() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD
                + " " + ListCommand.COMMAND_OPTION_FAV) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD
                + " " + ListCommand.COMMAND_OPTION_FAV + " 3") instanceof ListCommand);
    }
```
###### \java\seedu\address\logic\parser\AddressBookParserTest.java
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
        assertEquals(new UnFavoriteCommand(Arrays.asList(INDEX_SECOND_PERSON, INDEX_FIRST_PERSON)), command);
    }
```
###### \java\seedu\address\logic\parser\FavoriteCommandParserTest.java
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
###### \java\seedu\address\logic\parser\ParserUtilTest.java
``` java
    @Test
    public void parseMultiIndex_invalidInput_throwsIllegalValueException() throws Exception {
        thrown.expect(IllegalValueException.class);
        ParserUtil.parseMultipleIndexes("1 2 3 a"); // Two trailing spaces in front
    }
```
###### \java\seedu\address\logic\parser\ParserUtilTest.java
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
###### \java\seedu\address\logic\parser\ThemeCommandParserTest.java
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
###### \java\seedu\address\logic\parser\UnFavoriteCommandParserTest.java
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
###### \java\seedu\address\testutil\EditPersonDescriptorBuilder.java
``` java
    /**
     * Sets the {@code Favorite} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withFavorite(boolean favorite) {
        descriptor.setFavorite(new Favorite(favorite));
        return this;
    }
```
###### \java\seedu\address\testutil\modelstubs\ModelStub.java
``` java
    @Override
    public void toggleFavoritePerson(ReadOnlyPerson target, String type)
            throws DuplicatePersonException, PersonNotFoundException {
        fail("This method should not be called.");
    }
```
###### \java\seedu\address\testutil\PersonBuilder.java
``` java
    /**
     * Sets the {@code Favorite} of the {@code Person} that we are building.
     */
    public PersonBuilder withFavorite(boolean favorite) {
        this.person.setFavorite(new Favorite(favorite));
        return this;
    }

    /**
     * Sets the {@code DisplayPhoto} of the {@code Person} that we are building.
     */
    public PersonBuilder withDisplayPhoto(String displayPhoto) {
        try {
            this.person.setDisplayPhoto(new DisplayPhoto(displayPhoto));
        } catch (IllegalValueException ive) {
            throw new IllegalArgumentException("display photo file does not exist or it exceeded maximum size of 1MB");
        }
        return this;
    }
```
###### \java\seedu\address\testutil\TypicalIndexes.java
``` java
    public static final Index INDEX_FOURTH_PERSON = Index.fromOneBased(4);
    public static final Index INDEX_FIFTH_PERSON = Index.fromOneBased(5);
```
###### \java\systemtests\FavoriteCommandSystemTest.java
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
            names.append("\n\t★ ").append(favoritedPerson.getName().toString());
        }
        String expectedResultMessage = MESSAGE_FAVORITE_PERSON_SUCCESS + names;
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

        /* ---------------- Performing favorite operation while a filtered list is being shown ------------------- */

        /* Case: filtered person list, favorite index within bounds of address book and person list -> favorited */
        showPersonsWithName(KEYWORD_MATCHING_KUNZ);
        expectedModel = getModel();
        Index index = INDEX_FIRST_PERSON;
        assertTrue(index.getZeroBased() < getModel().getFilteredPersonList().size());
        command = FavoriteCommand.COMMAND_WORD + " " + index.getOneBased();
        ReadOnlyPerson favoritedPerson = favoritePerson(expectedModel, index);
        expectedResultMessage = MESSAGE_FAVORITE_PERSON_SUCCESS + "\n\t★ " + favoritedPerson.getName().toString();
        assertCommandSuccess(command, expectedModel, expectedResultMessage);

        /*
         * Case: filtered person list, favorite index within bounds of address book but out of bounds of person list
         * -> rejected
         */
        showPersonsWithName(KEYWORD_MATCHING_KUNZ);
        int invalidIndex = getModel().getAddressBook().getPersonList().size();
        command = FavoriteCommand.COMMAND_WORD + " " + invalidIndex;
        assertCommandFailure(command, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);

        /* ------------------- Performing favorite operation while a person card is selected ---------------------- */

        /* Case: favorite the selected person -> person list panel selects the person before the favoriting */
        showAllPersons();
        expectedModel = getModel();
        Index selectedIndex = getLastIndex(expectedModel);
        Index expectedIndex = Index.fromZeroBased(selectedIndex.getZeroBased());
        selectPerson(selectedIndex);
        command = FavoriteCommand.COMMAND_WORD + " " + selectedIndex.getOneBased();
        favoritedPerson = favoritePerson(expectedModel, selectedIndex);
        expectedResultMessage = MESSAGE_FAVORITE_PERSON_SUCCESS + "\n\t★ " + favoritedPerson.getName().toString();
        assertCommandSuccess(command, expectedModel, expectedResultMessage, expectedIndex);

        /* ------------------------------- Performing invalid favorite operation ---------------------------------- */

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
###### \java\systemtests\UnFavoriteCommandSystemTest.java
``` java
public class UnFavoriteCommandSystemTest extends AddressBookSystemTest {

    private static final String MESSAGE_INVALID_UNFAVORITE_COMMAND_FORMAT =
            String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, UnFavoriteCommand.MESSAGE_USAGE);

    @Test
    public void unFavorite() {
        /* ------------- Performing unfavorite operation while an unfiltered list is being shown ---------------- */

        /*
         * Case: unfavorites the 1st & 2nd person in the list,
         * command with leading spaces and trailing spaces -> unfavorited
         */
        Model expectedModel = getModel();
        String command = "     " + UnFavoriteCommand.COMMAND_WORD + "      " + INDEX_FIRST_PERSON.getOneBased()
                + "       " + INDEX_SECOND_PERSON.getOneBased();

        // Store in reverse just like how it would be sorted in the UnFavoriteCommandParser
        List<Index> indexList = Arrays.asList(INDEX_SECOND_PERSON, INDEX_FIRST_PERSON);
        StringBuilder names = new StringBuilder();
        for (Index index : indexList) {
            ReadOnlyPerson unFavoritedPerson = unFavoritePerson(expectedModel, index);
            names.append("\n\t- ").append(unFavoritedPerson.getName().toString());
        }
        String expectedResultMessage = MESSAGE_UNFAVORITE_PERSON_SUCCESS + names;
        assertCommandSuccess(command, expectedModel, expectedResultMessage);

        /* Case: undo unfavoriting the 1st & 2nd person in the list -> 1st & 2nd person unfavorited */
        command = UndoCommand.COMMAND_WORD;
        expectedResultMessage = UndoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, expectedModel, expectedResultMessage);

        /* Case: redo unfavoriting the 1st & 2nd person in the list -> 1st & 2nd person unfavorited again */
        command = RedoCommand.COMMAND_WORD;
        for (Index index : indexList) {
            unFavoritePerson(expectedModel, index);
        }
        expectedResultMessage = RedoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, expectedModel, expectedResultMessage);

        /* Case: we're undoing again so that we can perform unfavorite operations (with success message) below */
        command = UndoCommand.COMMAND_WORD;
        expectedResultMessage = UndoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, expectedModel, expectedResultMessage);

        /* ---------------- Performing unfavorite operation while a filtered list is being shown ------------------- */

        /* Case: filtered person list, unfavorite index within bounds of address book and person list -> unfavorited */
        showPersonsWithName(KEYWORD_MATCHING_MEIER);
        expectedModel = getModel();
        Index index = INDEX_FIRST_PERSON;
        assertTrue(index.getZeroBased() < getModel().getFilteredPersonList().size());
        command = UnFavoriteCommand.COMMAND_WORD + " " + index.getOneBased();
        ReadOnlyPerson unFavoritedPerson = unFavoritePerson(expectedModel, index);
        expectedResultMessage = MESSAGE_UNFAVORITE_PERSON_SUCCESS + "\n\t- " + unFavoritedPerson.getName().toString();
        assertCommandSuccess(command, expectedModel, expectedResultMessage);

        /*
         * Case: filtered person list, unfavorite index within bounds of address book but out of bounds of person list
         * -> rejected
         */
        showPersonsWithName(KEYWORD_MATCHING_MEIER);
        int invalidIndex = getModel().getAddressBook().getPersonList().size();
        command = UnFavoriteCommand.COMMAND_WORD + " " + invalidIndex;
        assertCommandFailure(command, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);

        /* ------------------ Performing unfavorite operation while a person card is selected --------------------- */

        /* Case: unfavorite the selected person -> person list panel selects the person before the unfavoriting */
        showAllPersons();
        expectedModel = getModel();
        Index selectedIndex = INDEX_FIRST_PERSON;
        Index expectedIndex = Index.fromZeroBased(selectedIndex.getZeroBased());
        selectPerson(selectedIndex);
        command = UnFavoriteCommand.COMMAND_WORD + " " + selectedIndex.getOneBased();
        unFavoritedPerson = unFavoritePerson(expectedModel, selectedIndex);
        expectedResultMessage = MESSAGE_UNFAVORITE_PERSON_SUCCESS + "\n\t- " + unFavoritedPerson.getName().toString();
        assertCommandSuccess(command, expectedModel, expectedResultMessage, expectedIndex);

        /* ------------------------------ Performing invalid unfavorite operation --------------------------------- */

        /* Case: multiple invalid indexes (0 0 0) -> rejected */
        command = UnFavoriteCommand.COMMAND_WORD + " 0 0 0";
        assertCommandFailure(command, MESSAGE_INVALID_UNFAVORITE_COMMAND_FORMAT);

        /* Case: multiple indexes with only one valid (1 0 -1 -2 -3) -> rejected */
        command = UnFavoriteCommand.COMMAND_WORD + " 1 0 -1 -2 -3";
        assertCommandFailure(command, MESSAGE_INVALID_UNFAVORITE_COMMAND_FORMAT);

        /* Case: invalid index (size + 1) -> rejected */
        Index outOfBoundsIndex = Index.fromOneBased(
                getModel().getAddressBook().getPersonList().size() + 1);
        command = UnFavoriteCommand.COMMAND_WORD + " " + outOfBoundsIndex.getOneBased();
        assertCommandFailure(command, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);

        /* Case: invalid arguments (alphabets) -> rejected */
        assertCommandFailure(UnFavoriteCommand.COMMAND_WORD + " 1 2 a",
                MESSAGE_INVALID_UNFAVORITE_COMMAND_FORMAT);

        /* Case: mixed case command word -> rejected */
        assertCommandFailure("UnFaV 1", MESSAGE_UNKNOWN_COMMAND);
    }

    /**
     * Unfavorites the {@code ReadOnlyPerson} at the specified {@code index} in {@code model}'s address book.
     * @return the unfavorited person
     */
    private ReadOnlyPerson unFavoritePerson(Model model, Index index) {
        ReadOnlyPerson targetPerson = model.getFilteredPersonList().get(index.getZeroBased());
        try {
            model.toggleFavoritePerson(targetPerson, UnFavoriteCommand.COMMAND_WORD);
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
     * @see UnFavoriteCommandSystemTest#assertCommandSuccess(String, Model, String)
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
