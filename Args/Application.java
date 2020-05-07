public class Application {

    public static void main(String[] args) throws Exception {
        try {
            Args arg = new Args("l,p#,d*", args);
            boolean logging = arg.getBoolean('l');
            int port = arg.getInt('p');
            String directory = arg.getString('d');
            //executeApplication(logging, port, directory);
        } catch (ArgsException e) {
            System.out.printf("Argument error: %s\n", e.errorMessage());
        }
    }
}
