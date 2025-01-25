package com.lawerens.commons.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import static xyz.lawerens.Lawerens.COLOR;
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

    public static void sendMessageWithPrefix(CommandSender sender, String prefix, String message){
        sendMessage(sender, COLOR+prefix+" &8» &f"+message);
    }

}
