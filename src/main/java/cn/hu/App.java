package cn.hu;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        System.out.println( "Hello World!" );
        Jregex r = new Jregex("/[^0-9]+ab/");
        System.out.println(r);
        System.out.println(r.matches("asaab"));
    }
}
