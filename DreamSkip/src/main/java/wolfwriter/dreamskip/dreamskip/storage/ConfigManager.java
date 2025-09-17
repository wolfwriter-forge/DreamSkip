package wolfwriter.dreamskip.dreamskip.storage;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigManager {

    private final JavaPlugin plugin;
    private final File configFile;
    private final Gson gson;
    private Map<String, WorldConfig> worldConfigs;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "dreamskip_worldconfig.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.worldConfigs = new HashMap<>();
    }

    public void loadConfig() {
        plugin.getDataFolder().mkdirs();
        if (!configFile.exists()) {
            plugin.getLogger().info("Konfigurationsdatei nicht gefunden, erstelle neue...");
            saveConfig(); // Erstellt leere Datei
            return;
        }

        try (Reader reader = new FileReader(configFile)) {
            Type type = new TypeToken<Map<String, WorldConfig>>() {}.getType();
            Map<String, WorldConfig> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                worldConfigs.putAll(loaded);
                plugin.getLogger().info("DreamSkip-Konfiguration erfolgreich geladen.");
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Fehler beim Laden der Konfiguration: " + e.getMessage());
        }
    }

    public void saveConfig() {
        plugin.getDataFolder().mkdirs(); // Sicherheitshalber auch beim Speichern

        try (Writer writer = new FileWriter(configFile)) {
            gson.toJson(worldConfigs, writer);
            plugin.getLogger().info("DreamSkip-Konfiguration erfolgreich gespeichert.");
        } catch (IOException e) {
            plugin.getLogger().warning("Fehler beim Speichern der Konfiguration: " + e.getMessage());
        }
    }


    public boolean isWorldEnabled(String world) {
        return worldConfigs.getOrDefault(world, new WorldConfig(false, 1)).plugin_online;
    }


    public int getRequiredPlayers(String world) {
        return worldConfigs.getOrDefault(world, new WorldConfig(false, 1)).Playeramount;
    }

    public void setWorldConfig(String world, boolean enabled, int amount) {
        worldConfigs.put(world, new WorldConfig(enabled, amount));
    }

    public Map<String, WorldConfig> getAllConfigs() {
        return worldConfigs;
    }

    public static class WorldConfig {
        boolean plugin_online;
        int Playeramount;

        public WorldConfig(boolean plugin_online, int Playeramount) {
            this.plugin_online = plugin_online;
            this.Playeramount = Playeramount;
        }
    }


}
