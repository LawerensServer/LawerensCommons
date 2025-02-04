package com.lawerens.commons.model;

import com.lawerens.commons.LawerensCommons;
import io.lumine.mythic.bukkit.MythicBukkit;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import static com.lawerens.commons.utils.CommonsUtils.sendCustomMessageWithPrefix;
import static com.lawerens.commons.utils.CommonsUtils.sendTitle;
import static xyz.lawerens.utils.LawerensUtils.color;

@Getter
@Setter
public class MoonsManager implements Listener {

    private int nightsUntilBloodMoon = 10;
    private BossBar bloodMoonBossBar;
    private BossBar blueMoonBossBar;
    public static NamespacedKey key;
    private boolean bloodStarted = false;
    private long lastBlueMoonTime = 0;
    private boolean blueStarted = false;

    private List<BukkitTask> bloodTasks = new ArrayList<>(), blueTAsks = new ArrayList<>();

    private final BloodMoon bloodMoon;

    public static void toPlayersInSurvival(Consumer<Player> player) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(onlinePlayer.getWorld().getName().equalsIgnoreCase("Survival"))
                player.accept(onlinePlayer);
        }
    }

    public MoonsManager() {
        bloodMoon = new BloodMoon();
    }

    public void onDisable() {

        for (Player player : bloodMoonBossBar.getPlayers()) {
            bloodMoonBossBar.removePlayer(player);
        }
        for (Player player : bloodMoonBossBar.getPlayers()) {
            blueMoonBossBar.removePlayer(player);
        }

        Bukkit.removeBossBar(key);

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        bloodMoonBossBar.addPlayer(e.getPlayer());
        blueMoonBossBar.addPlayer(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        bloodMoonBossBar.removePlayer(e.getPlayer());
        blueMoonBossBar.removePlayer(e.getPlayer());
    }


    private void startBloodMoonCycle() {
        new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.getWorld("Survival");
                if (world == null) return;

                if (world.getTime() == 13000) {
                    if (nightsUntilBloodMoon == 0) {
                       // activateBloodMoon(world);
                    }
                }

                if (world.getTime() == 0) {
                    updateNightCounter();
                    announceBloodMoonCountdown();
                }
            }
        }.runTaskTimer(LawerensCommons.get(), 0, 1200);
    }

    private void startBlueMoonCycle() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastBlueMoonTime >= 21600000) {
                    World world = Bukkit.getWorld("Survival");
                    if (world != null) {
                        activateBlueMoon(world);
                    }
                    lastBlueMoonTime = currentTime;
                }
            }
        }.runTaskTimer(LawerensCommons.get(), 0, 6000);
    }

    public String getBlueMoonTimeRemaining() {
        long currentTime = System.currentTimeMillis();
        long timeRemaining = 21600000 - (currentTime - lastBlueMoonTime); // 6 horas en milisegundos

        return formatTime(timeRemaining);
    }

    public String getBloodMoonTimeRemaining() {
        World world = Bukkit.getWorld("Survival");
        if (world == null) return "Mundo no encontrado";

        long time = world.getTime(); // Obtener el tiempo en el mundo actual

        // Determina el tiempo restante para la Luna de Sangre
        if (time == 13000) {
            return "Luna de Sangre activa";
        }

        long remainingTime = 13000 - time; // Tiempo restante hasta el ciclo de Luna de Sangre
        if (remainingTime < 0) {
            remainingTime += 24000; // Resetea el ciclo si es negativo
        }

        return formatTime(remainingTime * 50); // Convierte a milisegundos
    }

    public void setNightsUntilBloodMoon(int nights) {
        if (nights >= 0) {
            this.nightsUntilBloodMoon = nights;
        }
    }

    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds %= 60;
        minutes %= 60;

        return String.format("%02dhs %02dmin %02dseg", hours, minutes, seconds);
    }

    public void activateBloodMoon() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (bloodMoonBossBar.getProgress() > 0) {
                    bloodMoonBossBar.setProgress(bloodMoonBossBar.getProgress() - 0.05);
                } else {
                    resetBloodMoonCycle();
                }
            }
        }.runTaskTimer(LawerensCommons.get(), 0, 600);
    }

    public void activateBlueMoon(World world) {

        blueMoonBossBar.setProgress(1.0);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(onlinePlayer.getWorld().getName().equals("Survival")){
                sendTitle(onlinePlayer, "#22ccffLa &lLUNA AZUL #22ccffempezó", "&9Los mobs son muy fuertes, pero con buen loot", 1, 6, 1);
                onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 6 * 20, 5, false, false, false));
                onlinePlayer.playSound(onlinePlayer, Sound.AMBIENT_CAVE, 1f, 1f);
            }

            blueMoonBossBar.addPlayer(onlinePlayer);
        }
        spawnBlueMoonMobs(world);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (blueMoonBossBar.getProgress() > 0) {
                    blueMoonBossBar.setProgress(blueMoonBossBar.getProgress() - 0.05);
                } else {
                    blueMoonBossBar.removeAll();
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        sendTitle(onlinePlayer, "&eLa luna se aleja...", "&fLa &9Luna Azul&f se ha ido", 1, 6, 1);
                        onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 6 * 20, 5, false, false, false));
                        onlinePlayer.playSound(onlinePlayer, Sound.MUSIC_GAME, 1f, 1f);
                    }
                }
            }
        }.runTaskTimer(LawerensCommons.get(), 0, 600);
    }

    private void spawnBloodMoonMobs(World world) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : world.getPlayers()) {
                    Location playerLocation = player.getLocation();
                    if(!playerLocation.getWorld().getName().equals("Survival")) continue;
                   // spawnMobsInRadius(world, playerLocation, "bloodmoon");
                }
            }
        }.runTaskTimer(LawerensCommons.get(), 0, 300);
    }

    private void spawnBlueMoonMobs(World world) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : world.getPlayers()) {
                    Location playerLocation = player.getLocation();
                    if(!playerLocation.getWorld().getName().equals("Survival")) continue;
                 //   spawnMobsInRadius(world, playerLocation, "bluemoon");
                }
            }
        }.runTaskTimer(LawerensCommons.get(), 0, 300);
    }

    public static void spawnMobsInRadius(Location center, String moonType) {
        Random rand = new Random();
        int spawnAttempts = 30;

        for (int i = 0, mobsSpawned = 0; i < spawnAttempts && mobsSpawned < 10; i++) {
            double x = center.getX() + rand.nextInt(50 * 2) - 50;
            double z = center.getZ() + rand.nextInt(50 * 2) - 50;
            double y = center.getWorld().getHighestBlockYAt((int) x, (int) z) + 1;

            Location spawnLocation = new Location(center.getWorld(), x, y, z);

            if (center.getWorld().getBlockAt(spawnLocation).getLightLevel() < 8) {
                continue;
            }

            List<Entity> nearbyEntities = center.getWorld().getNearbyEntities(spawnLocation, 5, 5, 5)
                    .stream()
                    .filter(e -> e instanceof LivingEntity)
                    .toList();

            if (nearbyEntities.size() > 10) {
                continue;
            }

            String mobType = moonType.equals("bloodmoon") ? getRandomBloodMoonMob(rand) : getRandomBlueMoonMob(rand);

            if(MythicBukkit.inst().getMobManager().getMythicMob(mobType).isEmpty()){
                continue;
            }

            MythicBukkit.inst().getMobManager().spawnMob(mobType, spawnLocation);
            mobsSpawned++;
        }
    }

    private static String getRandomBloodMoonMob(Random rand) {
        return switch (rand.nextInt(6)) {
            case 0 -> "Cabernicola";
            case 1 -> "CabernicolaBebe";
            case 2 -> "MineroInfectadoBebe";
            case 3 -> "MineroInfectado";
            case 4 -> "CazadorBebe";
            case 5 -> "Cazador";
            default -> "Cabernicola";
        };
    }

    private static String getRandomBlueMoonMob(Random rand) {
        switch (rand.nextInt(3)) {
            case 0: return "CazadorElite";
            case 1: return "CabernicolaElite";
            case 2: return "MineroInfectadoElite";
            default: return "CazadorElite";
        }
    }

    private void updateNightCounter() {
        if (nightsUntilBloodMoon > 0) {
            nightsUntilBloodMoon--;
        } else {
            nightsUntilBloodMoon = 10;
        }
    }

    private void announceBloodMoonCountdown() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            sendCustomMessageWithPrefix(onlinePlayer, "#ff0000", "LUNA SANGRIENTA", "&fLa luna sangrienta aparecerá en &c"+nightsUntilBloodMoon+"&f noches.");
        }
    }

    public void resetBloodMoonCycle() {

       // actived = false;
        bloodMoonBossBar.removeAll();
        bloodMoonBossBar.setProgress(1.0);
        nightsUntilBloodMoon = 10;
    }

}
