package net.neferett.linaris.rush.event.weather;

import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.ThunderChangeEvent;

import net.neferett.linaris.rush.RushPlugin;
import net.neferett.linaris.rush.event.RushListener;

public class ThunderChange extends RushListener {
    public ThunderChange(final RushPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onThunderChange(final ThunderChangeEvent event) {
        if (event.toThunderState()) {
            event.setCancelled(true);
        }
    }
}
