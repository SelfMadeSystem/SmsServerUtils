package uwu.smsgamer.smsserverutils.evaluator;

import java.util.*;

public class EvalToken {
    public final boolean whitespace;
    public final String stringMatch;
    public final int argNum;

    public EvalToken(boolean whitespace, String stringMatch, int argNum) {
        this.whitespace = whitespace;
        this.stringMatch = stringMatch;
        this.argNum = argNum;
    }

    public EvalToken(boolean whitespace) {
        this.whitespace = whitespace;
        this.stringMatch = "";
        this.argNum = -1;
    }

    public EvalToken(String stringMatch) {
        this.whitespace = false;
        this.stringMatch = stringMatch;
        this.argNum = -1;
    }

    public EvalToken(int argNum) {
        this.whitespace = false;
        this.stringMatch = "";
        this.argNum = argNum;
    }
    
    public static EvalToken[] getTokensForOperator(String input) {
        List<EvalToken> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        char[] charArray = input.toCharArray();
        boolean whitespace = false;
        int argNum = -1;
        for (char c : charArray) {
            if (Character.isWhitespace(c)) {
                if (sb.length() > 0) tokens.add(new EvalToken(sb.toString()));
                if (!whitespace) tokens.add(new EvalToken(true));
                whitespace = true;
                sb = new StringBuilder();
                continue;
            }
            whitespace = false;
            if (c == '%') {
                if (sb.length() > 0) tokens.add(new EvalToken(sb.toString()));
                tokens.add(new EvalToken(++argNum));
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
        }
        return tokens.toArray(new EvalToken[0]);
    }

    @Override
    public String toString() {
        return "EvalToken{" +
          "whitespace=" + whitespace +
          ", stringMatch='" + stringMatch + '\'' +
          ", argNum=" + argNum +
          '}';
    }
}
