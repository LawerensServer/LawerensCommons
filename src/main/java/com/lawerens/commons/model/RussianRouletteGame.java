package com.lawerens.commons.model;

import com.lawerens.commons.LawerensCommons;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.lawerens.commons.utils.CommonsUtils.sendMessageWithPrefix;

@Data
public class RussianRouletteGame {

    public static final List<RussianRouletteGame> games = new ArrayList<>();

    private final RussianRouletteRoom room;
    private final LawerensCommons plugin;
    private RussianRouletteState state;
    private final Player first, second;
    private final ArmorStand firstSeat, secondSeat;
    private final PlayerInventory firstInventory, secondInventory;
    private int bulletsLeft;
    private boolean turn;

    public RussianRouletteGame(RussianRouletteRoom room, LawerensCommons plugin, Player first, Player second) {
        this.room = room;
        this.plugin = plugin;
        this.state = RussianRouletteState.WAITING;
        this.first = first;
        this.second = second;

        this.firstSeat = (ArmorStand)room.getFirstSeat().getWorld().spawnEntity(room.getFirstSeat().clone().add((double)0.0F, (double)-1.5F, (double)0.0F), EntityType.ARMOR_STAND);
        this.firstSeat.setVisible(false);
        this.firstSeat.setGravity(false);
        this.firstSeat.setMarker(true);
        this.firstSeat.setInvulnerable(true);

        this.secondSeat = (ArmorStand)room.getSecondSeat().getWorld().spawnEntity(room.getSecondSeat().clone().add((double)0.0F, (double)-1.5F, (double)0.0F), EntityType.ARMOR_STAND);
        this.secondSeat.setVisible(false);
        this.secondSeat.setGravity(false);
        this.secondSeat.setMarker(true);
        this.secondSeat.setInvulnerable(true);

        this.firstInventory = first.getInventory();
        this.secondInventory = second.getInventory();
        this.bulletsLeft = (new Random()).nextInt(6) + 1;
        this.turn = false;
        this.start();
    }

    private void start() {
        firstSeat.addPassenger(first);
        secondSeat.addPassenger(second);

        //TODO: Ambient sound

        broadcast(" ");
        broadcast("El juego ha comenzado...");
        broadcast("&e&l! &7El chat solo lo podrán ver solo tu y el contrario.");
        broadcast("&7y no podrán ver el chat global.");
        broadcast(" ");

        girarArco();
    }

    private void girarArco() {
        getRoom().getCenterBlock().getWorld().getBlockAt(getRoom().getCenterBlock()).setType(Material.ITEM_FRAME);
        ItemFrame frame = (ItemFrame) getRoom().getCenterBlock().getWorld().getBlockAt(getRoom().getCenterBlock());
        frame.setRotation(Rotation.CLOCKWISE);
    }

    private void broadcast(String message){
        sendMessageWithPrefix(first, "RULETA RUSA", message);
        sendMessageWithPrefix(second, "RULETA RUSA", message);

    }
    public static Optional<RussianRouletteGame> getGame(Player player){
        return games.stream().filter(russianRouletteGame ->
            russianRouletteGame.first == player || russianRouletteGame.second == player
        ).findFirst();
    }
}
