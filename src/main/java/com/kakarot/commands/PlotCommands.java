package com.kakarot.commands;

import com.kakarot.Main;
import com.kakarot.data.Plot;
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
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command can only be run by a player.");
            return true;
        }
        Player player = (Player) commandSender;
        if(strings.length == 0) {
            player.sendMessage(ChatColor.GOLD + "--- Kakarot Plots Help ---");
            player.sendMessage(ChatColor.YELLOW + "/chamber create " + ChatColor.GRAY + "- Creates your personal chamber.");
            player.sendMessage(ChatColor.YELLOW + "/chamber home " + ChatColor.GRAY + "- Teleports you to your chamber.");
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
                player.sendMessage(ChatColor.RED + "Unknown command. Use /chamber for help");
                break;
        }
        return true;
    }
    private void handleCreate(Player player) {
        if(plugin.hasDimension(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You already have a chamber!, use /chamber home to go there.");
            return;
        }
        player.sendMessage(ChatColor.GREEN + "Finding an empty spot for your chamber...");
        PlotManager.GridLocation gridLocation = plotManager.findNextAvailableLocation();
        if(gridLocation == null) {
            player.sendMessage(ChatColor.RED + "ERROR: Could not find a valid spot for your chamber. Please contact an admin.");
            return;
        }
        Plot newChamber = new Plot(player.getUniqueId(), gridLocation.getX(), gridLocation.getZ());
        plugin.setPlayerDimension(player.getUniqueId(), newChamber);
        player.sendMessage(ChatColor.GREEN + " Your chamber has been created, teleporting you now...");
        Location destination = plotManager.gridToBukkitLocation(gridLocation.getX(), gridLocation.getZ());
        player.teleport(destination);
    }
    private void handleHome(Player player) {
        if(!plugin.hasDimension(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + " You don't have a chamber.");
            return;
        }
        Plot chamber = plugin.getPlayerPlots().get(player.getUniqueId());
        Location destination = plotManager.gridToBukkitLocation(chamber.getGridX(), chamber.getGridZ());
        player.teleport(destination);
    }
}
