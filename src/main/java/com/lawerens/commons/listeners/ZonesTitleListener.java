package com.lawerens.commons.listeners;

import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.raidstone.wgevents.events.RegionEnteredEvent;
import net.raidstone.wgevents.events.RegionsChangedEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Set;

import static com.lawerens.commons.utils.CommonsUtils.sendTitle;

public class ZonesTitleListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onRegionsChanged(RegionsChangedEvent event){
        if(event.getPlayer() == null) return;

        Player player = event.getPlayer();
        Set<ProtectedRegion> enteredRegions = event.getCurrentRegions();
        Set<ProtectedRegion> leftRegions = event.getPreviousRegions();

        boolean wasPvP = false;
        for (ProtectedRegion region : leftRegions) {
            StateFlag.State pvpFlag = region.getFlag(Flags.PVP);
            wasPvP = pvpFlag == StateFlag.State.ALLOW;
        }

        for (ProtectedRegion region : enteredRegions) {
            StateFlag.State pvpFlag = region.getFlag(Flags.PVP);
            boolean isPvP = pvpFlag == StateFlag.State.ALLOW;

            if (isPvP != wasPvP) {
                if (isPvP) {
                    sendTitle(player, "#ff0000&lZONA PRELIGROSA", "&7¡En esta zona hay #FB0857PvP&7!", 0, 6, 0);
                } else {
                    sendTitle(player, "#00ff00&lZONA SEGURA", "&7Has entrado en una #78FF33Zona Segura", 0, 6, 0);
                }
                break;
            }
        }
    }

}
