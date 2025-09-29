package com.kakarot.managers;

import com.kakarot.Main;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.util.HashSet;
import java.util.Set;

public class PlotManager {
    private final Main plugin;
    @Getter private World dimensionWorld;

    public static final String WORLD_NAME = "KakarotChamberWorld";
    public static final int PLOT_SIZE = 500;
    public static final int PLOT_SPACING = 500;
    public static final int GRID_CELL_SIZE = PLOT_SIZE + PLOT_SPACING;

    private final Set<String> occupiedPlots = new HashSet<>(); //Change to database read later on

    public PlotManager(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Creates or load the dedicated world for dimensions
     */
    public void loadDimensionWorld() {
        plugin.getLogger().info("Loading or creating dimension world " + WORLD_NAME);
        WorldCreator wc = new WorldCreator(WORLD_NAME);
        wc.environment(World.Environment.NORMAL);
        wc.type(WorldType.FLAT);
        wc.generateStructures(false);
        wc.generator("2;0;1;");
        this.dimensionWorld = wc.createWorld();
        plugin.getLogger().info("Dimension world loaded successfully!");
    }

    /**
     * Finds the next available "plot"
     * @return A GridLocation object representing the coordinates
     */
    public GridLocation findNextAvailableLocation() {
        int x = 0, z = 0;
        int dx = 0, dz = -1;
        int i = 0; //For safety
        while(true) {
            GridLocation plot = new GridLocation(x, z);
            if(!isOccupied(plot)) {
                return plot;
            }
            if((x == z) || (x < 0 && x == -z) || (x > 0 && x == 1 - z)) {
                int temp = dx;
                dx = -dz;
                dz = temp;
            }
            x += dx;
            z += dz;
            i++;
            if(i > 5000) {
                plugin.getLogger().severe("Could not find an empty plot after 5,000 tries... Is the world full?");
                return null;
            }
        }
    }

    /**
     * Converts the given grid coordinates to a bukkit location.
     * For example, (1, 1) to (1000, 1000)
     * @param gridX X value for the grid location
     * @param gridZ Z value for the grid location
     * @return A bukkit location to the exact coordinates and world (y level = 32)
     */
    public Location gridToBukkitLocation(int gridX, int gridZ) {
        double bukkitX = gridX * GRID_CELL_SIZE;
        double bukkitZ = gridZ * GRID_CELL_SIZE;
        return new Location(dimensionWorld, bukkitX, 32, bukkitZ);
    }

    //Temporary methods (these will be replaced by database reading later on...)
    public boolean isOccupied(GridLocation location) {
        return this.occupiedPlots.contains(location.toString());
    }
    public void markAsOccupied(GridLocation location) {
        occupiedPlots.add(location.toString());
    }

    //Inner class for Grid coordinates
    @AllArgsConstructor
    @Getter
    public static class GridLocation {
        private final int x;
        private final int z;
        @Override
        public String toString() {
            return x + "," + z;
        }
    }
}
