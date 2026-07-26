package MVlate.mbelt.client.screen;

import MVlate.mbelt.MBeltConstants;
import MVlate.mbelt.Mbelt;
import MVlate.mbelt.network.OpenEnderBagPacket;
import MVlate.mbelt.network.PacketHandler;
import MVlate.mbelt.network.RemoveUpgradePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import MVlate.mbelt.item.BagItem;
import MVlate.mbelt.client.menu.CreatorSlots;

public class ContainerScreen extends AbstractContainerScreen<CreatorSlots> {

    private static final ResourceLocation QUICK_SLOT = new ResourceLocation(Mbelt.MODID, "textures/gui/quick_access_slot.png");
    private static final ResourceLocation EJECT_TEXTURE_ENDER_BAG = new ResourceLocation(Mbelt.MODID, "textures/gui/remove_button_ender_bag.png");
    private static final ResourceLocation EJECT_TEXTURE_BAG = new ResourceLocation(Mbelt.MODID, "textures/gui/remove_button_bag.png");
    private static final ResourceLocation EJECT_TEXTURE_QUICK_SLOT = new ResourceLocation(Mbelt.MODID, "textures/gui/remove_button_quick_slot.png");
    private final ResourceLocation texture;
    int sizeContainer = 0;

    public ContainerScreen(CreatorSlots pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        this.imageWidth = MBeltConstants.WIDTH;
        this.imageHeight = MBeltConstants.HEIGHT;

        sizeContainer =getBagSlotsCount();

        if (sizeContainer > 8) {
            this.texture = new ResourceLocation(Mbelt.MODID, "textures/gui/large_bag_gui.png");
        } else if (sizeContainer > 4) {
            this.texture = new ResourceLocation(Mbelt.MODID, "textures/gui/medium_bag_gui.png");
        } else if (sizeContainer > 0) {
            this.texture = new ResourceLocation(Mbelt.MODID, "textures/gui/small_bag_gui.png");
        } else {
            this.texture = new ResourceLocation(Mbelt.MODID, "textures/gui/base_belt_gui.png");
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        guiGraphics.blit(this.texture, x, y, 0, 0, this.imageWidth, this.imageHeight);
        if (getQuickSlotsCount() > 0) {
            int slots = getQuickSlotsCount();
            for (int i = 0; i < slots; i++) {
                int row = i % 3;
                int cordX = x + MBeltConstants.QUICK_START_X + (row * MBeltConstants.SLOT_SIZE) - 1;
                int cordY = ((i < 3) ? MBeltConstants.QUICK_START_Y : MBeltConstants.QUICK_END_Y) + y - 1;
                guiGraphics.blit(QUICK_SLOT, cordX, cordY, 0, 0, 256, 256);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
        if (getBagSlotsCount() == 0) {
            pGuiGraphics.drawString(this.font, Component.translatable("tooltip.mbelt.bag_equip"), 20, 20, 0x404040, false);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        if (this.menu.lockedSlotIndex != -1) {
            Slot lockedSlot = this.menu.slots.get(this.menu.lockedSlotIndex);

            int x = this.leftPos + lockedSlot.x;
            int y = this.topPos + lockedSlot.y;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, 200);
            guiGraphics.fill(x, y, x + 16, y + 16, 0x80000000);
            guiGraphics.pose().popPose();
        }
    }


    private int getQuickSlotsCount() {
        ItemStack stack = this.menu.getContainerStack();
        if (stack.hasTag() && stack.getTag().contains(MBeltConstants.NBT_QUICK_SLOT)) {
            return stack.getTag().getInt(MBeltConstants.NBT_QUICK_SLOT);
        }
        return 0;
    }

    private int getBagSlotsCount() {
        ItemStack stack = this.menu.getContainerStack();
        if (stack.getItem() instanceof BagItem bag) {
            return bag.getSlots();
        }
        if (stack.hasTag() && stack.getTag().contains(MBeltConstants.NBT_BAG_SIZE)) {
            return stack.getTag().getInt(MBeltConstants.NBT_BAG_SIZE);
        }
        return 0;
    }


    private static final ResourceLocation ENDER_BUTTON_TEXTURE = new ResourceLocation(Mbelt.MODID, "textures/gui/ender_button.png");

    @Override
    protected void init() {
        super.init();
        ItemStack belt = this.menu.getContainerStack();
        boolean hasEnderBag = belt.hasTag() && belt.getTag().contains(MBeltConstants.NBT_ENDER_BAG);
            if(hasEnderBag){
                int buttonX = this.leftPos + MBeltConstants.ENDER_BAG_BUTTON_X - 1;
                int buttonY = this.topPos + MBeltConstants.ENDER_BAG_BUTTON_Y + 1;

                int buttonWidth = 20;
                int buttonHeight = 18;

                Button enderButton = new ImageButton(
                        buttonX,
                        buttonY,
                        buttonWidth,
                        buttonHeight,
                        0,
                        0,
                        20,
                        ENDER_BUTTON_TEXTURE,
                        20,
                        40,
                        button -> {
                            PacketHandler.INSTANCE.sendToServer(new OpenEnderBagPacket());
                        }
                );

                this.addRenderableWidget(enderButton);
            }

        boolean hasEnder = false;
        boolean hasBag = false;
        boolean hasQuickSlots = false;

        if (belt.hasTag()) {
            CompoundTag nbt = belt.getTag();
            hasEnder = nbt.contains(MBeltConstants.NBT_ENDER_BAG);
            hasBag = nbt.contains(MBeltConstants.NBT_BAG);
            hasQuickSlots = nbt.getInt(MBeltConstants.NBT_QUICK_SLOT) > 0;
        }

        ImageButton ejectEnderButton = new ImageButton(
                this.leftPos + 160, this.topPos + 30, 12, 12, 0, 0, 12, EJECT_TEXTURE_ENDER_BAG, MBeltConstants.EJECT_BUTTON_WIDTH, MBeltConstants.EJECT_BUTTON_HEIGHT,
                button -> {
                    PacketHandler.INSTANCE.sendToServer(new RemoveUpgradePacket(0));
                    button.visible = false;
                    this.minecraft.setScreen(null);
                }
        );
        ejectEnderButton.visible = hasEnder;

        ImageButton ejectBagButton = new ImageButton(
                this.leftPos + 160, this.topPos + 42, 12, 12, 0, 0, 12, EJECT_TEXTURE_BAG, MBeltConstants.EJECT_BUTTON_WIDTH, MBeltConstants.EJECT_BUTTON_HEIGHT,
                button -> {
                    PacketHandler.INSTANCE.sendToServer(new RemoveUpgradePacket(1));
                    button.visible = false;
                    this.minecraft.setScreen(null);
                }
        );
        ejectBagButton.visible = hasBag;

        ImageButton ejectQuickSlotButton = new ImageButton(
                this.leftPos + 160, this.topPos + 54, 12, 12, 0, 0, 12, EJECT_TEXTURE_QUICK_SLOT, MBeltConstants.EJECT_BUTTON_WIDTH, MBeltConstants.EJECT_BUTTON_HEIGHT,
                button -> {
                    PacketHandler.INSTANCE.sendToServer(new RemoveUpgradePacket(2));
                    button.visible = false;
                    this.minecraft.setScreen(null);
                }
        );
        ejectQuickSlotButton.visible = hasQuickSlots;

        this.addRenderableWidget(ejectEnderButton);
        this.addRenderableWidget(ejectBagButton);
        this.addRenderableWidget(ejectQuickSlotButton);
        }

}