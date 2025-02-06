package com.lawerens.commons.listeners;

import com.lawerens.core.LawerensUtils;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.raidstone.wgevents.events.RegionsChangedEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Set;

public class ZonesTitleListener implements Listener {


    @EventHandler(priority = EventPriority.LOW)
    public void onRegionsChanged(RegionsChangedEvent event){
        if(event.getPlayer() == null) return;

        Player player = event.getPlayer();
        Set<ProtectedRegion> enteredRegions = event.getCurrentRegions();
        Set<ProtectedRegion> leftRegions = event.getPreviousRegions();

        boolean wasPvP = getHighestPriorityFlag(leftRegions);
        boolean isPvP = getHighestPriorityFlag(enteredRegions);

        if (isPvP != wasPvP) {
            if (isPvP) {
                LawerensUtils.sendTitle(player, "#ff0000&lZONA PELIGROSA", "&7¡En esta zona hay #FB0857PvP&7!", 0, 6 * 20, 0);
            } else {
                LawerensUtils.sendTitle(player, "#00ff00&lZONA SEGURA", "&7Has entrado en una #78FF33Zona Segura", 0, 6 * 20, 0);
            }
        }
    }

    private boolean getHighestPriorityFlag(Set<ProtectedRegion> regions) {
        ProtectedRegion highestPriorityRegion = null;
        for (ProtectedRegion region : regions) {
            if (highestPriorityRegion == null || region.getPriority() > highestPriorityRegion.getPriority()) {
                highestPriorityRegion = region;
            }
        }

        if (highestPriorityRegion != null) {
            StateFlag.State flagState = highestPriorityRegion.getFlag(Flags.PVP);
            return flagState == StateFlag.State.ALLOW;
        }

        return false;
    }


}
