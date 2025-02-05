package com.lawerens.commons.configuration;

import com.lawerens.commons.LawerensCommons;
import com.lawerens.commons.model.RussianRouletteRoom;
import com.lawerens.core.LawerensUtils;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

@Getter
public class ConfigManager {

    private final RussianRouletteConfig russianRouletteConfig;

    public ConfigManager(LawerensCommons plugin) {
        this.russianRouletteConfig = new RussianRouletteConfig(plugin);
    }

    public void loadRussianRoulettesRooms() {
        RussianRouletteRoom.getRooms().clear();
        ConfigurationSection sec = russianRouletteConfig.asConfig().getConfigurationSection("Rooms");
        if(sec != null){
            for (String key : sec.getKeys(false)) {
                RussianRouletteRoom rouletteRoom = RussianRouletteRoom.fromConfig(sec.getConfigurationSection(key));
                RussianRouletteRoom.getRooms().add(rouletteRoom);
            }
        }
    }

    public void updateRussianRouletteRoom(RussianRouletteRoom room) {
        ConfigurationSection sec = russianRouletteConfig.asConfig().createSection("Rooms." + room.getName());

        LawerensUtils.writeLocation(sec, "firstSeat", room.getFirstSeat());
        LawerensUtils.writeLocation(sec, "secondSeat", room.getSecondSeat());
        LawerensUtils.writeLocation(sec, "centerBlock", room.getCenterBlock());

        russianRouletteConfig.saveConfig();
    }

    public void deleteRussianRouletteRoom(RussianRouletteRoom room){
        ConfigurationSection sec = russianRouletteConfig.asConfig().getConfigurationSection("Rooms." + room.getName());
        if(sec != null) russianRouletteConfig.set("Rooms."+room.getName(), null);
        russianRouletteConfig.saveConfig();
    }
}
