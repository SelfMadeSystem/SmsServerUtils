package uwu.smsgamer.smsserverutils.utils;

import org.bukkit.command.CommandSender;
import uwu.smsgamer.smsserverutils.evaluator.*;

public class EvalUtils {
    public static Evaluator newEvaluator(CommandSender sender) {
        return new Evaluator(new EvalVar.Str("name", sender.getName()));
    }
}
