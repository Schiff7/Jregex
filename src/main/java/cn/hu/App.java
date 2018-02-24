package cn.hu;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        System.out.println( "Hello World!" );
        Jregex r = new Jregex("/^[a-g]*$/");
        System.out.println(r);
        System.out.println(r.match("ac"));
        System.out.println(r.patterns("caccccccccccccacbaksjdhklaac"));
    }
}
