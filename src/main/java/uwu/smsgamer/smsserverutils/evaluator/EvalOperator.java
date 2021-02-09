package uwu.smsgamer.smsserverutils.evaluator;

import org.jetbrains.annotations.NotNull;
import uwu.smsgamer.smsserverutils.evaluator.EvalVar.VarType;

import java.util.regex.Pattern;

import static uwu.smsgamer.smsserverutils.evaluator.EvalVar.VarType.*;

public class EvalOperator extends EvalToken implements Comparable<EvalOperator> {
    public final FunType type;
    public EvalToken[] args;

    public EvalOperator(int nestingLevel, FunType type) {
        super(nestingLevel);
        this.type = type;
    }

    @Override
    public int compareTo(@NotNull EvalOperator o) {
        int c = Integer.compare(this.nestingLevel, o.nestingLevel);
        if (c == 0) c = Integer.compare(this.type.priority, o.type.priority);
        if (c == 0) return -1;
        return c;
    }

    @Override
    public String toString() {
        return "EvalOperator{" +
          "type=" + type +
          ", nestingLevel=" + nestingLevel +
          "}\n";
    }

    public EvalVar<?> execute() {
        EvalVar<?>[] vars = new EvalVar[args.length];
        for (int i = 0; i < args.length; i++) vars[i] = args[i].toVar();
        return type.fun.run(vars);
    }

    @Override
    public EvalVar<?> toVar() {
        return execute();
    }

    public enum FunType {
        // Boolean operators
        NOT((v) -> new EvalVar.Bool(!v[0].bool()), "! %", 16, BOOLEAN, BOOLEAN),
        AND((v) -> new EvalVar.Bool(v[0].bool() && v[1].bool()), "% && %", 48, BOOLEAN, BOOLEAN, BOOLEAN),
        OR((v) -> new EvalVar.Bool(v[0].bool() || v[1].bool()), "% || %", 32, BOOLEAN, BOOLEAN, BOOLEAN),
        XOR((v) -> new EvalVar.Bool(v[0].bool() == v[1].bool()), "% ^^ %", 32, BOOLEAN, BOOLEAN, BOOLEAN),
        NOR((v) -> NOT.run(OR.run(v)), "% !| %", 32, BOOLEAN, BOOLEAN, BOOLEAN),
        XNOR((v) -> NOT.run(XOR.run(v)), "% !^ %", 32, BOOLEAN, BOOLEAN, BOOLEAN),
        NAND((v) -> NOT.run(AND.run(v)), "% !& %", 48, BOOLEAN, BOOLEAN, BOOLEAN),
        // Number operators
        ADD((v) -> new EvalVar.Num(v[0].d() + v[1].d()), "% + %", 128, NUMBER, NUMBER, NUMBER),
        SUB((v) -> new EvalVar.Num(v[0].d() - v[1].d()), "% - %", 128, NUMBER, NUMBER, NUMBER),
        MULT((v) -> new EvalVar.Num(v[0].d() * v[1].d()), "% * %", 64, NUMBER, NUMBER, NUMBER),
        DIV((v) -> new EvalVar.Num(v[0].d() / v[1].d()), "% / %", 64, NUMBER, NUMBER, NUMBER),
        POW((v) -> new EvalVar.Num(Math.pow(v[0].d(), v[1].d())), "% ^ %", 16, NUMBER, NUMBER, NUMBER),
        SQRT((v) -> new EvalVar.Num(Math.sqrt(v[0].d())), "sqrt %", NUMBER, NUMBER),
        ROOT((v) -> new EvalVar.Num(Math.pow(v[0].d(), 1 / v[1].d())), "root % %", NUMBER, NUMBER, NUMBER),
        MAX((v) -> new EvalVar.Num(Math.max(v[0].d(), v[1].d())), "max % %", NUMBER, NUMBER, NUMBER),
        MIN((v) -> new EvalVar.Num(Math.min(v[0].d(), v[1].d())), "min % %", NUMBER, NUMBER, NUMBER),
        FLOOR((v) -> new EvalVar.Num(Math.floor(v[0].d())), "floor %", NUMBER, NUMBER),
        CEIL((v) -> new EvalVar.Num(Math.ceil(v[0].d())), "ceil %", NUMBER, NUMBER),
        ROUND((v) -> new EvalVar.Num(Math.round(v[0].d())), "round %", NUMBER, NUMBER),
        RANDOM((v) -> new EvalVar.Num(Math.random()), "random", 0, NUMBER),
        GREATER((v) -> new EvalVar.Bool(v[0].d() > v[1].d()), "% > %", NUMBER, NUMBER, BOOLEAN),
        GREATER_E((v) -> new EvalVar.Bool(v[0].d() >= v[1].d()), "% >= %", NUMBER, NUMBER, BOOLEAN),
        LESSER((v) -> new EvalVar.Bool(v[0].d() < v[1].d()), "% < %", NUMBER, NUMBER, BOOLEAN),
        LESSER_E((v) -> new EvalVar.Bool(v[0].d() <= v[1].d()), "% <= %", NUMBER, NUMBER, BOOLEAN),
        // String operators
        CONCAT((v) -> new EvalVar.Str(v[0].s().concat(v[1].s())), "% s+ %", STRING, STRING, STRING),
        SUB_STR((v) -> new EvalVar.Str(v[0].s().substring(v[1].i(), v[2].i())), "% substr % % ", STRING, NUMBER, NUMBER, BOOLEAN),
        REPLACE((v) -> new EvalVar.Str(v[0].s().replace(v[1].s(), v[2].s())), "% replace % % ", STRING, STRING, STRING, BOOLEAN),
        REPLACE_FIRST((v) -> new EvalVar.Str(v[0].s().replace(v[1].s(), v[2].s())), "% replaceFirst % % ", STRING, STRING, STRING, BOOLEAN),
        REPLACE_REG((v) -> new EvalVar.Str(v[0].s().replaceAll(v[1].s(), v[2].s())), "% replaceReg % % ", STRING, STRING, STRING, BOOLEAN),
        CONTAINS((v) -> new EvalVar.Bool(v[0].s().contains(v[1].s())), "% contains % ", STRING, STRING, BOOLEAN),
        CONTAINS_IC((v) -> new EvalVar.Bool(v[0].s().toLowerCase().contains(v[1].s().toLowerCase())), "% containsIc % ", STRING, STRING, BOOLEAN),
        MATCHES((v) -> new EvalVar.Bool(v[0].s().matches(v[1].s())), "% matches % ", STRING, STRING, BOOLEAN),
        INDEX_OF((v) -> new EvalVar.Num(v[0].s().indexOf(v[1].s())), "% indexOf % % ", STRING, STRING, NUMBER),
        LENGTH((v) -> new EvalVar.Num(v[0].s().length()), "% length", STRING, NUMBER),
        EQUALS_IC((v) -> new EvalVar.Bool(v[0].s().equalsIgnoreCase(v[1].s())), "% equalsIc % ", STRING, STRING, NUMBER),
        // Any operators
        EQUALS((v) -> new EvalVar.Bool(v[0].s().equals(v[1].s())), "% == %", ANY, ANY, NUMBER),
        ;

