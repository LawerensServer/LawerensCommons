package com.lawerens.commons;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
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
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.lawerens.commons.utils.CommonsUtils.sendMessageWithPrefix;

public final class LawerensCommons extends JavaPlugin {

    private static LawerensCommons INSTANCE;
    public static LawerensCommons get() {
        return INSTANCE;
    }

    @Getter
    public static ConfigManager configManager;
    @Getter
    public static Essentials essentials;


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
        essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");

        CustomItemsListener.totemTriadaKey = new NamespacedKey(this, "totem_usos");

        getCommand("lrr").setExecutor(new RussianRouletteRoomCommand());
        getCommand("lrr").setTabCompleter(new RussianRouletteRoomCommand());
        getCommand("rr").setExecutor(new RussianRouletteRequestCommand());
        getCommand("rr").setTabCompleter(new RussianRouletteRequestCommand());

        DebugCommand dc = new DebugCommand();

        dc.add("totem", (player, args) -> {

            ItemStack it = CustomItemsListener.getTotemTriadaItem(3);
            CustomItemsListener.setReamingTotemTriadaUses(it, 3);

            player.getInventory().addItem(
                    it
            );
        });

        dc.add("homes", (player, args) -> {
            if(args.length > 1){
                int radio;

                try{
                    radio = Integer.parseInt(args[1]);
                }catch (NumberFormatException e){
                    player.sendMessage("Numero inválido.");
                    return;
                }

                for(OfflinePlayer p : Bukkit.getOnlinePlayers()){
                    User user = essentials.getOfflineUser(p.getName());
                    if(user == null) return;
                    List<String> homes = new ArrayList<>();
                    for (String home : user.getHomes()) {
                        if(PlayerInOutBoundsWorldListener.isOutsideRadius(user.getHome(home), radio)){
                            homes.add(home);
                        }
                    }
                    if(!homes.isEmpty()){
                        sendMessageWithPrefix(player, "DEPURACIÓN", "&e"+user.getName()+"&f: &e"+String.join("&f, &e", homes));
                    }
                }
            }else{
                player.sendMessage("Específica el radio, ejemplo: 6000, 15000");
            }
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
