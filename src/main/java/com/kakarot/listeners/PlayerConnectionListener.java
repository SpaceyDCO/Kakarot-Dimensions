package com.kakarot.listeners;

import com.kakarot.Main;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class PlayerConnectionListener implements Listener {
    private final Main plugin;
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getPlotManager().getPlot(player.getUniqueId()).thenAccept(plot -> {
            if(plot != null) {
                plugin.getLogger().info("Loaded chamber data for player " + player.getName());
            }
        });
    }
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getPlotManager().removePlotFromCache(player.getUniqueId());
    }
}
