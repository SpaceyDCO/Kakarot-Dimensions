package com.kakarot.database;

import com.kakarot.data.Plot;
import com.kakarot.managers.PlotManager;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class InMemoryDataManager implements DataManager {
    private final Map<UUID, Plot> playerPlots = new HashMap<>();
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
}
