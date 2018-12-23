package net.neferett.linaris.rush.event.weather;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;

import net.neferett.linaris.rush.RushPlugin;
import net.neferett.linaris.rush.event.RushListener;

public class WeatherChange extends RushListener {
    public WeatherChange(final RushPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onWeatherChange(final WeatherChangeEvent event) {
        final World world = event.getWorld();
        if (!world.isThundering() && !world.hasStorm()) {
            event.setCancelled(true);
        }
    }
}
