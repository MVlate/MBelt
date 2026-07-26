package MVlate.mbelt.network;

import MVlate.mbelt.MBeltConstants;
import MVlate.mbelt.item.BeltItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BeltSwapPacket {

    private final int slotId;
    private final boolean toOffhand;

    public BeltSwapPacket(int slotId, boolean toOffhand) {
        this.slotId = slotId;
        this.toOffhand = toOffhand;
    }

    public BeltSwapPacket(FriendlyByteBuf buf) {
        this.slotId = buf.readInt();
        this.toOffhand = buf.readBoolean();;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.slotId);
        buf.writeBoolean(this.toOffhand);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            ItemStack belt = BeltItem.getEquippedBelt(player);

            if (belt.isEmpty() || !belt.hasTag()) return;

            CompoundTag nbt = belt.getTag();
            if (nbt.getInt(MBeltConstants.NBT_QUICK_SLOT) <= 0) return;

            if (this.slotId >= nbt.getInt(MBeltConstants.NBT_QUICK_SLOT)) return;

            ItemStackHandler beltInventory = new ItemStackHandler(10);
            if (nbt.contains(MBeltConstants.NBT_QUICK_SLOT_INVENTORY)) {
                beltInventory.deserializeNBT(nbt.getCompound(MBeltConstants.NBT_QUICK_SLOT_INVENTORY));
            }


            InteractionHand hand = this.toOffhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            ItemStack handItem = player.getItemInHand(hand);
            ItemStack storedItem = beltInventory.getStackInSlot(this.slotId);

            player.setItemInHand(hand, storedItem);
            beltInventory.setStackInSlot(this.slotId, handItem);

            nbt.put(MBeltConstants.NBT_QUICK_SLOT_INVENTORY, beltInventory.serializeNBT());
        });

        context.setPacketHandled(true);
        return true;
    }
}