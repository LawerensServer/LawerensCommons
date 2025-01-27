package com.lawerens.commons.listeners;

import com.lawerens.commons.LawerensCommons;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import emanondev.itemedit.ItemEdit;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.lawerens.Lawerens;
import xyz.lawerens.utils.ItemBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static xyz.lawerens.utils.LawerensUtils.sendMessage;

public class CustomItemsListener implements Listener {

    public static StateFlag flag;
    private final List<Block> glassBlocks = new ArrayList<>();
    private final List<UUID> jaulasCreated = new ArrayList<>();
    public static NamespacedKey totemTriadaKey;

    public static ItemStack getTotemTriadaItem(int uses){

        return new ItemBuilder(Material.TOTEM_OF_UNDYING)
                .setDisplayName("#ccff2dTotem Tríada ("+uses+" usos)")

                .addLore(" ")

                .addLore("#ccff2d▍ &fEste totem no es como cualquier")
                .addLore("#ccff2d▍ &fotro, ya que este tiene la")
                .addLore("#ccff2d▍ &fposibilidad de funcionar varías")
                .addLore("#ccff2d▍ &fveces seguidas.")
                .addLore(" ")
                .addLore("#ccff2d▍ Información:")
                .addLore("#ccff2d▍ &fUsos restantes: &e"+uses+"")

                .build();
    }

    private boolean isTotemTriada(ItemStack item) {
        if (item != null && item.getType() == Material.TOTEM_OF_UNDYING) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                return container.has(totemTriadaKey, PersistentDataType.INTEGER);
            }
        }
        return false;
    }

    @EventHandler
    public void onEntityResurrect(EntityResurrectEvent e){
        if(!(e.getEntity() instanceof Player p)) return;
        if(e.getHand() == null) return;
        ItemStack item = p.getInventory().getItem(e.getHand());
        if(!isTotemTriada(item)) return;

        int reamingUses = getReamingTotemTriadaUses(item);
        if (reamingUses > 1) {
            ItemStack newItem = getTotemTriadaItem(reamingUses - 1);

            setReamingTotemTriadaUses(newItem, reamingUses - 1);
            p.getInventory().setItem(e.getHand(), newItem);
        }
    }

    public static void setReamingTotemTriadaUses(ItemStack item, int i) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(totemTriadaKey, PersistentDataType.INTEGER, i);
        item.setItemMeta(meta);
    }

    private static int getReamingTotemTriadaUses(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.getOrDefault(totemTriadaKey, PersistentDataType.INTEGER, 3);
    }


    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e){
        Player p = e.getPlayer();
        if(e.getItem().isSimilar(ItemEdit.get().getServerStorage().getItem("zanahoria_eterna"))){
            ItemStack newI = ItemEdit.get().getServerStorage().getItem("zanahoria_eterna");
            assert newI != null;
            newI.setAmount(e.getItem().getAmount());
            e.setReplacement(newI);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(e.getItem() == null) return;
        if(e.getItem().isSimilar(ItemEdit.get().getServerStorage().getItem("jaula"))) {
            e.setCancelled(true);
            if (jaulasCreated.contains(p.getUniqueId())) {
                sendMessage(p, Lawerens.COLOR + "JAULA &8» &c¡No puedes crear más de una jaula a la vez!");
                return;
            }
            RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(p.getWorld()));
            BlockVector3 vector = BlockVector3.at(
                    p.getLocation().getBlockX(),
                    p.getLocation().getBlockY(),
                    p.getLocation().getBlockZ()
            );
            assert regionManager != null;
            ApplicableRegionSet set = regionManager.getApplicableRegions(vector);
    
            for (ProtectedRegion protectedRegion : set) {
                if (protectedRegion.getFlag(flag) == StateFlag.State.DENY) {
                    sendMessage(p, Lawerens.COLOR + "JAULA &8» &c¡No puedes generar una jaula en esta región!");
                    return;
                }
            }
    
            Location center = p.getLocation().toCenterLocation();
            World world = center.getWorld();
            int radius = 6;
    
            List<Block> blocksToRestore = new ArrayList<>();
    
            Bukkit.getScheduler().runTask(LawerensCommons.get(), () -> {
                for (int x = -radius; x <= radius; x++) {
                    for (int y = -radius; y <= radius; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            double distance = Math.sqrt(x * x + y * y + z * z);
    
                            if (distance >= radius - 0.5 && distance <= radius + 0.5) {
                                Location blockLocation = center.clone().add(x, y, z);
                                Block block = world.getBlockAt(blockLocation);
    
                                if (block.getType() == Material.AIR) {
                                    blocksToRestore.add(block);
                                    glassBlocks.add(block);
                                    block.setType(Material.GLASS);
                                }
                            }
                        }
                    }
                }
            });
    
            sendMessage(p, Lawerens.COLOR + "JAULA &8» &f¡Has creado una jaula en tu posición!");
            removeSpecificItem(p.getInventory(), e.getItem());
            UUID id = p.getUniqueId();
            jaulasCreated.add(id);
    
            Bukkit.getScheduler().runTaskLater(LawerensCommons.get(), () -> {
                for (Block block : blocksToRestore) {
                    block.setType(Material.AIR);
                    glassBlocks.remove(block);
                    jaulasCreated.remove(id);
                }
            }, 30L * 20L);

        }
    }

    private void removeSpecificItem(PlayerInventory inventory, ItemStack targetItem) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack currentItem = inventory.getItem(slot);
            if (currentItem != null && currentItem.isSimilar(targetItem)) {
                if (currentItem.getAmount() > 1) {
                    currentItem.setAmount(currentItem.getAmount() - 1);
                    inventory.setItem(slot, currentItem);
                } else {
                    inventory.setItem(slot, null);
                }
                break;
            }
        }
    }


    @EventHandler
    public void onBreak(BlockBreakEvent e){
        if(glassBlocks.contains(e.getBlock())){
            e.setCancelled(true);
            e.setDropItems(false);
            sendMessage(e.getPlayer(), Lawerens.COLOR+"JAULA &8» &c¡Los bloques de una jaula no se pueden romper, espera a que desaparezca!");
        }
    }

}
