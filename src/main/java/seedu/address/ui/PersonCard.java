package seedu.address.ui;

import java.awt.Color;
import java.util.HashMap;
import java.util.Random;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import seedu.address.model.person.ReadOnlyPerson;

/**
 * An UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";
    //@@author keithsoc
    private static HashMap<String, String> personColors = new HashMap<>();
    private static HashMap<String, String> tagColors = new HashMap<>();
    private static Random random = new Random();
    private static final String defaultThemeTagColor = "#fc4465";
    private static final double GOLDEN_RATIO = 0.618033988749895;
    //@@author

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final ReadOnlyPerson person;

    @FXML
    private HBox cardPane;
    //@@author keithsoc
    @FXML
    private StackPane displayPhotoStackPane;
    @FXML
    private ImageView displayPhotoImageView;
    //@@author
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private ImageView favoriteImageView;
    @FXML
    private FlowPane tags;
    @FXML
    private VBox socialInfos;

    public PersonCard(ReadOnlyPerson person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        initDisplayPhoto(person);
        initFavorite(person);
        initTags(person);
        initSocialInfos(person);
        bindListeners(person);
    }

    //@@author keithsoc
    /**
     * Generates a random pastel color for display photos.
     * @return String containing hex value of the color.
     */
    private String generateRandomPastelColor() {
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);

        Color mixer = new Color(235, 235, 235);
        red = (red + mixer.getRed()) / 2;
        green = (green + mixer.getGreen()) / 2;
        blue = (blue + mixer.getBlue()) / 2;

        Color result = new Color(red, green, blue);
        return String.format("#%02x%02x%02x", result.getRed(), result.getGreen(), result.getBlue());
    }

    /**
     * Generates a random bright color (using golden ratio for even color distribution) for tag labels.
     * @return String containing hex value of the color.
     */
    private String generateRandomColor() {
        float randomHue = random.nextFloat();
        randomHue += GOLDEN_RATIO;
        randomHue = randomHue % 1;

        Color result = Color.getHSBColor(randomHue, 0.5f, 0.85f);
        return String.format("#%02x%02x%02x", result.getRed(), result.getGreen(), result.getBlue());
    }

    /**
     * Binds a display photo background with a random pastel color and store it into personColors HashMap.
     */
    private String getColorForPerson(String name) {
        if (!personColors.containsKey(name)) {
            personColors.put(name, generateRandomPastelColor());
        }
        return personColors.get(name);
    }


    /**
     * Binds a tag label with a specific or random color and store it into tagColors HashMap.
     */
    private String getColorForTag(String tagValue) {
        if (!tagColors.containsKey(tagValue)) {
            if (tagValue.equalsIgnoreCase("family")) {
                tagColors.put(tagValue, defaultThemeTagColor); // Assign a default value for "family" tags
            } else {
                tagColors.put(tagValue, generateRandomColor());
            }
        }
        return tagColors.get(tagValue);
    }
    //@@author

    /**
     * Binds the individual UI elements to observe their respective {@code Person} properties
     * so that they will be notified of any changes.
     */
    private void bindListeners(ReadOnlyPerson person) {
        name.textProperty().bind(Bindings.convert(person.nameProperty()));
        phone.textProperty().bind(Bindings.convert(person.phoneProperty()));
        address.textProperty().bind(Bindings.convert(person.addressProperty()));
        email.textProperty().bind(Bindings.convert(person.emailProperty()));
        person.favoriteProperty().addListener((observable, oldValue, newValue) -> {
            favoriteImageView.setId("favoriteImageView");
            initFavorite(person);
        });
        person.tagProperty().addListener((observable, oldValue, newValue) -> {
            tags.getChildren().clear();
            initTags(person);
        });
        person.socialInfoProperty().addListener((observable, oldValue, newValue) -> {
            socialInfos.getChildren().clear();
            initSocialInfos(person);
        });
    }

    //@@author keithsoc
    /**
     * Adds a display photo for each {@code person}.
     * If {@code person} has a non-null display photo field, set ImageView to an image of user's choice.
     * If {@code person} has a null display photo field, set ImageView to a colored thumbnail with name initials.
     */
    private void initDisplayPhoto(ReadOnlyPerson person) {
        // Round display photo
        double value = displayPhotoImageView.getFitWidth() / 2;
        Circle clip = new Circle(value, value, value);
        displayPhotoImageView.setClip(clip);

        if (person.getDisplayPhoto().value != null) {
            // Set image to user's choice
            displayPhotoImageView.setImage(new Image(person.getDisplayPhoto().getAbsoluteFilePath()));
        } else {
            // Add background circle with a random pastel color
            String nameOfPerson = person.getName().toString().trim();
            Circle backgroundCircle = new Circle(value);
            backgroundCircle.setFill(Paint.valueOf(getColorForPerson(nameOfPerson)));

            // Add text
            Text personInitialsText = new Text(extractInitials(nameOfPerson));
            personInitialsText.setFill(Paint.valueOf("white"));
            displayPhotoStackPane.getChildren().addAll(backgroundCircle, personInitialsText);
        }
    }

    /**
     * Extracts the initials from the name of the given {@code name}.
     * Extract only one initial if the name contains a single word;
     * Extract two initials if the name contains more than one word.
     */
    private String extractInitials (String name) {
        int noOfInitials = 1;
        if (name.split("\\s+").length > 1) {
            noOfInitials = 2;
        }
        return name.replaceAll("(?<=\\w)\\w+(?=\\s)\\s+", "").substring(0, noOfInitials);
    }

    /**
     * Adds a star metaphor icon for each favorite {@code person}
     */
    private void initFavorite(ReadOnlyPerson person) {
        if (person.getFavorite().isFavorite()) {
            favoriteImageView.setId("favorite");
        }
    }
    //@@author

    /**
     * Creates a tag label for each {@code Person} and assign a color to the style of each tag label.
     */
    private void initTags(ReadOnlyPerson person) {
        person.getTags().forEach(tag -> {
            Label tagLabel = new Label(tag.tagName);
            tagLabel.setStyle("-fx-background-color: " + getColorForTag(tag.tagName));
            tags.getChildren().add(tagLabel);
        });
    }

    //@@author marvinchin
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
    //@@author

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonCard)) {
            return false;
        }

        // state check
        PersonCard card = (PersonCard) other;
        return id.getText().equals(card.id.getText())
                && person.equals(card.person);
    }
}
