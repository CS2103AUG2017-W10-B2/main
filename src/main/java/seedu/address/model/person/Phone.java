package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import seedu.address.commons.exceptions.IllegalValueException;

/**
 * Represents a Person's phone number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {
    public static final String MESSAGE_PHONE_CONSTRAINTS =
            "Phone numbers can only contain numbers, and should be at least 3 digits long";
    public static final String PHONE_VALIDATION_REGEX = "\\d{3,}";
    public final String value;
    //@@author sarahnzx
    public final List<String> phonelist;

    //@@author alexfoodw
    /**
     * Constructs a blank phone field
     */
    public Phone() {
        this.value = " ";
        this.phonelist = null;
    }
    //@@author

    /**
     * Validates given phone number.
     *
     * @throws IllegalValueException if given phone string is invalid.
     */
    public Phone(String phone) throws IllegalValueException {
        requireNonNull(phone);
        String[] numbers = phone.split("\n");

        List<String> phones = new ArrayList<>();
        boolean invalid = false;
        String phoneStr = "";

        for (int i = 0; i < numbers.length; i++) {
            String trimmedPhone = numbers[i].trim();
            if (isValidPhone(trimmedPhone)) {
                phones.add(numbers[i]);
                phoneStr += numbers[i] + "\n";
            } else {
                invalid = true;
            }
        }

        if (phones.isEmpty() || invalid) {
            throw new IllegalValueException(MESSAGE_PHONE_CONSTRAINTS);
        }

        // remove the last newline
        phoneStr = phoneStr.substring(0, phoneStr.length() - 1);

        this.phonelist = phones;
        this.value = phoneStr;
    }
    //@@author

    /**
     * Returns true if a given string is a valid person phone number.
     */
    public static boolean isValidPhone(String test) {
        //@@author alexfoodw
        // allow blank phone number
        if (test.isEmpty()) {
            return true;
        }
        //@@author
        return test.matches(PHONE_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Phone // instanceof handles nulls
                && this.value.equals(((Phone) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
