package net.neferett.linaris.rush.event.entity;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;

import net.neferett.linaris.rush.RushPlugin;
import net.neferett.linaris.rush.event.RushListener;
import net.neferett.linaris.rush.handler.Step;
import net.neferett.linaris.rush.handler.Team;

public class EntityExplode extends RushListener {
    public EntityExplode(final RushPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEntityExplode(final EntityExplodeEvent event) {
        if (Step.isStep(Step.LOBBY)) {
            event.setCancelled(true);
            return;
        } else if (Step.isStep(Step.IN_GAME)) {
            Player player = null;
            Team playerTeam = null;
            if (event.getEntity() instanceof TNTPrimed) {
                final TNTPrimed tnt = (TNTPrimed) event.getEntity();
                if (tnt.getSource() instanceof Player) {
                    player = (Player) tnt.getSource();
                    playerTeam = Team.getPlayerTeam(player);
                }
            }
            boolean check = false;
            final Iterator<Block> blocks = event.blockList().iterator();
            while (blocks.hasNext()) {
                final Block block = blocks.next();
                if (block.getType() == Material.BED_BLOCK) {
                    blocks.remove();
                    final Location blockLocation = block.getLocation();
                    if (playerTeam != null && playerTeam.getBedLocation() != null) {
                        final Location bedLocation = playerTeam.getBedLocation();
                        if (!check && blockLocation.distance(bedLocation) <= 1) {
                            check = true;
                            player.sendMessage(RushPlugin.prefix + ChatColor.RED + "Vous ne pouvez pas faire exploser votre propre lit.");
                            continue;
                        }
                    }
                    if (!check) {
                        for (final Team team : Team.allTeams) {
                            if (team.getBedLocation() != null && blockLocation.equals(team.getBedLocation())) {
                                check = true;
                                Bukkit.broadcastMessage(RushPlugin.prefix + (player == null ? ChatColor.GRAY + "Le lit de l'équipe " + team.getColor() + team.getDisplayName() + ChatColor.GRAY + " vient de se faire exploser !" : playerTeam.getColor() + "" + player.getName() + ChatColor.GRAY + "" + " vient d'exploser le lit de l'équipe " + team.getColor() + team.getDisplayName()));
                                if (player != null) {
                                    this.plugin.getData(player).addCoins(2);
                                }
                                team.setBedLocation(null);
                                break;
                            }
                        }
                    }
                    block.setType(Material.AIR);
                }
            }
        }
    }
}
