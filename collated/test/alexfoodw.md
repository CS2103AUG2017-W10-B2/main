# alexfoodw
###### \java\seedu\address\logic\parser\AddressBookParserTest.java
``` java
    @Test
    public void parseCommand_facebookAddAllFriends() throws Exception {
        FacebookAddAllFriendsCommand command = (FacebookAddAllFriendsCommand) parser.parseCommand(
                FacebookAddAllFriendsCommand.COMMAND_WORD);
        assertEquals(new FacebookAddAllFriendsCommand(), command);
    }

    @Test
    public void parseCommand_facebookAdd() throws Exception {
        FacebookAddCommand command = (FacebookAddCommand) parser.parseCommand(
                FacebookAddCommand.COMMAND_WORD + " " + FacebookAddCommand.EXAMPLE_NAME);
        assertEquals(new FacebookAddCommand(FacebookAddCommand.EXAMPLE_NAME), command);
    }

    @Test
    public void parseCommand_facebookConnect() throws Exception {
        FacebookConnectCommand command = (FacebookConnectCommand) parser.parseCommand(
                FacebookConnectCommand.COMMAND_WORD);
        assertEquals(new FacebookConnectCommand(), command);
    }
    @Test
    public void parseCommand_facebookLink() throws Exception {
        FacebookLinkCommand command = (FacebookLinkCommand) parser.parseCommand(
                FacebookLinkCommand.COMMAND_WORD + " " + FacebookLinkCommand.EXAMPLE_LINK);
        assertEquals(new FacebookLinkCommand(FacebookLinkCommand.EXAMPLE_LINK), command);
    }
    @Test
    public void parseCommand_facebookPost() throws Exception {
        FacebookPostCommand command = (FacebookPostCommand) parser.parseCommand(
                FacebookPostCommand.COMMAND_WORD + " " + FacebookPostCommand.EXAMPLE_POST);
        assertEquals(new FacebookPostCommand(FacebookPostCommand.EXAMPLE_POST), command);
    }
```
###### \java\seedu\address\logic\parser\FacebookAddCommandParserTest.java
``` java
public class FacebookAddCommandParserTest {

    private FacebookAddCommandParser parser = new FacebookAddCommandParser();

    @Test
    public void parse_validArgs_returnsFacebookLinkCommand() {
        assertParseSuccess(parser, FacebookAddCommand.EXAMPLE_NAME,
                new FacebookAddCommand(FacebookAddCommand.EXAMPLE_NAME));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, " ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FacebookAddCommand.MESSAGE_USAGE));
    }
}
```
###### \java\seedu\address\logic\parser\FacebookLinkCommandParserTest.java
``` java
public class FacebookLinkCommandParserTest {

    private FacebookLinkCommandParser parser = new FacebookLinkCommandParser();

    @Test
    public void parse_validArgs_returnsFacebookLinkCommand() {
        assertParseSuccess(parser, FacebookLinkCommand.EXAMPLE_LINK,
                new FacebookLinkCommand(FacebookLinkCommand.EXAMPLE_LINK));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "www.google.com",
                String.format(MESSAGE_INVALID_URL, "Example: " + FacebookLinkCommand.EXAMPLE_LINK));
    }
}
```
###### \java\seedu\address\logic\parser\FacebookPostCommandParserTest.java
``` java
public class FacebookPostCommandParserTest {
    private FacebookPostCommandParser parser = new FacebookPostCommandParser();

    @Test
    public void parse_validArgs_returnsFacebookPostCommand() {
        assertParseSuccess(parser, FacebookPostCommand.EXAMPLE_POST,
                new FacebookPostCommand(FacebookPostCommand.EXAMPLE_POST));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, " ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FacebookPostCommand.MESSAGE_USAGE));
    }
}
```
