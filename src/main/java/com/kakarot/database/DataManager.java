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

    /**
     * Loads the set of all upgrade IDs a player has unlocked
     * @param uuid The player's UUID
     * @return A CompletableFuture containing the set of unlocked upgrades for this player
     */
    CompletableFuture<Set<String>> loadPlayerUpgrades(UUID uuid);

    /**
     * Adds an upgrade to the player's data
     * @param uuid The player's UUID
     * @param upgradeID The ID of the upgrade to be added to the data
     * @return A CompletableFuture that completes when the save is done
     */
    CompletableFuture<Void> addPlayerUpgrade(UUID uuid, String upgradeID);

    /**
     * Retrieves the amount of fragments the player has (FROM DB)
     * @param uuid The player's UUID
     * @return A CompletableFuture containing the amount of fragments this player has
     */
    CompletableFuture<Integer> getPlayerFragments(UUID uuid);

    /**
     * Sets the fragments of the specified player to the specified amount
     * @param uuid The UUID of the player whose fragments' amount will be changed
     * @param amount The amount of fragments to set
     * @return A CompletableFuture that completes when the entire operation is finished in database
     */
    CompletableFuture<Void> setPlayerFragments(UUID uuid, int amount);
}
