package seedu.address.storage;

import javax.xml.bind.annotation.XmlElement;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.social.SocialInfo;

//@@author marvinchin
/**
 * JAXB-friendly adapted version of the SocialInfo.
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
     * Converts a given SocialInfo into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created
     */
    public XmlAdaptedSocialInfo(SocialInfo source) {
        socialType = source.getSocialType();
        username = source.getUsername();
        socialUrl = source.getSocialUrl();
    }

    /**
     * Converts this jaxb-friendly adapted social info object into the model's SocialInfo object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person
     */
    public SocialInfo toModelType() throws IllegalValueException {
        return new SocialInfo(socialType, username, socialUrl);
    }

}
