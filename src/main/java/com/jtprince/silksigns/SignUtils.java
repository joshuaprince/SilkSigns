package com.jtprince.silksigns;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class SignUtils {
    public static boolean isBlank(final Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component).isBlank();
    }

    public static boolean isBlank(final org.bukkit.block.sign.SignSide side) {
        for (Component line : side.lines()) {
            if (!isBlank(line)) return false;
        }
        return true;
    }

    public static boolean isBlank(final org.bukkit.block.Sign sign) {
        for (org.bukkit.block.sign.Side side : org.bukkit.block.sign.Side.values()) {
            if (!isBlank(sign.getSide(side))) return false;
        }
        return true;
    }

    public static String getSideName(final org.bukkit.block.sign.Side side) {
        return switch (side) {
            case BACK -> "Back";
            case FRONT -> "Front";
            default -> side.name().toLowerCase(); // Just in case...
        };
    }
}
