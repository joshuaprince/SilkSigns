package com.jtprince.silksigns.config;

import de.exlll.configlib.Comment;
import de.exlll.configlib.ConfigLib;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.YamlConfigurationProperties;

@Configuration
public class SilkSignsConfig {
    static YamlConfigurationProperties properties = ConfigLib.BUKKIT_DEFAULT_PROPERTIES.toBuilder()
            .build();

    @Configuration
    public static class WrittenSignItemConfig {
        @Comment(
                """
                Rename written signs according to the specified format.
                MiniMessage tags are supported, see https://docs.advntr.dev/minimessage/format.html
                <name> will be replaced with the original (translatable) name of the sign,
                  e.g. "Oak Sign".
                If blank, signs containing text will not be renamed."""
        )
        public String nameFormat = "Written <name>";

        @Comment("Copy sign text to written sign item lore (hover text).")
        public boolean contentsInLore = true;

        @Comment(
                """
                Apply an enchantment glint effect on written sign items to make them
                  visually distinct."""
        )
        public boolean enchantmentGlint = false;
    }

    public WrittenSignItemConfig writtenSignItem = new WrittenSignItemConfig();

    // TODO: This is hard to implement since there's no easy hook for all the ways a sign could break
//    @Comment(
//        """
//        Drop a written sign when a sign is broken through physical events (e.g.
//          breaking the support block).
//        NOTE: this means players do NOT need silk touch to obtain a sign with text
//          (and you should consider giving the permission
//          `silksigns.break.withoutsilktouch` too)"""
//    )
//    public boolean physicsEventsDropWrittenSigns = false;

    @Comment("Remove wax from signs when broken with silk touch, making them editable again")
    public boolean unwaxOnBreak = true;
}
