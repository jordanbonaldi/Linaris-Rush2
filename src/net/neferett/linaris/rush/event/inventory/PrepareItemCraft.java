package net.neferett.linaris.rush.event.inventory;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

import net.neferett.linaris.rush.RushPlugin;
import net.neferett.linaris.rush.event.RushListener;

public class PrepareItemCraft extends RushListener {
    public PrepareItemCraft(final RushPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPrepareItemCraft(final PrepareItemCraftEvent event) {
        if (event.getRecipe().getResult().getType() == Material.BED) {
            event.getRecipe().getResult().setType(null);
        }
    }
}
