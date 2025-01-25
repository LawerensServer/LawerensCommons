package com.lawerens.commons.model;

import com.lawerens.commons.LawerensCommons;
import com.lawerens.commons.utils.CommonsUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class RussianRouletteRoom {

    @Getter
    private final static List<RussianRouletteRoom> rooms = new ArrayList<>();

    private final String name;
    @Nullable
    private Location firstSeat, secondSeat, centerBlock;

    private RussianRouletteRoom(String name, @Nullable Location firstSeat, @Nullable Location secondSeat, @Nullable Location centerBlock) {
        this.name = name;
        this.firstSeat = firstSeat;
        this.secondSeat = secondSeat;
        this.centerBlock = centerBlock;
    }

    public static RussianRouletteRoom fromConfig(ConfigurationSection s){
        Location firstSeat = CommonsUtils.readLocation(s, "firstSeat");
        Location secondSeat = CommonsUtils.readLocation(s, "secondSeat");
        Location centerBlock = CommonsUtils.readLocation(s, "centerBlock");

        return new RussianRouletteRoom(
                s.getName(),
                firstSeat,
                secondSeat,
                centerBlock != null ? centerBlock.toBlockLocation() : null
        );
    }

    public static Optional<RussianRouletteRoom> getRoom(String name){
        return getRooms().stream().filter(rouletteRoom -> rouletteRoom.getName().equalsIgnoreCase(name)).findFirst();
    }

    public static void create(String name) {
        LawerensCommons.configManager.getRussianRouletteConfig().asConfig().createSection("Rooms."+name);
        RussianRouletteRoom room = new RussianRouletteRoom(
                name, null, null, null
        );
        rooms.add(room);
        LawerensCommons.configManager.getRussianRouletteConfig().saveConfig();
    }

    public boolean isInUse() {
        return RussianRouletteGame.games.stream().anyMatch(russianRouletteGame ->
                russianRouletteGame.getRoom() == this);
    }

    @Nullable
    public RussianRouletteGame getGame() {
        return RussianRouletteGame.games.stream().filter(russianRouletteGame ->
                russianRouletteGame.getRoom() == this).findFirst().get();
    }
}
