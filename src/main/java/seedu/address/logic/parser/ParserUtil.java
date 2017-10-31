package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.SocialInfoMapping.parseSocialInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Favorite;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.social.SocialInfo;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 * {@code ParserUtil} contains methods that take in {@code Optional} as parameters. However, it goes against Java's
 * convention (see https://stackoverflow.com/a/39005452) as {@code Optional} should only be used a return type.
 * Justification: The methods in concern receive {@code Optional} return values from other methods as parameters and
 * return {@code Optional} values based on whether the parameters were present. Therefore, it is redundant to unwrap the
 * initial {@code Optional} before passing to {@code ParserUtil} as a parameter and then re-wrap it into an
 * {@code Optional} return value inside {@code ParserUtil} methods.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index is not a non-zero unsigned integer.";
    public static final String MESSAGE_INSUFFICIENT_PARTS = "Number of parts must be more than 1.";
    public static final String MESSAGE_INVALID_SOCIAL_TYPE = "Social type is not valid. Social type"
            + " should be facebook or instagram.";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws IllegalValueException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws IllegalValueException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new IllegalValueException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses {@code args} into an {@code List<Index>} and returns it.
     * Used for commands that need to parse multiple indexes
     * @throws IllegalValueException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static List<Index> parseMultipleIndexes(String args) throws IllegalValueException {
        // Example of proper args: " 1 2 3" (has a space in front) -> Hence apply trim() first then split
        List<String> argsList = Arrays.asList(args.trim().split("\\s+")); // split by one or more whitespaces
        List<Index> indexList = new ArrayList<>();

        for (String index : argsList) {
            indexList.add(parseIndex(index)); // Add each valid index into indexList
        }
        return indexList;
    }

    /**
     * Parses a {@code Optional<String> name} into an {@code Optional<Name>} if {@code name} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<Name> parseName(Optional<String> name) throws IllegalValueException {
        requireNonNull(name);
        return name.isPresent() ? Optional.of(new Name(name.get())) : Optional.empty();
    }

    /**
     * Parses a {@code Optional<String> phone} into an {@code Optional<Phone>} if {@code phone} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<Phone> parsePhone(Optional<String> phone) throws IllegalValueException {
        requireNonNull(phone);
        return phone.isPresent() ? Optional.of(new Phone(phone.get())) : Optional.empty();
    }

    /**
     * Parses a {@code Optional<String> address} into an {@code Optional<Address>} if {@code address} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<Address> parseAddress(Optional<String> address) throws IllegalValueException {
        requireNonNull(address);
        return address.isPresent() ? Optional.of(new Address(address.get())) : Optional.empty();
    }

    /**
     * Parses a {@code Optional<String> email} into an {@code Optional<Email>} if {@code email} is present.
     * See header comment of this class regarding the use of {@code Optional} parameters.
     */
    public static Optional<Email> parseEmail(Optional<String> email) throws IllegalValueException {
        requireNonNull(email);
        return email.isPresent() ? Optional.of(new Email(email.get())) : Optional.empty();
    }

    /**
     * Checks if favorite and unfavorite prefixes are present in {@code ArgumentMultimap argMultimap}
     * Catered for both AddCommandParser and EditCommandParser usage
     */
    public static Optional<Favorite> parseFavorite(ArgumentMultimap argMultimap,
                                         Prefix prefixFav,
                                         Prefix prefixUnFav) throws ParseException {

        // Disallow both f/ and uf/ to be present in the same instance of user input when editing
        if (argMultimap.isPrefixPresent(prefixFav) && argMultimap.isPrefixPresent(prefixUnFav)) {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                    EditCommand.MESSAGE_USAGE));
        } else if (argMultimap.isPrefixPresent(prefixFav)) { // Allow favoriting simply by supplying prefix
            if (!argMultimap.getValue(prefixFav).get().isEmpty()) { // Disallow text after prefix
                throw new ParseException(Favorite.MESSAGE_FAVORITE_CONSTRAINTS);
            } else {
                return Optional.of(new Favorite(true));
            }
        } else if (argMultimap.isPrefixPresent(prefixUnFav)) { // Allow unfavoriting simply by supplying prefix
            if (!argMultimap.getValue(prefixUnFav).get().isEmpty()) { // Disallow text after prefix
                throw new ParseException(Favorite.MESSAGE_FAVORITE_CONSTRAINTS);
            } else {
                return Optional.of(new Favorite(false));
            }
        } else {
            return Optional.empty();
        }
    }

    //@@author sarahnzx
    /**
     * Checks if the specified social type is valid.
     */
    public static Optional<String> parseSelect(String arg) throws IllegalValueException {
        requireNonNull(arg);
        if (!("facebook".equals(arg) || "instagram".equals(arg))) {
            throw new IllegalValueException(MESSAGE_INVALID_SOCIAL_TYPE);
        }

        return Optional.of(arg);
    }
    //@@author

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws IllegalValueException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(new Tag(tagName));
        }
        return tagSet;
    }

    /**
     * Parses {@code Collection<String> rawSocialInfos} into {@code Set<SocialInfo}.
     * @param rawSocialInfos
     * @return
     */
    public static Set<SocialInfo> parseSocialInfos(Collection<String> rawSocialInfos) throws IllegalValueException {
        requireNonNull(rawSocialInfos);
        final Set<SocialInfo> socialInfoSet = new HashSet<>();
        for (String rawSocialInfo : rawSocialInfos) {
            socialInfoSet.add(parseSocialInfo(rawSocialInfo));
        }
        return socialInfoSet;
    }
}
