package com.kakarot.managers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class MessageManager {
    //Temporal
    private final Map<String, String> messages = new HashMap<>();
    private final String prefix = "&6[Chambers] &r";
    public MessageManager() {
        loadMessages();
    }
    //Temporal message load, later on this will be done reading a yml file...
    private void loadMessages() {
        messages.put("error.no-permission", "&cYou do not have permission to use this command.");
        messages.put("error.player-only", "&cThis command can only be run by a player.");
        messages.put("error.unknown-command", "&cUnknown command. Use /chamber for help.");
        messages.put("help.header", "&6--- Kakarot Chambers help ---");
        messages.put("help.create", "&e/chamber create &7- Creates your personal chamber.");
        messages.put("help.home", "&e/chamber home &7- Teleports you to your chamber.");
        messages.put("create.already-exists", "&cYou already have a Chamber! Use /chamber home.");
        messages.put("create.locating", "&aFinding an empty spot for your chamber...");
        messages.put("create.error", "&cError: Could not find a space. Please contact an admin.");
        messages.put("create.success", "&6Your chamber has been created! Teleporting you now...");
        messages.put("home.no-chamber", "&cYou don't have a chamber yet! Use /chamber create.");
        messages.put("home.teleporting", "&aTeleporting you to your chamber...");
    }

    /**
     * Sends a formated message to the CommandSender.
     * Includes prefix
     * @param sender The recipient of the message
     * @param key The key of the message to send
     */
    public void sendMessage(CommandSender sender, String key) {
        String message = messages.getOrDefault(key, "&cError: missing message for key: " + key);
        sender.sendMessage(format(prefix + message));
    }

    /**
     * Sends a formated message to the CommandSender.
     * Doesn't include prefix
     * @param sender The recipient of the message
     * @param key The key of the message to send
     */
    public void sendRawMessage(CommandSender sender, String key) {
        String message = messages.getOrDefault(key, "&cError: missing message for key: " + key);
        sender.sendMessage(format(message));
    }

    /**
     * Helper method to translate color codes
     * @param msg The message to be translated
     * @return The translated message as a String
     */
    private String format(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
