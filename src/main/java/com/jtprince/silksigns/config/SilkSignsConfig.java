package com.jtprince.silksigns.config;

import de.exlll.configlib.*;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;

@Configuration
public class SilkSignsConfig {
    static YamlConfigurationProperties properties = ConfigLib.BUKKIT_DEFAULT_PROPERTIES.toBuilder()
            .build();

    private static class EnchantmentRegistrySerializer extends RegistrySerializer<Enchantment> {
        public EnchantmentRegistrySerializer() {
            super(Registry.ENCHANTMENT, "Enchantment");
        }
    }

    @Configuration
    public static class ToolConfig {
        @SerializeWith(serializer = EnchantmentRegistrySerializer.class)
        public Enchantment enchantment = Enchantment.SILK_TOUCH;
        public int minimumLevel = 1;
    }

    @Comment(
            """
            Requirements for the tool used to break a sign to drop a written sign item.
            Signs broken by players with the permission "silksigns.break.withoutsilktouch"
              will drop as a written sign even when not using this tool."""
    )
    public ToolConfig tool = new ToolConfig();

    @Configuration
    public static class WrittenSignItemConfig {
        @Comment(
                """
                Rename written signs according to the specified format.
                MiniMessage tags are supported: https://docs.advntr.dev/minimessage/format.html
                <name> will be replaced with the original (translatable) name of the sign,
                  e.g. "Oak Sign" or "Warped Hanging Sign".
                If empty (set to two single-quotes ''), signs containing text will not be
                  renamed."""
        )
        public String nameFormat = "Written <name>";

        @Comment("Copy sign text to written sign item lore (hover text).")
        public boolean contentsInLore = true;

        @Comment(
                """
                Apply an enchantment glint effect on written sign items to make them
                  visually distinct."""
        )
        public boolean enchantmentGlint = true;
    }

    @Comment("Settings for the written sign item dropped when broken with silk touch.")
    public WrittenSignItemConfig writtenSignItem = new WrittenSignItemConfig();

    // TODO: This is hard to implement since there's no easy hook for all the ways a sign could break
//    @Comment(
//        """
//        Drop a written sign when a sign is broken through physical events (e.g.
//          breaking the support block).
//        NOTE: this means players do NOT need silk touch to obtain a sign with text
//          (and you should consider giving the permission
//          "silksigns.break.withoutsilktouch" too)"""
//    )
//    public boolean physicsEventsDropWrittenSigns = false;

    @Comment("Remove wax from signs when broken with silk touch, making them editable again.")
    public boolean unwaxOnBreak = true;
}
