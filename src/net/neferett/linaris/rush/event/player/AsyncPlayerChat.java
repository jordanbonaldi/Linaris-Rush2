package net.neferett.linaris.rush.event.player;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.neferett.linaris.rush.RushPlugin;
import net.neferett.linaris.rush.event.RushListener;
import net.neferett.linaris.rush.handler.Team;

public class AsyncPlayerChat extends RushListener {
    public AsyncPlayerChat(final RushPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final Team playerTeam = Team.getPlayerTeam(player);
        event.setFormat((playerTeam != null ? playerTeam.getColor() : ChatColor.GRAY) + player.getName() + ChatColor.WHITE + ": " + event.getMessage());
    }
}
