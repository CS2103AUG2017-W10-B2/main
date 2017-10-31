package seedu.address.logic.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

//@@author marvinchin
public class OptionBearingArgumentTest {

    @Test
    public void optionBearingArgument_argsWithoutOptions_success() {
        String args = "a/123 some building #01-01 e/hello@world.com";
        OptionBearingArgument opArg = new OptionBearingArgument(args);

        assertEquals(args, opArg.getRawArgs());
        assertEquals(args, opArg.getFilteredArgs());
        assertTrue(opArg.getOptions().isEmpty());
    }

    @Test
    public void optionBearingArgument_argsWithSingleLeadingOption_success() {
        String args = "-opt a/123 some building #01-01 e/hello@world.com";
        OptionBearingArgument opArg = new OptionBearingArgument(args);

        String expectedFilteredArgs = "a/123 some building #01-01 e/hello@world.com";
        Set<String> expectedOptions = new HashSet<>(Arrays.asList("opt"));
        assertEquals(args, opArg.getRawArgs());
        assertEquals(expectedFilteredArgs, opArg.getFilteredArgs());
        assertEquals(expectedOptions, opArg.getOptions());
    }

    @Test
    public void optionBearingArgument_argsWithSingleEmbeddedOption_success() {
        String args = "a/123 some building -opt #01-01 e/hello@world.com";
        OptionBearingArgument opArg = new OptionBearingArgument(args);

        String expectedFilteredArgs = "a/123 some building #01-01 e/hello@world.com";
        Set<String> expectedOptions = new HashSet<>(Arrays.asList("opt"));
        assertEquals(args, opArg.getRawArgs());
        assertEquals(expectedFilteredArgs, opArg.getFilteredArgs());
        assertEquals(expectedOptions, opArg.getOptions());
    }

    @Test
    public void optionBearingArgument_argsWithMultipleOptions_success() {
        String args = "-tag a/123 some building -opt #01-01 e/hello@world.com";
        OptionBearingArgument opArg = new OptionBearingArgument(args);

        String expectedFilteredArgs = "a/123 some building #01-01 e/hello@world.com";
        Set<String> expectedOptions = new HashSet<>(Arrays.asList("opt", "tag"));
        assertEquals(args, opArg.getRawArgs());
        assertEquals(expectedFilteredArgs, opArg.getFilteredArgs());
        assertEquals(expectedOptions, opArg.getOptions());
    }
}
