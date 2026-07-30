package MVlate.mbelt.client.menu;

import MVlate.mbelt.MBeltConstants;
import MVlate.mbelt.RegisterClass;
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

public class CreatorSlots extends AbstractContainerMenu {
    public int lockedSlotIndex = -1; //

    private final ItemStackHandler quickSlotsInventory = new ItemStackHandler(MBeltConstants.QUICK_SLOT_INVENTORY_SIZE) {
        @Override protected void onContentsChanged(int slot) { saveContainerData(); }
    };
    private final ItemStackHandler bagInventory = new ItemStackHandler(MBeltConstants.BAG_INVENTORY_SIZE) {
        @Override protected void onContentsChanged(int slot) { saveContainerData(); }
    };

    private final ItemStack containerStack;
    private int totalContainerSlots = 0;

    public CreatorSlots(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, extraData.readItem());
    }

    public CreatorSlots(int containerId, Inventory playerInventory, ItemStack containerStack) {
        super(RegisterClass.CREATOR_SLOTS.get(), containerId);
        this.containerStack = containerStack;

        if (containerStack.getItem() instanceof BagItem) {
            if (containerStack.hasTag() && containerStack.getTag().contains("ContainerInventory")) {
                bagInventory.deserializeNBT(containerStack.getTag().getCompound("ContainerInventory"));
            }

            BagItem bag = (BagItem) containerStack.getItem();
            int bagSlots = bag.getSlots();
            this.totalContainerSlots += drawLogicSlotsBag(bagSlots);

        } else {
            CompoundTag beltNbt = containerStack.getOrCreateTag();


            if (beltNbt.contains("QuickSlotsInventory")) {
                quickSlotsInventory.deserializeNBT(beltNbt.getCompound("QuickSlotsInventory"));
            }

            int quickSlots = beltNbt.getInt("quick_slots");
            for (int i = 0; i < quickSlots; i++) {
                int col = i % 4;
                int x = MBeltConstants.QUICK_START_X + (col * MBeltConstants.SLOT_SIZE);

                int y = (i < 4) ? MBeltConstants.QUICK_START_Y : MBeltConstants.QUICK_END_Y;

                this.addSlot(new SlotItemHandler(quickSlotsInventory, i, x, y));
                this.totalContainerSlots++;
            }


            if (beltNbt.contains("EquippedBag")) {
                ItemStack equippedBag = ItemStack.of(beltNbt.getCompound("EquippedBag"));
                if (equippedBag.hasTag() && equippedBag.getTag().contains("ContainerInventory")) {
                    bagInventory.deserializeNBT(equippedBag.getTag().getCompound("ContainerInventory"));
                }

                int bagSizeGuardado = beltNbt.getInt("bag_size");
                this.totalContainerSlots += drawLogicSlotsBag(bagSizeGuardado);
            }
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

            nbt.put("ContainerInventory", bagInventory.serializeNBT());
        } else {

            nbt.put("QuickSlotsInventory", quickSlotsInventory.serializeNBT());

            if (nbt.contains("EquippedBag")) {
                ItemStack equippedBag = ItemStack.of(nbt.getCompound("EquippedBag"));
                equippedBag.getOrCreateTag().put("ContainerInventory", bagInventory.serializeNBT());
                nbt.put("EquippedBag", equippedBag.save(new CompoundTag()));
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

    private int drawLogicSlotsBag(int bagSize){

        int containerSlots= 0;
        for (int i = 0; i < bagSize; i++) {
            int xPos = MBeltConstants.BAG_START_X + (i % 4 * MBeltConstants.SLOT_SIZE);
            int yPos = MBeltConstants.BAG_START_Y - (i / 4 * MBeltConstants.SLOT_SIZE);

            this.addSlot(new SlotItemHandler(bagInventory, i, xPos, yPos));
            containerSlots++;
        }
        return containerSlots;

    }


}