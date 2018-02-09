package cn.hu;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        System.out.println( "Hello World!" );
        Jregex r = new Jregex("/[0-9]+/");
        System.out.println(r.getTokens());
        Utils.show(r.getNFA());
        Utils.show(r.getDFA());
        System.out.println(r.matches("122323987492834729384"));

    }
}
