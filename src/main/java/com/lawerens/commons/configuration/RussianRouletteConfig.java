package com.lawerens.commons.configuration;

import com.lawerens.core.configuration.LawerensConfig;
import org.bukkit.plugin.Plugin;

public class RussianRouletteConfig extends LawerensConfig {

    public RussianRouletteConfig(Plugin plugin) {
        super("RussianRoulette", plugin.getDataFolder(), true, plugin);
    }

}
