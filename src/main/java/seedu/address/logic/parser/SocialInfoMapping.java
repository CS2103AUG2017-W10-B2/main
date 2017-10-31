package seedu.address.logic.parser;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.social.SocialInfo;

/**
 * Handles mappings of social related identifiers when parsing SocialInfo
 */
public class SocialInfoMapping {

    private static final int SOCIAL_TYPE_INDEX = 0;
    private static final int SOCIAL_USERNAME_INDEX = 1;

    public static final String FACEBOOK_IDENTIFIER = "facebook";
    private static final String FACEBOOK_IDENTIFIER_ALIAS = "fb";
    public static final String INSTAGRAM_IDENTIFIER = "instagram";
    private static final String INSTAGRAM_IDENTIFIER_ALIAS = "ig";

    private static final String INVALID_SYNTAX_EXCEPTION_MESSAGE = "Invalid syntax for social info";
    private static final String UNRECOGNIZED_SOCIAL_TYPE_MESSAGE = "Unrecognized social type.\n"
            + "Currently supported platforms: "
            + FACEBOOK_IDENTIFIER + "(aliases: " + FACEBOOK_IDENTIFIER_ALIAS + "), "
            + INSTAGRAM_IDENTIFIER + "(aliases: " + INSTAGRAM_IDENTIFIER_ALIAS + ")\n";


    /**
     * Returns the SocialInfo object represented by the input String.
     * @throws IllegalValueException if the input does not represent a valid SocialInfo recognized
     * by the defined mappings
     */
    public static SocialInfo parseSocialInfo(String rawSocialInfo) throws IllegalValueException {
        String[] splitRawSocialInfo = rawSocialInfo.split(" ", 2);
        if (splitRawSocialInfo.length != 2) {
            throw new IllegalValueException(INVALID_SYNTAX_EXCEPTION_MESSAGE);
        }

        if (isFacebookInfo(splitRawSocialInfo)) {
            return buildFacebookInfo(splitRawSocialInfo);
        } else if (isInstagramInfo(splitRawSocialInfo)) {
            return buildInstagramInfo(splitRawSocialInfo);
        } else {
            throw new IllegalValueException(UNRECOGNIZED_SOCIAL_TYPE_MESSAGE);
        }

    }

    private static boolean isFacebookInfo(String[] splitRawSocialInfo) {
        String trimmedSocialType = splitRawSocialInfo[SOCIAL_TYPE_INDEX].trim();
        return trimmedSocialType.equals(FACEBOOK_IDENTIFIER) || trimmedSocialType.equals(FACEBOOK_IDENTIFIER_ALIAS);
    }

    private static SocialInfo buildFacebookInfo(String[] splitRawSocialInfo) {
        String trimmedSocialUsername = splitRawSocialInfo[SOCIAL_USERNAME_INDEX].trim();
        String socialUrl = "https://facebook.com/" + trimmedSocialUsername;
        return new SocialInfo(FACEBOOK_IDENTIFIER, trimmedSocialUsername, socialUrl);
    }

    private static boolean isInstagramInfo(String[] splitRawSocialInfo) {
        String trimmedSocialType = splitRawSocialInfo[SOCIAL_TYPE_INDEX].trim();
        return trimmedSocialType.equals(INSTAGRAM_IDENTIFIER) || trimmedSocialType.equals(INSTAGRAM_IDENTIFIER_ALIAS);
    }

    private static SocialInfo buildInstagramInfo(String[] splitRawSocialInfo) {
        String trimmedSocialUsername = splitRawSocialInfo[SOCIAL_USERNAME_INDEX].trim();
        String socialUrl = "https://instagram.com/" + trimmedSocialUsername;
        return new SocialInfo(INSTAGRAM_IDENTIFIER, trimmedSocialUsername, socialUrl);
    }
}
