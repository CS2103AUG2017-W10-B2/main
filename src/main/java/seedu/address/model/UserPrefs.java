package seedu.address.model;

import java.util.Objects;

import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.ThemeSettings;

/**
 * Represents User's preferences.
 */
public class UserPrefs {

    private GuiSettings guiSettings;
    //@@author keithsoc
    private ThemeSettings themeSettings;
    //@@author
    private String addressBookFilePath = "data/addressbook.xml";
    //@@author keithsoc
    private String addressBookName = "KayPoh";
    //@@author

    //@@author keithsoc
    public UserPrefs() {
        this.setGuiSettings(1100, 835, 0, 0);
        this.setThemeSettings("view/ThemeDay.css", "view/ThemeDayExtensions.css");
    }
    //@@author

    public GuiSettings getGuiSettings() {
        return guiSettings == null ? new GuiSettings() : guiSettings;
    }

    public void updateLastUsedGuiSetting(GuiSettings guiSettings) {
        this.guiSettings = guiSettings;
    }

    public void setGuiSettings(double width, double height, int x, int y) {
        guiSettings = new GuiSettings(width, height, x, y);
    }

    //@@author keithsoc
    public ThemeSettings getThemeSettings() {
        return themeSettings == null ? new ThemeSettings() : themeSettings;
    }

    public void updateLastUsedThemeSetting(ThemeSettings themeSettings) {
        this.themeSettings = themeSettings;
    }

    public void setThemeSettings(String theme, String themeExtensions) {
        themeSettings = new ThemeSettings(theme, themeExtensions);
    }
    //@@author

    public String getAddressBookFilePath() {
        return addressBookFilePath;
    }

    public void setAddressBookFilePath(String addressBookFilePath) {
        this.addressBookFilePath = addressBookFilePath;
    }

    public String getAddressBookName() {
        return addressBookName;
    }

    public void setAddressBookName(String addressBookName) {
        this.addressBookName = addressBookName;
    }


    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UserPrefs)) { // this handles null as well.
            return false;
        }

        UserPrefs o = (UserPrefs) other;

        return Objects.equals(guiSettings, o.guiSettings)
                && Objects.equals(addressBookFilePath, o.addressBookFilePath)
                && Objects.equals(addressBookName, o.addressBookName)
                && Objects.equals(themeSettings, o.themeSettings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guiSettings, themeSettings, addressBookFilePath, addressBookName);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Gui Settings : " + guiSettings.toString());
        sb.append("\nTheme Settings : " + themeSettings.toString());
        sb.append("\nLocal data file location : " + addressBookFilePath);
        sb.append("\nAddressBook name : " + addressBookName);
        return sb.toString();
    }

}
