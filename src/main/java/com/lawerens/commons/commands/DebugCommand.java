package com.lawerens.commons.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class DebugCommand implements CommandExecutor {

    private final Map<String, Consumer<Player>> debugFunctions = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.isOp()) return false;
        if(args.length == 0) return false;
        for (Map.Entry<String, Consumer<Player>> entry : debugFunctions.entrySet()) {
            if(entry.getKey().equals(args[0])){
                entry.getValue().accept((Player) sender);
                return true;
            }
        }
        return false;
    }

    public void add(String key, Consumer<Player> consumer){
        debugFunctions.put(key, consumer);
    }
}