package net.neferett.linaris.rush.event.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;

import net.neferett.linaris.rush.RushPlugin;
import net.neferett.linaris.rush.event.RushListener;
import net.neferett.linaris.rush.handler.Step;
import net.neferett.linaris.rush.handler.Team;

public class PlayerPickupItem extends RushListener {
    public PlayerPickupItem(final RushPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
        if (Step.isStep(Step.LOBBY) || Team.getPlayerTeam(event.getPlayer()) == null) {
            event.setCancelled(true);
        }
    }
}
