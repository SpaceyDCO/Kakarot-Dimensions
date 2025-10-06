package com.kakarot.managers;

import com.kakarot.Main;
import com.kakarot.data.Upgrade;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

public class UpgradeManager {
    private final Main plugin;
    private final Map<String, Upgrade> upgrades = new HashMap<>();

    public UpgradeManager(Main plugin) {
        this.plugin = plugin;
    }

    public void loadUpgrades() {
        File upgradesFile = new File(plugin.getDataFolder(), "upgrades.yml");
        if(!upgradesFile.exists()) {
            plugin.saveResource("upgrades.yml", false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(upgradesFile);
        ConfigurationSection upgradesSection = config.getConfigurationSection("upgrades");
        if(upgradesSection == null) {
            plugin.getLogger().warning("No 'upgrades' section found in upgrades.yml");
            return;
        }
        for(String id : upgradesSection.getKeys(false)) {
            try {
                String name = upgradesSection.getString(id + ".name");
                List<String> lore = upgradesSection.getStringList(id + ".lore");
                String guiItem = upgradesSection.getString(id + ".gui_item");
                int cost = upgradesSection.getInt(id + ".cost");
                List<String> requiredUpgrades = upgradesSection.getStringList(id + ".requirements.upgrades");
                List<String> requiredAchievements = upgradesSection.getStringList(id + ".requirements.achievements");
                Map<String, Object> actions = upgradesSection.getConfigurationSection(id + ".actions").getValues(false);
                Upgrade upgrade = new Upgrade(id, name, lore, guiItem, cost, requiredUpgrades, requiredAchievements, actions);
                this.upgrades.put(id, upgrade);
                plugin.getLogger().info("Loaded upgrade " + id);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to load upgrade with ID: " + id, e);
            }
        }
    }

    /**
     * Retrieves an upgrade by its unique ID
     * @param id The id of the upgrade to retrieve
     * @return An Optional containing the upgrade, empty Optional if the upgrade doesn't exist
     */
    public Optional<Upgrade> getUpgrade(String id) {
        return Optional.ofNullable(this.upgrades.get(id));
    }
}
