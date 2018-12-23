package net.neferett.linaris.rush.scheduler;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.neferett.linaris.rush.RushPlugin;

public class HubTeleportation extends BukkitRunnable {
    private final RushPlugin plugin;
    private final Player player;

    public HubTeleportation(final RushPlugin plugin, final Player player) {
        this.plugin = plugin;
        this.player = player;
        this.runTaskLater(plugin, 300);
    }

    @Override
    public void run() {
        this.plugin.teleportToLobby(this.player);
    }
}
