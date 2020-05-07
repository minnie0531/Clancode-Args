import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Args1 {

    private String schema;
    private String[] args;
    private boolean valid = true;
    private Set<Character> unexpectedArguments = new TreeSet<Character>();

    private Map<Character, ArgumentMarshaler> booleanArgs = new HashMap<Character, ArgumentMarshaler>();
    private Map<Character, ArgumentMarshaler> stringArgs = new HashMap<Character, ArgumentMarshaler>();
    private Map<Character, ArgumentMarshaler> intArgs = new HashMap<Character, ArgumentMarshaler>();

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

    public Args1(String schema, String[] args) throws ParseException {
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

    private void parseSchemaElement(String element) throws ParseException {
        char elementId = element.charAt(0);
        String elementTail = element.substring(1);
        validateSchemaElementId(elementId);
        if (isBooleanSchemaElement(elementTail))
            parseBooleanSchemaElement(elementId);
        else if (isStringSchemaElement(elementTail))
            parseStringSchemaElement(elementId);
        else if (isIntegerSchemaElement(elementTail)) {
            parseIntegerSchemaElement(elementId);
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

    private void parseBooleanSchemaElement(char elementId) {
        booleanArgs.put(elementId, new BooleanArgumentMarshaler());
    }

    private void parseIntegerSchemaElement(char elementId) {
        intArgs.put(elementId, new IntegerArgumentMarshaler());
    }

    private void parseStringSchemaElement(char elementId) {
        stringArgs.put(elementId, new StringArgumentMarshaler());
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
        if (isBooleanArg(argChar))
        
            //    5-2. Let the function use BooleanArgumentMashaler
            //    setBooleanArg(argChar, true);
            setBooleanArg(argChar);
        else if (isStringArg(argChar))
            setStringArg(argChar);
        else if (isIntArg(argChar))
            setIntArg(argChar);
        else
            return false;
        return true;
    }

    private boolean isIntArg(char argChar) {
        return intArgs.containsKey(argChar);
    }

    @SuppressWarnings("deprecation")
    private void setIntArg(char argChar) throws ArgsException {
        currentArgument++;
        String parameter = null;
        try {
            parameter = args[currentArgument];
            System.out.println("int parameter : " + parameter);
            //Change Integer argument
            //intArgs.get(argChar).setInteger(Integer.parseInt(parameter));
            intArgs.get(argChar).set(parameter);
        } catch (ArrayIndexOutOfBoundsException e) {
            valid = false;
            errorArgumentId = argChar;
            errorCode = ErrorCode.MISSING_INTEGER;
            throw new ArgsException();
            //Hide NumberFormatException behind IntegerArguemntMashaler
            //} catch (NumberFormatException e) {
        } catch (ArgsException e) {
            valid = false;
            errorArgumentId = argChar;
            errorParameter = parameter;
            errorCode = ErrorCode.INVALID_INTEGER;
            //throw new ArgsException();
            throw e;
        }
    }

    private void setStringArg(char argChar) throws ArgsException {
        currentArgument++;
        try {
            //Change String argument
            //stringArgs.get(argChar).setString(args[currentArgument]);
            stringArgs.get(argChar).set(args[currentArgument]);
        } catch (ArrayIndexOutOfBoundsException e) {
            valid = false;
            errorArgumentId = argChar;
            errorCode = ErrorCode.MISSING_STRING;
            throw new ArgsException();
        }
    }

    private boolean isStringArg(char argChar) {
        return stringArgs.containsKey(argChar);
    }

    //    5-1. Let the function use BooleanArgumentMashaler
    //    private void setBooleanArg(char argChar, boolean value) {
    private void setBooleanArg(char argChar) {
        // booleanArgs.get(argChar).setBoolean(value);
        try {
            booleanArgs.get(argChar).set("true");
        } catch (ArgsException e) {
        }
    }

    private boolean isBooleanArg(char argChar) {
        return booleanArgs.containsKey(argChar);
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

    public String getString(char arg) {
        //Change String argument
        Args1.ArgumentMarshaler am = stringArgs.get(arg);
        //  return am == null ? "" : am.getString();
        return am == null ? "" : (String) am.get();
    }

    public int getInt(char arg) {
        //Change Integer argument
        Args1.ArgumentMarshaler am = intArgs.get(arg);
        //return am == null ? 0 : am.getInteger();
        return am == null ? 0 : (Integer) am.get();
    }

    public boolean getBoolean(char arg) {
        Args1.ArgumentMarshaler am = booleanArgs.get(arg);
        // Get in abstract class 1.
        //return am != null && am.getBoolean();
        return am != null && (Boolean) am.get();
    }

    public boolean has(char arg) {
        return argsFound.contains(arg);
    }

    public boolean isValid() {
        return valid;
    }

    private class ArgsException extends Exception {

    }

    //    1. Push functionality into the derivatives
    //    private class ArgumentMarshaler {
    private abstract class ArgumentMarshaler {

        // 4. TypedArgumentMarshaler has its own values - move booleanValue to BoolenaArgumentMarshaler
        //    // 2. Change private to protected
        //    //    private boolean booleanValue = false;
        //    protected boolean booleanValue = false;

        //Change String and Integer arguments
        //private String stringValue;
        //private int integerValue;

        //5. remove set function for Boolean
        //        public void setBoolean(boolean value) {
        //            booleanValue = value;
        //        }

        //Get in abstract class 4.
        //        public boolean getBoolean() {
        //            return booleanValue;
        //        }

        //Change String and Integer arguments
        //        public void setString(String s) {
        //            stringValue = s;
        //        }
        //
        //        public String getString() {
        //            return stringValue == null ? "" : stringValue;
        //        }

        //Change Integer argument
        //        public void setInteger(int i) {
        //            integerValue = i;
        //        }
        //
        //        public int getInteger() {
        //            return integerValue;
        //
        //        }

        //    3. Change set to thorw Excpetion
        //    FROM
        //    public abstract void set(String s);
        //    TO
        public abstract void set(String s) throws ArgsException;

        // Get in abstract class 2
        public abstract Object get();
    }

    // 4. TypedArgumentMarshaler has its own values
    //    private class BooleanArgumentMarshaler extends ArgumentMarshaler {
    private class BooleanArgumentMarshaler extends ArgumentMarshaler {

        private boolean booleanValue = false;

        @Override
        public void set(String s) {
            booleanValue = true;
        }

        //Get in abstract class 3.
        @Override
        public Object get() {
            return booleanValue;
        }
    }

    private class StringArgumentMarshaler extends ArgumentMarshaler {

        //Change String argument
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

        //Change Integer argument
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
