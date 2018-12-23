package net.neferett.linaris.rush.handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;
import net.neferett.linaris.rush.util.ItemBuilder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

@Data
public class Team {
    public static List<Team> allTeams = new ArrayList<>();
    public static Team BLUE = new Team("blue", "Bleue", new ItemBuilder(Material.INK_SACK, DyeColor.BLUE.getDyeData()).setTitle(ChatColor.BLUE + "Rejoindre l'équipe Bleue").build(), ChatColor.BLUE);
    public static Team RED = new Team("red", "Rouge", new ItemBuilder(Material.INK_SACK, DyeColor.RED.getDyeData()).setTitle(ChatColor.RED + "Rejoindre l'équipe Rouge").build(), ChatColor.RED);

    public static List<Team> getTeams() {
        return Team.allTeams;
    }

    public static Team getPlayerTeam(final Player player) {
        for (final Team team : Team.allTeams) {
            if (team.getScoreboardTeam().getPlayers().contains(player)) return team;
        }
        return null;
    }

    public static Team getRandomTeam() {
        Team lowest = Team.BLUE;
        for (final Team team : Team.allTeams) {
            if (lowest != team && team.getScoreboardTeam().getPlayers().size() < lowest.getScoreboardTeam().getPlayers().size()) {
                lowest = team;
            }
        }
        return lowest;
    }

    public static Team getTeam(final String name) {
        for (final Team team : Team.allTeams) {
            if (team.getScoreboardTeam() != null && team.getScoreboardTeam().getName().equalsIgnoreCase(name)) return team;
        }
        return null;
    }

    public static Team getTeam(final ChatColor color) {
        for (final Team team : Team.allTeams) {
            if (team.getColor() == color) return team;
        }
        return null;
    }

    private String name;
    private final String displayName;
    private final ItemStack icon;
    private final ChatColor color;
    private org.bukkit.scoreboard.Team scoreboardTeam;
    private Location bedLocation;

    private Team(final String name, final String displayName, final ItemStack icon, final ChatColor color) {
        this.name = name;
        this.displayName = displayName;
        this.icon = icon;
        this.color = color;
        Team.allTeams.add(this);
    }

    public void addPlayer(final Player player) {
        this.scoreboardTeam.addPlayer(player);
        final Score score = this.getScore();
        score.setScore(score.getScore() + 1);
    }

    public void removePlayer(final Player player) {
        this.scoreboardTeam.removePlayer(player);
        final Score score = this.getScore();
        score.setScore(score.getScore() - 1);
    }

    public Score getScore() {
        final Score objScore = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("teams").getScore(this.color + "Equipe " + this.displayName);
        return objScore;
    }

    public void setScore(final int score) {
        final Score objScore = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("teams").getScore(this.color + "Equipe " + this.displayName);
        if (score == 0) {
            objScore.setScore(1);
        }
        objScore.setScore(score);
    }

    public Set<Player> getPlayers() {
        final Set<Player> players = new HashSet<>();
        for (final OfflinePlayer offline : this.scoreboardTeam.getPlayers()) {
            if (offline instanceof Player) {
                players.add((Player) offline);
            }
        }
        return players;
    }

    public void broadcastMessage(final String msg) {
        for (final Player player : this.getPlayers()) {
            player.sendMessage(msg);
        }
    }

    public void createTeam(final Scoreboard scoreboard) {
        this.scoreboardTeam = scoreboard.getTeam(this.name);
        if (this.scoreboardTeam == null) {
            this.scoreboardTeam = scoreboard.registerNewTeam(this.name);
        }
        this.scoreboardTeam.setPrefix(this.color.toString());
        this.scoreboardTeam.setDisplayName(this.name);
        this.scoreboardTeam.setAllowFriendlyFire(false);
    }
}
