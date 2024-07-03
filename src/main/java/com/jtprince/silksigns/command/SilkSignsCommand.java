package com.jtprince.silksigns.command;

import com.jtprince.silksigns.SilkSigns;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class SilkSignsCommand extends Command {
    private final SilkSigns plugin;

    public SilkSignsCommand(SilkSigns plugin) {
        super(
                "silksigns",
                "SilkSigns plugin configuration",
                "/silksigns [reload]",
                Collections.emptyList()
        );
        this.plugin = plugin;
        this.setPermission("silksigns.reload");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length == 0) {
            return false;
        }

        if (!testPermission(sender)) return true;

        this.plugin.reload();
        sender.sendMessage("SilkSigns reload complete.");

        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return Collections.singletonList("reload");
        } else {
            return Collections.emptyList();
        }
    }
}
