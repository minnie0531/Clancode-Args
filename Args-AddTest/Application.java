import java.text.ParseException;

/**
* Please Add your test case to test it by modifying ArgsTest.java in Args folder
**/

public class Application {

    public static void main(String[] args) {

        try {
            Args arg = new Args("l,p#,d*", args);
            boolean logging = arg.getBoolean('l');
            int port = arg.getInt('p');
            String directory = arg.getString('d');

            System.out.printf("Boolean element : %s, Integer element : %s, String element: %s\n", logging, port, directory);
            //executeApplication(logging, port, directory);
        } catch (ParseException e) {
            System.out.printf("Argument error: %s\n", e.getMessage());
        }

    }
}
