package com.jtprince.silksigns.config;

import com.jtprince.silksigns.SilkSigns;
import de.exlll.configlib.YamlConfigurations;
import org.jetbrains.annotations.NotNull;

public class ConfigProvider {
    private final SilkSigns plugin;
    private SilkSignsConfig currentConfig;

    public ConfigProvider(SilkSigns plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public @NotNull SilkSignsConfig get() {
        return currentConfig;
    }

    private void loadConfig() {
        currentConfig = YamlConfigurations.update(
                plugin.getDataFolder().toPath().resolve("config.yml"),
                SilkSignsConfig.class,
                SilkSignsConfig.properties
        );
    }

    public void reload() {
        loadConfig();
    }
}
