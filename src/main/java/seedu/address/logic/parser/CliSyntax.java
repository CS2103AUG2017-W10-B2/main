package seedu.address.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions */
    public static final Prefix PREFIX_NAME = new Prefix("n/");
    public static final Prefix PREFIX_PHONE = new Prefix("p/");
    public static final Prefix PREFIX_EMAIL = new Prefix("e/");
    public static final Prefix PREFIX_ADDRESS = new Prefix("a/");
    //@@author keithsoc
    public static final Prefix PREFIX_FAV = new Prefix("f/");
    public static final Prefix PREFIX_UNFAV = new Prefix("uf/");
    public static final Prefix PREFIX_DISPLAY_PHOTO = new Prefix("dp/");
    //@@author
    public static final Prefix PREFIX_TAG = new Prefix("t/");
    public static final Prefix PREFIX_SOCIAL = new Prefix("s/");
    //@@author keithsoc
    public static final Prefix PREFIX_OPTION = new Prefix("-");
    //@@author

}
