package net.neferett.linaris.rush.event.entity;

import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import net.neferett.linaris.rush.RushPlugin;
import net.neferett.linaris.rush.event.RushListener;
import net.neferett.linaris.rush.handler.Step;

public class EntityDamage extends RushListener {
    public EntityDamage(final RushPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEntityDamage(final EntityDamageEvent event) {
        if (Step.isStep(Step.LOBBY) || event.getEntity() instanceof Villager) {
            event.setCancelled(true);
        }
    }
}
