package com.kakarot.services;

import com.kakarot.Main;
import com.kakarot.data.Plot;
import com.kakarot.data.Upgrade;
import com.kakarot.util.WorldEditUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.logging.Level;

public class ActionService {
    private final Main plugin;
    public ActionService(Main plugin) {
        this.plugin = plugin;
    }

    public void executeAction(Player player, Upgrade upgrade) {
        plugin.getPlotManager().getPlot(player.getUniqueId()).thenAccept(plot -> {
           if(plot == null) {
               plugin.getLogger().severe("Could not execute upgrade actions for player " + player.getName() + " because their plot data was not found");
               return;
           }
            Map<String, Object> actions = upgrade.getActions();
           if(actions.isEmpty()) {
               plugin.getLogger().info("actions is empty");
               return;
           }
           if(actions.containsKey("remove_barriers")) {
               Object rawActionData = actions.get("remove_barriers");
               if(rawActionData instanceof ConfigurationSection) {
                   ConfigurationSection barrierData = (ConfigurationSection) rawActionData;
                   executeRemoveBarriers(plot, barrierData);
               }
           }
           if(actions.containsKey("paste_schematic")) {
               //execute paste schematic, Add later
           }
           //Add more actions in the future, TO DO: remove_npc, add_npc, run_command
        });
    }
    private void executeRemoveBarriers(Plot plot, ConfigurationSection data) {
        World worldPlots = plugin.getPlotManager().getDimensionWorld();
        if(worldPlots == null) {
            plugin.getLogger().severe("Cannot execute remove_barriers... Plot world is not loaded");
            return;
        }
        try {
            Location plotOrigin = plugin.getPlotManager().gridToBukkitLocation(plot.getGridX(), plot.getGridZ());
            String[] pos1Parts = data.getString("pos1").split(",");
            String[] pos2Parts = data.getString("pos2").split(",");
            double relX1 = Double.parseDouble(pos1Parts[0]);
            double relY1 = Double.parseDouble(pos1Parts[1]);
            double relZ1 = Double.parseDouble(pos1Parts[2]);
            double relX2 = Double.parseDouble(pos2Parts[0]);
            double relY2 = Double.parseDouble(pos2Parts[1]);
            double relZ2 = Double.parseDouble(pos2Parts[2]);
            Location corner1 = plotOrigin.clone().add(relX1, relY1, relZ1);
            Location corner2 = plotOrigin.clone().add(relX2, relY2, relZ2);
            plugin.getLogger().info("Attempting remove_barriers action at coordinates:");
            plugin.getLogger().info("- Absolute: " + corner1.getX() + " " + corner1.getY() + " " + corner1.getZ() + " and " + corner2.getX() + " " + corner2.getY() + " " + corner2.getZ());
            plugin.getLogger().info("- Relative: " + relX1 + " " + relY1 + " " + relZ1 + " and " + relX2 + " " + relY2 + " " + relZ2);
            WorldEditUtils.setRegionToAir(corner1, corner2);
            plugin.getLogger().info("Successfully executed 'remove_barriers' action for plot (" + plot.getGridX() + "," + plot.getGridZ() + ").");
        }catch(Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to parse coordinates for remove_barriers action. There might be a syntax error in upgrades.yml", e);
        }
    }
}
