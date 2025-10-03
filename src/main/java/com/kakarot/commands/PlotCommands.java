package com.kakarot.commands;

import com.kakarot.Main;
import com.kakarot.data.Plot;
import com.kakarot.managers.MessageManager;
import com.kakarot.managers.PlotManager;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
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
            default:
                messageManager.sendMessage(commandSender, "error.unknown-command");
                break;
        }
        return true;
    }
    private void handleCreate(Player player) {
        if(plugin.hasDimension(player.getUniqueId())) {
            messageManager.sendMessage(player, "create.already-exists");
            return;
        }
        messageManager.sendMessage(player, "create.locating");
        PlotManager.GridLocation gridLocation = plotManager.findNextAvailableLocation();
        if(gridLocation == null) {
            messageManager.sendMessage(player, "create.error");
            return;
        }
        Plot newChamber = new Plot(player.getUniqueId(), gridLocation.getX(), gridLocation.getZ());
        plugin.setPlayerDimension(player.getUniqueId(), newChamber);
        messageManager.sendMessage(player, "create.success");
        Location destination = plotManager.gridToBukkitLocation(gridLocation.getX(), gridLocation.getZ());
        player.teleport(destination);
    }
    private void handleHome(Player player) {
        if(!plugin.hasDimension(player.getUniqueId())) {
            messageManager.sendMessage(player, "home.no-chamber");
            return;
        }
        Plot chamber = plugin.getPlayerPlots().get(player.getUniqueId());
        Location destination = plotManager.gridToBukkitLocation(chamber.getGridX(), chamber.getGridZ());
        messageManager.sendMessage(player, "home.teleporting");
        player.teleport(destination);
    }
}
