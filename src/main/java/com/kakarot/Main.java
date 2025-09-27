package com.kakarot;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Getter private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Kakarot Dimensions extension enabled...");
    }

    @Override
    public void onDisable() {
        getLogger().info("Kakarot Dimensions extension disabled...");
    }

}