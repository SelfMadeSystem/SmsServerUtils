package uwu.smsgamer.smsserverutils.evaluator;

import java.util.HashMap;

/**
 * JS/Py like evaluator for simple arithmetic based operations.
 */
public class Evaluator {
    public HashMap<String, EvalVar<?>> varMap = new HashMap<>();

    public Evaluator(EvalVar<?>... defVars) {
        for (EvalVar<?> defVar : defVars) addVar(defVar);
    }

    private <T> void addVar(EvalVar<T> evalVar) {
        varMap.put(evalVar.getName(), evalVar);
    }

    public EvalVar<?> eval(String str) {
        return new EvalTokenizer(str).tokenize().parseToVars().parseToFuns().sortFuns().tokens.get(0).toVar(this);
    }

    public static void main(String[] args) {
        Evaluator evaluator = new Evaluator(new EvalVar.Str("msg", "HAWK: Your_Mom flagged OwO VL: 7"),
          new EvalVar.Str("user", "Your_Mom"));
        System.out.println(evaluator.eval("!(msg contains user)").value);
    }
}
