
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

public class Args1 {

    private String schema;
    //    1. args array into a list and pass an iterator down to the set
    //private String[] args;
    private boolean valid = true;
    private Set<Character> unexpectedArguments = new TreeSet<Character>();
    private Map<Character, ArgumentMarshaler> marshalers = new HashMap<Character, ArgumentMarshaler>();
    private Set<Character> argsFound = new HashSet<Character>();
    //    1. args array into a list and pass an iterator down to the set - add Iterator for currentArgument
    private Iterator<String> currentArgument;
    private char errorArgumentId = '\0';
    private String errorParameter = "TILT";
    private ErrorCode errorCode = ErrorCode.OK;
    //    1. args array into a list and pass an iterator down to the set - add argsLst
    private List<String> argsList;

    private enum ErrorCode {
        OK,
        MISSING_STRING,
        MISSING_INTEGER,
        INVALID_INTEGER,
        UNEXPECTED_ARGUMENT
    }

    public Args1(String schema, String[] args) throws ParseException {
        this.schema = schema;
        //    2.Fix the broken code
        //    this.args = args;
        argsList = Arrays.asList(args);
        valid = parse();
    }

    private boolean parse() throws ParseException {
        //    2. Fix the broken code
        //    if (schema.length() == 0 && args.length == 0)
        if (schema.length() == 0 && argsList.size() == 0)
            return true;
        parseSchema();
        try {
            parseArguments();
        } catch (ArgsException e) {
        }
        return valid;
    }

    private boolean parseSchema() throws ParseException {
        for (String element : schema.split(",")) {
            if (element.length() > 0) {
                String trimmedElement = element.trim();
                parseSchemaElement(trimmedElement);
            }
        }
        return true;
    }

    private void parseSchemaElement(String element) throws ParseException {
        char elementId = element.charAt(0);
        String elementTail = element.substring(1);
        validateSchemaElementId(elementId);
        
        if (isBooleanSchemaElement(elementTail))
            marshalers.put(elementId, new BooleanArgumentMarshaler());
        else if (isStringSchemaElement(elementTail))
            marshalers.put(elementId, new StringArgumentMarshaler());
        else if (isIntegerSchemaElement(elementTail)) {
            marshalers.put(elementId, new IntegerArgumentMarshaler());
        } else {
            throw new ParseException(String.format("Argument: %c has invalid format: %s.", elementId, elementTail), 0);
        }
    }

    private void validateSchemaElementId(char elementId) throws ParseException {
        if (!Character.isLetter(elementId)) {
            throw new ParseException("Bad character:" + elementId + "in Args format: " + schema, 0);
        }
    }

    private boolean isStringSchemaElement(String elementTail) {
        return elementTail.equals("*");
    }

    private boolean isBooleanSchemaElement(String elementTail) {
        return elementTail.length() == 0;
    }

    private boolean isIntegerSchemaElement(String elementTail) {
        return elementTail.equals("#");
    }

    private boolean parseArguments() throws ArgsException {
        //    2. fix the broken code
        //        for (currentArgument = 0; currentArgument < args.length; currentArgument++) {
        //            String arg = args[currentArgument];
        for (currentArgument = argsList.iterator(); currentArgument.hasNext();) {
            String arg = currentArgument.next();
            parseArgument(arg);
        }
        return true;
    }

    private void parseArgument(String arg) throws ArgsException {
        if (arg.startsWith("-"))
            parseElements(arg);
    }

    private void parseElements(String arg) throws ArgsException {
        for (int i = 1; i < arg.length(); i++)
            parseElement(arg.charAt(i));
    }

    private void parseElement(char argChar) throws ArgsException {
        if (setArgument(argChar))
            argsFound.add(argChar);
        else {
            unexpectedArguments.add(argChar);
            errorCode = ErrorCode.UNEXPECTED_ARGUMENT;
            valid = false;
        }
    }

    
    private boolean setArgument(char argChar) throws ArgsException {
        ArgumentMarshaler m = marshalers.get(argChar);
        if (m == null)
            return false;
        try {
            if (m instanceof BooleanArgumentMarshaler)
                //    5.  Eliminate setBooleanArgs!
                //    //    4. Set functions down into the appropriate derivatives
                //    //   setBooleanArg(m);
                //    setBooleanArg(m, currentArgument);
                m.set(currentArgument);

            else if (m instanceof StringArgumentMarshaler)
                //    5. Eliminate setStringArg!
                //    //    4. Set functions down into the appropriate derivatives
                //    //setStringArg(m);
                //    setStringArg(m, currentArgument);
                m.set(currentArgument);
            else if (m instanceof IntegerArgumentMarshaler)
                //    5. Eliminate setIntArg
                //    //    4. Set functions down into the appropriate derivatives
                //    //    setIntArg(m);
                //    setIntArg(m, currentArgument)
                m.set(currentArgument);
            //    4. Set functions down into the appropriate derivatives
            //    else
            //        return false;
        } catch (ArgsException e) {
            valid = false;
            errorArgumentId = argChar;
            throw e;
        }
        return true;
    }

    private void setIntArg(ArgumentMarshaler m) throws ArgsException {
        //    3. Apply chagned type for currentArgument
        //    currentArgument++;
        String parameter = null;
        try {
            //    3. Apply chagned type for currentArgument
            //    parameter = args[currentArgument];
            parameter = currentArgument.next();
            m.set(parameter);
            //    3. Apply chagned type for currentArgument
            //    } catch (ArrayIndexOutOfBoundsException e) {
        } catch (NoSuchElementException e) {
            errorCode = ErrorCode.MISSING_INTEGER;
            throw new ArgsException();
        } catch (ArgsException e) {
            errorParameter = parameter;
            errorCode = ErrorCode.INVALID_INTEGER;
            throw e;
        }
    }

