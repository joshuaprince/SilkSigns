package com.jtprince.silksigns;

import com.jtprince.silksigns.command.SilkSignsCommand;
import com.jtprince.silksigns.config.ConfigProvider;
import com.jtprince.silksigns.listener.BlockBreakListener;
import com.jtprince.silksigns.listener.BlockPlaceListener;
import com.jtprince.silksigns.listener.SignEditListener;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public final class SilkSigns extends JavaPlugin implements CommandExecutor {
    static SilkSigns instance;
    ConfigProvider configProvider;
    MetricsWrapper metricsWrapper;

    @Override
    public void onEnable() {
        instance = this;
        configProvider = new ConfigProvider(this);

        SignItemConverter signItemConverter = new SignItemConverter(configProvider);

        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(configProvider, signItemConverter), this);
        // Bukkit.getPluginManager().registerEvents(new BlockNaturalBreakListener(configProvider, signItemConverter), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(signItemConverter), this);
        Bukkit.getPluginManager().registerEvents(new SignEditListener(), this);

        Bukkit.getCommandMap().register("silksigns", new SilkSignsCommand(this));

        try {
            metricsWrapper = new MetricsWrapper(this, configProvider);
            metricsWrapper.startReports();
        } catch (Exception e) {
            getLogger().warning("Failed to initialize metrics.");
        }
    }

    @Override
    public void onDisable() {
        if (metricsWrapper != null) {
            metricsWrapper.stopReports();
            metricsWrapper = null;
        }
        configProvider = null;
        instance = null;
    }

    public void reload() {
        configProvider.reload();
    }
}
