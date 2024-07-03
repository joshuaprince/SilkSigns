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

    @Override
    public void onEnable() {
        instance = this;
        configProvider = new ConfigProvider(this);

        SignItemConverter signItemConverter = new SignItemConverter(configProvider);

        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(signItemConverter), this);
        // Bukkit.getPluginManager().registerEvents(new BlockNaturalBreakListener(configProvider, signItemConverter), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(signItemConverter), this);
        Bukkit.getPluginManager().registerEvents(new SignEditListener(), this);

        Bukkit.getCommandMap().register("silksigns", new SilkSignsCommand(this));
    }

    @Override
    public void onDisable() {
        configProvider = null;
        instance = null;
    }

    public void reload() {
        configProvider.reload();
    }
}
