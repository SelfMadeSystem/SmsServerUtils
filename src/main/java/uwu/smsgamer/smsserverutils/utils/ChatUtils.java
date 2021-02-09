package uwu.smsgamer.smsserverutils.utils;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import uwu.smsgamer.senapi.utils.StringUtils;
import uwu.smsgamer.smsserverutils.config.ConfVal;
import java.util.List;

public class ChatUtils {

    public static final ConfVal<String> prefix = new ConfVal<>("prefix", "messages", "&6[&aServer&6]&r");
    public static final ConfVal<String> errorNoMessage = new ConfVal<>("messages.error-no-message", "messages",
      "%prefix% &cAn error occurred whilst executing this command.");
    public static final ConfVal<String> errorWithMessage = new ConfVal<>("messages.error-with-message", "messages",
      "%prefix% &cAn error occurred whilst executing this command: %msg%");

    public static void init() {}

    public static void execCmd(ConfVal<List<String>> command, CommandSender player) {
        for (String s : command.getValue()) execCmd(s, player);
    }

    public static void execCmd(String command, CommandSender player) {
        if (command == null || command.isEmpty()) return;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), toChatString(command, player));
    }

    public static void sendMessage(ConfVal<String> message, CommandSender player) {
        sendMessage(message.getValue(), player);
    }

    public static void sendMessage(String message, CommandSender player) {
        if (message == null || message.isEmpty()) return;
        player.sendMessage(toChatString(message, player));
    }

    public static String toChatString(String message, @Nullable CommandSender player) {
        message = message.replace("%prefix%", prefix.getValue());
        if (player instanceof OfflinePlayer)
            return StringUtils.colorize(StringUtils.replacePlaceholders((OfflinePlayer) player, message));
        else return StringUtils.colorize(message);
    }

    public static void errorOccurred(CommandSender sender) {
        sendMessage(errorNoMessage, sender);
    }

    public static void errorOccurred(CommandSender sender, String message) {
        sendMessage(errorWithMessage.getValue().replace("%msg%", message), sender);
    }
}
