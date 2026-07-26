package MVlate.mbelt.client;

import MVlate.mbelt.Mbelt;
import MVlate.mbelt.client.screen.BeltHudOverlay;
import MVlate.mbelt.client.screen.BeltWheelScreen;
import MVlate.mbelt.item.BeltItem;
import MVlate.mbelt.network.BeltInventoryPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;
import org.lwjgl.glfw.GLFW;
import MVlate.mbelt.network.BeltSwapPacket;
import MVlate.mbelt.network.PacketHandler;


public class BeltKeybinds {

    static String category="category.mbelt.keys";
    public static final KeyMapping QUICK_KEY_1 = new KeyMapping("key.mbelt.quick1", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), category);
    public static final KeyMapping QUICK_KEY_2 = new KeyMapping("key.mbelt.quick2", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), category);
    public static final KeyMapping QUICK_KEY_3 = new KeyMapping("key.mbelt.quick3", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), category);
    public static final KeyMapping QUICK_KEY_4 = new KeyMapping("key.mbelt.quick4", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), category);
    public static final KeyMapping QUICK_KEY_5 = new KeyMapping("key.mbelt.quick5", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), category);
    public static final KeyMapping GUI_KEY = new KeyMapping("key.mbelt.gui", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,GLFW.GLFW_KEY_V, category);
    public static final KeyMapping WHEEL_KEY = new KeyMapping("key.mbelt.wheel", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM,GLFW.GLFW_KEY_N, category);

    @Mod.EventBusSubscriber(modid = Mbelt.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class KeyRegister {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(QUICK_KEY_1);
            event.register(QUICK_KEY_2);
            event.register(QUICK_KEY_3);
            event.register(QUICK_KEY_4);
            event.register(QUICK_KEY_5);
            event.register(WHEEL_KEY);
            event.register(GUI_KEY);
        }
    }

    @Mod.EventBusSubscriber(modid = Mbelt.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class KeyInputListener {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if (QUICK_KEY_1.consumeClick()){
                PacketHandler.INSTANCE.sendToServer(new BeltSwapPacket(0,false)) ;
                BeltHudOverlay.displayTicks = 60;
            }
            if (QUICK_KEY_2.consumeClick()) {
                PacketHandler.INSTANCE.sendToServer(new BeltSwapPacket(1,false));
                BeltHudOverlay.displayTicks = 60;
            }
            if (QUICK_KEY_3.consumeClick()) {
                PacketHandler.INSTANCE.sendToServer(new BeltSwapPacket(2,false));
                BeltHudOverlay.displayTicks = 60;
            }
            if (QUICK_KEY_4.consumeClick()) {
                PacketHandler.INSTANCE.sendToServer(new BeltSwapPacket(3,false));
                BeltHudOverlay.displayTicks = 60;
            }
            if (QUICK_KEY_5.consumeClick()) {
                PacketHandler.INSTANCE.sendToServer(new BeltSwapPacket(4,false));
                BeltHudOverlay.displayTicks = 60;
            }
            if (GUI_KEY.consumeClick()) {
                PacketHandler.INSTANCE.sendToServer(new BeltInventoryPacket());
            }

            if (WHEEL_KEY.consumeClick()) {
                Minecraft minecraft = Minecraft.getInstance();
                Player player = minecraft.player;

                if (player != null && minecraft.screen == null) {

                    ItemStack belt = BeltItem.getEquippedBelt(player);

                    if (!belt.isEmpty() && belt.hasTag()) {
                        CompoundTag tag = belt.getTag();
                        int quickSlots = tag.getInt("quick_slots");

                        if (quickSlots > 0) {
                            ItemStackHandler inventory = new ItemStackHandler(quickSlots);
                            if (tag.contains("QuickSlotsInventory")) {
                                inventory.deserializeNBT(tag.getCompound("QuickSlotsInventory"));
                            }

                            minecraft.setScreen(new BeltWheelScreen(belt, inventory, quickSlots));
                        }
                    }
                }
            }
        }
    }
}