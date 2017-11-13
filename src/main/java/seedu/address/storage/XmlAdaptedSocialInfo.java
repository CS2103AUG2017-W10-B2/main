package seedu.address.storage;

import javax.xml.bind.annotation.XmlElement;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.social.SocialInfo;

//@@author marvinchin
/**
 * JAXB-friendly adapted version of the {@code SocialInfo}.
 */
public class XmlAdaptedSocialInfo {

    @XmlElement(required = true)
    private String username;
    @XmlElement(required = true)
    private String socialType;
    @XmlElement(required = true)
    private String socialUrl;

    /**
     * Constructs an XmlAdaptedSocialInfo.
     * This is the no-arg constructor that is required by JAXB.
     */
    public XmlAdaptedSocialInfo() {}

    /**
     * Converts a given {@code SocialInfo} into this class for JAXB use.
     * @param source future changes to this will not affect the created
     */
    public XmlAdaptedSocialInfo(SocialInfo source) {
        socialType = source.getSocialType();
        username = source.getUsername();
        socialUrl = source.getSocialUrl();
    }

    /**
     * Converts this JAXB-friendly adapted {@code SocialInfo} object into the model's {@code SocialInfo} object.
     * @throws IllegalValueException if there were any data constraints violated in the adapted {@code SocialInfo}
     */
    public SocialInfo toModelType() throws IllegalValueException {
        return new SocialInfo(socialType, username, socialUrl);
    }

}
