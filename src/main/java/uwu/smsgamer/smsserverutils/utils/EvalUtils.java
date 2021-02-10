package uwu.smsgamer.smsserverutils.utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import uwu.smsgamer.senapi.ConsolePlayer;
import uwu.smsgamer.smsserverutils.evaluator.*;

public class EvalUtils {
    public static Evaluator newEvaluator(CommandSender sender) {
        return new Evaluator(sender instanceof OfflinePlayer ? (OfflinePlayer) sender : ConsolePlayer.getInstance(),
          new EvalVar.Str("name", sender.getName()));
    }
}
