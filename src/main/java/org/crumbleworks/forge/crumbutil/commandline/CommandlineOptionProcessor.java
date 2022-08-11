package org.crumbleworks.forge.crumbutil.commandline;

import static org.crumbleworks.forge.crumbutil.validation.Parameters.notNull;
import static org.crumbleworks.forge.crumbutil.validation.Parameters.verifyAgainstRegex;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import org.crumbleworks.forge.crumbutil.callbacks.Callback;
import org.crumbleworks.forge.crumbutil.callbacks.ParameterizedCallback;
import org.crumbleworks.forge.crumbutil.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//FIXME the whole thing needs a drastic makeover
/**
 * Processes commandline input according to a set of rules passed in
 * 
 * @author Michael Stocker
 * @since 0.1.0
 */
@SuppressWarnings("serial") //suppressing warnings for no serial numbers in internally used hashsets..
public final class CommandlineOptionProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CommandlineOptionProcessor.class);
    
    private static final String optionRegex = "--?[A-Za-z0-9]+(?:-[A-Za-z0-9]+)*";
    private static final String shortNameRegex = "^-[A-Za-z0-9]+$";
    private static final String longNameRegex = "^-(?:-[A-Za-z0-9]+)+$";
    
    private final String argDescription;
    private final ParameterizedCallback<Queue<String>> argProcessor;
    
    private final Set<Option> configuredOptions;
    
    //these are filled as a reaction to checks on the above Set
    private final Map<String, Callback> configuredFlags;
    private final Map<String, ParameterizedCallback<String>> configuredParameters;
    
    /**
     * Creates a CommandLineProcessor that gets passed in all arguments occuring before the first <code>flag</code> or <code>option</code>.
     * 
     * @param argDescription a description of arguments that get processed, optional
     * @param argProcessor the code that processes the first arguments
     * @param generateHelp generates a help that can be queried with the <i>-h</i> and <i>--help</i> options
     */
    public CommandlineOptionProcessor(final String argDescription, final ParameterizedCallback<Queue<String>> argProcessor, final boolean generateHelp) {
        configuredOptions = new TreeSet<Option>(Option.OPTION_COMPARATOR); //we need to use a TreeSet because the HashSet#contains(Object o) method is a stinky, lying, little bastard
        
        configuredFlags = new HashMap<String, Callback>();
        configuredParameters = new HashMap<String, ParameterizedCallback<String>>();
        
        this.argDescription = argDescription;
        this.argProcessor = argProcessor;
        
        if(generateHelp) {
            try {
                createFlag("-h", "--help", "Generates a help output describing all possible options.", ()->{
                    System.out.println(getHelp());
                    System.exit(1); //exit is okay because we generated a help output, but program did not finish
                    });
            } catch(OptionAlreadyExistsException e) {
                //drop exception. It cannot possibly occur at this time unless someone is magically modifying this object during creation which would be a whole hell of another problem..
            }
        }
    }
    
    public CommandlineOptionProcessor(final boolean generateHelp) {
        this(null, null, generateHelp);
    }
    
    /**
     * Processes a list of inputs according to the {@link Option Options} defined before calling this.
     * 
     * @param args an array of Strings representing the calling arguments
     * 
     * @return a String[] containing all unprocessed arguments
     */
    public final String[] processInput(final String[] args) {
        if(args.length <= 0) { //empty array
            return new String[0];
        }
        
        Queue<String> inputStack = new LinkedList<String>() {{
            Arrays.asList(args).stream().forEach((s)->{add(s);});
        }};
        
        if(argProcessor != null && !inputStack.peek().matches(optionRegex)) {
            logger.debug("Preparing arguments for argProcessor");
            Queue<String> argProcessorStack = new LinkedList<String>();
            while(inputStack.size() > 0 && !inputStack.peek().matches(optionRegex)) {
                logger.trace("> Adding '{}'", inputStack.peek());
                argProcessorStack.add(inputStack.remove()); //shove them onto the other stack
            }
            logger.debug("Calling callback for argProcessor");
            argProcessor.call(argProcessorStack);
        }
        
        List<String> unprocessedArgs = new ArrayList<String>();
        
        while(!inputStack.isEmpty()) {
            String currentElement = inputStack.remove();
            
            if(currentElement.matches(optionRegex)) {
                if(configuredFlags.containsKey(currentElement)) {
                    logger.debug("Calling callback for '{}' flag.", currentElement);
                    configuredFlags.get(currentElement).call();
                } else if(configuredParameters.containsKey(currentElement)) {
                    String value = inputStack.remove();
                    logger.debug("Calling callback for '{}' param with '{}'.", currentElement, value);
                    configuredParameters.get(currentElement).call(value); //next element must be an argument - we can't check if it's valid ourselves
                } else {
                    //we add the unprocessed option to the returned list of unprocessed arguments
                    unprocessedArgs.add(currentElement);
                }
            } else {
                //we add the unprocessed whatever to the returned list of unprocessed arguments
                unprocessedArgs.add(currentElement);
            }
        }
        
        return unprocessedArgs.toArray(new String[unprocessedArgs.size()]);
    }
    
    /**
     * Creates a <i>flag</i>; a simple true/false {@link Option}.
     * <p>You need to define at least either of short- or longName and the callback. The rest is optional.
     * 
     * @param shortName (either this or longName)
     * @param longName (either this or shortName)
     * @param description (optional)
     * @param callback the {@link Callback} to be called when short- or longName are processed
     * 
     * @throws OptionAlreadyExistsException if short- or longName have been defined for another {@link Option} already
     */
    public final void createFlag(final String shortName, final String longName, final String description, final Callback callback) throws OptionAlreadyExistsException {
        Option flag = new Option(shortName,
                                 longName,
                                 description != null
                                 ? new ArrayList<Option.ValueDescriptionPair>() {{
                                       add(new Option.ValueDescriptionPair(null, description));
                                       }}
                                 : null
                                );
        
        if(configuredOptions.contains(flag)) {
            throw new OptionAlreadyExistsException();
        } else {
            configuredOptions.add(flag);
            
            if(StringUtil.neitherNullNorEmpty(flag.getShortName())) {
                configuredFlags.put(flag.getShortName(), notNull(callback));
            }
            if(StringUtil.neitherNullNorEmpty(flag.getLongName())) {
                configuredFlags.put(flag.getLongName(), notNull(callback));
            }
        }
    }
    
   /**
    * Creates a <i>parameter</i>; an {@link Option} that expects a value.
    * <p>You need to define at least either of short- or longName and the callback. The rest is optional.
    * 
    * @param shortName (either this or longName)
    * @param longName (either this or shortName)
    * @param valueDescriptionPairs a {@link Map Map&lt;String, String&gt;} of possible values and (their) descriptions; you can pass a <code>null</code>/"" value if you only want to add a description, likely you can pass <code>null</code>/"" descriptions
    * @param callback the {@link Callback} to be called when short- or longName are processed
    * 
    * @throws OptionAlreadyExistsException if short- or longName have been defined for another {@link Option} already
    */
    public final void createParameter(final String shortName, final String longName, final Map<String, String> valueDescriptionPairs, final ParameterizedCallback<String> callback) throws OptionAlreadyExistsException {
        Option parameter = new Option(shortName,
                                      longName,
                                      valueDescriptionPairs != null
                                          ? new ArrayList<Option.ValueDescriptionPair>() {{
                                              valueDescriptionPairs.entrySet().stream()
                                              .forEach((e) -> {add(new Option.ValueDescriptionPair(e.getKey(), e.getValue()));});
                                              }}
                                          : null
                                     );
        
        if(configuredOptions.contains(parameter)) {
            throw new OptionAlreadyExistsException();
        } else {
            configuredOptions.add(parameter);
            
            if(StringUtil.neitherNullNorEmpty(parameter.getShortName())) {
                configuredParameters.put(parameter.getShortName(), notNull(callback));
            }
            if(StringUtil.neitherNullNorEmpty(parameter.getLongName())) {
                configuredParameters.put(parameter.getLongName(), notNull(callback));
            }
        }
    }
    
    /* ************************************************************************
     * INFORMATIVE
     */
    
    /**
     * @return an {@link Collections#unmodifiableSet(Set) unmodifiable set} of all configured options
     */
    public final Set<Option> getConfiguredOptions() {
        return Collections.unmodifiableSet(configuredOptions);
    }

    //FIXME representation of options needs to be better, maybe more empty lines and some dashed-line as a divider or such
    /**
     * Creates and returns a string which lists all configured options and whatever additional information was defined with them in the following format:
     * <pre>
     * -short, --long-form
     *   value
     *  description
     *   another value
     *  another description
     *  
     *  .
     *  .
     *  .
     * </pre>
     */
    public final String getHelp() {
        StringBuilder result = new StringBuilder()
                .append("/---------------------------------------------------------------------------\\\n")
                .append("Help:\n");

        if(StringUtil.neitherNullNorEmpty(argDescription)){
            result.append(argDescription).append("\n\n"); //appending w/ double linebreak
        }
        
        Iterator<Option> options = new ArrayList<Option>(getConfiguredOptions()).iterator();
        
        if(options.hasNext()) {
            if(StringUtil.neitherNullNorEmpty(argDescription)) {
                result.append("Options:"); //don't need the header if there's nothing coming before the options
            }
            while(options.hasNext()) {
                result.append("\n").append(processOptionIntoHelpRepresentation(options.next()));
            }
        }
        
        result.append("\\---------------------------------------------------------------------------/\n");
        return result.toString();
    }
    
    private final String processOptionIntoHelpRepresentation(final Option option) {
        StringBuilder sb = new StringBuilder();
        
        String tmp = "";
        
        sb.append(option.getShortName()); //append shortName
        if(sb.length() > 0 && (tmp = (", " + option.getLongName())).length() > 2) {
            sb.append(tmp); //appending longName in addition
        } else {
            sb.append(option.getLongName()); //appending longName as the only one
        }
        sb.append("\n");
        
        if(option.getValueDescriptionPairs() != null) {
            for(Option.ValueDescriptionPair valDescPair : option.getValueDescriptionPairs()) {
                if((tmp = ("  " + valDescPair.getValue())).length() > 2) {
                    sb.append(tmp).append("\n");
                }
                if((tmp = (" " + valDescPair.getDescription())).length() > 1) {
                    sb.append(tmp).append("\n");
                }
            }
        }
        
        return sb.toString();
    }
    
    /* ************************************************************************
     * CLASSES
     */

    /**
     * Represents a single option of a {@link CommandlineOptionProcessor}
     * <p>An option is considered equal if shortName and/or longName of the compared options match.
     * <p>e.g.
     * <table>
     *     <caption>A table of examples for matching &amp; non-matching Options</caption>
     *     <thead>
     *         <tr>
     *             <th colspan="2">Option1</th><td>&lt;-&gt;</td><th colspan="2">Option2</th><td>--&gt;</td><th>Match?</th>
     *         </tr>
     *     </thead>
     *     <tbody>
     *         <tr>
     *             <td>-a</td><td>--alpha</td><td></td><td>-a</td><td>--alpha</td><td></td><td>true</td>
     *         </tr>
     *         <tr>
     *             <td>-a</td><td>--alpha</td><td></td><td>-a</td><td></td><td></td><td>true</td>
     *         </tr>
     *         <tr>
     *             <td>-a</td><td>--alpha</td><td></td><td></td><td>--alpha</td><td></td><td>true</td>
     *         </tr>
     *         <tr>
     *             <td>-a</td><td>--alpha</td><td></td><td>-b</td><td>--alpha</td><td></td><td>true</td>
     *         </tr>
     *         <tr>
     *             <td>-a</td><td>--alpha</td><td></td><td>-a</td><td>--beta</td><td></td><td>true</td>
     *         </tr>
     *         <tr>
     *             <td>-a</td><td></td><td></td><td></td><td>--alpha</td><td></td><td>false</td>
     *         </tr>
     *         <tr>
     *             <td>-a</td><td>--alpha</td><td></td><td>-b</td><td>--beta</td><td></td><td>false</td>
     *         </tr>
     *     </tbody>
     * </table>
     * 
     * <p>Glossary:
     * <table>
     *     <caption>A table of terms used in describing Options.</caption>
     *     <thead>
     *         <tr>
     *             <th>Term</th><th>Description</th><th>Example</th>
     *         </tr>
     *     </thead>
     *     <tbody>
     *         <tr>
     *             <td>shortName</td><td>an identifier preceded by a single dash, usually consisting of one or at most two letters</td><td><i>-a</i>, <i>-hw</i></td>
     *         </tr>
     *         <tr>
     *             <td>longName</td><td>an identifier preceded by a double dash, usually a word or multiple words separated by single dashes</td><td><i>--automated</i>, <i>--hello-world</i></td>
     *         </tr>
     *     </tbody>
     * </table>
     */
    public final static class Option {

        /**
         * Returns an instance of {@link OptionComparator}
         */
        public static final Comparator<Option> OPTION_COMPARATOR = new OptionComparator();

        private final String shortName;
        private final String longName;
        private final List<ValueDescriptionPair> valueDescriptionPairs;
        
        /**
         * Creates an Option; <code>null</code>-strings are converted to empty-strings ("").
         * <p>- shortName must be preceded by a single dash '-'
         * <p>- longName must be preceded by a double dash '--'
         * <p><b>Attention</b>: due to the way this class is designed and used, the {@link #hashCode()} method cannot be implemented and thus does not match the {@link #equals(Object)} method and essentially makes this class unusable with hash-based jdk collections as they are lying bastards pretending to use equals methods when they use hashcode
         * 
         * @param shortName
         * @param longName
         * @param valueDescriptionPairs
         * 
         * @throws IllegalArgumentException if names don't adhere to the rules
         * @throws IllegalArgumentException if neither short- nor longName has been set
         */
        private Option(final String shortName, final String longName, final List<ValueDescriptionPair> valueDescriptionPairs) {
            boolean shortSet = false, longSet = false;
            
            if(StringUtil.neitherNullNorEmpty(shortName)) {
                this.shortName = verifyAgainstRegex(shortName, shortNameRegex);
                shortSet = true;
            } else {
                this.shortName = "";
            }
            
            if(StringUtil.neitherNullNorEmpty(longName)) {
                this.longName = verifyAgainstRegex(longName, longNameRegex);
                longSet = true;
            } else {
                this.longName = "";
            }
            
            this.valueDescriptionPairs = valueDescriptionPairs;
            
            
            if(!shortSet && !longSet) {
                throw new IllegalArgumentException("At least one of 'shortName' or 'longName' needs to be specified");
            }
        }
        
        public final String getShortName() {
            return shortName;
        }
        
        public final String getLongName() {
            return longName;
        }
        
        public final List<ValueDescriptionPair> getValueDescriptionPairs() {
            return valueDescriptionPairs;
        }

        @Override
        public final boolean equals(final Object obj) {
            if(this == obj) {
                return true;
            }
            
            if(obj == null) {
                return false;
            }
            
            if(getClass() != obj.getClass()) {
                return false;
            }
            //END of standard comparisons; START of custom stuff
            
            Option other = (Option)obj;
            
            if(longName != null && longName.equals(other.longName)) {
                return true;
            }
            
            if(shortName != null && shortName.equals(other.shortName)) {
                return true;
            }
            
            return false;
        }
        
        @Override
        public final String toString() {
            StringBuilder sb = new StringBuilder()
                    .append(shortName).append(':').append(longName)
                    .append('-');
            
            Iterator<ValueDescriptionPair> valDescIterator = valueDescriptionPairs.iterator();
            if(valDescIterator.hasNext()) {
                ValueDescriptionPair current = valDescIterator.next();
                
                sb.append(current.value).append('/').append(current.description);
                while(valDescIterator.hasNext()) {
                    current = valDescIterator.next();
                    
                    sb.append(':')
                    .append(current.value).append('/').append(current.description);
                }
            }
            
            return sb.toString();
        }
        
        /* ********************************************************************
         * CLASSES (In classes that are in classes, oh-my-nested-god)
         */
        
        /**
         * A POJO to hold a value and its description
         * 
         * @author Michael Stocker
         * @since 0.1.0
         */
        private static final class ValueDescriptionPair {
            private final String value;
            private final String description;
            
            private ValueDescriptionPair(final String value, final String description) {
                boolean valueSet = false, descriptionSet = false;
                
                if(StringUtil.neitherNullNorEmpty(value)) {
                    this.value = value;
                    valueSet = true;
                } else {
                    this.value = "";
                }
                
                if(StringUtil.neitherNullNorEmpty(description)) {
                    this.description = description;
                    descriptionSet = true;
                } else {
                    this.description = "";
                }
                
                if(!valueSet && !descriptionSet) {
                    throw new IllegalArgumentException("At least one of 'value' or 'description' needs to be specified");
                }
            }
            
            public final String getValue() {
                return value;
            }
            
            public final String getDescription() {
                return description;
            }
        }
        
        /**
         * Compares options and orders them priorizing shortNames over longNames & according to {@link Locale#ENGLISH}
         * 
         * @author Michael Stocker
         * @since 0.1.0
         */
        private static final class OptionComparator implements Comparator<Option> {
            static final Collator collator = Collator.getInstance(Locale.ENGLISH);
            static{
                collator.setStrength(Collator.IDENTICAL);
            }
            
            public final int compare(final Option o1, final Option o2) {
                String o1Name = null, o2Name = null;
                boolean isO1Short = false, isO2Short = false;
                
                //preparations
                if(StringUtil.neitherNullNorEmpty(o1.getShortName())) {
                    o1Name = o1.getShortName();
                    isO1Short = true;
                } else {
                    o1Name = o1.getLongName();
                }
                
                if(StringUtil.neitherNullNorEmpty(o2.getShortName())) {
                    o2Name = o2.getShortName();
                    isO2Short = true;
                } else {
                    o1Name = o2.getLongName();
                }
                
                //actual comparison
                if(isO1Short && !isO2Short) {
                    return -1;
                } else if(!isO1Short && isO2Short) {
                    return 1;
                }
                
                return collator.compare(o1Name, o2Name);
            }
        }
    }
}
