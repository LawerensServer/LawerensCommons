package com.lawerens.commons;

import com.lawerens.commons.commands.DebugCommand;
import com.lawerens.commons.commands.RussianRouletteRequestCommand;
import com.lawerens.commons.commands.RussianRouletteRoomCommand;
import com.lawerens.commons.configuration.ConfigManager;
import com.lawerens.commons.listeners.CustomItemsListener;
import com.lawerens.commons.listeners.JobsDrillNerfListener;
import com.lawerens.commons.listeners.PlayerInOutBoundsWorldListener;
import com.lawerens.commons.listeners.RussianRouletteListener;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class LawerensCommons extends JavaPlugin {

    private static LawerensCommons INSTANCE;
    public static LawerensCommons get() {
        return INSTANCE;
    }

    @Getter
    public static ConfigManager configManager;

    @Override
    public void onLoad() {
        try {
            StateFlag flag = new StateFlag("allow-jaula", false);
            WorldGuard.getInstance().getFlagRegistry().register(flag);
            CustomItemsListener.flag = flag;
        }catch (IllegalStateException | FlagConflictException e){
            CustomItemsListener.flag = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get("allow-jaula");
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onEnable() {
        INSTANCE = this;

        CustomItemsListener.totemTriadaKey = new NamespacedKey(this, "totem_usos");

        getCommand("lrr").setExecutor(new RussianRouletteRoomCommand());
        getCommand("lrr").setTabCompleter(new RussianRouletteRoomCommand());
        getCommand("rr").setExecutor(new RussianRouletteRequestCommand());
        getCommand("rr").setTabCompleter(new RussianRouletteRequestCommand());

        DebugCommand dc = new DebugCommand();

        dc.add("totem", player -> {

            ItemStack it = CustomItemsListener.getTotemTriadaItem(3);
            CustomItemsListener.setReamingTotemTriadaUses(it, 3);

            player.getInventory().addItem(
                    it
            );
        });

        getCommand("ldebug").setExecutor(dc);

        getServer().getPluginManager().registerEvents(new JobsDrillNerfListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInOutBoundsWorldListener(), this);
        getServer().getPluginManager().registerEvents(new CustomItemsListener(), this);
        getServer().getPluginManager().registerEvents(new RussianRouletteListener(), this);

        configManager = new ConfigManager(this);
        configManager.loadRussianRoulettesRooms();
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
