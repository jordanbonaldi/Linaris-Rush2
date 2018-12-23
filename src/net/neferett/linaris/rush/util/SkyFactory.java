package net.neferett.linaris.rush.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/*
 * Class made by BigTeddy98.
 *
 * SkyFactory is a simple class to change the environment of the sky.
 *
 * 1. No warranty is given or implied.
 * 2. All damage is your own responsibility.
 * 3. If you want to use this in your plugins, a credit would we appreciated.
 */
public class SkyFactory implements Listener {
    private final Plugin plugin;

    //everything needed for our reflection
    private static Constructor<?> packetPlayOutRespawn;

    private static Method getHandle;
    private static Field playerConnection;
    private static Method sendPacket;
    private static Field normal;
    static {
        try {
            //get the packet's constructor
            SkyFactory.packetPlayOutRespawn = SkyFactory.getMCClass("PacketPlayOutRespawn").getConstructor(int.class, SkyFactory.getMCClass("EnumDifficulty"), SkyFactory.getMCClass("WorldType"), SkyFactory.getMCClass("EnumGamemode"));
            //get CraftPlayer's handle
            SkyFactory.getHandle = SkyFactory.getCraftClass("entity.CraftPlayer").getMethod("getHandle");
            //get the PlayerConnection
            SkyFactory.playerConnection = SkyFactory.getMCClass("EntityPlayer").getDeclaredField("playerConnection");
            //get the sendPacket method
            SkyFactory.sendPacket = SkyFactory.getMCClass("PlayerConnection").getMethod("sendPacket", SkyFactory.getMCClass("Packet"));
            //get the field to specify the worldtype for the packet
            SkyFactory.normal = SkyFactory.getMCClass("WorldType").getDeclaredField("NORMAL");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    // easy way to get CraftBukkit classes
    private static Class<?> getCraftClass(final String name) throws ClassNotFoundException {
        final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        final String className = "org.bukkit.craftbukkit." + version + name;
        return Class.forName(className);
    }

    // easy way to get NMS classes
    private static Class<?> getMCClass(final String name) throws ClassNotFoundException {
        final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        final String className = "net.minecraft.server." + version + name;
        return Class.forName(className);
    }

    //list of changed environments
    private final Map<String, Environment> worldEnvironments = new HashMap<String, Environment>();

    public SkyFactory(final Plugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    //loop through the NMS difficulty enum, and check if it equals the Bukkit difficulty
    private Object getDifficulty(final World w) throws ClassNotFoundException {
        for (final Object dif : SkyFactory.getMCClass("EnumDifficulty").getEnumConstants()) {
            if (dif.toString().equalsIgnoreCase(w.getDifficulty().toString())) return dif;
        }
        return null;
    }

    //loop through the NMS gamemode enum, and check if it equals the Bukkit gamemode
    private Object getGameMode(final Player p) throws ClassNotFoundException {
        for (final Object dif : SkyFactory.getMCClass("EnumGamemode").getEnumConstants()) {
            if (dif.toString().equalsIgnoreCase(p.getGameMode().toString())) return dif;
        }
        return null;
    }

    //convert Bukkit environment to NMS environment ID
    private int getID(final Environment env) {
        if (env == Environment.NETHER) return -1;
        else if (env == Environment.NORMAL) return 0;
        else if (env == Environment.THE_END) return 1;
        return -1;
    }

    //get the level type, normal by default
    private Object getLevel(final World w) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
        return SkyFactory.normal.get(null);
    }

    private Object getPacket(final Player p) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
        final World w = p.getWorld();
        //create the new packet instance;
        return SkyFactory.packetPlayOutRespawn.newInstance(this.getID(this.worldEnvironments.get(w.getName())), this.getDifficulty(w), this.getLevel(w), this.getGameMode(p));
    }

    @EventHandler
    private void onJoin(final PlayerJoinEvent event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        final Player p = event.getPlayer();
        //only continue if the world environment is changed
        if (this.worldEnvironments.containsKey(p.getWorld().getName())) {
            //get the EntityPlayer
            final Object nms_entity = SkyFactory.getHandle.invoke(p);
            //get the connection
            final Object nms_connection = SkyFactory.playerConnection.get(nms_entity);
            //send the packet
            SkyFactory.sendPacket.invoke(nms_connection, this.getPacket(p));
        }
    }

    @EventHandler
    private void onRespawn(final PlayerRespawnEvent event) {
        //same as onJoin, but execute 1 tick later, otherwise the packet will be ignored by the client because the client is still respawning
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    final Player p = event.getPlayer();
                    if (SkyFactory.this.worldEnvironments.containsKey(p.getWorld().getName())) {
                        final Object nms_entity = SkyFactory.getHandle.invoke(p);
                        final Object nms_connection = SkyFactory.playerConnection.get(nms_entity);
                        SkyFactory.sendPacket.invoke(nms_connection, SkyFactory.this.getPacket(p));
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskLater(this.plugin, 1);
    }

    public void setDimension(final World w, final Environment env) {
        this.worldEnvironments.put(w.getName(), env);
    }
}