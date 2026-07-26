package MVlate.mbelt;

import MVlate.mbelt.client.render.*;
import MVlate.mbelt.client.screen.ContainerScreen;
import MVlate.mbelt.config.MBeltConfig;
import MVlate.mbelt.network.PacketHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

import static MVlate.mbelt.RegisterClass.LOOT_MODIFIERS;

@Mod(Mbelt.MODID)
public class Mbelt {

    public static final String MODID = "mbelt";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> MBelt = CREATIVE_MODE_TABS.register("mbelt", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.mbelt")).icon(() -> RegisterClass.LEATHER_BELT.get().getDefaultInstance()).displayItems((parameters, output) -> {
        output.accept(RegisterClass.STRING_BELT.get());
        output.accept(RegisterClass.LEATHER_BELT.get());
        output.accept(RegisterClass.HARDENED_BELT.get());
        output.accept(RegisterClass.SMALL_BAG.get());
        output.accept(RegisterClass.MEDIUM_BAG.get());
        output.accept(RegisterClass.LARGE_BAG.get());
        output.accept(RegisterClass.ENDER_BAG.get());
        output.accept(RegisterClass.HARDENED_LEATHER.get());
    }).build());

    public Mbelt() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);

        RegisterClass.ITEMS.register(modEventBus);
        LOOT_MODIFIERS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
        PacketHandler.register();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MBeltConfig.SPEC, "mbelt-common.toml");
        RegisterClass.MENUS.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {

                CuriosRendererRegistry.register(RegisterClass.STRING_BELT.get(), () -> {
                    ModelPart root = String_Belt.createBodyLayer().bakeRoot();
                    ResourceLocation beltTex = new ResourceLocation(Mbelt.MODID, "textures/item/string_belt_3d.png");
                    return new BeltRenderer(new String_Belt<>(root), beltTex);
                });

                CuriosRendererRegistry.register(RegisterClass.LEATHER_BELT.get(), () -> {
                    ModelPart root = Leather_Belt.createBodyLayer().bakeRoot();
                    ResourceLocation leatherTex = new ResourceLocation(Mbelt.MODID, "textures/item/leather_belt_3d.png");
                    return new BeltRenderer(new Leather_Belt<>(root), leatherTex);
                });

                CuriosRendererRegistry.register(RegisterClass.HARDENED_BELT.get(), () -> {
                    ModelPart root = Hardened_Belt.createBodyLayer().bakeRoot();
                    ResourceLocation hardenedTex = new ResourceLocation(Mbelt.MODID, "textures/item/hardened_belt_3d.png");
                    return new BeltRenderer(new Hardened_Belt<>(root), hardenedTex);
                });

                CuriosRendererRegistry.register(Items.BUNDLE, () -> {
                    ModelPart root = Bundle_3d.createBodyLayer().bakeRoot();
                    ResourceLocation bundleTex = new ResourceLocation(Mbelt.MODID, "textures/item/bundle_texture_3d.png");
                    return new BeltRenderer(new Bundle_3d<>(root), bundleTex);
                });

                MenuScreens.register(RegisterClass.CREATOR_SLOTS.get(), ContainerScreen::new);
            });
        }
    }
}
