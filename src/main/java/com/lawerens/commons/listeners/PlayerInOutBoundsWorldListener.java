package com.lawerens.commons.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerInOutBoundsWorldListener implements Listener {

    @EventHandler
    public void onMove(PlayerTeleportEvent e){
        if(isOutsideRadius(e.getTo(), 6000)){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spawn "+e.getPlayer().getName());
        }
    }

    public static boolean isOutsideRadius(Location location, double radius) {
        if(!location.getWorld().getName().equals("Survival")) return false;
        double x = location.getX();
        double z = location.getZ();

        double distanceSquared = (x * x) + (z * z);

        return distanceSquared > (radius * radius);
    }

}
