package uwu.smsgamer.smsserverutils.managers;

import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.packetwrappers.play.out.chat.WrappedPacketOutChat;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import uwu.smsgamer.smsserverutils.config.ConfigManager;
import uwu.smsgamer.smsserverutils.evaluator.*;
import uwu.smsgamer.smsserverutils.utils.EvalUtils;

public class ChatFilterManager {
    private static ChatFilterManager instance;

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
        String msg = chat.getMessage();
        System.out.println("Msg:" + msg); // Doesn't work on java 11 for some reason...???
//        for (String key : conf.getKeys(false)) {
//            ConfigurationSection section = conf.getConfigurationSection(key);
//            try {
//                Evaluator evaluator = EvalUtils.newEvaluator(e.getPlayer());
//                evaluator.addVar(new EvalVar.Str("msg", msg));
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
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
