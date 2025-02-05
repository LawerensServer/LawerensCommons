package com.lawerens.commons.model;

import com.lawerens.commons.LawerensCommons;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import static com.lawerens.core.LawerensUtils.sendCustomMessageWithPrefix;
import static com.lawerens.core.LawerensUtils.sendTitle;

@Data
public final class BloodMoon implements Listener {

    private boolean started = false;
    private int daysToTheNext = 10;

    @NotNull
    private BossBar bossBar;

    private BukkitTask mobSpawningTask;
    private BukkitTask checkingTask;
    private BukkitTask finishTask;

    private long startTime = 0;

    public BloodMoon() {
        bossBar = Bukkit.createBossBar("#22ccff&lLUNA SANGRIENTA", BarColor.RED, BarStyle.SEGMENTED_12);
    }

    public void scheduleCheck() {
        checkingTask = new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.getWorld("Survival");
                if (world == null) return;

                // Comienza la Luna Sangrienta cuando se alcanza el tiempo especificado
                if (world.getTime() >= 13000) {
                    if (daysToTheNext == 0) {
                        start();
                    }
                }

                // Cuenta hacia atrás para la siguiente Luna Sangrienta
                if (world.getTime() >= 0 && world.getTime() <= 1000) {
                    if (daysToTheNext > 0) {
                        daysToTheNext--;
                    } else {
                        daysToTheNext = 10;
                    }

                    MoonsManager.toPlayersInSurvival(player -> sendCustomMessageWithPrefix(player, "#22ccff&l", "LUNA SANGRIENTA", "&fFaltan #ff0000" + daysToTheNext + " noches &fpara la siguiente luna sangrienta."));
                }
            }
        }.runTaskTimer(LawerensCommons.get(), 0, 1200); // 1200 ticks = 1 minuto
    }

    public void start() {
        started = true;

        if (checkingTask != null && !checkingTask.isCancelled()) checkingTask.cancel();

        startTime = System.currentTimeMillis();

        bossBar.setProgress(1.0);
        MoonsManager.toPlayersInSurvival(player -> {
            sendTitle(player, "#ff0000La &lLUNA SANGRIENTA #22ccffempezó", "&cLos mobs son muy fuertes, ten cuidado", 1, 6, 1);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 6 * 20, 5, false, false, false));
            player.playSound(player, Sound.AMBIENT_CAVE, 1f, 1f);
        });

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(onlinePlayer);
            MoonsManager.toPlayersInSurvival(player -> sendCustomMessageWithPrefix(player, "#22ccff&l", "LUNA SANGRIENTA", "&fLa luna sangrienta ha comenzado..."));
        }
        spawnMobs();
        scheduleEnd();
    }

    private void scheduleEnd() {
        finishTask = new BukkitRunnable() {
            int i = 7;
            @Override
            public void run() {
                if (bossBar.getProgress() > 0) {
                    i--;
                    bossBar.setProgress(Math.max(0, bossBar.getProgress() - (1.0 / 7.0)));
                    MoonsManager.toPlayersInSurvival(player -> sendCustomMessageWithPrefix(player, "#22ccff&l", "LUNA SANGRIENTA", "&fLa luna sangrienta terminará en #ff0000" + i + " minutos&f."));
                } else {
                    end();
                    reset();
                }
            }
        }.runTaskTimer(LawerensCommons.get(), 0, 1200); // 1200 ticks = 1 minuto
    }

    public void spawnMobs() {
        mobSpawningTask = new BukkitRunnable() {
            @Override
            public void run() {
                    MoonsManager.toPlayersInSurvival(player -> {
                        Location playerLocation = player.getLocation();
                        MoonsManager.spawnMobsInRadius(playerLocation, "bloodmoon");
                    });
            }
        }.runTaskTimer(LawerensCommons.get(), 20 * 20L, 20 * 20L); // Genera mobs cada 10 segundos
    }

    public void end() {
        MoonsManager.toPlayersInSurvival(onlinePlayer -> {
            sendTitle(onlinePlayer, "&eLa luna se aleja...", "&fLa &cLuna sangrienta&f se ha ido", 1, 6, 1);
            onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 6 * 20, 5, false, false, false));
            onlinePlayer.playSound(onlinePlayer, Sound.MUSIC_GAME, 1f, 1f);
        });
    }

    public void reset() {
        if (mobSpawningTask != null && !mobSpawningTask.isCancelled()) mobSpawningTask.cancel();
        if (finishTask != null && !finishTask.isCancelled()) finishTask.cancel();

        started = false;
        daysToTheNext = 10;
        bossBar.removeAll();
        bossBar.setProgress(1.0);
        startTime = 0;

        scheduleCheck();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!bossBar.getPlayers().contains(e.getPlayer()) && started) {
            bossBar.addPlayer(e.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (bossBar.getPlayers().contains(e.getPlayer())) {
            bossBar.removePlayer(e.getPlayer());
        }
    }

    // INTERNAL API METHODS

    public String getCurrentDuration() {
        if (started) {
            long elapsedMillis = System.currentTimeMillis() - startTime;
            long elapsedSeconds = TimeUnit.MILLISECONDS.toSeconds(elapsedMillis);
            long minutes = elapsedSeconds / 60;
            long seconds = elapsedSeconds % 60;
            return "&fDuración actual:&e " + minutes + " minutos y " + seconds + " segundos&f.";
        }
        return "&cLa Luna Sangrienta no ha comenzado aún.";
    }

    public String getStatus() {
        return "Estado: " + (started ? "Comenzado" : "Detenido") + "\n" +
                "Días para la siguiente luna: " + daysToTheNext;
    }
}
