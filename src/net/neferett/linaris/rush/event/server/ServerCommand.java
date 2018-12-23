package net.neferett.linaris.rush.event.server;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerCommandEvent;

import net.neferett.linaris.rush.RushPlugin;
import net.neferett.linaris.rush.event.RushListener;

public class ServerCommand extends RushListener {

    public ServerCommand(final RushPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onServerCommand(final ServerCommandEvent event) {
        if (event.getCommand().split(" ")[0].contains("reload")) {
            event.setCommand("/reload");
            event.getSender().sendMessage(ChatColor.RED + "Cette fonctionnalité est désactivée par le plugin Rush à cause de contraintes techniques (reset de map).");
        }
    }
}
