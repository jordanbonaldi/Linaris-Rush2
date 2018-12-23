package net.neferett.linaris.rush.event.block;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPhysicsEvent;

import net.neferett.linaris.rush.RushPlugin;
import net.neferett.linaris.rush.event.RushListener;
import net.neferett.linaris.rush.handler.Step;

public class BlockPhysics extends RushListener {
    public BlockPhysics(final RushPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBlockPhysics(final BlockPhysicsEvent event) {
        if (!Step.isStep(Step.LOBBY) && event.getBlock().getType() == Material.BED_BLOCK) {
            event.setCancelled(true);
        }
    }
}
