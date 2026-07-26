package MVlate.mbelt.client.screen;

import MVlate.mbelt.Mbelt;
import MVlate.mbelt.client.BeltKeybinds;
import MVlate.mbelt.network.BeltSwapPacket;
import MVlate.mbelt.network.PacketHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class BeltWheelScreen extends Screen {

    private static final ResourceLocation WHEEL_TEXTURE = new ResourceLocation(Mbelt.MODID, "textures/gui/wheel_texture.png");
    private final ItemStackHandler inventory;
    private final int quickSlots;
    private int hoveredSlot = -1;

    public BeltWheelScreen(ItemStack belt, ItemStackHandler inventory, int quickSlots) {
        super(Component.empty());
        this.inventory = inventory;
        this.quickSlots = quickSlots;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int imageWidth = 256;
        int imageHeight = 256;
        int renderX = centerX - (imageWidth / 2);
        int renderY = centerY - (imageHeight / 2);
        guiGraphics.blit(WHEEL_TEXTURE, renderX, renderY, 0, 0, imageWidth, imageHeight);

        // ------------------------------------------ MATH :(

        double angle = Math.toDegrees(Math.atan2(mouseY - centerY, mouseX - centerX));
        if (angle < 0) angle += 360;

        double slice = 360.0 / quickSlots;
        //Distance from center
        double distance = Math.sqrt(Math.pow(mouseX - centerX, 2) + Math.pow(mouseY - centerY, 2));

        if (distance > 20) {
            this.hoveredSlot = (int) (angle / slice);
        } else {
            this.hoveredSlot = -1;
        }


        int radius = 60;
        for (int i = 0; i < quickSlots; i++) {
            double slotAngle = Math.toRadians((i * slice) + (slice / 2));
            int slotX = centerX + (int) (Math.cos(slotAngle) * radius) - 8;
            int slotY = centerY + (int) (Math.sin(slotAngle) * radius) - 8;

            ItemStack stack = inventory.getStackInSlot(i);

            if (i == hoveredSlot) {
                guiGraphics.fill(slotX - 4, slotY - 4, slotX + 20, slotY + 20, 0x80FFFFFF);
            } else {
                guiGraphics.fill(slotX - 4, slotY - 4, slotX + 20, slotY + 20, 0x80000000);
            }

            if (!stack.isEmpty()) {
                guiGraphics.renderItem(stack, slotX, slotY);
            }
        }
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (BeltKeybinds.WHEEL_KEY.matches(keyCode, scanCode)) {


            if (this.hoveredSlot != -1) {
                executeAction(this.hoveredSlot);
            }


            this.minecraft.setScreen(null);
            return true;
        }

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    private void executeAction(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < this.quickSlots) {
            PacketHandler.INSTANCE.sendToServer(new BeltSwapPacket(slotIndex,false));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (this.hoveredSlot != -1) {

            if (button == 0 || button == 1) {
                boolean isRightClick = (button == 1);

                PacketHandler.INSTANCE.sendToServer(new BeltSwapPacket(this.hoveredSlot, isRightClick));

                BeltHudOverlay.displayTicks = 60;
                this.minecraft.setScreen(null);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

}