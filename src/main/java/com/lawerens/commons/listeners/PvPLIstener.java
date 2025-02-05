package com.lawerens.commons.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static com.lawerens.core.LawerensUtils.sendMessageWithPrefix;

public class PvPLIstener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Arrow){
            e.setDamage(e.getDamage()*0.6);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate != null && chestplate.getType() == Material.ELYTRA && (player.getWorld().getName().equalsIgnoreCase("Yate") || player.getWorld().getName().equalsIgnoreCase("world"))) {
            Inventory inventory = player.getInventory();

            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack item = inventory.getItem(i);
                if (item != null && isChestplate(item)) {
                    player.getInventory().setChestplate(item);
                    inventory.setItem(i, chestplate);
                    sendMessageWithPrefix(player, "#FF4949&lC#FF615A&lo#FE786A&lm#FE907B&lb#FEA88C&la#FDBF9C&lt#FDD7AD&le", "&7Tu &eÉlitra&7 ha sido reemplazada por una pechera. En este lugar no la puedes usar");
                    return;
                }
            }

            boolean addedToInventory = inventory.addItem(chestplate).isEmpty();

            if (addedToInventory) {
                player.getInventory().setChestplate(null);
                sendMessageWithPrefix(player, "#FF4949&lC#FF615A&lo#FE786A&lm#FE907B&lb#FEA88C&la#FDBF9C&lt#FDD7AD&le", "&7Tu &eÉlitra&7 se guardó en el inventario. Aquí no la puedes usar en este lugar.");
            } else {
                player.getInventory().setChestplate(null);
                player.getWorld().dropItemNaturally(player.getLocation(), chestplate);
                sendMessageWithPrefix(player, "#FF4949&lC#FF615A&lo#FE786A&lm#FE907B&lb#FEA88C&la#FDBF9C&lt#FDD7AD&le", "&7Tu &eÉlitra&7 se sacó de tu inventario. No están permitidas en este lugar.");
            }

            event.setCancelled(true);
        }
    }

    private boolean isChestplate(ItemStack item) {
        Material type = item.getType();
        return type == Material.LEATHER_CHESTPLATE ||
                type == Material.CHAINMAIL_CHESTPLATE ||
                type == Material.IRON_CHESTPLATE ||
                type == Material.GOLDEN_CHESTPLATE ||
                type == Material.DIAMOND_CHESTPLATE ||
                type == Material.NETHERITE_CHESTPLATE;
    }

}
