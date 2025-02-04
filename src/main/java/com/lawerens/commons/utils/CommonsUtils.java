package com.lawerens.commons.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

import static xyz.lawerens.Lawerens.COLOR;
import static xyz.lawerens.utils.LawerensUtils.color;
import static xyz.lawerens.utils.LawerensUtils.sendMessage;

public class CommonsUtils {

    public static void writeLocation(ConfigurationSection config, String path, @Nullable  Location location){
        if(location == null) return;

        config.set(path+".world", location.getWorld().getName());
        config.set(path+".x", location.getX());
        config.set(path+".y", location.getY());
        config.set(path+".z", location.getZ());
        config.set(path+".yaw", location.getPitch());
        config.set(path+".pitch", location.getYaw());
    }

    @Nullable
    public static Location readLocation(ConfigurationSection config, String path){
        if(config.getString(path+".world") == null) return null;

        return new Location(
                Bukkit.getWorld(config.getString(path+".world")),
                config.getDouble(path+".x"),
                config.getDouble(path+".y"),
                config.getDouble(path+".z"),
                (float) config.getDouble(path+".yaw"),
                (float) config.getDouble(path+".pitch")
        );
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int start, int fadeOut){
        player.showTitle(
                Title.title(fromLegacy(title), fromLegacy(subtitle), Title.Times.times(Duration.ofSeconds(fadeIn), Duration.ofSeconds(start), Duration.ofSeconds(fadeOut)))
        );
    }

    public static Component fromLegacy(String legacyText) {
        legacyText = color(legacyText);

        return LegacyComponentSerializer.legacySection().deserialize(legacyText);
    }

    public static void sendMessageWithPrefix(CommandSender sender, String prefix, String message){
        sendMessage(sender, COLOR+prefix+" &8» &f"+message);
    }

    public static void sendCustomMessageWithPrefix(CommandSender sender, String color, String prefix, String message){
        sendMessage(sender, color+prefix+" &8» &f"+message);
    }

}
