package com.lawerens.commons.listeners;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.api.JobsExpGainEvent;
import com.gamingmesh.jobs.api.JobsPrePaymentEvent;
import com.gamingmesh.jobs.container.ActionType;
import es.pollitoyeye.vehicles.VehiclesMain;
import es.pollitoyeye.vehicles.enums.VehicleType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class JobsDrillNerfListener implements Listener {

    @EventHandler
    public void onMinerJob(JobsPrePaymentEvent e){
        if(VehiclesMain.getPlugin().getPlayerVehicle((Player) e.getPlayer()) == null) return;
        if(e.getActionInfo().getType() == ActionType.BREAK && e.getJob() == Jobs.getJob("Minero") && VehiclesMain.getPlugin().getPlayerVehicle((Player) e.getPlayer()).getType() == VehicleType.DRILL){
            e.setAmount(e.getAmount() / 10);
        }
    }

    @EventHandler
    public void onMinerExpGain(JobsExpGainEvent e){
        if(VehiclesMain.getPlugin().getPlayerVehicle((Player) e.getPlayer()) == null) return;
        if(e.getActionInfo().getType() == ActionType.BREAK && e.getJob() == Jobs.getJob("Minero") && VehiclesMain.getPlugin().getPlayerVehicle((Player) e.getPlayer()).getType() == VehicleType.DRILL){
            e.setExp(e.getExp() / 10);
        }
    }

}
