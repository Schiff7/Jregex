package cn.hu;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        System.out.println( "Hello World!" );
        Jregex r = new Jregex("/ab*c|d/");
        Utils.show(r.getNFA());
        Utils.show(r.getDFA());
        System.out.println(r.matches("ac"));

    }
}
