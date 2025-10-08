package com.kakarot.services;

import com.kakarot.Main;
import com.kakarot.actions.Action;
import com.kakarot.actions.ActionFactory;
import com.kakarot.data.Upgrade;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;

public class ActionService {
    private final Main plugin;
    public ActionService(Main plugin) {
        this.plugin = plugin;
    }

    public void executeActions(Player player, Upgrade upgrade) {
        plugin.getPlotManager().getPlot(player.getUniqueId()).thenAccept(plot -> {
           if(plot == null) {
               plugin.getLogger().severe("Could not execute upgrade actions for player " + player.getName() + " because their plot data was not found");
               return;
           }
           for(Map.Entry<String, Object> entry : upgrade.getActions().entrySet()) {
                if(entry.getValue() instanceof ConfigurationSection) {
                    Action action = ActionFactory.createAction(entry.getKey(), (ConfigurationSection) entry.getValue());
                    if(action != null) action.execute(plot);
                }
           }
           //Add more actions in the future, TO DO: remove_npc, add_npc, run_command
        });
    }
}
