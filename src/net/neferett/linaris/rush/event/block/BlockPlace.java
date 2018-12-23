package net.neferett.linaris.rush.event.block;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

import net.neferett.linaris.rush.RushPlugin;
import net.neferett.linaris.rush.event.RushListener;
import net.neferett.linaris.rush.handler.Step;
import net.neferett.linaris.rush.handler.Team;

public class BlockPlace extends RushListener {
    public BlockPlace(final RushPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        if (Step.isStep(Step.LOBBY)) {
            if (!player.isOp()) {
                event.setCancelled(true);
            }
        } else {
            final Location blockLocation = block.getLocation();
            final Team playerTeam = Team.getPlayerTeam(player);
            for (final Team team : Team.allTeams) {
                if (team.getBedLocation() != null && blockLocation.distance(team.getBedLocation()) <= 2) {
                    if (playerTeam == team) {
                        event.setCancelled(true);
                        player.sendMessage(RushPlugin.prefix + ChatColor.RED + "Vous ne pouvez pas poser de blocs à côté de votre lit.");
                    } else if (block.getType() != Material.TNT) {
                        event.setCancelled(true);
                        player.sendMessage(RushPlugin.prefix + ChatColor.RED + "Vous devez obligatoirement faire exploser le lit de vos ennemis avec de la TNT.");
                    }
                    break;
                }
            }
        }
    }
}
