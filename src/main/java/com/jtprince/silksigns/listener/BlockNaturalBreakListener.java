package com.jtprince.silksigns.listener;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import com.jtprince.silksigns.SignItemConverter;
import com.jtprince.silksigns.config.ConfigProvider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class BlockNaturalBreakListener implements Listener {
    private final ConfigProvider config;
    private final SignItemConverter signItemConverter;

    public BlockNaturalBreakListener(ConfigProvider config, SignItemConverter signItemConverter) {
        this.config = config;
        this.signItemConverter = signItemConverter;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockDestroy(BlockDestroyEvent event) {
        // FIXME! if (!config.get().physicsEventsDropWrittenSigns) return;

        // Only affect signs
        if (!(event.getBlock().getState() instanceof org.bukkit.block.Sign sign)) return;

        // All checks passed; drop this sign with text.
        ItemStack replacedItemDrop = signItemConverter.getItemFromSign(sign);
        if (replacedItemDrop == null) return;
        event.setWillDrop(false);
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), replacedItemDrop);
    }
}
