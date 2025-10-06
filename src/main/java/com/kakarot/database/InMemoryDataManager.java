package com.kakarot.database;

import com.kakarot.data.Plot;
import com.kakarot.managers.PlotManager;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class InMemoryDataManager implements DataManager {
    private final Map<UUID, Plot> playerPlots = new HashMap<>();
    private final Map<UUID, Set<String>> playerUpgrades = new HashMap<>();
    private final Map<UUID, Integer> playerFragments = new HashMap<>();
    @Override
    public void initialize() {
        //Nothing to do since this is RAM only
    }

    @Override
    public void shutdown() {
        //Nothing to do here either
    }

    @Override
    public CompletableFuture<Plot> loadDimension(UUID uuid) {
        Plot plot = playerPlots.getOrDefault(uuid, null);
        return CompletableFuture.completedFuture(plot);
    }

    @Override
    public CompletableFuture<Set<PlotManager.GridLocation>> loadOccupiedPlots() {
        Set<PlotManager.GridLocation> occupied = this.playerPlots.values().stream()
                .map(chamber -> new PlotManager.GridLocation(chamber.getGridX(), chamber.getGridZ()))
                .collect(Collectors.toSet());
        return CompletableFuture.completedFuture(occupied);
    }

    @Override
    public CompletableFuture<Void> saveDimension(UUID owner, Plot plot) {
        this.playerPlots.put(owner, plot);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Set<String>> loadPlayerUpgrades(UUID uuid) {
        return CompletableFuture.completedFuture(this.playerUpgrades.getOrDefault(uuid, new HashSet<>()));
    }

    @Override
    public CompletableFuture<Void> addPlayerUpgrade(UUID uuid, String upgradeID) {
        this.playerUpgrades.computeIfAbsent(uuid, k -> new HashSet<>()).add(upgradeID);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Integer> getPlayerFragments(UUID uuid) {
        return CompletableFuture.completedFuture(this.playerFragments.getOrDefault(uuid, 0));
    }

    @Override
    public CompletableFuture<Void> setPlayerFragments(UUID uuid, int amount) {
        this.playerFragments.put(uuid, amount);
        return CompletableFuture.completedFuture(null);
    }
}
