package com.kakarot.database;

import com.kakarot.data.Plot;
import com.kakarot.managers.PlotManager;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface DataManager {
    /**
     * Initializes the DB connection
     */
    void initialize();

    /**
     * Shuts down the DB connection
     */
    void shutdown();

    /**
     * Loads a chamber
     * @param uuid The UUID of the player whose chamber to load
     * @return A CompletableFuture containing the Plot class
     */
    CompletableFuture<Plot> loadDimension(UUID uuid);

    /**
     * Loads all occupied plots to cache for fast reading
     * @return A CompletableFuture containing a Set of GridLocations
     */
    CompletableFuture<Set<PlotManager.GridLocation>> loadOccupiedPlots();

    /**
     * Saves the chamber to the database
     * @param uniqueId UUID of the chamber's owner
     * @param plot The plot that will be saved under this player's ownership
     */
    CompletableFuture<Void> saveDimension(UUID uniqueId, Plot plot);
}
