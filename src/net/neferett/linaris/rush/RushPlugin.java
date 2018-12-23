package net.neferett.linaris.rush;

import java.io.File;
import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import lombok.SneakyThrows;
import net.neferett.linaris.rush.event.RushListener;
import net.neferett.linaris.rush.event.block.BlockBreak;
import net.neferett.linaris.rush.event.block.BlockPhysics;
import net.neferett.linaris.rush.event.block.BlockPlace;
import net.neferett.linaris.rush.event.entity.EntityDamage;
import net.neferett.linaris.rush.event.entity.EntityDamageByPlayer;
import net.neferett.linaris.rush.event.entity.EntityExplode;
import net.neferett.linaris.rush.event.entity.FoodLevelChange;
import net.neferett.linaris.rush.event.inventory.PrepareItemCraft;
import net.neferett.linaris.rush.event.player.AsyncPlayerChat;
import net.neferett.linaris.rush.event.player.PlayerCommandPreprocess;
import net.neferett.linaris.rush.event.player.PlayerDamage;
import net.neferett.linaris.rush.event.player.PlayerDeath;
import net.neferett.linaris.rush.event.player.PlayerDropItem;
import net.neferett.linaris.rush.event.player.PlayerInteract;
import net.neferett.linaris.rush.event.player.PlayerJoin;
import net.neferett.linaris.rush.event.player.PlayerKick;
import net.neferett.linaris.rush.event.player.PlayerLogin;
import net.neferett.linaris.rush.event.player.PlayerMove;
import net.neferett.linaris.rush.event.player.PlayerPickupItem;
import net.neferett.linaris.rush.event.player.PlayerQuit;
import net.neferett.linaris.rush.event.server.ServerCommand;
import net.neferett.linaris.rush.event.server.ServerListPing;
import net.neferett.linaris.rush.event.weather.ThunderChange;
import net.neferett.linaris.rush.event.weather.WeatherChange;
import net.neferett.linaris.rush.handler.MySQL;
import net.neferett.linaris.rush.handler.PlayerData;
import net.neferett.linaris.rush.handler.Step;
import net.neferett.linaris.rush.handler.Team;
import net.neferett.linaris.rush.scheduler.HubTeleportation;
import net.neferett.linaris.rush.util.FileUtils;
import net.neferett.linaris.rush.util.ReflectionHandler;
import net.neferett.linaris.rush.util.SkyFactory;
import net.neferett.linaris.rush.util.ReflectionHandler.PackageType;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class RushPlugin extends JavaPlugin {
    public static Random random = new Random();
    public static String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "BW" + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE + " ";

    private World world;
    public MySQL database;
    public Location lobbyLocation;
    private final Map<UUID, PlayerData> data = new HashMap<>();

    @Override
    public void onEnable() {
        Step.setCurrentStep(Step.LOBBY);
        this.world = Bukkit.getWorlds().get(0);
        new SkyFactory(this).setDimension(this.world, Environment.THE_END);
        this.load();
        this.database = new MySQL(this, this.getConfig().getString("mysql.host"), this.getConfig().getString("mysql.port"), this.getConfig().getString("mysql.database"), this.getConfig().getString("mysql.user"), this.getConfig().getString("mysql.pass"));
        try {
            this.database.openConnection();
            this.database.updateSQL("CREATE TABLE IF NOT EXISTS `players` ( `id` int(11) NOT NULL AUTO_INCREMENT, `name` varchar(30) NOT NULL, `uuid` varbinary(16) NOT NULL, `coins` double NOT NULL, `sw_more_health` int(11) DEFAULT '0' NOT NULL, `sw_better_bow` int(11) DEFAULT '0' NOT NULL, `sw_better_sword` int(11) DEFAULT '0' NOT NULL, `sw_mobility` int(11) DEFAULT '0' NOT NULL, `sw_more_sheep` int(11) DEFAULT '0' NOT NULL, `created_at` datetime NOT NULL, `updated_at` datetime NOT NULL, PRIMARY KEY (`id`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;");
        } catch (ClassNotFoundException | SQLException e) {
            this.getLogger().severe("Impossible de se connecter à la base de données :");
            e.printStackTrace();
            this.getLogger().severe("Arrêt du serveur...");
            Bukkit.shutdown();
            return;
        }
        this.register(BlockBreak.class, BlockPhysics.class, BlockPlace.class, EntityDamage.class, EntityDamageByPlayer.class, EntityExplode.class, FoodLevelChange.class, PrepareItemCraft.class, AsyncPlayerChat.class, PlayerCommandPreprocess.class, PlayerDamage.class, PlayerDeath.class, PlayerDropItem.class, PlayerInteract.class, PlayerJoin.class, PlayerKick.class, PlayerLogin.class, PlayerMove.class, PlayerPickupItem.class, PlayerQuit.class, ServerCommand.class, ServerListPing.class, ThunderChange.class, WeatherChange.class);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public void onDisable() {
        this.save();
    }

    @SneakyThrows
    @Override
    public void onLoad() {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer(ChatColor.RED + "Le serveur redémarre...");
        }
        Bukkit.unloadWorld("world", false);
        final File worldContainer = this.getServer().getWorldContainer();
        final File worldFolder = new File(worldContainer, "world");
        final File copyFolder = new File(worldContainer, "rush");
        if (copyFolder.exists()) {
            ReflectionHandler.getClass("RegionFileCache", PackageType.MINECRAFT_SERVER).getMethod("a").invoke(null);
            FileUtils.delete(worldFolder);
            FileUtils.copyFolder(copyFolder, worldFolder);
        }
    }

    @SneakyThrows
    private void register(final Class<? extends RushListener>... classes) {
        for (final Class<? extends RushListener> clazz : classes) {
            final Constructor<? extends RushListener> constructor = clazz.getConstructor(RushPlugin.class);
            Bukkit.getPluginManager().registerEvents(constructor.newInstance(this), this);
        }
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (command.getName().equalsIgnoreCase("rush")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Vous devez être un joueur.");
                return true;
            }
            final Player player = (Player) sender;
            if (args.length != 0)  {
                final String sub = args[0];
                if (sub.equalsIgnoreCase("help")) {
                    player.sendMessage(ChatColor.GOLD + "Aide du plugin Rush :");
                    player.sendMessage("/rush setlobby" + ChatColor.YELLOW + " - définit le lobby du jeu");
                    player.sendMessage("/rush setbed <couleur>" + ChatColor.YELLOW + " - définit le lit de l'équipe <couleur>");
                } else if (sub.equalsIgnoreCase("setlobby")) {
                    this.lobbyLocation = player.getLocation();
                    player.sendMessage(ChatColor.GREEN + "Vous avez défini le lobby avec succès.");
                    this.getConfig().set("lobby", this.toString(player.getLocation()));
                    this.saveConfig();
                } else if (sub.equalsIgnoreCase("setbed") && args.length == 2) {
                    if (!args[1].equalsIgnoreCase("red") && !args[1].equalsIgnoreCase("blue")) {
                        player.sendMessage(ChatColor.RED + "La couleur " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " n'existe pas.");
                    } else {
                        final Location location = player.getTargetBlock(null, 4).getLocation();
                        if (location.getBlock().getType() != Material.BED_BLOCK) {
                            player.sendMessage(ChatColor.RED + "Vous devez viser un lit.");
                        } else {
                            final Team team = Team.getTeam(args[1]);
                            player.sendMessage(ChatColor.GREEN + "Vous avez défini avec succès le lit de l'équipe " + team.getColor() + team.getDisplayName());
                            team.setBedLocation(location);
                            this.getConfig().set("teams." + args[1], this.toString(location));
                            this.saveConfig();
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Mauvais arguments ou commande inexistante. Tapez " + ChatColor.DARK_RED + "/rush help" + ChatColor.RED + " pour de l'aide.");
                }
                return true;
            }
        }
        return false;
    }

    public void playerLoose(final Player player, final boolean needTeleportation) {
        if (Step.isStep(Step.LOBBY)) {
            this.data.remove(player.getUniqueId());
        }
        if (needTeleportation) {
            new HubTeleportation(RushPlugin.this, player);
        }
        final Team team = Team.getPlayerTeam(player);
        if (team != null) {
            team.getScoreboardTeam().removePlayer(player);
            player.getScoreboard().getObjective("teams").getScore(Bukkit.getOfflinePlayer(team.getColor() + StringUtils.capitalize(team.getDisplayName()))).setScore(team.getScoreboardTeam().getPlayers().size());
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (Step.isStep(Step.IN_GAME) && team.getScoreboardTeam().getPlayers().size() == 0) {
                        Team.allTeams.remove(team);
                        if (Team.allTeams.size() == 1) {
                            Step.setCurrentStep(Step.POST_GAME);
                            final Team winnerTeam = Team.allTeams.get(0);
                            Bukkit.broadcastMessage(RushPlugin.prefix + ChatColor.GOLD + ChatColor.BOLD + "Victoire de l'équipe " + winnerTeam.getColor() + ChatColor.BOLD + winnerTeam.getDisplayName() + " " + ChatColor.YELLOW + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.MAGIC + "|" + ChatColor.GREEN + ChatColor.MAGIC + "|" + ChatColor.RED + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "|" + ChatColor.YELLOW + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.MAGIC + "|" + ChatColor.GREEN + ChatColor.MAGIC + "|" + ChatColor.RED + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.BOLD + " Félicitations " + ChatColor.YELLOW + ChatColor.MAGIC + " |" + ChatColor.AQUA + ChatColor.MAGIC + "|" + ChatColor.GREEN + ChatColor.MAGIC + "|" + ChatColor.RED + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "|" + ChatColor.YELLOW + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.MAGIC + "|" + ChatColor.GREEN + ChatColor.MAGIC + "|" + ChatColor.RED + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "|");
                            for (final Player online : Bukkit.getOnlinePlayers()) {
                                if (!needTeleportation || online != player) {
                                    new HubTeleportation(RushPlugin.this, online);
                                }
                            }
                            for (final Entry<UUID, PlayerData> entry : RushPlugin.this.data.entrySet()) {
                                final String uuid = entry.getKey().toString().replaceAll("-", "");
                                final PlayerData data = entry.getValue();
                                final Player online = Bukkit.getPlayer(entry.getKey());
                                if (online != null && online.isOnline()) {
                                    if (Team.getPlayerTeam(online) == winnerTeam) {
                                        data.addCoins(7, false);
                                    } else {
                                        data.addCoins(1.25, false);
                                    }
                                }
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            final ResultSet res = RushPlugin.this.database.querySQL("SELECT name FROM players WHERE uuid=UNHEX('" + uuid + "')");
                                            if (res.first()) {
                                                RushPlugin.this.database.updateSQL("UPDATE players SET name='" + data.getName() + "', coins=coins+" + data.getCoins() + ", updated_at=NOW() WHERE uuid=UNHEX('" + uuid + "')");
                                            } else {
                                                RushPlugin.this.database.updateSQL("INSERT INTO players(name, uuid, coins, created_at, updated_at) VALUES('" + data.getName() + "', UNHEX('" + uuid + "'), " + data.getCoins() + ", NOW(), NOW())");
                                            }
                                        } catch (ClassNotFoundException | SQLException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.runTaskAsynchronously(RushPlugin.this);
                            }
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    for (final Player online : Bukkit.getOnlinePlayers()) {
                                        RushPlugin.this.teleportToLobby(online);
                                    }
                                    Bukkit.shutdown();
                                }
                            }.runTaskLater(RushPlugin.this, 300l);
                        }
                    }
                }
            }.runTaskLater(this, 1l);
        }
    }

    public PlayerData getData(final Player player) {
        final PlayerData data = this.data.get(player.getUniqueId());
        if (data == null) {
            player.kickPlayer(ChatColor.RED + "Erreur");
            return null;
        }
        return data;
    }

    public void loadData(final Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    final ResultSet res = RushPlugin.this.database.querySQL("SELECT * FROM players WHERE uuid=UNHEX('" + player.getUniqueId().toString().replaceAll("-", "") + "')");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            PlayerData data = null;
                            try {
                                if (res.first()) {
                                    data = new PlayerData(player.getUniqueId(), res.getString("name"), res.getInt("sw_more_health"), res.getInt("sw_better_bow"), res.getInt("sw_better_sword"), res.getInt("sw_more_sheep"), res.getInt("sw_mobility"), 0);
                                } else {
                                    data = new PlayerData(player.getUniqueId(), player.getName(), 0, 0, 0, 0, 0, 0);
                                }
                                RushPlugin.this.data.put(player.getUniqueId(), data);
                            } catch (final SQLException e) {
                                player.kickPlayer(ChatColor.RED + "Impossible de charger vos statistiques... :(");
                            }
                        }
                    }.runTask(RushPlugin.this);
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(this);
    }

    private void load() {
        final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team.BLUE.createTeam(scoreboard);
        Team.RED.createTeam(scoreboard);
        final ConfigurationSection teams = this.getConfig().getConfigurationSection("teams");
        if (teams != null) {
            final Objective objective = scoreboard.registerNewObjective("teams", "dummy");
            objective.setDisplayName(ChatColor.DARK_GRAY + "-" + ChatColor.YELLOW + "BedWars" + ChatColor.DARK_GRAY + "-");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            if (teams.isString("blue")) {
                Team.BLUE.setBedLocation(this.toLocation(teams.getString("blue")));
                final String name = Team.BLUE.getColor() + StringUtils.capitalize(Team.BLUE.getDisplayName());
            }
            if (teams.isString("red")) {
                Team.RED.setBedLocation(this.toLocation(teams.getString("red")));
                final String name = Team.RED.getColor() + StringUtils.capitalize(Team.RED.getDisplayName());
            }
        }
        this.lobbyLocation = this.toLocation(this.getConfig().getString("lobby", this.toString(this.world.getSpawnLocation())));
    }

    private void save() {
        this.getConfig().set("lobby", this.toString(this.lobbyLocation));
        if (Team.BLUE.getBedLocation() != null) {
            this.getConfig().set("teams.blue", this.toString(Team.BLUE.getBedLocation()));
        }
        if (Team.RED.getBedLocation() != null) {
            this.getConfig().set("teams.red", this.toString(Team.RED.getBedLocation()));
        }
        this.saveConfig();
    }

    public void teleportToLobby(final Player player) {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF("hub");
        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }

    private Location toLocation(final String string) {
        final String[] splitted = string.split("_");
        World world = Bukkit.getWorld(splitted[0]);
        if (world == null) {
            world = this.world;
        }
        return new Location(world, Double.parseDouble(splitted[1]), Double.parseDouble(splitted[2]), Double.parseDouble(splitted[3]));
    }

    private String toString(final Location location) {
        final World world = location.getWorld();
        return world.getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
    }
}
