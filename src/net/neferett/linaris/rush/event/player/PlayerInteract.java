package net.neferett.linaris.rush.event.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.neferett.linaris.rush.RushPlugin;
import net.neferett.linaris.rush.event.RushListener;
import net.neferett.linaris.rush.handler.Step;
import net.neferett.linaris.rush.handler.Team;

public class PlayerInteract extends RushListener {
    public PlayerInteract(final RushPlugin plugin) {
        super(plugin);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (Step.isStep(Step.LOBBY)) {
            if (event.hasItem()) {
                final ItemStack item = event.getItem();
                if (item.getType() == Material.INK_SACK && item.hasItemMeta()) {
                    for (final Team team : Team.getTeams()) {
                        if (item.isSimilar(team.getIcon())) {
                            final String displayName = team.getDisplayName();
                            final Team playerTeam = Team.getPlayerTeam(player);
                            if (playerTeam != team) {
                                if (Bukkit.getOnlinePlayers().length > 1 && team.getScoreboardTeam().getSize() >= Bukkit.getOnlinePlayers().length / 2) {
                                    player.sendMessage(RushPlugin.prefix + ChatColor.GRAY + "Impossible de rejoindre cette équipe, trop de joueurs !");
                                } else {
                                    if (playerTeam != null) {
                                        playerTeam.removePlayer(player);
                                    }
                                    team.addPlayer(player);
                                    player.sendMessage(RushPlugin.prefix + ChatColor.GRAY + "Vous rejoignez l'équipe " + team.getColor() + displayName);
                                }
                            }
                            break;
                        }
                    }
                    player.updateInventory();
                    return;
                }
            }
            if (!player.isOp()) {
                event.setCancelled(true);
                return;
            }
        }
        final Block block = event.getClickedBlock();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block != null && block.getType() == Material.BED_BLOCK) {
            event.setCancelled(true);
            final Team team = Team.getPlayerTeam(player);
            if (!Step.isStep(Step.LOBBY) && team != null && team.getBedLocation() != null && block.getLocation().distance(team.getBedLocation()) <= 1) {
                player.sendMessage(RushPlugin.prefix + ChatColor.RED + "Vous n'avez pas besoin de dormir dans votre lit.");
            }
        }
    }
}
