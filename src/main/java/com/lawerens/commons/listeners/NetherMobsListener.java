package com.lawerens.commons.listeners;

import es.pollitoyeye.vehicles.events.VehiclePlaceEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import static com.lawerens.commons.utils.CommonsUtils.sendMessageWithPrefix;

public class NetherMobsListener implements Listener {

    @EventHandler
    public void onVehiclePlace(VehiclePlaceEvent e){
        if(e.getLocation().getWorld().getName().equalsIgnoreCase("Survival_nether") && e.getLocation().getY() >= 128){
            e.setCancelled(true);
            sendMessageWithPrefix(e.getOwner(), "VEHICULOS", "&c¡No puedes colocar vehiculos en el techo del Nether!");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCreatureSpawn(CreatureSpawnEvent e){
        if(e.getLocation().getWorld().getName().equalsIgnoreCase("Survival_nether") && e.getLocation().getY() >= 128)
            e.setCancelled(true);

    }
}
