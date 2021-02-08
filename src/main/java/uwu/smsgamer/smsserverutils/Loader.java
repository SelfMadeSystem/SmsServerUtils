package uwu.smsgamer.smsserverutils;

import me.godead.lilliputian.*;
import org.bukkit.plugin.java.JavaPlugin;

public class Loader extends JavaPlugin {
    private static Loader instance;

    public static Loader getInstance() {
        if (instance == null) instance = new Loader();
        return instance;
    }

    public Loader() {
        instance = this;
    }

    @Override
    public void onLoad() {
        final Lilliputian lilliputian = new Lilliputian(this);
        lilliputian.getDependencyBuilder()
          .addDependency(new Dependency(Repository.JITPACK,
            "com.github.True-cc", "SenAPI", "0.2.3-rc1"))
          .addDependency(new Dependency(Repository.JITPACK,
            "com.github.retrooper", "packetevents", "v1.8-pre-3"))
          .addDependency(new Dependency(Repository.MAVENCENTRAL,
            "org.python", "jython-slim", "2.7.2"))
          .loadDependencies();

        SmsServerUtils.getInstance().onLoad();
    }

    @Override
    public void onEnable() {
        SmsServerUtils.getInstance().onEnable();
    }

    @Override
    public void onDisable() {
        SmsServerUtils.getInstance().onDisable();
    }
}
