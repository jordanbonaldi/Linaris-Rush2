package net.neferett.linaris.rush.event.server;

import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerListPingEvent;

import net.neferett.linaris.rush.RushPlugin;
import net.neferett.linaris.rush.event.RushListener;
import net.neferett.linaris.rush.handler.Step;

public class ServerListPing extends RushListener {
    public ServerListPing(final RushPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onServerListPing(final ServerListPingEvent event) {
        event.setMotd(Step.getMOTD());
    }
}
