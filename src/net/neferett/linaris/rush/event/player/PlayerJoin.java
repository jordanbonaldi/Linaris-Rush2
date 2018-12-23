package net.neferett.linaris.rush.event.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import net.neferett.linaris.rush.RushPlugin;
import net.neferett.linaris.rush.event.RushListener;
import net.neferett.linaris.rush.handler.Step;
import net.neferett.linaris.rush.handler.Team;
import net.neferett.linaris.rush.scheduler.BeginCountdown;

public class PlayerJoin extends RushListener {
    public PlayerJoin(final RushPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        player.getInventory().clear();
        if (!Step.isStep(Step.LOBBY) && player.hasPermission("games.join")) {
            event.setJoinMessage(null);
            player.setGameMode(GameMode.CREATIVE);
            for (final Player online : Bukkit.getOnlinePlayers()) {
                if (player != online && Team.getPlayerTeam(online) != null) {
                    online.hidePlayer(player);
                }
            }
        } else if (Step.isStep(Step.LOBBY)) {
            event.setJoinMessage(RushPlugin.prefix + ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " a rejoint la partie " + ChatColor.GREEN + "(" + Bukkit.getOnlinePlayers().length + "/" + Bukkit.getMaxPlayers() + ")");
            for (final Team team : Team.allTeams) {
                if (team.getBedLocation() != null) {
                    player.getInventory().addItem(team.getIcon());
                }
            }
            this.plugin.loadData(player);
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(this.plugin.lobbyLocation);
            if (Bukkit.getOnlinePlayers().length == 2 && !BeginCountdown.started) {
                for (final Team team : Team.allTeams) {
                    if (team.getBedLocation() == null) {
                        BeginCountdown.started = true;
                        return;
                    }
                }
                new BeginCountdown(this.plugin);
            }
        }
    }
}
