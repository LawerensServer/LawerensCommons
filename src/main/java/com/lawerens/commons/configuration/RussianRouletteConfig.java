package com.lawerens.commons.configuration;

import org.bukkit.plugin.Plugin;
import xyz.lawerens.utils.configuration.LawerensConfig;

public class RussianRouletteConfig extends LawerensConfig {

    public RussianRouletteConfig(Plugin plugin) {
        super("RussianRoulette", plugin.getDataFolder(), true, plugin);
    }

}
