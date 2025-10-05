package com.kakarot.data;

import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
public class Upgrade {
    private final String id;
    private final String name;
    private final List<String> lore;
    private final String guiItem;
    private final int cost;
    private final List<String> requiredUpgrades;
    private final List<String> requiredAchievements;
    private final Map<String, Object> actions;

    public Upgrade(String id, String name, List<String> lore, String guiItem, int cost, List<String> requiredUpgrades, List<String> requiredAchievements, Map<String, Object> actions) {
        this.id = id;
        this.name = name;
        this.lore = lore;
        this.guiItem = guiItem;
        this.cost = cost;
        this.requiredUpgrades = requiredUpgrades != null ? requiredUpgrades : Collections.emptyList();
        this.requiredAchievements = requiredAchievements != null ? requiredAchievements : Collections.emptyList();
        this.actions = actions != null ? actions : Collections.emptyMap();
    }
}
