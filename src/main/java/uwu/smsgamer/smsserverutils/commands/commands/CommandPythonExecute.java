package uwu.smsgamer.smsserverutils.commands.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import uwu.smsgamer.smsserverutils.SmsServerUtils;
import uwu.smsgamer.smsserverutils.commands.SmsCommand;
import uwu.smsgamer.smsserverutils.utils.*;

import java.util.List;

public class CommandPythonExecute extends SmsCommand {
    public CommandPythonExecute() {
        super("python-execute", true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (testPermission(sender)) {
            Bukkit.getScheduler().runTaskAsynchronously(SmsServerUtils.getInstance().plugin, () -> {
                ScriptReturn result = PyUtils.exec(String.join(" ", args), 5);
                sender.sendMessage("Oki" + result.type + ":" + result.object);
            });
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
