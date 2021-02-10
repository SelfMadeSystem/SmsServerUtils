package uwu.smsgamer.smsserverutils.managers;

import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import io.github.retrooper.packetevents.packetwrappers.play.out.chat.WrappedPacketOutChat;
import io.github.retrooper.packetevents.utils.nms.NMSUtils;
import io.github.retrooper.packetevents.utils.reflection.Reflection;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import uwu.smsgamer.smsserverutils.config.ConfigManager;
import uwu.smsgamer.smsserverutils.evaluator.*;
import uwu.smsgamer.smsserverutils.utils.*;

import java.lang.reflect.InvocationTargetException;

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

    public static ChatFilterManager getInstance() {
        if (instance == null) instance = new ChatFilterManager();
        return instance;
    }

    public ChatFilterManager() {
        instance = this;
    }

    public void packetSendEvent(PacketPlaySendEvent e) {
        YamlConfiguration conf = ConfigManager.getConfig("chat-filter");
        WrappedPacketOutChat chat = new WrappedPacketOutChat(e.getNMSPacket());
        String msg = getMessage(chat);
        if (msg == null || msg.isEmpty()) msg = getMessage1(chat);

        for (String key : conf.getKeys(false)) {
            ConfigurationSection section = conf.getConfigurationSection(key);
            try {
                Evaluator evaluator = EvalUtils.newEvaluator(e.getPlayer());
                evaluator.addVar(new EvalVar.Str("msg", msg));
                evaluator.addVar(new EvalVar.Str("no-color-msg", msg));
                evaluator.addVar(new EvalVar.Str("name", e.getPlayer().getName()));
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
                    }
                } else {
                    System.out.println(key + ":" + result.getClass());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
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
