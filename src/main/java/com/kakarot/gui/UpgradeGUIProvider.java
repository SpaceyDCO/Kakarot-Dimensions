package com.kakarot.gui;

import com.kakarot.Main;
import com.kakarot.data.Upgrade;
import com.kakarot.managers.MessageManager;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UpgradeGUIProvider implements InventoryProvider {
    private final Main plugin;
    private final MessageManager messageManager;
    public UpgradeGUIProvider(Main plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("upgradeGUI")
            .provider(new UpgradeGUIProvider(Main.getInstance()))
            .size(6, 9)
            .title(ChatColor.DARK_AQUA + "Chamber Upgrades")
            .build();

    @Override
    //OPTIMIZE FOR CACHE READING
    public void init(Player player, InventoryContents inventoryContents) {
        inventoryContents.set(0, 4, ClickableItem.empty(new ItemStack(Material.GLASS, 1)));
        plugin.getDataManager().loadPlayerUpgrades(player.getUniqueId()).thenAcceptBoth(plugin.getDataManager().getPlayerFragments(player.getUniqueId()), (unlockedUpgrades, fragments) -> {
            inventoryContents.set(0, 4, ClickableItem.empty(new ItemStack(Material.AIR)));
            //Training room
            plugin.getUpgradeManager().getUpgrade("unlock_training_room").ifPresent(upgrade -> {
                ItemStack icon = createIcon(upgrade, unlockedUpgrades, fragments);
                inventoryContents.set(1, 1, ClickableItem.of(icon, e -> {
                    handlePurchase(player, upgrade.getId(), inventoryContents);
                }));
            });
            //Senzu garden
            plugin.getUpgradeManager().getUpgrade("unlock_senzu_garden").ifPresent(upgrade -> {
                ItemStack icon = createIcon(upgrade, unlockedUpgrades, fragments);
                inventoryContents.set(1, 4, ClickableItem.of(icon, e -> {
                    handlePurchase(player, upgrade.getId(), inventoryContents);
                }));
            });
            //Training room upgrade
            plugin.getUpgradeManager().getUpgrade("upgrade_training_dummy").ifPresent(upgrade -> {
                ItemStack icon = createIcon(upgrade, unlockedUpgrades, fragments);
                inventoryContents.set(1, 7, ClickableItem.of(icon, e -> {
                    handlePurchase(player, upgrade.getId(), inventoryContents);
                }));
            });
        });
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {
    }

    private void handlePurchase(Player player, String upgradeID, InventoryContents contents) {
        messageManager.sendMessage(player, "gui.purchase-attempt");
        plugin.getUpgradeService().purchaseUpgrade(player, upgradeID).thenAccept(purchaseResult -> {
           switch(purchaseResult) {
               case SUCCESS:
                   messageManager.sendMessage(player, "gui.purchase-successful");
                   INVENTORY.close(player);
                   break;
               case ALREADY_OWNED:
                   messageManager.sendMessage(player, "gui.purchase-already-owned");
                   break;
               case INSUFFICIENT_FUNDS:
                   messageManager.sendMessage(player, "gui.purchase-insufficient-funds");
                   break;
               case REQUIREMENTS_NOT_MET:
                   messageManager.sendMessage(player, "gui.purchase-requirements-not-met");
                   break;
               case INVALID_UPGRADE:
                   messageManager.sendMessage(player, "gui.purchase-invalid-upgrade");
                   break;
           }
        });
    }
    private ItemStack createIcon(Upgrade upgrade, Set<String> unlockedUpgrades, int playerFragments) {
        Material material = Material.matchMaterial(upgrade.getGuiItem());
        if(material == null) material = Material.BEDROCK;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', upgrade.getName()));
        List<String> lore = new ArrayList<>();
        //REMEMBER TO ADD THE ACHIEVEMENTS CHECK
        if(unlockedUpgrades.contains(upgrade.getId())) {
            lore.add(ChatColor.GREEN + "✔ UNLOCKED");
        }else if(playerFragments < upgrade.getCost()) {
            lore.add(ChatColor.RED + "✖ NOT ENOUGH FRAGMENTS");
        }else if(!unlockedUpgrades.containsAll(upgrade.getRequiredUpgrades())) {
            lore.add(ChatColor.RED + "✖ REQUIREMENTS NOT MET");
        }else {
            lore.add(ChatColor.YELLOW + "▶ CLICK TO PURCHASE");
        }
        lore.add("");
        for(String line : upgrade.getLore()) {
            line = line.replace("{cost}", String.valueOf(upgrade.getCost()));
            lore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
