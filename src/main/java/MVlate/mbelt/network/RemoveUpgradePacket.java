package MVlate.mbelt.network;
import MVlate.mbelt.client.menu.CreatorSlots;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;
import MVlate.mbelt.item.BeltItem;

public class RemoveUpgradePacket {
    private final int upgradeType;

    public RemoveUpgradePacket(int upgradeType) {
        this.upgradeType = upgradeType;
    }
    public RemoveUpgradePacket(FriendlyByteBuf buf) {
        this.upgradeType = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.upgradeType);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            ItemStack belt = ItemStack.EMPTY;

            if (player.containerMenu instanceof CreatorSlots menu) {
                belt = menu.getContainerStack();
            }

            if (belt.isEmpty() || !(belt.getItem() instanceof BeltItem)) {
                if (player.getMainHandItem().getItem() instanceof BeltItem) {
                    belt = player.getMainHandItem();
                } else if (player.getOffhandItem().getItem() instanceof BeltItem) {
                    belt = player.getOffhandItem();
                } else {
                    belt = BeltItem.getEquippedBelt(player);
                }
            }

            if (belt.isEmpty() || !belt.hasTag()) return;

            CompoundTag nbt = belt.getTag();
            switch (this.upgradeType) {
                case 0:
                    if (nbt.contains("has_ender_bag")) {
                        ItemStack enderBag = ItemStack.of(nbt.getCompound("has_ender_bag"));
                        if (!player.getInventory().add(enderBag)) player.drop(enderBag, false);
                        nbt.remove("has_ender_bag");
                        player.closeContainer();
                    }
                    break;

                case 1:
                    if (nbt.contains("EquippedBag")) {
                        ItemStack normalBag = ItemStack.of(nbt.getCompound("EquippedBag"));

                        if (!player.getInventory().add(normalBag)) player.drop(normalBag, false);

                        nbt.remove("EquippedBag");
                        nbt.remove("bag_size");
                        player.closeContainer();
                    }
                    break;

                case 2:
                    int currentQuickSlots = nbt.getInt("quick_slots");
                    if (currentQuickSlots > 0) {

                        if (nbt.contains("QuickSlotsInventory")) {

                            ItemStackHandler quickHandler = new ItemStackHandler(currentQuickSlots);
                            quickHandler.deserializeNBT(nbt.getCompound("QuickSlotsInventory"));

                            for (int i = 0; i < quickHandler.getSlots(); i++) {
                                ItemStack storedItem = quickHandler.getStackInSlot(i);
                                if (!storedItem.isEmpty()) {
                                    if (!player.getInventory().add(storedItem)) {
                                        player.drop(storedItem, false);
                                    }
                                }
                            }
                            nbt.remove("QuickSlotsInventory");
                        }


                        ItemStack leads = new ItemStack(Items.LEAD, currentQuickSlots);
                        if (!player.getInventory().add(leads)) {
                            player.drop(leads, false);
                        }

                        nbt.putInt("quick_slots", 0);
                        player.closeContainer();
                    }
                    break;
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}