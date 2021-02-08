package uwu.smsgamer.smsserverutils.utils;

public class ScriptReturn {
    public final Type type;
    public final Object object;

    public ScriptReturn(Object object) {
        this.type = Type.PASS;
        this.object = object;
    }

    public ScriptReturn(Type type, Object object) {
        this.type = type;
        this.object = object;
    }

    public ScriptReturn(Type type) {
        this.type = type;
        this.object = null;
    }

    public enum Type {
        PASS,
        INTERRUPTED,
        EXCEPTION,
        TIMEOUT
    }
}
