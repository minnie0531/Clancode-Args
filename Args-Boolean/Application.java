public class Application {

    public static void main(String[] args) {

        Args arg = new Args("l,p#,d*", args);
        boolean logging = arg.getBoolean('l');
        //int port = arg.getInt('p');
         // String directory = arg.getString('d');
        //executeApplication(logging, port, directory);
        System.out.printf("Boolean element : %s\n", logging);
        System.out.println(arg.errorMessage());

    }
}
