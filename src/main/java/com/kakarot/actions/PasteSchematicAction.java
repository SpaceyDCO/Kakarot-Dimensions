package com.kakarot.actions;

import com.kakarot.Main;
import com.kakarot.data.Plot;
import com.kakarot.util.WorldEditUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.logging.Level;

public class PasteSchematicAction implements Action {
    private final File schematicFile;
    private final String offsetString;
    public PasteSchematicAction(ConfigurationSection data) {
        String schematicName = data.getString("name");
        this.schematicFile = new File(Main.getInstance().getDataFolder(), "schematics/" + schematicName);
        this.offsetString = data.getString("offset", "0,0,0");
    }


    @Override
    public boolean isValid() {
        return schematicFile.exists();
    }

    @Override
    public void execute(Plot plot) {
        Main plugin = Main.getInstance();
        World plotWorld = plugin.getPlotManager().getDimensionWorld();
        if(plotWorld == null) return;
        try {
            Location plotOrigin = plugin.getPlotManager().gridToBukkitLocation(plot.getGridX(), plot.getGridZ());
            String[] posParts = this.offsetString.split(",");
            double offsetX = Double.parseDouble(posParts[0]);
            double offsetY = Double.parseDouble(posParts[1]);
            double offsetZ = Double.parseDouble(posParts[2]);
            Location pasteLocation = plotOrigin.clone().add(offsetX, offsetY, offsetZ);
            WorldEditUtils.pasteSchematic(this.schematicFile, pasteLocation, false);
        }catch(Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to parse coordinates for paste_schematic action. There might be a syntax error in upgrades.yml", e);
        }
    }
}
