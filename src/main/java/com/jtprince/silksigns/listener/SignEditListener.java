package com.jtprince.silksigns.listener;

import com.jtprince.silksigns.SignUtils;
import io.papermc.paper.event.player.PlayerOpenSignEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class SignEditListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSignChange(PlayerOpenSignEvent event) {
        // Placing a sign with text on it opens the editor, which forcibly overwrites the text.
        // Prevent overwrites by not opening the editor if the sign already contains text.
        if (event.getCause() != PlayerOpenSignEvent.Cause.PLACE) return;

        if (SignUtils.isBlank(event.getSign())) return;

        event.setCancelled(true);
    }
}
