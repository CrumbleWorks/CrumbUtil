package org.crumbleworks.forge.crumbutil.commandline;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.crumbleworks.forge.crumbutil.commandline.CommandlineOptionProcessor.Option;
import org.junit.jupiter.api.Test;

/**
 * @author Michael Stocker
 * @since 0.1.0
 */
@SuppressWarnings("serial")
public class CommandlineOptionProcessorTest {
    //FIXME needs tests for ArgProcessor
    //TODO needs more tests
    
    @Test
    public void testAddingFlag() throws OptionAlreadyExistsException {
        CommandlineOptionProcessor cop = new CommandlineOptionProcessor(false); //first bool doesnt matter here
        
        cop.createFlag("-f", null, null, ()->{});
        cop.createFlag(null, "--gulf", null, ()->{});
        cop.createFlag("-c", "--charlie", null, ()->{});
        cop.createFlag("-z", "--zulu", null, ()->{});
        
        List<Option> options = new ArrayList<Option>(cop.getConfiguredOptions());
        options.sort(Option.OPTION_COMPARATOR);
        
        List<String> optionFlags = new ArrayList<String>();
        optionFlags.add(options.get(0).getShortName());
        optionFlags.add(options.get(1).getShortName());
        optionFlags.add(options.get(2).getLongName());
        optionFlags.add(options.get(3).getLongName());
        
        //zulu before gulf because zulu also has a shortName which is rated higher than long names
        assertEquals(new ArrayList<String>(Arrays.asList(new String[]{"-c", "-f", "--zulu", "--gulf"})), optionFlags); 
    }
    
    @Test
    public void testAddingParam() throws OptionAlreadyExistsException {
        CommandlineOptionProcessor cop = new CommandlineOptionProcessor(false); //first bool doesnt matter here
        
        cop.createFlag("-f", null, null, ()->{});
        cop.createFlag(null, "--gulf", null, ()->{});
        cop.createFlag("-c", "--charlie", null, ()->{});
        cop.createFlag("-z", "--zulu", null, ()->{});
        
        List<Option> options = new ArrayList<Option>(cop.getConfiguredOptions());
        options.sort(Option.OPTION_COMPARATOR);
        
        List<String> optionParameters = new ArrayList<String>();
        optionParameters.add(options.get(0).getShortName());
        optionParameters.add(options.get(1).getShortName());
        optionParameters.add(options.get(2).getLongName());
        optionParameters.add(options.get(3).getLongName());
        
        //zulu before gulf because zulu also has a shortName which is rated higher than long names
        assertEquals(new ArrayList<String>(Arrays.asList(new String[]{"-c", "-f", "--zulu", "--gulf"})), optionParameters);
    }
    
    @Test
    public void testAddingOptionTwice() throws OptionAlreadyExistsException {
        CommandlineOptionProcessor cop = new CommandlineOptionProcessor(false); //first bool doesnt matter here
        
        cop.createParameter("-f", "--flag", null, (s)->{});
        assertThrows(OptionAlreadyExistsException.class, () -> cop.createParameter("-f", "--flag", null, (s)->{}));
    }
    
    @Test
    public void testHelpOutput() throws OptionAlreadyExistsException {
        CommandlineOptionProcessor cop = new CommandlineOptionProcessor(false); //first bool doesnt matter here
        
        cop.createFlag("-f", "--flag", "A random flag", ()->{});
        cop.createParameter("-p",
                            "--amount-of-peanuts",
                            new HashMap<String, String>(){{
                                put("#", "The amount of single peanuts, e.g. 34");
                                put("#b", "The amount of peanuts in bags of 100pc each, e.g. 12b -> 1200 peanuts");
                                }},
                            (s)->{});

        assertEquals("/---------------------------------------------------------------------------\\\nHelp:\n\n-f, --flag\n A random flag\n\n-p, --amount-of-peanuts\n  #\n The amount of single peanuts, e.g. 34\n  #b\n The amount of peanuts in bags of 100pc each, e.g. 12b -> 1200 peanuts\n\\---------------------------------------------------------------------------/\n", cop.getHelp());
    }
    
    @Test
    public void testProcessingInputIgnoringUnknownOptions() throws OptionAlreadyExistsException {
        String[] testInput = {"--beta", "schinken", "-c", "brothot", "-a", "fluroid", "-f"};
        
        String[] resultSet = {"","","",""};
        
        CommandlineOptionProcessor cop = new CommandlineOptionProcessor(false);
        
        cop.createFlag("-a", null, "Decides if the ALPHA param is set.", ()->{resultSet[0] = "ALPHA";});
        cop.createParameter(null,
                "--beta",
                new HashMap<String, String>() {{
                    put("Fleischerzeugnis", "Any meat-produce by its german name.");
                }},
                (s)->{resultSet[1] = s;}
                );
        cop.createParameter("-c",
                null,
                new HashMap<String, String>() {{
                    put("kasiopeia", "Some random string whatsoever.");
                }},
                (s)->{resultSet[2] = s;}
                );
        cop.createFlag("-f", null, "A \"flag\".", ()->{resultSet[3] = "FLAG";});
        
        cop.processInput(testInput);
        
        assertArrayEquals(new String[]{"ALPHA", "schinken", "brothot", "FLAG"}, resultSet);
    }
    
    @Test
    public void testProcessingInputWithUnknownOptions() throws OptionAlreadyExistsException {
        String[] testInput = {"--beta", "schinken", "-c", "brothot", "-a", "fluroid", "-f"};
        
        String[] resultSet = {"","","",""};
        
        CommandlineOptionProcessor cop = new CommandlineOptionProcessor(false);
        
        cop.createFlag("-f", null, "A \"flag\".", ()->{resultSet[3] = "FLAG";});
        cop.createParameter("-c",
                null,
                new HashMap<String, String>() {{
                    put("kasiopeia", "Some random string whatsoever.");
                }},
                (s)->{resultSet[2] = s;}
                );
        cop.createFlag("-a", null, "Decides if the ALPHA param is set.", ()->{resultSet[0] = "ALPHA";});
        
        assertArrayEquals(new String[]{"--beta", "schinken", "fluroid"}, cop.processInput(testInput));
    }
}