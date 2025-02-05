package com.lawerens.commons.listeners;

import com.lawerens.commons.model.RussianRouletteGame;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import static com.lawerens.core.LawerensUtils.sendMessageWithPrefix;

public class RussianRouletteListener implements Listener {

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e){
        if(RussianRouletteGame.getGame(e.getPlayer()).isPresent()){
            e.setCancelled(true);
            sendMessageWithPrefix(e.getPlayer(), "&#FF0000&lR&#FF0000&lR", "&c¡No recojer items aquí!");
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        if(RussianRouletteGame.getGame(e.getPlayer()).isPresent()){
            e.setCancelled(true);
            sendMessageWithPrefix(e.getPlayer(), "&#FF0000&lR&#FF0000&lR", "&c¡No puedes tirar tu apuesta!");
        }
    }

    @EventHandler
    public void onDismount(EntityDismountEvent e){
        if(e.getDismounted().getType() == EntityType.ARMOR_STAND && e.getEntity() instanceof Player p) {
            if (RussianRouletteGame.getGame(p).isPresent()) {
                e.setCancelled(true);
                sendMessageWithPrefix(p, "&#FF0000&lR&#FF0000&lR", "&c¡No puedes abandonar tu puesto!");
            }
        }
    }

    @EventHandler
    public void onEntityAttack(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof Player p) {
            if (RussianRouletteGame.getGame(p).isPresent()) {
                e.setCancelled(true);
                sendMessageWithPrefix(p, "&#FF0000&lR&#FF0000&lR", "&c¡No puedes hacer ese daño aquí!");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent e){
        if(e.isCancelled()) return;
        if(RussianRouletteGame.getGame(e.getPlayer()).isPresent()){
            e.getRecipients().clear();

            e.getRecipients().add(RussianRouletteGame.getGame(e.getPlayer()).get().getFirst());
            e.getRecipients().add(RussianRouletteGame.getGame(e.getPlayer()).get().getSecond());

            e.setFormat("&a[Chat RR] &f"+e.getPlayer().getName()+"&8: &e"+e.getMessage());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        if (RussianRouletteGame.getGame(e.getPlayer()).isPresent()) {
            // RussianRouletteGame.getGame(e.getPlayer()).get().checkWinner();
        }
    }

}
