# sarahnzx
###### /java/seedu/address/logic/parser/EditCommandParserTest.java
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
###### /java/seedu/address/logic/parser/EditCommandParserTest.java
``` java
    @Test
    public void parse_validValueFollowedByInvalidValue_success() {
        // no other valid values specified
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + PHONE_DESC_BOB;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // valid value followed by invalid value specified
        userInput = targetIndex.getOneBased() + EMAIL_DESC_BOB + PHONE_DESC_BOB + ADDRESS_DESC_BOB
                + INVALID_PHONE_DESC;
        descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB)
                .withAddress(VALID_ADDRESS_BOB).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }
```
