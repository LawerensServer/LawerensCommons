package com.lawerens.commons.commands;

import com.lawerens.commons.gui.RussianRouletteRoomsGUI;
import com.lawerens.commons.model.RussianRouletteGame;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.lawerens.core.LawerensUtils.sendMessageWithPrefix;

public class RussianRouletteRequestCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.isOp()) return false;
        if(sender instanceof Player player){
            if(args.length == 0){
                sendMessageWithPrefix(player, "RULETA RUSA", "&c¡Debes específicar un jugador!");
                return false;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if(target == null || !target.isOnline()){
                sendMessageWithPrefix(player, "RULETA RUSA", "&cJugador desconocido.");
                return false;
            }

            if(RussianRouletteGame.getGame(target).isPresent()) {
                sendMessageWithPrefix(player, "RULETA RUSA", "&cEse jugador ya esta jugando la ruleta rusa.");
            }

            RussianRouletteRoomsGUI gui = new RussianRouletteRoomsGUI(player, target);
            gui.open(player);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }
}
