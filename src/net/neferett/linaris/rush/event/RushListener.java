package net.neferett.linaris.rush.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.neferett.linaris.rush.RushPlugin;

import org.bukkit.event.Listener;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class RushListener implements Listener {
    protected RushPlugin plugin;
}
