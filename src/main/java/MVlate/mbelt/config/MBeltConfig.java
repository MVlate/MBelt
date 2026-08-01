package MVlate.mbelt.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class MBeltConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;


    /////////////////////////////////////////// UNUSED ///////////////////////////////////////////////
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ALLOWED_ATTACHMENTS;

    static {
        BUILDER.translation("tooltip.mbelt.config_category").push("mbelt_options");

        ALLOWED_ATTACHMENTS = BUILDER
                .comment("List of item IDs allowed in the belt accessory slot (Bags, Bundles, etc.).")
                .defineList("allowed_attachments",
                        List.of("minecraft:bundle", "mbelt:small_bag"),
                        obj -> obj instanceof String);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static boolean isValidAttachment(ItemStack stack) {
        if (stack.isEmpty()) return false;

        ResourceLocation itemID = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (itemID == null) return false;

        return ALLOWED_ATTACHMENTS.get().contains(itemID.toString());
    }
}