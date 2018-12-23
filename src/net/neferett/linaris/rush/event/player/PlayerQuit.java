package net.neferett.linaris.rush.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import net.neferett.linaris.rush.RushPlugin;
import net.neferett.linaris.rush.event.RushListener;

public class PlayerQuit extends RushListener {
    public PlayerQuit(final RushPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        event.setQuitMessage(null);
        final Player player = event.getPlayer();
        player.getInventory().clear();
        this.plugin.playerLoose(player, false);
    }
}
