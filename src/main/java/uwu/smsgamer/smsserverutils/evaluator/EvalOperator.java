package uwu.smsgamer.smsserverutils.evaluator;

import uwu.smsgamer.smsserverutils.evaluator.EvalVar.VarType;

import java.util.List;

import static uwu.smsgamer.smsserverutils.evaluator.EvalVar.VarType.*;

public class EvalOperator {
    public final List<EvalVar<?>> params;
    public final FunType type;

    public EvalOperator(List<EvalVar<?>> params, FunType type) {
        this.params = params;
        this.type = type;
    }

    public enum FunType {
        // Boolean operators
        NOT((v) -> new EvalVar.Bool(!v[0].bool()), "! %0", 16, BOOLEAN, BOOLEAN),
        AND((v) -> new EvalVar.Bool(v[0].bool() && v[1].bool()), "%0 && %1", 48, BOOLEAN, BOOLEAN, BOOLEAN),
        OR((v) -> new EvalVar.Bool(v[0].bool() || v[1].bool()), "%0 || %1", 32, BOOLEAN, BOOLEAN, BOOLEAN),
        XOR((v) -> new EvalVar.Bool(v[0].bool() == v[1].bool()), "%0 ^^ %1", 32, BOOLEAN, BOOLEAN, BOOLEAN),
        NOR((v) -> NOT.run(OR.run(v)), "%0 !| %1", 32, BOOLEAN, BOOLEAN, BOOLEAN),
        XNOR((v) -> NOT.run(XOR.run(v)), "%0 !^ %1", 32, BOOLEAN, BOOLEAN, BOOLEAN),
        NAND((v) -> NOT.run(AND.run(v)), "%0 !& %1", 48, BOOLEAN, BOOLEAN, BOOLEAN),
        // Number operators
        ADD((v) -> new EvalVar.Num(v[0].d() + v[1].d()), "%0 + %1", 128, NUMBER, NUMBER, NUMBER),
        SUB((v) -> new EvalVar.Num(v[0].d() - v[1].d()), "%0 - %1", 128, NUMBER, NUMBER, NUMBER),
        MULT((v) -> new EvalVar.Num(v[0].d() * v[1].d()), "%0 * %1", 64, NUMBER, NUMBER, NUMBER),
        DIV((v) -> new EvalVar.Num(v[0].d() / v[1].d()), "%0 / %1", 64, NUMBER, NUMBER, NUMBER),
        POW((v) -> new EvalVar.Num(Math.pow(v[0].d(), v[1].d())), "%0 ^ %1", 16, NUMBER, NUMBER, NUMBER),
        SQRT((v) -> new EvalVar.Num(Math.sqrt(v[0].d())), "sqrt %0", NUMBER, NUMBER),
        ROOT((v) -> new EvalVar.Num(Math.pow(v[0].d(), 1 / v[1].d())), "root %0 %1", NUMBER, NUMBER, NUMBER),
        MAX((v) -> new EvalVar.Num(Math.max(v[0].d(), v[1].d())), "max %0 %1", NUMBER, NUMBER, NUMBER),
        MIN((v) -> new EvalVar.Num(Math.min(v[0].d(), v[1].d())), "min %0 %1", NUMBER, NUMBER, NUMBER),
        FLOOR((v) -> new EvalVar.Num(Math.floor(v[0].d())), "floor %0", NUMBER, NUMBER),
        CEIL((v) -> new EvalVar.Num(Math.ceil(v[0].d())), "ceil %0", NUMBER, NUMBER),
        ROUND((v) -> new EvalVar.Num(Math.round(v[0].d())), "round %0", NUMBER, NUMBER),
        RANDOM((v) -> new EvalVar.Num(Math.random()), "random", 0, NUMBER),
        GREATER((v) -> new EvalVar.Bool(v[0].d() > v[1].d()), "%0 > %1", NUMBER, NUMBER, BOOLEAN),
        GREATER_E((v) -> new EvalVar.Bool(v[0].d() >= v[1].d()), "%0 >= %1", NUMBER, NUMBER, BOOLEAN),
        LESSER((v) -> new EvalVar.Bool(v[0].d() < v[1].d()), "%0 < %1", NUMBER, NUMBER, BOOLEAN),
        LESSER_E((v) -> new EvalVar.Bool(v[0].d() <= v[1].d()), "%0 <= %1", NUMBER, NUMBER, BOOLEAN),
        // String operators
        CONCAT((v) -> new EvalVar.Str(v[0].s().concat(v[1].s())), "%0 s+ %1", STRING, STRING, STRING),
        SUB_STR((v) -> new EvalVar.Str(v[0].s().substring(v[1].i(), v[2].i())), "%0 substr %1 %2 ", STRING, NUMBER, NUMBER, BOOLEAN),
        REPLACE((v) -> new EvalVar.Str(v[0].s().replace(v[1].s(), v[2].s())), "%0 replace %1 %2 ", STRING, STRING, STRING, BOOLEAN),
        REPLACE_FIRST((v) -> new EvalVar.Str(v[0].s().replace(v[1].s(), v[2].s())), "%0 replaceFirst %1 %2 ", STRING, STRING, STRING, BOOLEAN),
        REPLACE_REG((v) -> new EvalVar.Str(v[0].s().replaceAll(v[1].s(), v[2].s())), "%0 replaceReg %1 %2 ", STRING, STRING, STRING, BOOLEAN),
        CONTAINS((v) -> new EvalVar.Bool(v[0].s().contains(v[1].s())), "%0 contains %1 ", STRING, STRING, BOOLEAN),
        CONTAINS_IC((v) -> new EvalVar.Bool(v[0].s().toLowerCase().contains(v[1].s().toLowerCase())), "%0 containsIc %1 ", STRING, STRING, BOOLEAN),
        MATCHES((v) -> new EvalVar.Bool(v[0].s().matches(v[1].s())), "%0 matches %1 ", STRING, STRING, BOOLEAN),
        INDEX_OF((v) -> new EvalVar.Num(v[0].s().indexOf(v[1].s())), "%0 indexOf %1 %2 ", STRING, STRING, NUMBER),
        LENGTH((v) -> new EvalVar.Num(v[0].s().length()), "%0 length", STRING, NUMBER),
        EQUALS_IC((v) -> new EvalVar.Bool(v[0].s().equalsIgnoreCase(v[1].s())), "%0 equalsIc %1 ", STRING, STRING, NUMBER),
        // Any operators
        EQUALS((v) -> new EvalVar.Bool(v[0].s().equals(v[1].s())), "%0 == %1", ANY, ANY, NUMBER),
        ;

        public EvalVar<?> run(EvalVar<?>... vars) {
            return fun.run(vars);
        }

        public final Fun fun;
        public final String format; // TODO: Convert to tokens somehow.
        public final int priority;
        public final VarType returnType;
        public final VarType[] inputTypes;

        FunType(Fun fun, String format, VarType returnType, VarType... inputTypes) {
            this.fun = fun;
            this.format = format;
            this.priority = Integer.MAX_VALUE;
            this.returnType = returnType;
            this.inputTypes = inputTypes;
        }

        FunType(Fun fun, String format, int priority, VarType returnType, VarType... inputTypes) {
            this.fun = fun;
            this.format = format;
            this.priority = priority;
            this.returnType = returnType;
            this.inputTypes = inputTypes;
        }
    }

    public interface Fun {
        EvalVar<?> run(EvalVar<?>... vars);
    }
}
