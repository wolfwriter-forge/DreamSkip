package wolfwriter.dreamskip;

import org.bukkit.plugin.java.JavaPlugin;
import wolfwriter.dreamskip.dreamskip.SleepCommand;
import wolfwriter.dreamskip.dreamskip.SleepListener;
import wolfwriter.dreamskip.dreamskip.storage.ConfigManager;

public final class DreamSkip extends JavaPlugin {

    private static DreamSkip instance;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        getServer().getPluginManager().registerEvents(new SleepListener(this), this);
        getCommand("sleep").setExecutor(new SleepCommand(this));
        getCommand("sleep").setTabCompleter(new SleepCommand(this));

        getLogger().info("------------------------------------------------");
        getLogger().info("|    DreamSkip   has been successfully started  |");
        getLogger().info("|                                               |");
        getLogger().info("|  Author:        Wolfwriter                    |");
        getLogger().info("|  Version:       " + getDescription().getVersion() + "                       |");
        getLogger().info("|  Server:        " + getServer().getName() + " (" + getServer().getVersion() + ") |");
        getLogger().info("|  Minecraft:     " + getServer().getMinecraftVersion() + "                  |");
        getLogger().info("------------------------------------------------");
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        configManager.saveConfig();

        getLogger().info("--------------------------------------------------------------");
        getLogger().info("|    DreamSkip plugin has been disabled                        |");
        getLogger().info("|                                                              |");
        getLogger().info("|  Plugin Name:   DreamSkip                                    |");
        getLogger().info("|  Author:         Wolfwriter                                  |");
        getLogger().info("|  Version:        " + getDescription().getVersion() + "                                |");
        getLogger().info("|  Server Type:    " + getServer().getName() + "                                    |");
        getLogger().info("|  Server Version: " + getServer().getVersion() + "                   |");
        getLogger().info("|  Minecraft:      " + getServer().getMinecraftVersion() + "                           |");
        getLogger().info("|                                                              |");
        getLogger().info("|  All config changes have been stopped.                       |");
        getLogger().info("|  Thanks for using DreamSkip – see you next time!             |");
        getLogger().info("--------------------------------------------------------------");

    // Plugin shutdown logic
    }

    public static DreamSkip getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }


}
