package net.neferett.linaris.rush.event.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;

import net.neferett.linaris.rush.RushPlugin;
import net.neferett.linaris.rush.event.RushListener;
import net.neferett.linaris.rush.handler.Step;

public class PlayerDropItem extends RushListener {
    public PlayerDropItem(final RushPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        if (Step.isStep(Step.LOBBY)) {
            event.setCancelled(true);
        }
    }
}
