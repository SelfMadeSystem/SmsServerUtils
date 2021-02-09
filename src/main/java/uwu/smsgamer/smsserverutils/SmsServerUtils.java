package uwu.smsgamer.smsserverutils;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packettype.PacketType;
import me.godead.lilliputian.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import uwu.smsgamer.smsserverutils.commands.CommandManager;
import uwu.smsgamer.smsserverutils.config.ConfigManager;
import uwu.smsgamer.smsserverutils.listener.PacketProcessor;
import uwu.smsgamer.smsserverutils.utils.ChatUtils;

public final class SmsServerUtils {
    private static SmsServerUtils INSTANCE;
    public static SmsServerUtils getInstance() {
      if (INSTANCE == null) INSTANCE = new SmsServerUtils();
      return INSTANCE;
    }

    public final JavaPlugin plugin;

    public SmsServerUtils() {
        INSTANCE = this;
        plugin = Loader.getInstance();
    }

    public void onLoad() {

        try {
            Class.forName("io.github.retrooper.packetevents.PacketEvents", true, this.getClass().getClassLoader());
            System.out.println("==================Found PacketEvents==================");
        } catch (ClassNotFoundException e) {
            System.out.println("==================Did not find PacketEvents==================");
        }

        try {
            Class.forName("org.python.Version", true, this.getClass().getClassLoader());
            System.out.println("==================Found Jython==================");
        } catch (ClassNotFoundException e) {
            System.out.println("==================Did not find Jython==================");
        }

        /*
         * PacketEvents likes to maintain compatibility with other plugins shading the same API.
         * If some other API loaded before you, the create method will return it's PacketEvents instance
         * to maintain compatibility, otherwise you will create the instance.
         * Set the settings.
         */
        //When a player injection fails, what should happen?
        PacketEvents.create(plugin).getSettings().checkForUpdates(true) //We won't check for updates
          .injectionFailureReaction(player -> {
              /*
               * NOTE: We are OFF the main thread. Kicking is not thread safe. We will switch over to the main thread to kick.
               * By default if you don't specify a reaction, PacketEvents will kick them! If you need to modify the kick message or the whole action,
               * use this feature!
               * You NOT kicking the player will result in them staying on the server, and PacketEvents won't notice if they are sending any packets.
               * We also won't notice if the server is sending any packets to that player.
               */
              Bukkit.getScheduler().runTask(plugin, () -> player.kickPlayer("Failed to inject. Please rejoin!"));
          });
        /*
         * Access the stored instance of the PacketEvents class and load PacketEvents.
         * A few settings need to be specified before loading PacketEvents
         * as PacketEvents already uses a few in the load method.
         * To be safe, just set them all before loading.
         */
        PacketEvents.get().load();
    }

    public void onEnable() {
        ConfigManager.setup("messages", "py-config");

//        uwu.smsgamer.senapi.ConsolePlayer.getInstance();

        //Initiate PacketEvents
        PacketEvents.get().init(plugin);

        //Register our listener (class extending PacketListenerDynamic)
        //By default it is configured to listen to all packets with no filter at all.
        PacketProcessor packetProcessor = new PacketProcessor();
        //We will FILTER all packets. We don't want to listen to anything.
        packetProcessor.filterAll();
        //Now since the filter has been applied, we are no longer listening to all packets. We can now "whitelist" our wanted packets.
        //Using this feature can improve performance.

        packetProcessor.addServerSidedPlayFilter(PacketType.Play.Server.CHAT);

        PacketEvents.get().registerListener(packetProcessor);

        ChatUtils.init();
        CommandManager.setupCommands();

        for (String s : ConfigManager.configs.keySet()) ConfigManager.saveConfig(s);
    }

    public void onDisable() {
        //Terminate PacketEvents
        PacketEvents.get().terminate();
    }
}
