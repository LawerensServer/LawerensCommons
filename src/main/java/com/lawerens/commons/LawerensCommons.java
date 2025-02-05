package com.lawerens.commons;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.lawerens.commons.commands.DebugCommand;
import com.lawerens.commons.commands.BloodMoonCommand;
import com.lawerens.commons.commands.RussianRouletteRequestCommand;
import com.lawerens.commons.commands.RussianRouletteRoomCommand;
import com.lawerens.commons.configuration.ConfigManager;
import com.lawerens.commons.listeners.*;
import com.lawerens.commons.model.MoonsManager;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

import static com.lawerens.core.LawerensUtils.sendMessageWithPrefix;

public final class LawerensCommons extends JavaPlugin {

    private static LawerensCommons INSTANCE;
    public static LawerensCommons get() {
        return INSTANCE;
    }

    @Getter
    public static ConfigManager configManager;
    @Getter
    public static Essentials essentials;

    @Getter
    public static MoonsManager moonsManager;


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
        CustomItemsListener.explosiveArrowKey = new NamespacedKey(this, "explosive_arrow");

        getCommand("lrr").setExecutor(new RussianRouletteRoomCommand());
        getCommand("lrr").setTabCompleter(new RussianRouletteRoomCommand());
        getCommand("rr").setExecutor(new RussianRouletteRequestCommand());
        getCommand("rr").setTabCompleter(new RussianRouletteRequestCommand());

        DebugCommand dc = getDebugCommand();

        getCommand("ldebug").setExecutor(dc);

        getServer().getPluginManager().registerEvents(new JobsDrillNerfListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInOutBoundsWorldListener(), this);
        getServer().getPluginManager().registerEvents(new CustomItemsListener(), this);
        getServer().getPluginManager().registerEvents(new RussianRouletteListener(), this);
        getServer().getPluginManager().registerEvents(new NetherMobsListener(), this);
        getServer().getPluginManager().registerEvents(new PvPLIstener(), this);
        getServer().getPluginManager().registerEvents(new YateListener(), this);
        getServer().getPluginManager().registerEvents(new ZonesTitleListener(), this);

        configManager = new ConfigManager(this);
        configManager.loadRussianRoulettesRooms();

        moonsManager = new MoonsManager();

        getCommand("bloodmoon").setTabCompleter(new BloodMoonCommand(moonsManager.getBloodMoon()));
        getCommand("bloodmoon").setExecutor(new BloodMoonCommand(moonsManager.getBloodMoon()));

        MoonsManager.key = new NamespacedKey(this, "bossbarmoons");
        getServer().getPluginManager().registerEvents(moonsManager.getBloodMoon(), this);
    }

    private static DebugCommand getDebugCommand() {
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

                for(OfflinePlayer p : Bukkit.getOfflinePlayers()){
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

        dc.add("rankall", (player, args) -> {
            if(args.length == 3){
                String group = args[1];
                String duration = args[2];

                for(Player p : Bukkit.getOnlinePlayers()){
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user "+p.getName()+" parent addtemp "+group+" "+duration);
                }
            }else{
                sendMessageWithPrefix(player, "DEPURACIÓN", "&CUso incorrecto. &fUsa /ldebug rankall [rango] [duracion: 1d, 1mo, 30m, 150s]");
            }
        });
        return dc;
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);

        HandlerList.unregisterAll(this);
    }

}
