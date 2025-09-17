package wolfwriter.dreamskip.dreamskip;

import org.bukkit.boss.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import wolfwriter.dreamskip.DreamSkip;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SleepListener implements Listener {

    private final DreamSkip plugin;
    private final Set<Player> sleepingPlayers = new HashSet<>();
    private final Map<World, BossBar> bossBars = new HashMap<>();

    public SleepListener(DreamSkip plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        String worldName = world.getName();

        if (!plugin.getConfigManager().isWorldEnabled(worldName)) return;

        if (world.getTime() < 12541 || world.getTime() > 23458) return;

        boolean enabled = plugin.getConfigManager().isWorldEnabled(worldName);

        if (!enabled) {
            // Stelle sicher, dass Minecraft Vanilla-Schlafsystem aktiv ist
            world.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, 100); // z. B. 100% müssen schlafen
            return; // Plugin greift nicht ein
        }


        // Blockiere Minecrafts automatische Nachtüberspringung
        world.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, 101);

        sleepingPlayers.add(player);

        int required = plugin.getConfigManager().getRequiredPlayers(worldName);
        int online = world.getPlayers().size();
        int sleeping = (int) sleepingPlayers.stream().filter(p -> p.getWorld().equals(world)).count();

        updateBossBar(world, sleeping, required);

        if (online < required) return;

        if (sleeping >= required) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    world.setTime(0);
                    world.setStorm(false);
                    world.setThundering(false);

                    for (Player sleeper : sleepingPlayers) {
                        sleeper.wakeup(true);
                    }

                    sleepingPlayers.clear();
                    hideBossBar(world);
                }
            }.runTaskLater(plugin, 40L);

        }
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        sleepingPlayers.remove(player);

        int required = plugin.getConfigManager().getRequiredPlayers(world.getName());
        int sleeping = (int) sleepingPlayers.stream().filter(p -> p.getWorld().equals(world)).count();

        if (sleeping == 0) {
            hideBossBar(world);
        } else {
            updateBossBar(world, sleeping, required);
        }
    }

    private void updateBossBar(World world, int sleeping, int required) {
        BossBar bar = bossBars.computeIfAbsent(world, w ->
                Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SEGMENTED_10)
        );

        bar.setTitle("§ePlayers in bed: §a" + sleeping + "§7/§a" + required);
        bar.setProgress(Math.min(1.0, (double) sleeping / required));
        bar.setVisible(true);

        for (Player p : world.getPlayers()) {
            bar.addPlayer(p);
        }
    }

    private void hideBossBar(World world) {
        BossBar bar = bossBars.remove(world); // Entfernt aus der Map!
        if (bar != null) {
            bar.setVisible(false);
            bar.removeAll();
        }
    }


}
