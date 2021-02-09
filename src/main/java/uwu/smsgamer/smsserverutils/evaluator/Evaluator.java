package uwu.smsgamer.smsserverutils.evaluator;

import java.util.HashMap;

/**
 * JS/Py like evaluator for simple arithmetic based operations.
 */
public class Evaluator {
    public HashMap<String, EvalVar<?>> varMap = new HashMap<>();

    private <T> void addVar(EvalVar<T> evalVar) {
        varMap.put(evalVar.getName(), evalVar);
    }

    public EvalVar<?> eval(String str) {
        return null;
    }
}
