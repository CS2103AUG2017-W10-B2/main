package seedu.address.model.social;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

//@@author marvinchin
/**
 * Represents information about a {@code Person}'s social media account.
 * Guarantees immutability.
 */
public class SocialInfo {

    private final String username;
    private final String socialType;
    private final String socialUrl;

    public SocialInfo(String socialType, String username, String socialUrl) {
        requireAllNonNull(username, socialType, socialUrl);
        String trimmedUsername = username.trim();
        String trimmedSocialType = socialType.trim();
        String trimmedSocialUrl = socialUrl.trim();
        this.username = trimmedUsername;
        this.socialType = trimmedSocialType;
        this.socialUrl = trimmedSocialUrl;
    }

    /**
     * Returns the username for the represented account.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Returns the platform of this social media information.
     */
    public String getSocialType() {
        return this.socialType;
    }

    /**
     * Returns the link to the social media feed for the represented account.
     */
    public String getSocialUrl() {
        return this.socialUrl;
    }

    /**
     * Formats state as text for viewing.
     */
    public String toString() {
        return "["
            + "Type: " + getSocialType() + ", "
            + "Username: " + getUsername() + ", "
            + "Link: " + getSocialUrl() + "]";
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof SocialInfo
                && this.getUsername().equals(((SocialInfo) other).getUsername())
                && this.getSocialType().equals(((SocialInfo) other).getSocialType())
                && this.getSocialUrl().equals(((SocialInfo) other).getSocialUrl()));

    }
}
