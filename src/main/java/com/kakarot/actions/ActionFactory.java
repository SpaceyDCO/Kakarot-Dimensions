package com.kakarot.actions;

import org.bukkit.configuration.ConfigurationSection;

public class ActionFactory {
    public static Action createAction(String actionType, ConfigurationSection data) {
        switch(actionType.toLowerCase()) {
            case "remove_barriers":
                return new RemoveBarriersAction(data);
            case "paste_schematic":
                return new PasteSchematicAction(data);
            default:
                return null;
        }
    }
}
