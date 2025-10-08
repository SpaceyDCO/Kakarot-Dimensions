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
        // General Messages
        messages.put("error.no-permission", "&cYou do not have permission to use this command.");
        messages.put("error.player-only", "&cThis command can only be run by a player.");
        messages.put("error.unknown-command", "&cUnknown command. Use /chamber for help.");
        // Plot Command Help
        messages.put("help.header", "&6--- Hyperbolic Chamber Help ---");
        messages.put("help.create", "&e/chamber create &7- Creates your personal chamber.");
        messages.put("help.home", "&e/chamber home &7- Teleports you to your chamber.");
        messages.put("help.upgrades", "&e/chamber upgrades &7- Opens the upgrade menu.");
        // Plot Command Actions
        messages.put("create.already-exists", "&cYou already have a Hyperbolic Chamber! Use /chamber home.");
        messages.put("create.locating", "&aFinding an empty spot for your chamber...");
        messages.put("create.error", "&cError: Could not find a space. Please contact an admin.");
        messages.put("create.success", "&6Your chamber has been created! Teleporting you now...");
        messages.put("create.no-schematic", "&cError: Initial schematic file was not found");
        messages.put("home.no-chamber", "&cYou don't have a chamber yet! Use /chamber create.");
        messages.put("home.teleporting", "&aTeleporting you to your chamber...");
        // Dimensional Fragments Admin Command
        messages.put("fragments.incorrect-syntax", "&cIncorrect usage, syntax is /fragments <give|set|take> <player> <amount>");
        messages.put("fragments.negative-value", "&cThe amount cannot be a negative value.");
        messages.put("fragments.invalid-value", "&cInvalid amount. You must specify a whole number.");
        messages.put("fragments.player-not-found", "&cError: Player '{player}' could not be found.");
        messages.put("fragments.successful-operation", "&aSuccessfully set &e{player}'s&a balance to &6{balance}&a fragments.");
        messages.put("fragments.fragments-changed", "&aYour Dimensional Fragments balance has been updated to: &6{balance}");
        // Upgrade GUI Purchase Process
        messages.put("gui.purchase-attempt", "&eAttempting to purchase upgrade...");
        messages.put("gui.purchase-successful", "&aUpgrade purchased successfully!");
        messages.put("gui.purchase-already-owned", "&cYou have already unlocked this upgrade.");
        messages.put("gui.purchase-insufficient-funds", "&cYou do not have enough Dimensional Fragments for this.");
        messages.put("gui.purchase-requirements-not-met", "&cYou do not meet the requirements for this upgrade.");
        messages.put("gui.purchase-invalid-upgrade", "&cAn error occurred: The selected upgrade is invalid.");
        messages.put("gui.purchase-action-invalid", "&cAn error occurred: The selected upgrade contains invalid actions. Please contact an admin.");
    }

    /**
     * Sends a formated message to the CommandSender.
     * Includes prefix
     * @param sender The recipient of the message
     * @param key The key of the message to send
     */
    public void sendMessage(CommandSender sender, String key, String... replacements) {
        String message = messages.getOrDefault(key, "&cError: missing message for key: " + key);
        if(replacements.length > 0) {
            for(int i = 0; i < replacements.length; i += 2) {
                if(i + 1 < replacements.length) {
                    message = message.replace(replacements[i], replacements[i+1]);
                }
            }
        }
        sender.sendMessage(format(prefix + message));
    }

    /**
     * Sends a formated message to the CommandSender.
     * Doesn't include prefix
     * @param sender The recipient of the message
     * @param key The key of the message to send
     */
    public void sendRawMessage(CommandSender sender, String key, String... replacements) {
        String message = messages.getOrDefault(key, "&cError: missing message for key: " + key);
        if(replacements.length > 0) {
            for(int i = 0; i < replacements.length; i += 2) {
                if(i + 1 < replacements.length) {
                    message = message.replace(replacements[i], replacements[i+1]);
                }
            }
        }
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
