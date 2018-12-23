package net.neferett.linaris.rush.event.entity;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import net.neferett.linaris.rush.RushPlugin;
import net.neferett.linaris.rush.event.RushListener;
import net.neferett.linaris.rush.handler.Step;
import net.neferett.linaris.rush.handler.Team;

public class EntityDamageByPlayer extends RushListener {
    public EntityDamageByPlayer(final RushPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEntityDamageByPlayer(final EntityDamageByEntityEvent event) {
        if (!Step.isStep(Step.IN_GAME) || event.getDamager() instanceof Player && Team.getPlayerTeam((Player) event.getDamager()) == null) {
            event.setCancelled(true);
        }
    }
}
