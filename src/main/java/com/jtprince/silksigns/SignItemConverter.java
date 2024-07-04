package com.jtprince.silksigns;

import com.jtprince.silksigns.config.ConfigProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.jtprince.silksigns.SignUtils.getSideName;
import static com.jtprince.silksigns.SignUtils.isBlank;

public class SignItemConverter {
    final ConfigProvider config;
    public SignItemConverter(ConfigProvider config) {
        this.config = config;
    }

    /**
     * Convert a placed sign blockstate to an ItemStack
     * @param sign Sign blockstate
     * @return The sign as an item, or null if the sign is blank or on other errors
     */
    public @Nullable ItemStack getItemFromSign(org.bukkit.block.Sign sign) {
        if (isBlank(sign)) {
            return null;
        }

        if (sign.isPlaced()) {
            // Try to make a copy of the blockstate so we don't accidentally modify the original block
            //  (this probably won't happen because we're not calling update(), but just in case)
            try {
                //noinspection UnstableApiUsage
                sign = (Sign) sign.copy(sign.getLocation());
            } catch (NoSuchMethodError e) {
                // BlockState#copy was added in 1.20.6; just move forward with the original blockstate
            }
        }

        // Wall signs have material WALL_SIGN, which cannot be converted to an item.
        //  Try to grab the item form of the sign from its drops, and fall back on the direct block type.
        Material itemType = sign.getBlock().getDrops().stream().findFirst().map(ItemStack::getType).orElse(sign.getType());
        if (!itemType.isItem()) {
            SilkSigns.instance.getLogger().warning("Tried to convert sign material " + itemType + " that is not an item!");
            return null;
        }

        ItemStack item = new ItemStack(itemType);
        BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();

        if (config.get().unwaxOnBreak) {
            sign.setWaxed(false);
        }

        Objects.requireNonNull(meta).setBlockState(sign);

        applyEnchantmentGlint(config.get().writtenSignItem.enchantmentGlint, meta);

        if (!config.get().writtenSignItem.nameFormat.isBlank()) {
            copySignNameToMeta(itemType, meta);
        } else {
            copySignNameToMeta(null, meta); // Clear any previous custom names the blockstate held on to
        }

        if (config.get().writtenSignItem.contentsInLore) {
            copySignTextToMeta(sign, meta);
        } else {
            copySignTextToMeta(null, meta);
        }

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Copy data from a sign ItemStack to a placed sign block
     * @param itemStack Stored sign blockstate from an ItemStack
     * @param placedSign Placed sign block
     */
    public void copyItemToPlacedSign(final org.bukkit.block.Sign itemStack, final org.bukkit.block.Sign placedSign) {
        placedSign.setWaxed(itemStack.isWaxed());
        for (org.bukkit.block.sign.Side side : org.bukkit.block.sign.Side.values()) {
            org.bukkit.block.sign.SignSide itemSide = itemStack.getSide(side);
            org.bukkit.block.sign.SignSide placedSide = placedSign.getSide(side);

            placedSide.setGlowingText(itemSide.isGlowingText());
            placedSide.setColor(itemSide.getColor());
            int lineIndex = 0;
            for (Component line : itemSide.lines()) {
                placedSide.line(lineIndex, line);
                lineIndex++;
            }
        }
        placedSign.update();
    }

    protected void applyEnchantmentGlint(boolean glint, BlockStateMeta meta) {
        try {
            meta.setEnchantmentGlintOverride(glint);
        } catch (NoSuchMethodError e) {
            if (glint) {
                // ItemMeta#setEnchantmentGlintOverride was added in 1.20.6; try using the old method
                // Have to use getByKey here since Unbreaking was "DURABILITY" in the enum in pre-1.20.6 API
                @SuppressWarnings("deprecation")
                Enchantment fallbackEnchantment = Enchantment.getByKey(NamespacedKey.fromString("unbreaking"));
                if (fallbackEnchantment != null) {
                    meta.addEnchant(fallbackEnchantment, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
            }
        }
    }

    protected void copySignNameToMeta(final @Nullable Material signType, final BlockStateMeta meta) {
        Component newName = null; // Clear custom name if null
        if (signType != null) {
            newName = MiniMessage.miniMessage().deserialize(
                    config.get().writtenSignItem.nameFormat,
                    TagResolver.resolver("name", Tag.inserting(Component.translatable(signType.translationKey())))
            );
        }

        try {
            meta.itemName(newName);
        } catch (NoSuchMethodError e) {
            // ItemMeta#itemName was added in 1.20.6; try using the old method
            if (newName != null) {
                newName = newName.decoration(TextDecoration.ITALIC, false);
            }
            meta.displayName(newName);
        }
    }

    protected void copySignTextToMeta(final @Nullable org.bukkit.block.Sign sign, BlockStateMeta meta) {
        List<Component> outputLines = new ArrayList<>();

        if (sign == null) {
            meta.lore(null);
            return;
        }

        if (sign.isWaxed()) {
            outputLines.add(Component.text("Waxed"));
        }

        // Special handling for signs only written on the front; no need for a "Front" indicator
        boolean hasBackText = !isBlank(sign.getSide(Side.BACK));

        for (org.bukkit.block.sign.Side sideEnum : org.bukkit.block.sign.Side.values()) {
            org.bukkit.block.sign.SignSide sideData = sign.getSide(sideEnum);
            if (isBlank(sideData)) continue;

            if (hasBackText) {
                outputLines.add(Component.text(getSideName(sideEnum)).append(Component.text(":")));
            }

            boolean seenLineWithText = false; // Trim leading blank lines
            for (Component signLine : sideData.lines()) {
                if (!seenLineWithText && isBlank(signLine)) continue;

                seenLineWithText = true;

                TextColor sideColor;
                if (sideData.getColor() == null || sideData.getColor() == DyeColor.BLACK) {
                    sideColor = NamedTextColor.GRAY;
                } else {
                    sideColor = TextColor.color(sideData.getColor().getColor().asRGB());
                }
                outputLines.add(Component.space()
                        .append(signLine)
                        .color(sideColor)
                        .decoration(TextDecoration.ITALIC, false)
                );
            }

            // Trim trailing blank lines
            for (int i = outputLines.size() - 1; i >= 0; i--) {
                if (!isBlank(outputLines.get(i))) break;
                outputLines.remove(i);
            }
        }

        meta.lore(outputLines);
    }
}
