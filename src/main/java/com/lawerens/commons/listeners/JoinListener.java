package com.lawerens.commons.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.geysermc.floodgate.api.FloodgateApi;

public class JoinListener implements Listener {

    @EventHandler
    public void onLogin(PlayerLoginEvent e){
        if(FloodgateApi.getInstance().isFloodgatePlayer(e.getPlayer().getUniqueId())){
            if(!e.getPlayer().getName().startsWith("BD_")) {
                e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Intenta conectarte de nuevo, por favor.");
            }
        }
    }

}
