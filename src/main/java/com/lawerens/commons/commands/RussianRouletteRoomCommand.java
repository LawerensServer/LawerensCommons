package com.lawerens.commons.commands;

import com.lawerens.commons.LawerensCommons;
import com.lawerens.commons.model.RussianRouletteRoom;
import com.lawerens.core.LawerensUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.lawerens.core.LawerensUtils.*;

public class RussianRouletteRoomCommand implements TabExecutor {

    private final List<String> subCommands = Arrays.asList(
            "createroom", "deleteroom", "setfirstseatroom", "setsecondseatroom", "setcenterblockroom"
    );

    private final List<String> help = Stream.of(
            " ",
            "&a&LAyuda Ruleta Rusa",
            " ",
            " &7- &f/lrr createroom <nombre> &7| &aCrea un cuarto para una ruleta rusa",
            " &7- &f/lrr delete <nombre> &7| &aElimina un cuarto de la ruleta rusa",
            " &7- &f/lrr setfirstseatroom <nombre> &7| &aEstablece la posición del primer asiento de un cuarto para un ruleta rusa",
            " &7- &f/lrr setsecondseatroom <nombre> &7| &aEstablece la posición del segundo asiento de un cuarto para un ruleta rusa",
            " &7- &f/lrr setcenterblockroom <nombre> &7| &aEstablece la posición del bloque central de un cuarto para un ruleta rusa",
            " "
    ).map(LawerensUtils::color).toList();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.hasPermission("russianroulette.admin")){
            sendMessageWithPrefix(sender, "RULETA RUSA", "&c¡No tienes permisos!");
            return false;
        }

        if(args.length < 2){
            sendMessage(sender, help);
            return false;
        }

        switch(args[0].toLowerCase()){
            case "createroom" -> {
                return handleCreate(sender, args);
            }
            case "deleteroom" -> {
                return handleDelete(sender, args);
            }
            case "setfirstseatroom" -> {
                return handleSetFirstSeat(sender, args);
            }
            case "setsecondseatroom" -> {
                return handleSetSecondSeat(sender, args);
            }
            case "setcenterblockroom" -> {
                return handleSetCenterBlock(sender, args);
            }
            default -> sendMessage(sender, help);
        }
        return false;
    }

    private boolean handleSetFirstSeat(@NotNull CommandSender sender, @NotNull String[] args) {
        if(!(sender instanceof Player p)){
            sendMessageWithPrefix(sender, "RULETA RUSA", "&c¡Solo jugadores pueden hacer esto!");
            return false;
        }

        String name = args[1];

        Optional<RussianRouletteRoom> room = RussianRouletteRoom.getRoom(name);

        if(room.isEmpty()){
            sendMessageWithPrefix(sender, "RULETA RUSA", "&c¡No hay un cuarto llamado así!");
            return false;
        }

        room.get().setFirstSeat(p.getLocation().toCenterLocation());
        LawerensCommons.configManager.updateRussianRouletteRoom(room.get());

        sendMessageWithPrefix(sender, "RUELTA RUSA", "&fEstableciste el primer asiento del cuarto &e"+name+"&f en tu posición.");

        return true;
    }

    private boolean handleSetSecondSeat(@NotNull CommandSender sender, @NotNull String[] args) {
        if(!(sender instanceof Player p)){
            sendMessageWithPrefix(sender, "RULETA RUSA", "&c¡Solo jugadores pueden hacer esto!");
            return false;
        }

        String name = args[1];

        Optional<RussianRouletteRoom> room = RussianRouletteRoom.getRoom(name);

        if(room.isEmpty()){
            sendMessageWithPrefix(sender, "RULETA RUSA", "&c¡No hay un cuarto llamado así!");
            return false;
        }

        room.get().setSecondSeat(p.getLocation().toCenterLocation());
        LawerensCommons.configManager.updateRussianRouletteRoom(room.get());

        sendMessageWithPrefix(sender, "RUELTA RUSA", "&fEstableciste el segundo asiento del cuarto &e"+name+"&f en tu posición.");

        return true;
    }

    private boolean handleSetCenterBlock(@NotNull CommandSender sender, @NotNull String[] args) {
        if(!(sender instanceof Player p)){
            sendMessageWithPrefix(sender, "RULETA RUSA", "&c¡Solo jugadores pueden hacer esto!");
            return false;
        }

        String name = args[1];

        Optional<RussianRouletteRoom> room = RussianRouletteRoom.getRoom(name);

        if(room.isEmpty()){
            sendMessageWithPrefix(sender, "RULETA RUSA", "&c¡No hay un cuarto llamado así!");
            return false;
        }

        room.get().setCenterBlock(p.getLocation().toBlockLocation());

        LawerensCommons.configManager.updateRussianRouletteRoom(room.get());
        sendMessageWithPrefix(sender, "RUELTA RUSA", "&fEstableciste el block central del cuarto &e"+name+"&f en tu posición.");

        return true;
    }

    private boolean handleDelete(@NotNull CommandSender sender, @NotNull String[] args) {
        String name = args[1];

        Optional<RussianRouletteRoom> room = RussianRouletteRoom.getRoom(name);

        if(room.isEmpty()){
            sendMessageWithPrefix(sender, "RULETA RUSA", "&c¡No hay un cuarto llamado así!");
            return false;
        }

        LawerensCommons.getConfigManager().deleteRussianRouletteRoom(room.get());

        sendMessageWithPrefix(sender, "RUELTA RUSA", "&fHas eliminado el cuarto &e"+name+"&f.");

        return true;
    }

    private boolean handleCreate(CommandSender sender, @NotNull String[] args) {
        String name = args[1];

        if(RussianRouletteRoom.getRoom(name).isPresent()){
            sendMessageWithPrefix(sender, "RULETA RUSA", "&cYa hay un cuarto llamado así");
            return false;
        }

        if(!StringUtils.isAlphanumeric(name)){
            sendMessageWithPrefix(sender, "RULETA RUSA", "&cEl nombre del cuarto no debe tener caracteres especiales.");
            return false;
        }

        RussianRouletteRoom.create(name);

        sendMessageWithPrefix(sender, "RUELTA RUSA", "&fHas creado el cuarto &e"+name+"&f.");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return filterSuggestions(subCommands, args[0]);
        if(args.length == 2 && !args[0].equalsIgnoreCase("createroom")){
            return filterSuggestions(RussianRouletteRoom.getRooms().stream().map(RussianRouletteRoom::getName).toList(), args[1]);
        }
        return List.of();
    }
}
