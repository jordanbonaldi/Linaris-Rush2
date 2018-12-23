package net.neferett.linaris.rush.event.block;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import net.neferett.linaris.rush.RushPlugin;
import net.neferett.linaris.rush.event.RushListener;
import net.neferett.linaris.rush.handler.Step;
import net.neferett.linaris.rush.handler.Team;

public class BlockBreak extends RushListener {
    public BlockBreak(final RushPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        final Team playerTeam = Team.getPlayerTeam(player);
        if (Step.isStep(Step.LOBBY) || playerTeam == null) {
            if (!player.isOp()) {
                event.setCancelled(true);
            }
        } else if (block.getType() == Material.MOB_SPAWNER) {
            event.setCancelled(true);
            player.sendMessage(RushPlugin.prefix + ChatColor.RED + "Vous ne pouvez pas casser les spawners.");
        } else if (block.getType() == Material.BED_BLOCK) {
            final Location loc = block.getLocation();
            if (loc.distance(playerTeam.getBedLocation()) <= 2) {
                event.setCancelled(true);
                player.sendMessage(RushPlugin.prefix + ChatColor.RED + "Vous ne pouvez pas déplacer votre lit.");
                return;
            }
            final Team team = Team.RED == playerTeam ? Team.BLUE : Team.RED;
            if (team.getBedLocation() != null && loc.distance(team.getBedLocation()) <= 2) {
                Bukkit.broadcastMessage(RushPlugin.prefix + playerTeam.getColor() + "" + player.getName() + ChatColor.GRAY + "" + " vient de casser le lit de l'équipe " + team.getColor() + team.getDisplayName());
                this.plugin.getData(player).addCoins(2);
                team.setBedLocation(null);
            }
        }
    }
}
