package cn.hu;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        System.out.println( "Hello World!" );
        NFA n = new NFA("n"), m = new NFA("m");
        n = MetaChr.UNION.opt().exe(n, m);
        Jregex r = new Jregex("/a|b*/");
        Utils.show(r.getDFA());
    }
}
