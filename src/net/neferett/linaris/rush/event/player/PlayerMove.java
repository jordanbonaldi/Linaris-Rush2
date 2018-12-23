package net.neferett.linaris.rush.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import net.neferett.linaris.rush.RushPlugin;
import net.neferett.linaris.rush.event.RushListener;
import net.neferett.linaris.rush.handler.Step;
import net.neferett.linaris.rush.handler.Team;

public class PlayerMove extends RushListener {
    public PlayerMove(final RushPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final int y = event.getTo().getBlockY();
        Team team = null;
        if (event.getFrom().getBlockY() != y && y <= 0 && (!Step.isStep(Step.IN_GAME) || (team = Team.getPlayerTeam(player)) == null)) {
            player.teleport(!Step.isStep(Step.IN_GAME) || team == null || team.getBedLocation() == null ? this.plugin.lobbyLocation : team.getBedLocation());
        }
    }
}
