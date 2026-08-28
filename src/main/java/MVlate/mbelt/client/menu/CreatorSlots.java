package MVlate.mbelt.client.menu;

import MVlate.mbelt.MBeltConstants;
import MVlate.mbelt.RegisterClass;
import MVlate.mbelt.config.MBeltConfig;
import MVlate.mbelt.item.BeltItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import MVlate.mbelt.item.BagItem;
import org.jetbrains.annotations.NotNull;

public class CreatorSlots extends AbstractContainerMenu {
    public int lockedSlotIndex = -1; //

    private ItemStackHandler quickSlotsInventory = new ItemStackHandler(0);
    private ItemStackHandler bagInventory = new ItemStackHandler(0);

    private final ItemStack containerStack;
    private int totalContainerSlots = 0;

    public CreatorSlots(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, extraData.readItem());
    }

    public CreatorSlots(int containerId, Inventory playerInventory, ItemStack containerStack) {
        super(RegisterClass.CREATOR_SLOTS.get(), containerId);
        this.containerStack = containerStack;
        int bagSize=0;

        if (containerStack.getItem() instanceof BagItem bag) {
            bagSize = bag.getSlots();

            bagInventoryItemStackHandler(bagSize);
            drawLogicSlotsBag(bagSize);

            if (containerStack.hasTag() && containerStack.getTag().contains(MBeltConstants.NBT_CONTAINER_INVENTORY)) {
                bagInventory.deserializeNBT(containerStack.getTag().getCompound(MBeltConstants.NBT_CONTAINER_INVENTORY));
            }
            this.totalContainerSlots = bagSize;

        } else {
            CompoundTag beltNbt = containerStack.getOrCreateTag();
            quickSlotsInventoryItemStackHandler(beltNbt.getInt(MBeltConstants.NBT_QUICK_SLOT));
            if (beltNbt.contains(MBeltConstants.NBT_QUICK_SLOT_INVENTORY)) {
                quickSlotsInventory.deserializeNBT(beltNbt.getCompound(MBeltConstants.NBT_QUICK_SLOT_INVENTORY));
            }

            if (beltNbt.contains(MBeltConstants.NBT_BAG)) {
                bagSize = beltNbt.getInt(MBeltConstants.NBT_BAG_SIZE);
                bagInventoryItemStackHandler(bagSize);
                ItemStack equippedBag = ItemStack.of(beltNbt.getCompound(MBeltConstants.NBT_BAG));
                if (equippedBag.hasTag() && equippedBag.getTag().contains(MBeltConstants.NBT_CONTAINER_INVENTORY)) {
                    bagInventory.deserializeNBT(equippedBag.getTag().getCompound(MBeltConstants.NBT_CONTAINER_INVENTORY));
                }

                drawLogicSlotsBag(bagSize);
            }

            int quickSlots = beltNbt.getInt(MBeltConstants.NBT_QUICK_SLOT);
            for (int i = 0; i < quickSlots; i++) {
                int col = i % 3;
                int x = MBeltConstants.QUICK_START_X + (col * MBeltConstants.SLOT_SIZE);

                int y = (i < 3) ? MBeltConstants.QUICK_START_Y : MBeltConstants.QUICK_END_Y;

                this.addSlot(new SlotItemHandler(quickSlotsInventory, i, x, y));
            }


            this.totalContainerSlots = quickSlots + bagSize;

        }

        //PLAYER INV
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, MBeltConstants.PLAYER_INV_X + col * MBeltConstants.SLOT_SIZE, MBeltConstants.PLAYER_INV_Y + row * MBeltConstants.SLOT_SIZE));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, MBeltConstants.PLAYER_INV_X + col * MBeltConstants.SLOT_SIZE, 142));
        }


        for (Slot slot : this.slots) {
            if (slot.hasItem() && ItemStack.isSameItemSameTags(slot.getItem(), this.containerStack)) {
                this.lockedSlotIndex = slot.index;
                break;
            }
        }
    }

    private void bagInventoryItemStackHandler(int bagSize){
        this.bagInventory = new ItemStackHandler(bagSize) {
            @Override
            protected void onContentsChanged(int slot) {
                saveContainerData();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {

                if (!MBeltConfig.ALLOW_CONTAINERS_IN_BELT_AND_BAGS.get()) {
                    if (!stack.getItem().canFitInsideContainerItems()) {
                        return false;
                    }
                }

                if (stack.getItem() instanceof BagItem) {
                    return false;
                }

                if (stack.getItem() instanceof BeltItem && stack.hasTag()) {
                    boolean hasItems = stack.getTag().contains(MBeltConstants.NBT_BAG) ||
                            stack.getTag().contains(MBeltConstants.NBT_ENDER_BAG) || stack.getTag().contains(MBeltConstants.NBT_QUICK_SLOT);
                    if (hasItems) {
                        return false;
                    }
                }

                return super.isItemValid(slot, stack);
            }
        };
    }

    private void quickSlotsInventoryItemStackHandler(int quickSlots){
        this.quickSlotsInventory= new ItemStackHandler(quickSlots) {

            @Override protected void onContentsChanged(int slot) { saveContainerData(); }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {

                if (!MBeltConfig.ALLOW_CONTAINERS_IN_BELT_AND_BAGS.get()) {
                    if (!stack.getItem().canFitInsideContainerItems()) {
                        return false;
                    }
                }
                if (stack.getItem() instanceof BagItem) {return false;}

                if (stack.getItem() instanceof BeltItem && stack.hasTag()) {
                    boolean hasItems = stack.getTag().contains(MBeltConstants.NBT_BAG) ||
                            stack.getTag().contains(MBeltConstants.NBT_ENDER_BAG)||stack.getTag().contains(MBeltConstants.NBT_QUICK_SLOT);
                    if (hasItems) {
                        return false;
                    }
                }

                return super.isItemValid(slot, stack);
            }
        };
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {

        if (slotId >= 0 && slotId == this.lockedSlotIndex) {
            return;
        }


        if (clickType == ClickType.SWAP && this.lockedSlotIndex != -1) {
            Slot lockedSlot = this.slots.get(this.lockedSlotIndex);

            if (lockedSlot.container == player.getInventory() && lockedSlot.getSlotIndex() == button) {
                return;
            }
        }

        super.clicked(slotId, button, clickType, player);
    }



    private void saveContainerData() {
        if (containerStack.isEmpty()) return;

        CompoundTag nbt = containerStack.getOrCreateTag();

        if (containerStack.getItem() instanceof BagItem) {

            nbt.put(MBeltConstants.NBT_CONTAINER_INVENTORY, bagInventory.serializeNBT());
        } else {

            nbt.put(MBeltConstants.NBT_QUICK_SLOT_INVENTORY, quickSlotsInventory.serializeNBT());

            if (nbt.contains(MBeltConstants.NBT_BAG) && bagInventory != null) {
                ItemStack equippedBag = ItemStack.of(nbt.getCompound(MBeltConstants.NBT_BAG));
                equippedBag.getOrCreateTag().put(MBeltConstants.NBT_CONTAINER_INVENTORY, bagInventory.serializeNBT());
                nbt.put(MBeltConstants.NBT_BAG, equippedBag.save(new CompoundTag()));
            }
        }
    }

    @Override public boolean stillValid(Player player) { return !this.containerStack.isEmpty(); }
    @Override public void removed(Player player) { super.removed(player); saveContainerData(); }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < totalContainerSlots) {
                if (!this.moveItemStackTo(itemstack1, totalContainerSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, totalContainerSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return itemstack;
    }

    public ItemStack getContainerStack() { return this.containerStack; }

    private void drawLogicSlotsBag(int bagSize){

        for (int i = 0; i < bagSize; i++) {
            int xPos = MBeltConstants.BAG_START_X + (i % 4 * MBeltConstants.SLOT_SIZE);
            int yPos = MBeltConstants.BAG_START_Y - (i / 4 * MBeltConstants.SLOT_SIZE);

            this.addSlot(new SlotItemHandler(bagInventory, i, xPos, yPos));
        }
    }


}