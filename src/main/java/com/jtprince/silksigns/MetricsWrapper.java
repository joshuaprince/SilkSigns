package com.jtprince.silksigns;

import com.jtprince.silksigns.config.ConfigProvider;
import com.jtprince.silksigns.config.SilkSignsConfig;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;

public class MetricsWrapper {
    @SuppressWarnings("FieldCanBeLocal")
    private final int BSTATS_PLUGIN_METRICS_ID = 22515;

    private final SilkSigns silkSigns;
    private final ConfigProvider config;

    private Metrics metrics;
    private final String defaultSignItemNameFormat = new SilkSignsConfig.WrittenSignItemConfig().nameFormat;

    MetricsWrapper(SilkSigns silkSigns, ConfigProvider config) {
        this.silkSigns = silkSigns;
        this.config = config;
    }

    void startReports() {
        /*
         * I collect these statistics out of my own interest, to keep me motivated, and to learn about which plugin
         *   features are being used and should be prioritized in development.
         * Data is publicly available at https://bstats.org/plugin/bukkit/SilkSigns/22515
         * Please feel free to disable metrics collection in the bStats config.yml if you don't want this.
         */
        if (metrics != null) return;
        metrics = new Metrics(silkSigns, BSTATS_PLUGIN_METRICS_ID);

        metrics.addCustomChart(new SingleLineChart("playersPermissionBreak", () ->
            (int) Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("silksigns.break")).count()));

        metrics.addCustomChart(new SingleLineChart("playersPermissionBreakCreative", () ->
            (int) Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("silksigns.break.creative")).count()));

        metrics.addCustomChart(new SingleLineChart("playersPermissionBreakNoTool", () ->
            (int) Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("silksigns.break.notool")).count()));

        metrics.addCustomChart(new SimplePie("writtenSignNameFormat", () -> {
            // Name formats are NOT submitted directly to preserve privacy.
            String format = config.get().writtenSignItem.nameFormat;
            if (format.isBlank()) {
                return "blank";
            } else if (format.equals(defaultSignItemNameFormat)) {
                return "default";
            } else {
                return "custom";
            }
        }));

        metrics.addCustomChart(new SimplePie("writtenSignContentsInLore", () ->
            String.valueOf(config.get().writtenSignItem.contentsInLore)));

        metrics.addCustomChart(new SimplePie("writtenSignEnchantmentGlint", () ->
            String.valueOf(config.get().writtenSignItem.enchantmentGlint)));

        metrics.addCustomChart(new SimplePie("unwaxOnSignBreak", () ->
            String.valueOf(config.get().unwaxOnBreak)));
    }

    void stopReports() {
        if (metrics != null) {
            metrics.shutdown();
            metrics = null;
        }
    }
}
