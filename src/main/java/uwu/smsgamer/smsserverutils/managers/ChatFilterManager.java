package uwu.smsgamer.smsserverutils.managers;

import io.github.retrooper.packetevents.event.impl.*;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import io.github.retrooper.packetevents.packetwrappers.play.in.chat.WrappedPacketInChat;
import io.github.retrooper.packetevents.packetwrappers.play.out.chat.WrappedPacketOutChat;
import io.github.retrooper.packetevents.utils.nms.NMSUtils;
import io.github.retrooper.packetevents.utils.reflection.Reflection;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.player.*;
import org.bukkit.event.server.TabCompleteEvent;
import uwu.smsgamer.senapi.utils.StringUtils;
import uwu.smsgamer.smsserverutils.config.ConfigManager;
import uwu.smsgamer.smsserverutils.evaluator.*;
import uwu.smsgamer.smsserverutils.utils.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ChatFilterManager {
    private static ChatFilterManager instance;
    private static Class<?> iChatBaseComponentClass;

    static {
        try {
            iChatBaseComponentClass = NMSUtils.getNMSClass("IChatBaseComponent");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private final YamlConfiguration conf;

    public static ChatFilterManager getInstance() {
        if (instance == null) instance = new ChatFilterManager();
        return instance;
    }

    public ChatFilterManager() {
        instance = this;
        conf = ConfigManager.getConfig("chat-filter");
    }

    public void packetSendEvent(PacketPlaySendEvent e) {
        if (!conf.contains("outgoing-chat")) return;

        WrappedPacketOutChat chat = new WrappedPacketOutChat(e.getNMSPacket());
        String msg = getMessage(chat);
        if (msg == null || msg.isEmpty()) msg = getMessage1(chat);

        Evaluator evaluator = EvalUtils.newEvaluator(e.getPlayer());
        evaluator.addVar(new EvalVar.Str("msg", msg));
        evaluator.addVar(new EvalVar.Str("name", e.getPlayer().getName()));
        for (String key : conf.getConfigurationSection("outgoing-chat").getKeys(false)) {
            ConfigurationSection section = conf.getConfigurationSection("outgoing-chat." + key);
            try {
                EvalVar<?> result = evaluator.eval(section.getString("check"));
                if (result.value.getClass().equals(Boolean.class)) {
                    if ((Boolean) result.value) {
                        if (section.getBoolean("cancel")) e.setCancelled(true);
                        else {
                            String s = evaluator.eval(section.getString("replacement")).value.toString();
                            s = ChatUtils.toChatString(s, e.getPlayer());
                            WrappedPacketOutChat newChat = new WrappedPacketOutChat(
                              s, WrappedPacketOutChat.ChatPosition.CHAT,
                              e.getPlayer().getUniqueId(), false);
                            e.setNMSPacket(new NMSPacket(newChat.asNMSPacket()));
                        }
                        ChatUtils.execCmd(section.getStringList("execute-commands"), e.getPlayer());
                    }
                } else {
                    System.out.println(key + ":" + result.getClass());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void commandReceiveEvent(PlayerCommandPreprocessEvent e) {
        String message = e.getMessage();
        String[] args = message.substring(message.indexOf(" ") + 1).split(" ");
        Evaluator evaluator = EvalUtils.newEvaluator(e.getPlayer());
        evaluator.addVar(new EvalVar.Str("msg", message));
        int indOf = message.indexOf(" ");
        evaluator.addVar(new EvalVar.Str("label", message.substring(0, indOf < 0 ? message.length() : indOf)));
        evaluator.addVar(new EvalVar.Str("name", e.getPlayer().getName()));
        for (String key : conf.getConfigurationSection("incoming-command").getKeys(false)) {
            ConfigurationSection section = conf.getConfigurationSection("incoming-command." + key);
            try {
                String check = section.getString("check");
                check = StringUtils.replaceArgsPlaceholders(check, args);
                EvalVar<?> result = evaluator.eval(check);
                if (result.value.getClass().equals(Boolean.class)) {
                    if ((Boolean) result.value) {
                        if (section.getBoolean("cancel")) e.setCancelled(true);
                        else {
                            String s = evaluator.eval(section.getString("replacement")).value.toString();
                            s = ChatUtils.toChatString(s, e.getPlayer());
                            e.setMessage(s);
                        }
                        execCmd(section.getStringList("execute-commands"), args, e.getPlayer());
                    }
                } else {
                    System.out.println(key + ":" + result.getClass());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void chatReceiveEvent(AsyncPlayerChatEvent e) {
        String message = e.getMessage();
        String[] args = message.substring(message.indexOf(" ") + 1).split(" ");
        Evaluator evaluator = EvalUtils.newEvaluator(e.getPlayer());
        evaluator.addVar(new EvalVar.Str("msg", message));
        int indOf = message.indexOf(" ");
        evaluator.addVar(new EvalVar.Str("label", message.substring(0, indOf < 0 ? message.length() : indOf)));
        evaluator.addVar(new EvalVar.Str("name", e.getPlayer().getName()));
        for (String key : conf.getConfigurationSection("incoming-chat").getKeys(false)) {
            ConfigurationSection section = conf.getConfigurationSection("incoming-chat." + key);
            try {
                String check = section.getString("check");
                check = StringUtils.replaceArgsPlaceholders(check, args);
                EvalVar<?> result = evaluator.eval(check);
                if (result.value.getClass().equals(Boolean.class)) {
                    if ((Boolean) result.value) {
                        if (section.getBoolean("cancel")) e.setCancelled(true);
                        else {
                            String s = evaluator.eval(section.getString("replacement")).value.toString();
                            s = ChatUtils.toChatString(s, e.getPlayer());
                            e.setMessage(s);
                        }
                    }
                    execCmd(section.getStringList("execute-commands"), args, e.getPlayer());
                } else {
                    System.out.println(key + ":" + result.getClass());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void tabReceiveEvent(TabCompleteEvent e) {

    }

    public static void execCmd(List<String> commands, String[] args, CommandSender player) {
        if (commands != null) for (String s : commands) ChatUtils.execCmd(StringUtils.replaceArgsPlaceholders(s, args), player);
    }

    public String getMessage(WrappedPacketOutChat chat) {
        final Object iChatBaseObj = chat.readObject(0, iChatBaseComponentClass);

        try {
            Object contentString = Reflection.getMethod(iChatBaseComponentClass, String.class, 0).invoke(iChatBaseObj);
            return contentString.toString();
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getMessage1(WrappedPacketOutChat chat) {
        final Object iChatBaseObj = chat.readObject(0, iChatBaseComponentClass);

        try {
            Object contentString = Reflection.getMethod(iChatBaseComponentClass, String.class, 1).invoke(iChatBaseObj);
            return contentString.toString();
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String name = "Sms_Gamer_3808";
        String msg = "&cHAWK: Sms_Gamer_3808 failed flight. VL: 13";
        String msgNoColor = "HAWK: Sms_Gamer_3808 failed flight. VL: 13";
        Evaluator evaluator = new Evaluator(null, new EvalVar.Str("msg", msg),
          new EvalVar.Str("no-color-msg", msgNoColor),
          new EvalVar.Str("name", name));
        System.out.println(evaluator.eval("(no-color-msg startsWith \"HAWK:\") && (msg contains \"VL:\") && (msg contains name)"));
        System.out.println(evaluator.eval("name s+ \" failed: \" s+ (no-color-msg substr ((no-color-msg indexOf \" failed \") + 8) " +
          "(no-color-msg indexOf \".\"))" +
          " s+ \" with VL: \"" +
          " s+ (no-color-msg substrr ((no-color-msg indexOf \"VL:\") + 4))"));
    }
}
