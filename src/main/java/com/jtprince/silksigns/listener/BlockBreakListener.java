package com.jtprince.silksigns.listener;

import com.jtprince.silksigns.SignItemConverter;
import com.jtprince.silksigns.config.ConfigProvider;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener implements Listener {
    private final ConfigProvider config;
    private final SignItemConverter signItemConverter;

    public BlockBreakListener(ConfigProvider config, SignItemConverter signItemConverter) {
        this.config = config;
        this.signItemConverter = signItemConverter;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getPlayer().hasPermission("silksigns.break")) return;

        // Only affect signs
        if (!(event.getBlock().getState() instanceof org.bukkit.block.Sign sign)) return;

        // Only affect behavior when the player is using silk touch or has permission to break signs without silk touch
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        if (tool.getEnchantmentLevel(config.get().tool.enchantment) < config.get().tool.minimumLevel
                && !event.getPlayer().hasPermission("silksigns.break.notool")) return;

        // If another plugin has disabled item drops, don't override it
        if (!event.isDropItems()) return;

        // In creative mode, items don't typically drop (even though isDropItems returns true)
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE
                && !event.getPlayer().hasPermission("silksigns.break.creative")) return;

        // All checks passed; drop this sign with text.
        ItemStack replacedItemDrop = signItemConverter.getItemFromSign(sign);
        if (replacedItemDrop == null) return;
        event.setDropItems(false);
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), replacedItemDrop);
    }
}
