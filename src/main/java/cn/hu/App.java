package cn.hu;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        System.out.println( "Hello World!" );
        Jregex r = new Jregex("/abc*/");
        Utils.show(r.getNFA());
        Utils.show(r.getDFA());
        System.out.println(r.matches("a"));

    }
}
