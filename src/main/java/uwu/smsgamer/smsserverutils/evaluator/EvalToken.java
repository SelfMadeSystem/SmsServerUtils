package uwu.smsgamer.smsserverutils.evaluator;

public abstract class EvalToken {
    public final int nestingLevel;

    protected EvalToken(int nestingLevel) {
        this.nestingLevel = nestingLevel;
    }
}
