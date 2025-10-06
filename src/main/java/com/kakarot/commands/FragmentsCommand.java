package com.kakarot.commands;

import com.kakarot.Main;
import com.kakarot.managers.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FragmentsCommand implements CommandExecutor {
    private final Main plugin;
    private final MessageManager messageManager;
    public FragmentsCommand(Main plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player) || !commandSender.isOp()) {
            messageManager.sendMessage(commandSender, "error.no-permission");
            return true;
        }
        Player player = (Player) commandSender;
        if(strings.length < 3) {
            messageManager.sendMessage(player, "fragments.incorrect-syntax");
            return true;
        }
        String action = strings[0].toLowerCase();
        String playerName = strings[1];
        int amount;
        try {
            amount = Integer.parseInt(strings[2]);
            if(amount < 0) {
                messageManager.sendMessage(player, "fragments.negative-value");
                return true;
            }
        }catch(NumberFormatException e) {
            messageManager.sendMessage(player, "fragments.invalid-value");
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
        if(!target.hasPlayedBefore() && !target.isOnline()) {
            messageManager.sendMessage(player, "fragments.player-not-found");
            return true;
        }
        //Optimize to cache
        plugin.getDataManager().getPlayerFragments(target.getUniqueId()).thenAccept(fragments -> {
           int newBalance = 0;
           switch(action) {
               case "give":
                   newBalance = fragments + amount;
                   break;
               case "set":
                   newBalance = amount;
                   break;
               case "take":
                   newBalance = fragments - amount;
                   break;
               default:
                   messageManager.sendMessage(player, "fragments.incorrect-syntax");
                   return;
           }
           plugin.getDataManager().setPlayerFragments(player.getUniqueId(), newBalance);
           messageManager.sendMessage(player, "fragments.successful-operation");
           if(target.isOnline()) {
               messageManager.sendMessage(target.getPlayer(), "fragments.fragments-changed");
           }
        });
        return true;
    }
}
