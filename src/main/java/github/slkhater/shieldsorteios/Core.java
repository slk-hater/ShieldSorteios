package github.slkhater.shieldsorteios;

import github.slkhater.shieldsorteios.commands.SortearCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Core extends JavaPlugin {
    private static Core instance;
    public static Core getInstance() {
        return instance;
    }
    private void setInstance(Core instance) {
        Core.instance = instance;
    }
    public void onEnable() {
        setInstance(this);
        saveDefaultConfig();
        getCommand("sortear").setExecutor(new SortearCommand());
    }
}
