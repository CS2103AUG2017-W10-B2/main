package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DISPLAY_PHOTO;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FAV;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SOCIAL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_UNFAV;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.social.SocialInfo;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL,
                PREFIX_ADDRESS, PREFIX_FAV, PREFIX_DISPLAY_PHOTO, PREFIX_UNFAV, PREFIX_TAG, PREFIX_SOCIAL);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        try {
            ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME)).ifPresent(editPersonDescriptor::setName);
            ParserUtil.parsePhone(argMultimap.getMultipleValues(
                    PREFIX_PHONE)).ifPresent(editPersonDescriptor::setPhone);
            ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL)).ifPresent(editPersonDescriptor::setEmail);
            ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS)).ifPresent(editPersonDescriptor::setAddress);
            ParserUtil.parseFavorite(argMultimap,
                    PREFIX_FAV,
                    PREFIX_UNFAV).ifPresent(editPersonDescriptor::setFavorite);
            ParserUtil.parseDisplayPhoto(
                    argMultimap.getValue(PREFIX_DISPLAY_PHOTO)).ifPresent(editPersonDescriptor::setDisplayPhoto);
            parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG)).ifPresent(editPersonDescriptor::setTags);
            parseSocialInfosForEdit(argMultimap.getAllValues(PREFIX_SOCIAL))
                    .ifPresent(editPersonDescriptor::setSocialInfos);

        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        }

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editPersonDescriptor);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contain only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero tags.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags) throws IllegalValueException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(ParserUtil.parseTags(getCollectionToParse(tags)));
    }

    //@@author marvinchin
    /**
     * Parses {@code Collection<String> socialInfos} into a {@code Set<SocialInfo>} if {@code tags} is non-empty.
     * If {@code socialInfos} contain only one element which is an empty string, it will be parsed into a
     * {@code Set<SocialInfo>} containing zero elements.
     */
    private Optional<Set<SocialInfo>> parseSocialInfosForEdit(Collection<String> socialInfos)
            throws IllegalValueException {
        assert socialInfos != null;

        if (socialInfos.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(ParserUtil.parseSocialInfos(getCollectionToParse(socialInfos)));
    }

    /**
     * Checks if the input collection represents an empty collection. Returns an empty collection if it is.
     */
    private Collection<String> getCollectionToParse(Collection<String> collection) {
        return collection.size() == 1 && collection.contains("")
                ? Collections.emptySet()
                : collection;
    }
    //@@author

}
