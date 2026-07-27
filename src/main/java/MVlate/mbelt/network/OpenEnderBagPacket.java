package MVlate.mbelt.network;
import MVlate.mbelt.MBeltConstants;
import MVlate.mbelt.client.menu.CreatorSlots;
import MVlate.mbelt.item.BeltItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class OpenEnderBagPacket {

    public OpenEnderBagPacket() {}

    public OpenEnderBagPacket(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();

            if (player == null || !(player.containerMenu instanceof CreatorSlots menu)) return;

            ItemStack belt = menu.getContainerStack();;

            if (belt.isEmpty() || !belt.hasTag() || !belt.getTag().contains(MBeltConstants.NBT_ENDER_BAG)) {
                return;
            }

            PlayerEnderChestContainer enderChest = player.getEnderChestInventory();
            player.openMenu(new SimpleMenuProvider(
                    (containerId, playerInventory, p) -> ChestMenu.threeRows(containerId, playerInventory, enderChest),
                    Component.translatable("container.enderchest")
            ));
        });
        context.setPacketHandled(true);
        return true;
        }
}
