package net.neferett.linaris.rush.event.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.neferett.linaris.rush.RushPlugin;
import net.neferett.linaris.rush.event.RushListener;
import net.neferett.linaris.rush.handler.Step;

public class PlayerDeath extends RushListener {
    public PlayerDeath(final RushPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        if (Step.isStep(Step.LOBBY)) {
            event.setDeathMessage(null);
            event.getDrops().clear();
            event.setDroppedExp(0);
        }
    }
}
