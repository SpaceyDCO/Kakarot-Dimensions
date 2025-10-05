package com.kakarot.managers;

import com.kakarot.Main;
import com.kakarot.data.Plot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class PlotManager {
    private final Main plugin;
    @Getter private World dimensionWorld;

    public static final String WORLD_NAME = "KakarotChamberWorld";
    public static final int PLOT_SIZE = 500;
    public static final int PLOT_SPACING = 500;
    public static final int GRID_CELL_SIZE = PLOT_SIZE + PLOT_SPACING;
    @Getter private final Map<UUID, Plot> playerPlotsCache = new HashMap<>();
    private final Set<GridLocation> occupiedPlots = new HashSet<>();

    public PlotManager(Main plugin) {
        this.plugin = plugin;
    }

    public void loadAllOccupiedPlots() {
        plugin.getLogger().info("Loading all occupied plots into cache...");
        try {
            Set<GridLocation> plots = plugin.getDataManager().loadOccupiedPlots().get();
            this.occupiedPlots.addAll(plots);
            plugin.getLogger().info("Successfully loaded all occupied plots into cache.");
        }catch(InterruptedException | ExecutionException e) {
            plugin.getLogger().log(Level.SEVERE, "FATAL ERROR while trying to load all occupied plots. Disabling plugin for safety...", e);
            plugin.getPluginLoader().disablePlugin(plugin); //Disable plugin
        }
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

    /**
     * Adds a Plot to cache
     * @param player The plot owner's UUID
     * @param plot The plot to save
     */
    public void addPlotToCache(UUID player, Plot plot) {
        this.playerPlotsCache.put(player, plot);
    }

    /**
     * Removes a Plot from cache
     * @param uuid The UUID of the owner of the Plot that will be removed
     */
    public void removePlotFromCache(UUID uuid) {
        this.playerPlotsCache.remove(uuid);
    }

    /**
     * Retrieves a Plot registered under the given UUID
     * @param ownerUUID The UUID of the plot's owner
     * @return A CompletableFuture containing the Plot
     */
    public CompletableFuture<Plot> getPlot(UUID ownerUUID) {
        if(this.playerPlotsCache.containsKey(ownerUUID)) {
            return CompletableFuture.completedFuture(this.playerPlotsCache.get(ownerUUID));
        }
        return plugin.getDataManager().loadDimension(ownerUUID).thenApply(plot -> {
            if(plot != null) {
                addPlotToCache(ownerUUID, plot);
            }
            return plot;
        });
    }
    public CompletableFuture<Void> registerNewPlot(Plot newPlot) {
        addPlotToCache(newPlot.getOwner(), newPlot);
        markAsOccupied(new GridLocation(newPlot.getGridX(), newPlot.getGridZ()));
        return plugin.getDataManager().saveDimension(newPlot.getOwner(), newPlot);
    }

    /**
     * Checks whether a GridLocation is occupied or not
     * @param location The GridLocation that will be checked
     * @return true if occupied (a plot already exists in that grid), false otherwise
     */
    public boolean isOccupied(GridLocation location) {
        return this.occupiedPlots.contains(location);
    }

    /**
     * Marks the location as occupied
     * @param location The GridLocation to be marked as occupied
     */
    public void markAsOccupied(GridLocation location) {
        occupiedPlots.add(location);
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