    private void setStringArg(ArgumentMarshaler m) throws ArgsException {
        //    3. Apply chagned type for currentArgument
        //    currentArgument++;
        try {
            //    3. Apply chagned type for currentArgument
            //    m.set(args[currentArgument]);
            m.set(currentArgument.next());
            //    3. Apply chagned type for currentArgument
            //    } catch (ArrayIndexOutOfBoundsException e) {
        } catch (NoSuchElementException e) {
            errorCode = ErrorCode.MISSING_STRING;
            throw new ArgsException();
        }
    }

    //    4. Set functions down into the appropriate derivatives
    //    private void setBooleanArg(ArgumentMarshaler m) {
    private void setBooleanArg(ArgumentMarshaler m, Iterator<String> currentArgument) throws ArgsException {
        //    4. Set functions down into the appropriate derivatives
        //     try {
        m.set("true");
        //    4. Set functions down into the appropriate derivatives
        //    } catch (ArgsException e) {
        //    }
    }

    public int cardinality() {
        return argsFound.size();
    }

    public String usage() {
        if (schema.length() > 0)
            return "-[" + schema + "]";
        else
            return "";
    }

    public String errorMessage() throws Exception {
        switch (errorCode) {
        case OK:
            throw new Exception("TILT: Should not get here.");
        case UNEXPECTED_ARGUMENT:
            return unexpectedArgumentMessage();
        case MISSING_STRING:
            return String.format("Could not find string parameter for -%c.",
                    errorArgumentId);
        case INVALID_INTEGER:
            return String.format("Argument -%c expects an integer but was '%s'.",
                    errorArgumentId, errorParameter);
        case MISSING_INTEGER:
            return String.format("Could not find integer parameter for -%c.",
                    errorArgumentId);
        }
        return "";
    }

    private String unexpectedArgumentMessage() {
        StringBuffer message = new StringBuffer("Argument(s) -");
        for (char c : unexpectedArguments) {
            message.append(c);
        }
        message.append(" unexpected.");
        return message.toString();
    }

    public boolean getBoolean(char arg) {
        Args1.ArgumentMarshaler am = marshalers.get(arg);
        boolean b = false;
        try {
            b = am != null && (Boolean) am.get();
        } catch (ClassCastException e) {
            b = false;
        }
        return b;
    }

    public String getString(char arg) {
        Args1.ArgumentMarshaler am = marshalers.get(arg);
        try {
            return am == null ? "" : (String) am.get();
        } catch (ClassCastException e) {
            return "";
        }
    }

    public int getInt(char arg) {
        Args1.ArgumentMarshaler am = marshalers.get(arg);
        try {
            return am == null ? 0 : (Integer) am.get();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean has(char arg) {
        return argsFound.contains(arg);
    }

    public boolean isValid() {
        return valid;
    }

    private class ArgsException extends Exception {
    }

    private abstract class ArgumentMarshaler {

        //    4. Set functions down into the appropriate derivatives - thorws Exception to handle exception in common
        public abstract void set(Iterator<String> currentArgument) throws ArgsException;

        public abstract void set(String s) throws ArgsException;

        public abstract Object get();
    }

    private class BooleanArgumentMarshaler extends ArgumentMarshaler {

        private boolean booleanValue = false;

        @Override
        public void set(Iterator<String> currentArgument) throws ArgsException {
            booleanValue = true;
        }

        @Override
        public void set(String s) {
            //    4. Set functions down into the appropriate derivatives - this function will be deleted
            //    booleanValue = true;
        }

        @Override
        public Object get() {
            return booleanValue;
        }
    }

    private class StringArgumentMarshaler extends ArgumentMarshaler {

        private String stringValue = "";

        //    4. Set functions down into the appropriate derivatives - add only set function thorws excpetion
        @Override
        public void set(Iterator<String> currentArgument) throws ArgsException {
            //    4. Set functions down into the appropriate derivatives for StringArgument
            try {
                stringValue = currentArgument.next();
            } catch (NoSuchElementException e) {
                errorCode = ErrorCode.MISSING_STRING;
                throw new ArgsException();
            }
        }

        @Override
        public void set(String s) {
            stringValue = s;
        }

        @Override
        public Object get() {
            return stringValue;
        }
    }

    private class IntegerArgumentMarshaler extends ArgumentMarshaler {

        private int intValue = 0;

        //    4. Set functions down into the appropriate derivatives - add only set function thorws excpetion
        @Override
        public void set(Iterator<String> currentArgument) throws ArgsException {
            //    4. Set functions down into the appropriate derivatives for IntegerArgument
            String parameter = null;
            try {
                parameter = currentArgument.next();
                set(parameter);
            } catch (NoSuchElementException e) {
                errorCode = ErrorCode.MISSING_INTEGER;
                throw new ArgsException();
            } catch (ArgsException e) {
                errorParameter = parameter;
                errorCode = ErrorCode.INVALID_INTEGER;
                throw e;
            }
        }

        @Override
        public void set(String s) throws ArgsException {
            try {
                intValue = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                throw new ArgsException();
            }
        }

        @Override
        public Object get() {
            return intValue;
        }
    }
}
