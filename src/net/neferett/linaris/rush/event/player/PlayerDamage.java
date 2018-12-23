package net.neferett.linaris.rush.event.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import net.neferett.linaris.rush.RushPlugin;
import net.neferett.linaris.rush.event.RushListener;
import net.neferett.linaris.rush.handler.Step;
import net.neferett.linaris.rush.handler.Team;
import net.neferett.linaris.rush.util.ItemBuilder;

public class PlayerDamage extends RushListener {
    public PlayerDamage(final RushPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            final Team team = Team.getPlayerTeam(player);
            if (!Step.isStep(Step.IN_GAME) || Step.isStep(Step.POST_GAME) || team == null) {
                event.setCancelled(true);
            } else if (player.getHealth() - event.getDamage() <= 0) {
                event.setCancelled(true);
                player.playEffect(EntityEffect.HURT);
                this.resetPlayer(player);
                Player killer = null;
                Team killerTeam = null;
                if (event instanceof EntityDamageByEntityEvent) {
                    final EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
                    if (damageEvent.getDamager() instanceof Player) {
                        killer = (Player) damageEvent.getDamager();
                        killerTeam = Team.getPlayerTeam(killer);
                    }
                }
                Bukkit.broadcastMessage(team.getColor() + player.getName() + ChatColor.WHITE + " " + (killer == null ? "est mort." : "a été tué par " + (killerTeam == null ? ChatColor.GRAY : killerTeam.getColor()) + killer.getName()));
                if (team.getBedLocation() != null) {
                    final Location teleport = team.getBedLocation().clone().add(2, 0, 0);
                    final int i = 0, max = 256 - teleport.getBlock().getY();
                    while (teleport.getBlock().getType() != Material.AIR && i < max) {
                        teleport.add(0, 1, 0);
                    }
                    player.teleport(teleport);
                } else {
                    player.teleport(this.plugin.lobbyLocation);
                    this.plugin.playerLoose(player, true);
                    player.sendMessage(RushPlugin.prefix + ChatColor.RED + "Vous avez perdu la partie car votre lit a été cassé.");
                }
            }
        }
    }

    private void resetPlayer(final Player player) {
        final World world = player.getWorld();
        final Location location = player.getLocation();
        for (final ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR && item.getType() != Material.WOOD_SWORD) {
                world.dropItem(location, item);
            }
        }
        for (final ItemStack item : player.getEquipment().getArmorContents()) {
            if (item != null && item.getType() != Material.AIR) {
                world.dropItem(location, item);
            }
        }
        player.setFireTicks(0);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExhaustion(5.0F);
        player.setFallDistance(0);
        player.setExp(0.0F);
        player.setLevel(0);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.closeInventory();
        for (final PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.getInventory().addItem(new ItemBuilder(Material.WOOD_SWORD).addEnchantment(Enchantment.LOOT_BONUS_MOBS, 5).build());
    }
}
