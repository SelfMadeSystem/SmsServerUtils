package uwu.smsgamer.smsserverutils.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.TabCompleteEvent;
import uwu.smsgamer.smsserverutils.managers.ChatFilterManager;

public class BukkitListener implements Listener {
    private static BukkitListener instance;

    public static BukkitListener getInstance() {
        if (instance == null) instance = new BukkitListener();
        return instance;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        System.out.println(e.getMessage());
        ChatFilterManager.getInstance().commandReceiveEvent(e);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        System.out.println(e.getMessage());
        ChatFilterManager.getInstance().chatReceiveEvent(e);
    }

    @EventHandler
    public void onTab(TabCompleteEvent e) {
        System.out.println(e.getBuffer());
        if (e.getSender() instanceof Player) ChatFilterManager.getInstance().tabReceiveEvent(e);
    }
}
