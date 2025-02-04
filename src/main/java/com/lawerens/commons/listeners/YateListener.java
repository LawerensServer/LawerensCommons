package com.lawerens.commons.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class YateListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        if(e.getPlayer().getWorld().getName().equalsIgnoreCase("Yate") && e.getPlayer().getLocation().getBlock().getType() == Material.WATER){
            e.getPlayer().setHealth(0);
        }
    }

}
