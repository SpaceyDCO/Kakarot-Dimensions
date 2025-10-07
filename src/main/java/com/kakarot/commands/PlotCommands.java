package com.kakarot.commands;

import com.kakarot.Main;
import com.kakarot.data.Plot;
import com.kakarot.gui.UpgradeGUIProvider;
import com.kakarot.managers.MessageManager;
import com.kakarot.managers.PlotManager;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class PlotCommands implements CommandExecutor {
    private final Main plugin;
    private final PlotManager plotManager;
    private final MessageManager messageManager;
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            messageManager.sendMessage(commandSender, "error.player-only");
            return true;
        }
        Player player = (Player) commandSender;
        if(strings.length == 0) {
            messageManager.sendMessage(commandSender, "help.header");
            messageManager.sendMessage(commandSender, "help.create");
            messageManager.sendMessage(commandSender, "help.home");
            return true;
        }
        String subcommand = strings[0].toLowerCase();
        switch(subcommand) {
            case "create":
                handleCreate(player);
                break;
            case "home":
                handleHome(player);
                break;
            case "upgrades":
                UpgradeGUIProvider.INVENTORY.open(player);
                break;
            default:
                messageManager.sendMessage(commandSender, "error.unknown-command");
                break;
        }
        return true;
    }
    private void handleCreate(Player player) {
        messageManager.sendMessage(player, "create.locating");
        this.plotManager.getPlot(player.getUniqueId()).thenAccept(existingPlot -> {
            if(existingPlot != null) {
                messageManager.sendMessage(player, "create.already-exists");
                return;
            }
            PlotManager.GridLocation gridLocation = plotManager.findNextAvailableLocation();
            if(gridLocation == null) {
                messageManager.sendMessage(player, "create.error");
                return;
            }
            Plot newPlot = new Plot(player.getUniqueId(), gridLocation.getX(), gridLocation.getZ());
            this.plotManager.registerNewPlot(newPlot).thenRunAsync(() -> {
                messageManager.sendMessage(player, "create.success");
                Location destination = plotManager.gridToBukkitLocation(gridLocation.getX(), gridLocation.getZ());
                player.teleport(destination);
            }, runnable -> Bukkit.getScheduler().runTask(plugin, runnable));
        });
    }
    private void handleHome(Player player) {
        this.plotManager.getPlot(player.getUniqueId()).thenAccept(plot -> {
           if(plot == null) {
               messageManager.sendMessage(player, "home.no-chamber");
               return;
           }
           messageManager.sendMessage(player, "home.teleporting");
           Location destination = this.plotManager.gridToBukkitLocation(plot.getGridX(), plot.getGridZ());
           player.teleport(destination);
        });
    }
}
