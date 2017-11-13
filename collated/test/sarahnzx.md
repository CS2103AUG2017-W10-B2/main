# sarahnzx
###### \java\seedu\address\logic\commands\DeleteByIndexCommandTest.java
``` java
    @Test
    public void execute_multipleValidIndicesUnfilteredList_success() throws Exception {
        ReadOnlyPerson personToDelete = null;
        String people = "";
        List<Index> indexList = new ArrayList<>();


        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        for (int i = 0; i < 3; i++) {
            Index indexToDelete = Index.fromZeroBased(i);
            personToDelete = model.getFilteredPersonList().get(indexToDelete.getZeroBased());
            expectedModel.deletePerson(personToDelete);
            people += "\n" + personToDelete.toString();
            indexList.add(indexToDelete);
        }

        DeleteByIndexCommand deleteByIndexCommand = prepareCommand(indexList);
        String expectedMessage = String.format(deleteByIndexCommand.MESSAGE_DELETE_PERSON_SUCCESS, people);
        assertCommandSuccess(deleteByIndexCommand, model, expectedMessage, expectedModel);
    }
```
###### \java\seedu\address\logic\commands\DeleteByIndexCommandTest.java
``` java
    @Test
    public void execute_multipleIndicesWithInvalidIndexUnfilteredList_throwsCommandException() throws Exception {
        List<Index> indexList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Index index = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
            indexList.add(index);
        }
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        indexList.add(outOfBoundIndex);
        DeleteByIndexCommand deleteByIndexCommand = prepareCommand(indexList);
        assertCommandFailure(deleteByIndexCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }
```
###### \java\seedu\address\logic\commands\DeleteByIndexCommandTest.java
``` java
    private DeleteByIndexCommand prepareCommand(Collection<Index> indexes) {
        DeleteByIndexCommand deleteByIndexCommand = new DeleteByIndexCommand(indexes);
        deleteByIndexCommand.setData(model, getDummyStorage(), new CommandHistory(), new UndoRedoStack());
        return deleteByIndexCommand;
    }
```
###### \java\seedu\address\logic\parser\EditCommandParserTest.java
``` java
    @Test
    public void parse_multipleRepeatedFields_acceptsMultipleUnrepeated() {
        Index targetIndex = INDEX_FIRST_PERSON;

        String userInput = targetIndex.getOneBased()
                + PHONE_DESC_AMY + ADDRESS_DESC_AMY + EMAIL_DESC_AMY + FAVORITE_DESC_YES + TAG_DESC_FRIEND
                + PHONE_DESC_AMY + ADDRESS_DESC_AMY + EMAIL_DESC_AMY + FAVORITE_DESC_YES + TAG_DESC_FRIEND
                + PHONE_DESC_BOB + ADDRESS_DESC_BOB + EMAIL_DESC_BOB + FAVORITE_DESC_YES + TAG_DESC_HUSBAND;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone(VALID_PHONE_AMY + "\n" + VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_BOB)
                .withAddress(VALID_ADDRESS_BOB)
                .withFavorite(VALID_FAVORITE_YES)
                .withTags(VALID_TAG_FRIEND, VALID_TAG_HUSBAND)
                .build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }
```
###### \java\seedu\address\logic\parser\EditCommandParserTest.java
``` java
    @Test
    public void parse_validValueFollowedByInvalidValue_success() {
        // no other valid values specified
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + PHONE_DESC_BOB;
        assertParseFailure(parser, userInput, Phone.MESSAGE_PHONE_CONSTRAINTS);

        // valid value followed by invalid value specified
        userInput = targetIndex.getOneBased() + EMAIL_DESC_BOB + PHONE_DESC_BOB + ADDRESS_DESC_BOB
                + INVALID_PHONE_DESC;
        assertParseFailure(parser, userInput, Phone.MESSAGE_PHONE_CONSTRAINTS);
    }
```
###### \java\systemtests\SelectCommandSystemTest.java
``` java
        /* Case: valid arguments (social type instagram) -> selected */
        command = SelectCommand.COMMAND_WORD + " " + validIndex.getOneBased() + " instagram";
        assertCommandFailure(command, MESSAGE_SOCIAL_TYPE_NOT_FOUND);

        /* Case: valid arguments (social type facebook) -> selected */
        command = SelectCommand.COMMAND_WORD + " " + validIndex.getOneBased() + " facebook";
        assertCommandSuccess(command, validIndex);

        /* Case: valid arguments (social type ig) -> selected */
        command = SelectCommand.COMMAND_WORD + " " + validIndex.getOneBased() + " ig";
        assertCommandFailure(command, MESSAGE_SOCIAL_TYPE_NOT_FOUND);

        /* Case: valid arguments (social type fb) -> selected */
        command = SelectCommand.COMMAND_WORD + " " + validIndex.getOneBased() + " fb";
        assertCommandSuccess(command, validIndex);
```
