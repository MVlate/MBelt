package MVlate.mbelt;

import MVlate.mbelt.client.menu.CreatorSlots;
import MVlate.mbelt.event.AddItemModifier;
import MVlate.mbelt.item.BagItem;
import MVlate.mbelt.item.BeltItem;
import MVlate.mbelt.item.EnderBagItem;
import com.mojang.serialization.Codec;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static MVlate.mbelt.Mbelt.MODID;

public class RegisterClass {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Mbelt.MODID);

    public static final RegistryObject<MenuType<CreatorSlots>> CREATOR_SLOTS =
            MENUS.register("creators_slots", () -> IForgeMenuType.create(CreatorSlots::new));

    public static final RegistryObject<Item> STRING_BELT = ITEMS.register("string_belt",
            () -> new BeltItem(new Item.Properties().stacksTo(1), 1,1,4));

    public static final RegistryObject<Item> LEATHER_BELT = ITEMS.register("leather_belt",
            () -> new BeltItem(new Item.Properties().stacksTo(1), 2,3,12));

    public static final RegistryObject<Item> HARDENED_BELT = ITEMS.register("hardened_belt",
            () -> new BeltItem(new Item.Properties().stacksTo(1),3,6,12));

    public static final RegistryObject<Item> SMALL_BAG = ITEMS.register("small_bag",
            () -> new BagItem(new Item.Properties().stacksTo(1), 4));

    public static final RegistryObject<Item> MEDIUM_BAG = ITEMS.register("medium_bag",
            () -> new BagItem(new Item.Properties().stacksTo(1), 8));

    public static final RegistryObject<Item> LARGE_BAG = ITEMS.register("large_bag",
            () -> new BagItem(new Item.Properties().stacksTo(1), 12));

    public static final RegistryObject<Item> ENDER_BAG = ITEMS.register("ender_bag",
            () -> new EnderBagItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> HARDENED_LEATHER = ITEMS.register("hardened_leather",
            () -> new Item(new Item.Properties()));



    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID    );

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_ITEM =
            LOOT_MODIFIERS.register("add_item", () -> AddItemModifier.CODEC);

}
