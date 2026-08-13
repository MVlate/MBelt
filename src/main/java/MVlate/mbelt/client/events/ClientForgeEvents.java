package MVlate.mbelt.client.events;

import MVlate.mbelt.MBeltConstants;
import MVlate.mbelt.Mbelt;
import MVlate.mbelt.client.render.String_Belt;
import MVlate.mbelt.client.screen.BeltHudOverlay;
import MVlate.mbelt.client.screen.BeltItemButton;
import MVlate.mbelt.config.MBeltConfig;
import MVlate.mbelt.item.BeltItem;
import MVlate.mbelt.network.BeltInventoryPacket;
import MVlate.mbelt.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Mbelt.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientForgeEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && BeltHudOverlay.displayTicks > 0) {
            BeltHudOverlay.displayTicks--;
        }
    }
    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {

        if (event.getScreen() instanceof InventoryScreen screen) {

            ItemStack belt = BeltItem.getEquippedBelt(Minecraft.getInstance().player);

            if (!belt.isEmpty() && belt.hasTag()) {
                if( belt.getTag().contains(MBeltConstants.NBT_BAG)||
                    belt.getTag().contains(MBeltConstants.NBT_ENDER_BAG)||
                    belt.getTag().contains(MBeltConstants.NBT_QUICK_SLOT))
                {
                    int x = screen.getGuiLeft() + MBeltConfig.BUTTON_X.get();
                    int y = screen.getGuiTop() + MBeltConfig.BUTTON_Y.get();
                    BeltItemButton openBeltButton = new BeltItemButton(x, y, belt, () -> {
                        PacketHandler.INSTANCE.sendToServer(new BeltInventoryPacket());
                    });
                    event.addListener(openBeltButton);
                }
            }
        }
    }
}