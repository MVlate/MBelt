package MVlate.mbelt.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class MBeltConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static ForgeConfigSpec.IntValue BUTTON_X;
    public static ForgeConfigSpec.IntValue BUTTON_Y;
    public static ForgeConfigSpec.BooleanValue ALLOW_CONTAINERS_IN_BELT_AND_BAGS;
    public static ForgeConfigSpec.BooleanValue ALLOW_BELT_IN_CONTAINERS;

    static {
        BUILDER.translation("tooltip.mbelt.config_category").push("mbelt_options");
        BUTTON_X = BUILDER
                .comment("The X coordinate offset for the Belt button in the inventory.")
                .defineInRange("buttonX", 76, -500, 500);

        BUTTON_Y = BUILDER
                .comment("The Y coordinate offset for the Belt button in the inventory.")
                .defineInRange("buttonY", 40, -500, 500);

        ALLOW_CONTAINERS_IN_BELT_AND_BAGS = BUILDER
                .comment("If TRUE, allows you to put Shulker Boxes and backpacks from other mods inside the belt and bag.")
                .define("allowContainersInBelt", false);

        ALLOW_BELT_IN_CONTAINERS = BUILDER
                .comment("If TRUE, allows this belt to be stored inside Shulker Boxes or other backpacks.")
                .define("allowBeltInContainers", true);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

}