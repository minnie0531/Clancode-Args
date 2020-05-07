import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Args3 {

    private String schema;
    private String[] args;
    private boolean valid = true;
    private Set<Character> unexpectedArguments = new TreeSet<Character>();

    //4. Remove the three old maps
    //private Map<Character, ArgumentMarshaler> booleanArgs = new HashMap<Character, ArgumentMarshaler>();
  //5. Remove the three old maps
    //private Map<Character, ArgumentMarshaler> stringArgs = new HashMap<Character, ArgumentMarshaler>();
    //private Map<Character, ArgumentMarshaler> intArgs = new HashMap<Character, ArgumentMarshaler>();
    //Make ArgumentMarshler generic
    private Map<Character, ArgumentMarshaler> marshalers = new HashMap<Character, ArgumentMarshaler>();

    private Set<Character> argsFound = new HashSet<Character>();
    private int currentArgument;
    private char errorArgumentId = '\0';
    private String errorParameter = "TILT";
    private ErrorCode errorCode = ErrorCode.OK;

    private enum ErrorCode {
        OK,
        MISSING_STRING,
        MISSING_INTEGER,
        INVALID_INTEGER,
        UNEXPECTED_ARGUMENT
    }

    public Args3(String schema, String[] args) throws ParseException {
        this.schema = schema;
        this.args = args;
        valid = parse();
    }

    private boolean parse() throws ParseException {
        if (schema.length() == 0 && args.length == 0)
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

    //6. Inline the tree parse methods
    private void parseSchemaElement(String element) throws ParseException {
        char elementId = element.charAt(0);
        String elementTail = element.substring(1);
        validateSchemaElementId(elementId);
        if (isBooleanSchemaElement(elementTail))
            //parseBooleanSchemaElement(elementId);
            marshalers.put(elementId, new BooleanArgumentMarshaler());
        else if (isStringSchemaElement(elementTail))
            //parseStringSchemaElement(elementId);
            marshalers.put(elementId, new StringArgumentMarshaler());
        else if (isIntegerSchemaElement(elementTail)) {
            //parseIntegerSchemaElement(elementId);
            marshalers.put(elementId, new IntegerArgumentMarshaler());
        } else {
            throw new ParseException(
                    String.format("Argument: %c has invalid format: %s.",
                            elementId, elementTail),
                    0);
        }
    }

    private void validateSchemaElementId(char elementId) throws ParseException {
        if (!Character.isLetter(elementId)) {
            throw new ParseException(
                    "Bad character:" + elementId + "in Args format: " + schema, 0);
        }
    }
//6. Inline the tree parse methods
    /*
     * private void parseBooleanSchemaElement(char elementId) {
     * //4. Remove the three old maps
     * //ArgumentMarshaler m = new BooleanArgumentMarshaler();
     * //booleanArgs.put(elementId, m);
     * //marshalers.put(elementId, m);
     * marshalers.put(elementId, new BooleanArgumentMarshaler());
     * }
     * private void parseIntegerSchemaElement(char elementId) {
     * //5. Remove the three old maps
     * // ArgumentMarshaler m = new IntegerArgumentMarshaler();
     * // intArgs.put(elementId, m);
     * // marshalers.put(elementId, m);
     * marshalers.put(elementId, new IntegerArgumentMarshaler());
     * }
     * private void parseStringSchemaElement(char elementId) {
     * //5. Remove the three old maps
     * // ArgumentMarshaler m = new StringArgumentMarshaler();
     * // stringArgs.put(elementId, m);
     * // marshalers.put(elementId, m);
     * marshalers.put(elementId, new StringArgumentMarshaler());
     * }
     */

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
        for (currentArgument = 0; currentArgument < args.length; currentArgument++) {
            String arg = args[currentArgument];
            System.out.println("arg : " + arg);
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
        //      1. Inline isXXX methods
        //        if (isBooleanArg(m))
        //            setBooleanArg(argChar);
        //        else if (isStringArg(m))
        //            setStringArg(argChar);
        //        else if (isIntArg(m))
        //            setIntArg(argChar);

        //3. Use marshalers map in the set
        //Handle Exceptions in Set methods and make them simple
        try {
            if (m instanceof BooleanArgumentMarshaler)
                //2. Use marshalers map in the set
                // setBooleanArg(argChar);
                setBooleanArg(m);
            else if (m instanceof StringArgumentMarshaler)
                //3. Use marshalers map in the set
                //setStringArg(argChar);
                setStringArg(m);
            else if (m instanceof IntegerArgumentMarshaler)
                //3. Use marshalers map in the set
                //setIntArg(argChar);
                setIntArg(m);
            else
                return false;
        } catch (ArgsException e) {
            valid = false;
            errorArgumentId = argChar;
            throw e;
        }
        return true;
    }
    //  1. Inline isXXX methods
    //    private boolean isIntArg(ArgumentMarshaler m) {
    //        return m instanceof IntegerArgumentMarshaler;
    //    }

    @SuppressWarnings("deprecation")
    //3. Use marshalers map in the set
    //private void setIntArg(char argChar) throws ArgsException {
    private void setIntArg(ArgumentMarshaler m) throws ArgsException {
        currentArgument++;
        String parameter = null;
        try {
            parameter = args[currentArgument];
            //intArgs.get(argChar).set(parameter);
            m.set(parameter);
        } catch (ArrayIndexOutOfBoundsException e) {
            //valid = false;
            //errorArgumentId = argChar;
            errorCode = ErrorCode.MISSING_INTEGER;
            throw new ArgsException();
        } catch (ArgsException e) {
            //valid = false;
            //errorArgumentId = argChar;
            errorParameter = parameter;
            errorCode = ErrorCode.INVALID_INTEGER;
            throw e;
        }
    }

    //3. Use marshalers map in the set
    //private void setStringArg(char argChar) throws ArgsException {
    private void setStringArg(ArgumentMarshaler m) throws ArgsException {
        currentArgument++;
        try {
            //stringArgs.get(argChar).set(args[currentArgument]);
            m.set(args[currentArgument]);
        } catch (ArrayIndexOutOfBoundsException e) {
            //valid = false;
            //errorArgumentId = argChar;
            errorCode = ErrorCode.MISSING_STRING;
            throw new ArgsException();
        }

    }

    //  1. Inline isXXX methods
    //    private boolean isStringArg(ArgumentMarshaler m) {
    //        return m instanceof StringArgumentMarshaler;
    //    }

    //2. Use marshalers map in the set
    //private void setBooleanArg(char argChar) {
    private void setBooleanArg(ArgumentMarshaler m) {
        try {
            //booleanArgs.get(argChar).set("true");
            m.set("true");
        } catch (ArgsException e) {
        }
    }

    //    1. Inline isXXX methods
    //    private boolean isBooleanArg(ArgumentMarshaler m) {
    //        return m instanceof BooleanArgumentMarshaler;
    //    }

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

    public String getString(char arg) {
        //5. Remove the three old maps
        //        Args3.ArgumentMarshaler am = stringArgs.get(arg);
        //        return am == null ? "" : (String) am.get();
        Args3.ArgumentMarshaler am = marshalers.get(arg);
        try {
            return am == null ? "" : (String) am.get();
        } catch (ClassCastException e) {
            return "";
        }
    }

    public int getInt(char arg) {
        //5. Remove the three old maps
        //        Args3.ArgumentMarshaler am = intArgs.get(arg);
        //        return am == null ? 0 : (Integer) am.get();
        Args3.ArgumentMarshaler am = marshalers.get(arg);
        try {
            return am == null ? 0 : (Integer) am.get();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean getBoolean(char arg) {
        //4. Remove the three old maps
        //        Args3.ArgumentMarshaler am = booleanArgs.get(arg);
        //        return am != null && (Boolean) am.get();
        Args3.ArgumentMarshaler am = marshalers.get(arg);
        boolean b = false;
        try {
            b = am != null && (Boolean) am.get();
        } catch (ClassCastException e) {
            b = false;
        }
        return b;
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

        public abstract void set(String s) throws ArgsException;

        public abstract Object get();
    }

    private class BooleanArgumentMarshaler extends ArgumentMarshaler {

        private boolean booleanValue = false;

        @Override
        public void set(String s) {
            booleanValue = true;
        }

        @Override
        public Object get() {
            return booleanValue;
        }
    }

    private class StringArgumentMarshaler extends ArgumentMarshaler {

        private String stringValue = "";

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
