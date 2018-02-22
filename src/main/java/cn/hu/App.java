package cn.hu;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        System.out.println( "Hello World!" );
        Jregex r = new Jregex("/(a|c)*/");
        System.out.println(r);
        System.out.println(r.matches("ac"));
        System.out.println(r._matches("ac"));
        System.out.println(r.pattern("caccccccccccccacbaksjdhklaac"));
    }
}
