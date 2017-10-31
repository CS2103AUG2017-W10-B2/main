package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_OPTION;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

//@@author marvinchin
/**
 * The OptionBearingArgument class encapsulates an argument that contains options, and handles the parsing and filtering
 * of these options from the argument.
 */
public class OptionBearingArgument {

    private String rawArgs;
    private Set<String> optionsList;
    private String filteredArgs;

    /**
     * Constructs an OptionBearingArgument for the input argument string.
     */
    public OptionBearingArgument(String args) {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        rawArgs = trimmedArgs;
        parse(rawArgs);
    }

    /**
     * Parses the string to get the list of options, and a filtered argument string with the options removed.
     */
    private void parse(String args) {
        String[] splitArgs = args.split("\\s+");
        optionsList = Arrays.stream(splitArgs)
                .filter(arg -> arg.startsWith(PREFIX_OPTION.getPrefix()))
                .map(optionArg -> optionArg.substring(PREFIX_OPTION.getPrefix().length())) // drop the leading prefix
                .collect(Collectors.toCollection(HashSet::new));

        filteredArgs = Arrays.stream(splitArgs)
                .filter(arg -> !arg.startsWith(PREFIX_OPTION.getPrefix()))
                .collect(Collectors.joining(" "));
    }

    public String getRawArgs() {
        return rawArgs;
    }

    public Set<String> getOptions() {
        return optionsList;
    }

    public String getFilteredArgs() {
        return filteredArgs;
    }

}
