package com.lawerens.commons;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.api.JobsPrePaymentEvent;
import com.gamingmesh.jobs.container.ActionType;
import com.lawerens.commons.listeners.JobsDrillNerfListener;
import com.lawerens.commons.listeners.PlayerInOutBoundsWorldListener;
import es.pollitoyeye.vehicles.VehiclesMain;
import es.pollitoyeye.vehicles.enums.VehicleType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class LawerensCommons extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new JobsDrillNerfListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInOutBoundsWorldListener(), this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
