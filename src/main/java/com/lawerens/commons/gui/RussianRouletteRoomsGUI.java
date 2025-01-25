package com.lawerens.commons.gui;

import com.lawerens.commons.LawerensCommons;
import com.lawerens.commons.model.RussianRouletteGame;
import com.lawerens.commons.model.RussianRouletteRoom;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.codehaus.plexus.util.StringUtils;
import xyz.lawerens.utils.ItemBuilder;
import xyz.lawerens.utils.menu.Menu;
import xyz.lawerens.utils.menu.MenuButton;

import java.util.function.Consumer;

import static com.lawerens.commons.utils.CommonsUtils.sendMessageWithPrefix;

public class RussianRouletteRoomsGUI extends Menu {

    private final Player viewer, target;

    public RussianRouletteRoomsGUI(Player viewer, Player target) {
        super("&0&nLista de cuartos", 1);
        this.viewer = viewer;
        this.target = target;

        int i = 0;
        for(RussianRouletteRoom room : RussianRouletteRoom.getRooms()){
            int finalI = i;
            addButton(new MenuButton() {
                @Override
                public int getSlot() {
                    return finalI;
                }

                @Override
                public ItemStack getItem() {
                    String color = room.isInUse() ? "&c" : "&a";
                    ItemBuilder ib =  new ItemBuilder(room.isInUse() ? Material.REDSTONE_BLOCK : Material.EMERALD_BLOCK)
                            .setDisplayName(color+"Cuarto "+room.getName())

                            .addLore(" ")
                            .addLore(color+"▍ Estado: &f" + (room.isInUse() ? StringUtils.capitalise(room.getGame().getState().name().toLowerCase()) : "Libre"))
                            .addLore(color+"▍ Zona: &fCasino");

                    if(room.isInUse()){
                        ib.addLore("");
                        ib.addLore(color+"▍ Jugadores: &f" +room.getGame().getFirst().getName()+", "+room.getGame().getSecond().getName());
                    }

                    return ib.build();
                }

                @Override
                public Consumer<ClickType> getAction() {
                    return clickType -> {
                        if(room.isInUse()){
                            sendMessageWithPrefix(viewer, "RULETA RUSA", "&cEse cuarto esta en uso ahora.");
                            viewer.playSound(viewer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.1f, 1f);
                            return;
                        }

                        if(!target.isOnline()){
                            sendMessageWithPrefix(viewer, "RULETA RUSA", "&cEl jugador se desconectó mientras elegías la sala.");
                            viewer.closeInventory();
                        }

                        RussianRouletteGame game = new RussianRouletteGame(
                                room, LawerensCommons.get(), viewer, target
                        );
                        RussianRouletteGame.games.add(game);
                    };
                }
            });
            i++;
        }
    }
}
