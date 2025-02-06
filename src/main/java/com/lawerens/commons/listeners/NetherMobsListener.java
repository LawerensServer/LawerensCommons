package com.lawerens.commons.listeners;

import es.pollitoyeye.vehicles.events.VehiclePlaceEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import static com.lawerens.core.LawerensUtils.sendMessageWithPrefix;


public class NetherMobsListener implements Listener {

    @EventHandler
    public void onVehiclePlace(VehiclePlaceEvent e){
        if(e.getLocation().getWorld().getName().equalsIgnoreCase("Survival_nether") && e.getLocation().getY() >= 128){
            e.setCancelled(true);
            sendMessageWithPrefix(e.getOwner(), "#4964FF&lV#5672FF&lE#6281FF&lH#6F8FFE&lI#7B9DFE&lC#88ABFE&lU#94BAFE&lL#A1C8FD&lO#ADD6FD&lS", "&c¡No puedes colocar vehiculos en el techo del Nether!");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCreatureSpawn(CreatureSpawnEvent e){
        if(e.getLocation().getWorld().getName().equalsIgnoreCase("Survival_nether") && e.getLocation().getY() >= 128)
            e.setCancelled(true);

    }
}
