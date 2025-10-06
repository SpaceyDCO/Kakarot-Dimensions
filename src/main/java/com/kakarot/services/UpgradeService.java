package com.kakarot.services;

import com.kakarot.Main;
import com.kakarot.data.PurchaseResult;
import com.kakarot.data.Upgrade;
import com.kakarot.managers.UpgradeManager;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class UpgradeService { //OPTIMIZE TO CACHE LATER
    private final Main plugin;
    private final UpgradeManager upgradeManager;
    public UpgradeService(Main plugin) {
        this.plugin = plugin;
        this.upgradeManager = plugin.getUpgradeManager();
    }

    public CompletableFuture<PurchaseResult> purchaseUpgrade(Player player, String upgradeID) {
        Upgrade upgrade = this.upgradeManager.getUpgrade(upgradeID)
                .orElse(null);
        if(upgrade == null) return CompletableFuture.completedFuture(PurchaseResult.INVALID_UPGRADE);
        CompletableFuture<Integer> fragmentsFuture = plugin.getDataManager().getPlayerFragments(player.getUniqueId());
        CompletableFuture<Set<String>> unlockedUpgradesFuture = plugin.getDataManager().loadPlayerUpgrades(player.getUniqueId());
        //Add achievements later
        return CompletableFuture.allOf(fragmentsFuture, unlockedUpgradesFuture).thenCompose(v -> {
            int currentFragments = fragmentsFuture.join();
            Set<String> unlockedUpgrades = unlockedUpgradesFuture.join();
            if(unlockedUpgrades.contains(upgradeID)) {
                return CompletableFuture.completedFuture(PurchaseResult.ALREADY_OWNED);
            }
            if(currentFragments < upgrade.getCost()) {
                return CompletableFuture.completedFuture(PurchaseResult.INSUFFICIENT_FUNDS);
            }
            if(!unlockedUpgrades.containsAll(upgrade.getRequiredUpgrades())) {
                return CompletableFuture.completedFuture(PurchaseResult.REQUIREMENTS_NOT_MET);
            }
            int newBalance = currentFragments - upgrade.getCost();
            CompletableFuture<Void> fragmentsUpdate = plugin.getDataManager().setPlayerFragments(player.getUniqueId(), newBalance);
            CompletableFuture<Void> upgradesUnlock = plugin.getDataManager().addPlayerUpgrade(player.getUniqueId(), upgradeID);
            return CompletableFuture.allOf(fragmentsUpdate, upgradesUnlock).thenApply(ignore -> PurchaseResult.SUCCESS);
        });
    }
}
