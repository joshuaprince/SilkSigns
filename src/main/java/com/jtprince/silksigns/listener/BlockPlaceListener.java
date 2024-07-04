package com.jtprince.silksigns.listener;

import com.jtprince.silksigns.SignItemConverter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class BlockPlaceListener implements Listener {
    SignItemConverter signItemConverter;

    public BlockPlaceListener(SignItemConverter signItemConverter) {
        this.signItemConverter = signItemConverter;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.getPlayer().hasPermission("silksigns.place")) return;

        ItemStack item = event.getItemInHand();
        //noinspection ConstantValue
        if (item == null || item.getItemMeta() == null) return;
        if (!(item.getItemMeta() instanceof BlockStateMeta itemMeta)) return;
        if (!(itemMeta.getBlockState() instanceof org.bukkit.block.Sign itemSign)) return;

        if (!(event.getBlock().getState() instanceof org.bukkit.block.Sign placedSign)) return;

        signItemConverter.copyItemToPlacedSign(itemSign, placedSign);
    }
}