        public EvalVar<?> run(EvalVar<?>... vars) {
            return fun.run(vars);
        }

        public final Fun fun;
        public final String format;
        public final String keyword;
        public final int argsBefore;
        public final int argsAfter;
        public final EvalOperatorToken[] tokens;
        public final int priority;
        public final VarType returnType;
        public final VarType[] inputTypes;

        FunType(Fun fun, String format, VarType returnType, VarType... inputTypes) {
            this.fun = fun;
            this.format = format;
            this.keyword = format.replace(" ", "").replace("%", "");
            String[] split = this.format.split(Pattern.quote(this.keyword));
            if (split.length == 0) {
                this.argsBefore = 0;
                this.argsAfter = 0;
            } else if (split.length == 1) {
                this.argsBefore = (int) split[0].chars().filter(c -> c == '%').count();
                this.argsAfter = 0;
            } else {
                this.argsBefore = (int) split[0].chars().filter(c -> c == '%').count();
                this.argsAfter = (int) split[1].chars().filter(c -> c == '%').count();
            }
            this.priority = Integer.MAX_VALUE;
            this.returnType = returnType;
            this.inputTypes = inputTypes;
            this.tokens = EvalOperatorToken.getTokensForOperator(format);
        }

        FunType(Fun fun, String format, int priority, VarType returnType, VarType... inputTypes) {
            this.fun = fun;
            this.format = format;
            this.keyword = format.replace(" ", "").replace("%", "");
            String[] split = this.format.split(Pattern.quote(this.keyword));
            if (split.length == 0) {
                this.argsBefore = 0;
                this.argsAfter = 0;
            } else if (split.length == 1) {
                this.argsBefore = (int) split[0].chars().filter(c -> c == '%').count();
                this.argsAfter = 0;
            } else {
                this.argsBefore = (int) split[0].chars().filter(c -> c == '%').count();
                this.argsAfter = (int) split[1].chars().filter(c -> c == '%').count();
            }
            this.priority = priority;
            this.returnType = returnType;
            this.inputTypes = inputTypes;
            this.tokens = EvalOperatorToken.getTokensForOperator(format);
        }

        public static FunType getFunType(String keyword) {
            for (FunType value : values()) {
                if (value.keyword.equals(keyword)) return value;
            }
            return null;
        }
    }

    public interface Fun {
        EvalVar<?> run(EvalVar<?>... args);
    }
}
