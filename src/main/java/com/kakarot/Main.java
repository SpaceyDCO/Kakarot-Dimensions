package com.kakarot;

import com.kakarot.commands.FragmentsCommand;
import com.kakarot.commands.PlotCommands;
import com.kakarot.database.DataManager;
import com.kakarot.database.InMemoryDataManager;
import com.kakarot.listeners.PlayerConnectionListener;
import com.kakarot.managers.MessageManager;
import com.kakarot.managers.PlotManager;
import com.kakarot.managers.UpgradeManager;
import com.kakarot.services.ActionService;
import com.kakarot.services.UpgradeService;
import lombok.Getter;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {
    @Getter private static Main instance;
    @Getter private PlotManager plotManager;
    @Getter private DataManager dataManager;
    @Getter private MessageManager messageManager;
    @Getter private UpgradeManager upgradeManager;
    @Getter private UpgradeService upgradeService;
    @Getter private ActionService actionService;
    private PlayerConnectionListener playerConnectionListener;

    @Override
    public void onEnable() {
        instance = this;
        this.messageManager = new MessageManager();
        this.dataManager = new InMemoryDataManager();
        this.dataManager.initialize();
        this.plotManager = new PlotManager(this);
        this.plotManager.loadDimensionWorld();
        this.plotManager.loadAllOccupiedPlots();
        this.upgradeManager = new UpgradeManager(this);
        this.upgradeManager.loadUpgrades();
        this.upgradeService = new UpgradeService(this);
        this.actionService = new ActionService(this);
        this.playerConnectionListener = new PlayerConnectionListener(this);
        File defaultSchematicFile = new File(getDataFolder(), "schematics/max_chamber.schematic");
        if(!defaultSchematicFile.exists()) {
            getLogger().info("Default chamber schematic not found, loading it now...");
            saveResource("schematics/max_chamber.schematic", false);
        }
        getServer().getPluginManager().registerEvents(this.playerConnectionListener, this);
        getCommand("chamber").setExecutor(new PlotCommands(this, this.plotManager, this.messageManager));
        getCommand("fragments").setExecutor(new FragmentsCommand(this));
        getLogger().info("Kakarot Dimensions extension enabled...");
    }

    @Override
    public void onDisable() {
        this.dataManager.shutdown();
        getLogger().info("Kakarot Dimensions extension disabled...");
        PlayerJoinEvent.getHandlerList().unregister(this.playerConnectionListener);
        PlayerQuitEvent.getHandlerList().unregister(this.playerConnectionListener);
    }
}