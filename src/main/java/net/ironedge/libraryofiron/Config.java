package net.ironedge.libraryofiron;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER
            .comment("Whether to log the dirt block on common setup")
            .define("logDirtBlock", true);

    public static final ModConfigSpec.IntValue MAGIC_NUMBER = BUILDER
            .comment("A magic number")
            .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
            .comment("Introduction message for the magic number")
            .define("magicNumberIntroduction", "The magic number is... ");

    // A simple list of item strings, no validation at static init
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), obj -> obj instanceof String);

    public static final ModConfigSpec SPEC = BUILDER.build();

    /**
     * Validate the list against the actual item registry.
     * Should be called after registries are ready (common setup).
     */
    public static List<String> validateItems() {
        return ITEM_STRINGS.get().stream()
                .filter(name -> {
                    ResourceLocation loc = ResourceLocation.tryParse(name);
                    return loc != null && BuiltInRegistries.ITEM.containsKey(loc);
                })
                .collect(Collectors.toList());
    }
}