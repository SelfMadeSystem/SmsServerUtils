package uwu.smsgamer.smsserverutils.evaluator;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
public class EvalVar<T> {
    public String name;
    @Nullable public T value;
    public final VarType type;

    protected EvalVar(String name, @Nullable T value, VarType type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    protected EvalVar(@Nullable T value, VarType type) {
        this.name = "__unnamed__";
        this.value = value;
        this.type = type;
    }

    public Number number() {
        if (value instanceof Number) {
            return (Number) value;
        } else if (value instanceof CharSequence) {
            return Double.valueOf(value.toString());
        } else return 0;
    }

    public int i() {
        return number().intValue();
    }

    public double d() {
        return number().doubleValue();
    }

    public String string() {
        return Objects.toString(value);
    }

    public String s() {
        return string();
    }

    public boolean bool() {
        if (value instanceof Number) return number().doubleValue() == 0D;
        if (value instanceof String) return ((String) value).isEmpty();
        return false;
    }

    public enum VarType {
        STRING,
        NUMBER,
        BOOLEAN,
        ANY
    }

    public static class Str extends EvalVar<String> {
        public Str(String name, @Nullable String value) {
            super(name, value, VarType.STRING);
        }
        public Str(@Nullable String value) {
            super(value, VarType.STRING);
        }
    }

    public static class Num extends EvalVar<Number> {
        public Num(String name, @Nullable Number value) {
            super(name, value, VarType.NUMBER);
        }
        public Num(@Nullable Number value) {
            super(value, VarType.NUMBER);
        }
    }

    public static class Bool extends EvalVar<Boolean> {
        public Bool(String name, @Nullable Boolean value) {
            super(name, value, VarType.BOOLEAN);
        }
        public Bool(@Nullable Boolean value) {
            super(value, VarType.BOOLEAN);
        }
    }
}
