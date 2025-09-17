package wolfwriter.dreamskip.dreamskip;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import wolfwriter.dreamskip.DreamSkip;
import wolfwriter.dreamskip.dreamskip.storage.ConfigManager;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SleepCommand implements CommandExecutor, TabCompleter {

    private final DreamSkip plugin;
    private final ConfigManager config;

    public SleepCommand(DreamSkip plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sleep.use")) {
            sender.sendMessage("§cYou don't have permission.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§cUse §e/sleep help §cfor assistance.");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "on" -> {
                if (args.length < 2) {
                    sender.sendMessage("§cPlease specify a world: §e/sleep on <world");
                    return true;
                }
                String world = args[1];
                config.setWorldConfig(world, true, config.getRequiredPlayers(world));
                config.saveConfig();
                sender.sendMessage("§aDreamSkip enabled in world §e" + world + " §a.");
            }

            case "off" -> {
                if (args.length < 2) {
                    sender.sendMessage("§cPlease specify a world: §e/sleep off <world>");
                    return true;
                }

                String worldName = args[1];
                World world = Bukkit.getWorld(worldName);

                if (world == null) {
                    sender.sendMessage("§cWorld §e" + worldName + " §cdoes not exist.");
                    return true;
                }

                // Plugin-Konfiguration aktualisieren
                config.setWorldConfig(worldName, false, config.getRequiredPlayers(worldName));
                config.saveConfig();

                // Vanilla-Schlafsystem aktivieren
                world.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, 100);

                sender.sendMessage("§cDreamSkip disabled in world §e" + worldName + " §c. Vanilla sleep system is now active.");
            }


            case "playeramount" -> {
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: §e/sleep playeramount <amount> <world>");
                    return true;
                }
                try {
                    int amount = Integer.parseInt(args[1]);
                    String world = args[2];
                    boolean enabled = config.isWorldEnabled(world);
                    config.setWorldConfig(world, enabled, amount);
                    config.saveConfig();
                    sender.sendMessage("§aRequired player count in §e" + world + " §aset to §b" + amount);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid number: " + args[1]);
                }
            }
            case "list" -> {
                sender.sendMessage("§eConfigured worlds:");
                for (String w : config.getAllConfigs().keySet()) {
                    boolean active = config.isWorldEnabled(w);
                    int amount = config.getRequiredPlayers(w);
                    sender.sendMessage(" - §a" + w + " §7[enabled: " + active + ", players: " + amount + "]");
                }
            }

            case "reload" -> {
                config.loadConfig();
                sender.sendMessage("§aConfiguration reloaded.");
            }

            case "help" -> {
                sender.sendMessage("§eDreamSkip Commands:");
                sender.sendMessage(" §7/sleep on <world> §8– Enables the plugin in a world");
                sender.sendMessage(" §7/sleep off <world> §8– Disables the plugin in a world");
                sender.sendMessage(" §7/sleep playeramount <amount> <world> §8– Sets the minimum required players");
                sender.sendMessage(" §7/sleep list §8– Shows all configured worlds");
                sender.sendMessage(" §7/sleep reload §8– Reloads the configuration");
                sender.sendMessage(" §7/sleep help §8– Displays this help menu");
            }

            default -> sender.sendMessage("§cUnknown command. Use §e/sleep help");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase("sleep")) return Collections.emptyList();

        if (args.length == 1) {
            return filterPrefix(args[0], List.of("on", "off", "playeramount", "list", "reload", "help"));
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "on", "off" -> {
                    return filterPrefix(args[1], getWorldNames());
                }
                case "playeramount" -> {
                    return filterPrefix(args[1], List.of("1", "2", "3", "4", "5"));
                }
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("playeramount")) {
            return filterPrefix(args[2], getWorldNames());
        }

        return Collections.emptyList();
    }

    private List<String> getWorldNames() {
        return Bukkit.getWorlds().stream()
                .map(world -> world.getName())
                .collect(Collectors.toList());
    }

    private List<String> filterPrefix(String input, List<String> options) {
        return options.stream()
                .filter(opt -> opt.toLowerCase().startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }

}
