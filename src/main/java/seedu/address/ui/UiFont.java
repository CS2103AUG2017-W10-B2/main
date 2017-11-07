package seedu.address.ui;

import javafx.scene.text.Font;

//@@author keithsoc
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
//@@author
