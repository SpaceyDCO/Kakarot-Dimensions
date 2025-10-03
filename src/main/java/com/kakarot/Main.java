package com.kakarot;

import com.kakarot.commands.PlotCommands;
import com.kakarot.data.Plot;
import com.kakarot.managers.MessageManager;
import com.kakarot.managers.PlotManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main extends JavaPlugin {
    @Getter private static Main instance;
    @Getter private PlotManager plotManager;
    @Getter private MessageManager messageManager;
    //Temporary
    @Getter private final Map<UUID, Plot> playerPlots = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        this.plotManager = new PlotManager(this);
        this.plotManager.loadDimensionWorld();
        this.messageManager = new MessageManager();
        getCommand("chamber").setExecutor(new PlotCommands(this, this.plotManager, this.messageManager));
        getLogger().info("Kakarot Dimensions extension enabled...");
    }

    @Override
    public void onDisable() {
        getLogger().info("Kakarot Dimensions extension disabled...");
    }

    public void setPlayerDimension(UUID playerUUID, Plot dimension) {
        playerPlots.put(playerUUID, dimension);
        plotManager.markAsOccupied(new PlotManager.GridLocation(dimension.getGridX(), dimension.getGridZ()));
    }
    public boolean hasDimension(UUID playerUUID) {
        return playerPlots.containsKey(playerUUID);
    }
}