package cn.hu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Jregex
 */
public class Jregex {
    private String raw;
    private List<Token> tokens;
    private NFA NFA;
    private DFA DFA;

    Jregex(String raw) {
        this.raw = raw;
        try {
            this.tokens = tokenize(raw);    
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }

        this.NFA = new NFA(tokens);
        this.DFA = new DFA(NFA);
    }
    /**
     * tokenize
     * @param s string
     */
    public static List<Token> tokenize(String s) throws TokenizeFailedException {
        List<Token> l = new ArrayList<>();
        final String NONE = "_";
        for (int i = 0; i < s.length();) {
            String c = String.valueOf(s.charAt(i));
            Meta meta = Meta.map(c);
            if (null == meta) {
                l.add(new Token(Meta.OPERAND, c));
                l.add(new Token(Meta.CONCAT, NONE));
                i++;
                continue;
            }

            switch (meta) {
                case SLASH:
                    if (i == 0) {
                        l.add(new Token(meta, c));
                        i++;
                    } else if (i == s.length() - 1) {
                        if (l.get(l.size() - 1).getName() == Meta.CONCAT) {
                            l.remove(l.size() - 1);
                        }
                        l.add(new Token(meta, c));
                        i++;
                    } else {
                        throw new TokenizeFailedException();
                    }
                    break;
                case BACKSLASH:
                    String next = String.valueOf(s.charAt(i + 1));
                    Meta tmp =  Meta.map(c + next);
                    if ( Meta.map(next) != null ) {
                        l.add(new Token(Meta.OPERAND, next));
                        i += 2;
                    } else if ( tmp != null ) {
                        l.add(new Token(tmp, c + next));
                        i += 2;
                    } else {
                        throw new TokenizeFailedException();
                    }
                    l.add(new Token(Meta.CONCAT, NONE));
                    break;
                case LEFT_PARENTHESIS:
                    int bracketsPattern = 0;
                    StringBuilder acc = new StringBuilder();
                    String prefix = s.substring(i, i + 3);
                    Meta group = Meta.map(prefix);
                    if (null == group) {
                        group = meta;
                    }
                    for (; i < s.length();) {
                        acc.append(s.charAt(i));
                        if (s.charAt(i) == ')') {
                            bracketsPattern--;
                        } else if (s.charAt(i) == '(') {
                            bracketsPattern++;
                        }
                        i++;
                        if (bracketsPattern == 0) break;
                    }
                    if (i < s.length()) {
                        l.add(new Token(group, acc.toString()));
                        l.add(new Token(Meta.CONCAT, NONE));
                    } else {
                        throw new TokenizeFailedException();
                    }
                    break;
                case LEFT_BRACE:
                    acc = new StringBuilder("{");
                    char ch;
                    i++;
                    for (; ( ch = s.charAt(i) ) != '}'; i++) {
                        if ( ch != ',' && (ch < 48 || ch > 57 || i == s.length() - 1)) {
                            throw new TokenizeFailedException();
                        }
                        acc.append(ch);
                    }
                    acc.append("}");
                    l.add(l.size() - 1, new Token(Meta.REPEAT, acc.toString()));
                    i++;
                    break;
                case LEFT_BRACKET:
                    acc = new StringBuilder("[");
                    i++;
                    for (; ( ch = s.charAt(i) ) != ']'; i++) {
                        acc.append(ch);
                    }
                    acc.append("]");
                    l.add(new Token(meta, acc.toString()));
                    l.add(new Token(Meta.CONCAT, NONE));
                    i++;
                    break;
                case UNION:
                    l.remove(l.size() - 1);
                    l.add(new Token(meta, c));
                    i++;
                    break;
                case STAR:
                    l.add(l.size() - 1, new Token(meta, c));
                    i++;
                    break;
                case PLUS:
                    l.add(l.size() - 1, new Token(meta, c));
                    i++;
                    break;
                case OPTIONAL:
                    l.add(l.size() - 1, new Token(meta, c));
                    i++;
                    break;
                case POINT:
                    l.add(new Token(meta, c));
                    l.add(new Token(Meta.CONCAT, NONE));
                    break;
                default:
                    l.add(new Token(meta, c));
                    i++;
            }
        }
        return l;
    }
    /**
     * matches
     * @param s
     */
    public boolean matches(String s) {
        if (null == s)
            return false;
        Map<DFA.Pairs, Set<State>> m = this.DFA.getMap();
        Set<State> currentState = this.DFA.getInitState();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            Set<State> nextState = m.get(new DFA.Pairs(currentState, String.valueOf(c)));
            if (null == nextState) {
                return false;
            } else {
                currentState = nextState;
            }
        }
        return this.DFA.getAcceptStates().contains(currentState);
    }

    /**
     * @return the DFA
     */
    public DFA getDFA() {
        return DFA;
    }

    /**
     * @return the NFA
     */
    public NFA getNFA() {
        return NFA;
    }

    /**
     * @return the tokens
     */
    public List<Token> getTokens() {
        return tokens;
    }

    /**
     * @return the raw
     */
    public String getRaw() {
        return raw;
    }
}

class TokenizeFailedException extends Exception {

	private static final long serialVersionUID = 1L;

}