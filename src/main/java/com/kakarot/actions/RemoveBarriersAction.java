package com.kakarot.actions;

import com.kakarot.Main;
import com.kakarot.data.Plot;
import com.kakarot.util.WorldEditUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.logging.Level;

public class RemoveBarriersAction implements Action {
    private final String pos1String;
    private final String pos2String;
    public RemoveBarriersAction(ConfigurationSection data) {
        this.pos1String = data.getString("pos1");
        this.pos2String = data.getString("pos2");
    }

    @Override
    public boolean isValid() {
        return pos1String != null && pos2String != null;
    }

    @Override
    public void execute(Plot plot) {
        Main plugin = Main.getInstance();
        World plotWorld = plugin.getPlotManager().getDimensionWorld();
        if(plotWorld == null) return;
        try {
            Location plotOrigin = plugin.getPlotManager().gridToBukkitLocation(plot.getGridX(), plot.getGridZ());
            String[] pos1Parts = pos1String.split(",");
            String[] pos2Parts = pos2String.split(",");
            double relX1 = Double.parseDouble(pos1Parts[0]);
            double relY1 = Double.parseDouble(pos1Parts[1]);
            double relZ1 = Double.parseDouble(pos1Parts[2]);
            double relX2 = Double.parseDouble(pos2Parts[0]);
            double relY2 = Double.parseDouble(pos2Parts[1]);
            double relZ2 = Double.parseDouble(pos2Parts[2]);
            Location corner1 = plotOrigin.clone().add(relX1, relY1, relZ1);
            Location corner2 = plotOrigin.clone().add(relX2, relY2, relZ2);
            WorldEditUtils.setRegionToAir(corner1, corner2);
        }catch(Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to parse coordinates for remove_barriers action. There might be a syntax error in upgrades.yml", e);
        }
    }
}
