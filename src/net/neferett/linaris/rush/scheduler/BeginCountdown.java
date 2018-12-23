package net.neferett.linaris.rush.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;

import net.neferett.linaris.rush.RushPlugin;
import net.neferett.linaris.rush.handler.Step;
import net.neferett.linaris.rush.handler.Team;

public class BeginCountdown extends BukkitRunnable {
    public static boolean started = false;
    public static int timeUntilStart = 60;

    public BeginCountdown(final RushPlugin plugin) {
        BeginCountdown.started = true;
        this.runTaskTimer(plugin, 0l, 20l);
    }

    @Override
    public void run() {
        if (BeginCountdown.timeUntilStart == 0) {
            this.cancel();
            if (Bukkit.getOnlinePlayers().length < 2) {
                Bukkit.broadcastMessage(RushPlugin.prefix + ChatColor.RED + "Il n'y a pas assez de joueurs !");
                BeginCountdown.timeUntilStart = 120;
                BeginCountdown.started = false;
            } else {
                final Objective objective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("teams");
                Bukkit.broadcastMessage(RushPlugin.prefix + ChatColor.AQUA + "La partie commence, bon jeu !");
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    player.setFallDistance(0);
                    player.setGameMode(GameMode.SURVIVAL);
                    player.getInventory().clear();
                    player.closeInventory();
                    Team team = Team.getPlayerTeam(player);
                    if (team == null) {
                        team = Team.getRandomTeam();
                        team.addPlayer(player);
                    }
                    final Location bedLocation = team.getBedLocation();
                    player.setBedSpawnLocation(bedLocation, true);
                    player.teleport(bedLocation.clone().add(2, 1, 0));
                }
                Step.setCurrentStep(Step.IN_GAME);
            }
            return;
        }
        final int remainingMins = BeginCountdown.timeUntilStart / 60 % 60;
        final int remainingSecs = BeginCountdown.timeUntilStart % 60;
        if (BeginCountdown.timeUntilStart % 30 == 0 || remainingMins == 0 && (remainingSecs % 10 == 0 || remainingSecs < 10)) {
            Bukkit.broadcastMessage(RushPlugin.prefix + ChatColor.GOLD + "Démarrage du jeu dans " + ChatColor.YELLOW + (remainingMins > 0 ? remainingMins + " minute" + (remainingMins > 1 ? "s" : "") : "") + (remainingSecs > 0 ? (remainingMins > 0 ? " " : "") + remainingSecs + " seconde" + (remainingSecs > 1 ? "s" : "") : "") + ".");
            if (remainingMins == 0 && remainingSecs <= 10) {
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), remainingSecs == 1 ? Sound.ANVIL_LAND : Sound.CLICK, 1f, 1f);
                }
            }
        }
        BeginCountdown.timeUntilStart--;
    }
}
