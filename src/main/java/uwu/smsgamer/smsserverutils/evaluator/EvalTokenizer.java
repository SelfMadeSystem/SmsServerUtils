package uwu.smsgamer.smsserverutils.evaluator;

import java.util.*;

public class EvalTokenizer {
    public List<EvalToken> tokens = new ArrayList<>();
    public char[] charArr;
    public int nest;
    public int at;
    public int stop;

    public EvalTokenizer(String str) {
        this.charArr = str.toCharArray();
        this.stop = charArr.length;
    }

    public void tokenize() {
        W:
        while (shouldContinue()) {
            char c = next();
            if (Character.isWhitespace(c)) continue;
            switch (c) {
                case '"':
                    getStringToken();
                    continue W;
                case '(':
                    nest++;
                    continue W;
                case ')':
                    nest--;
                    continue W;
                case '.':
                    continue W;
            }
            getNextToken(c);
        }
    }

    public void parseToVars() {
        for (int i = 0; i < tokens.size(); i++) {
            EvalToken token = tokens.get(i);
            if (token.getClass() == EvalVar.Unknown.class) {
                EvalVar.Unknown opToken = (EvalVar.Unknown) token;
                if (Character.isDigit(opToken.name.charAt(0))) {
                    if (opToken.name.startsWith("0x")) {
                        tokens.set(i, new EvalVar.Num(Integer.parseInt(opToken.name.substring(2), 16), token.nestingLevel));
                    } else if (opToken.name.startsWith("0o")) {
                        tokens.set(i, new EvalVar.Num(Integer.parseInt(opToken.name.substring(2), 8), token.nestingLevel));
                    } else if (opToken.name.startsWith("0b")) {
                        tokens.set(i, new EvalVar.Num(Integer.parseInt(opToken.name.substring(2), 2), token.nestingLevel));
                    } else {
                        tokens.set(i, new EvalVar.Num(Double.parseDouble(opToken.name), token.nestingLevel));
                    }
                } else {
                    if (opToken.name.equals("true")) tokens.set(i, new EvalVar.Bool(true, token.nestingLevel));
                    else if (opToken.name.equals("false")) tokens.set(i, new EvalVar.Bool(false, token.nestingLevel));
                    else tokens.set(i, new EvalVar.Unknown(opToken.name, token.nestingLevel));
                }
            }
        }
    }

    public void parseToFuns() {
        for (int i = 0; i < tokens.size(); i++) {
            EvalToken token = tokens.get(i);
            if (token.getClass() == EvalVar.Unknown.class) {
                EvalVar.Unknown opToken = (EvalVar.Unknown) token;
                EvalOperator.FunType type = EvalOperator.FunType.getFunType(opToken.name);
                if (type != null) {
                    tokens.set(i, new EvalOperator(token.nestingLevel, type));
                }
            }
        }
    }

    public void sortFuns() {
        float lastSize = tokens.size() + 1;
        while (tokens.size() > 1) {
            toFunsRound();
            if (lastSize == tokens.size()) throw new RuntimeException("Loop or invalid tokens.");
            lastSize = tokens.size();
        }
    }

    private float getHighestNestingLevel() {
        float max = -1;
        for (EvalToken token : tokens) if (token.getClass() == EvalOperator.class) max = Math.max(((EvalOperator) token).priority(), max);
        return max;
    }

    private void toFunsRound() {
        float highest = getHighestNestingLevel();
        for (int i = 0; i < tokens.size(); i++) {
            EvalToken token = tokens.get(i);
            if (token == null) {
                continue;
            }
            if (token.getClass() == EvalOperator.class) {
                EvalOperator op = (EvalOperator) token;
                if (op.priority() != highest) continue;
                if (op.args != null) continue;
                token.nestingLevel--;
                op.args = new EvalToken[op.type.argsBefore + op.type.argsAfter];
                int k = 0;
                for (int j = -op.type.argsBefore; j <= op.type.argsAfter; j++) {
                    if (j == 0) continue;
                    op.args[k] = tokens.get(i + j);
                    tokens.set(i + j, null);
                    k++;
                }
                break;
            }
        }
        while (tokens.remove(null));
    }

    private void getNextToken(char current) {
        StringBuilder buf = new StringBuilder(String.valueOf(current));
        char p = current;
        W:
        while (shouldContinue()) {
            char c = next();
            if (Character.isWhitespace(c)) break;
            switch (c) {
                case '"':
                case '(':
                case ')':
                    at--;
                    break W;
                case '.':
                    if (!Character.isDigit(p)) break W;
            }
            buf.append(c);
            p = c;
        }
        tokens.add(new EvalVar.Unknown(buf.toString(), nest));
    }

    private void getStringToken() {
        StringBuilder buf = new StringBuilder();
        while (shouldContinue()) {
            char c = next();
            if (c == '"') break;
            if (c == '\\') {
                char n = next();
                switch (n) {
                    case '"':
                        c = '"';
                        break;
                    case 'b':
                        c = '\b';
                        break;
                    case 'f':
                        c = '\f';
                        break;
                    case 'n':
                        c = '\n';
                        break;
                    case 'r':
                        c = '\r';
                        break;
                    case 't':
                        c = '\t';
                        break; // TODO:  case 'u':
                }
            }
            buf.append(c);
        }
        tokens.add(new EvalVar.Str(buf.toString(), nest));
    }

    private char next() {
        return charArr[at++];
    }

    private boolean shouldContinue() {
        return at < stop;
    }

    public static void main(String[] args) {
        EvalTokenizer tokenizer = new EvalTokenizer("x == 3"); // fixme
        tokenizer.tokenize();
        System.out.println(tokenizer.tokens);
        tokenizer.parseToVars();
        System.out.println(tokenizer.tokens);
        tokenizer.parseToFuns();
        System.out.println(tokenizer.tokens);
        tokenizer.sortFuns();
        System.out.println(tokenizer.tokens);
        System.out.println(tokenizer.tokens.get(0).toVar().value);
    }
}
