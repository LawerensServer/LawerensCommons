package com.lawerens.commons.commands;

import com.lawerens.commons.model.BloodMoon;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static com.lawerens.core.LawerensUtils.sendMessage;

public class BloodMoonCommand implements TabExecutor {
    
    private final BloodMoon bloodMoon;

    public BloodMoonCommand(BloodMoon bloodMoon) {
        this.bloodMoon = bloodMoon;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "&7&m-----------------------------");
            sendMessage(sender, "&a/BloodMoon status &7- Ver estado");
            sendMessage(sender, "&a/BloodMoon restart &7- Reiniciar Luna Sangrienta");
            sendMessage(sender, "&a/BloodMoon stop &7- Detener Luna Sangrienta");
            sendMessage(sender, "&a/BloodMoon nextnight &7- Ver noches para la siguiente luna");
            sendMessage(sender, "&a/BloodMoon duration &7- Ver duración actual");
            sendMessage(sender, "&7&m-----------------------------");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "status":
                sendMessage(sender, bloodMoon.getStatus());
                break;
            case "restart":
                bloodMoon.reset();
                bloodMoon.start();
                sendMessage(sender, "Luna Sangrienta reiniciada.");
                break;
            case "stop":
                bloodMoon.end();
                bloodMoon.reset();
                sendMessage(sender, "Luna Sangrienta detenida.");
                break;
            case "nextnight":
                sendMessage(sender, "Faltan " + bloodMoon.getDaysToTheNext() + " noches para la siguiente luna sangrienta.");
                break;
            case "duration":
                sendMessage(sender, bloodMoon.getCurrentDuration());
                break;
            default:
                sendMessage(sender, "&cComando desconocido.");
                break;
        }
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("status", "restart", "stop", "nextnight", "duration");
        }
        return Arrays.asList();
    }

}
