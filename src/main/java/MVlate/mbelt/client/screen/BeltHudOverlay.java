package MVlate.mbelt.client.screen;

import MVlate.mbelt.MBeltConstants;
import MVlate.mbelt.item.BeltItem;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.items.ItemStackHandler;

public class BeltHudOverlay {
    public static int displayTicks = 0;

    private static ItemStackHandler cachedInventory = new ItemStackHandler(0);
    private static int cachedQuickSlots = -1;
    private static CompoundTag cachedTag = new CompoundTag();

    public static final IGuiOverlay HUD_BELT = (gui, guiGraphics, partialTick, width, height) -> {

        if (displayTicks <= 0) return;
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;

        ItemStack belt = BeltItem.getEquippedBelt(player);
        if (belt.isEmpty()) return;

        CompoundTag tag = belt.getTag();
        if (tag == null || !tag.contains(MBeltConstants.NBT_QUICK_SLOT) || tag.getInt(MBeltConstants.NBT_QUICK_SLOT) == 0) return;

        int quickSlots = tag.getInt(MBeltConstants.NBT_QUICK_SLOT);

        CompoundTag quickSlotsTag = tag.getCompound(MBeltConstants.NBT_QUICK_SLOT_INVENTORY);
        if (quickSlots != cachedQuickSlots || !quickSlotsTag.equals(cachedTag)) {
            cachedInventory = new ItemStackHandler(quickSlots);
            cachedInventory.deserializeNBT(quickSlotsTag);
            cachedQuickSlots = quickSlots;
            cachedTag = quickSlotsTag;
        }

        int slotSize = 20;
        int totalWidth = quickSlots * slotSize;
        int startX = (width / 2) - (totalWidth / 2);
        int slotY = height - 60;

        for (int i = 0; i < quickSlots; i++) {
            int slotX = startX + (i * slotSize);

            guiGraphics.fill(slotX, slotY, slotX + 16, slotY + 16, 0x80000000);

            ItemStack stackInSlot = cachedInventory.getStackInSlot(i);

            if (!stackInSlot.isEmpty()) {
                guiGraphics.renderItem(stackInSlot, slotX, slotY);
                guiGraphics.renderItemDecorations(minecraft.font, stackInSlot, slotX, slotY);
            }
        }
    };
}