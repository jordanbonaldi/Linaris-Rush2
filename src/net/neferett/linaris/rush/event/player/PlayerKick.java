package net.neferett.linaris.rush.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerKickEvent;

import net.neferett.linaris.rush.RushPlugin;
import net.neferett.linaris.rush.event.RushListener;

public class PlayerKick extends RushListener {
    public PlayerKick(final RushPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerKick(final PlayerKickEvent event) {
        event.setLeaveMessage(null);
        final Player player = event.getPlayer();
        player.getInventory().clear();
        this.plugin.playerLoose(player, false);
    }
}
