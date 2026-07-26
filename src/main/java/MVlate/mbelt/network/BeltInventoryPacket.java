package MVlate.mbelt.network;


import MVlate.mbelt.RegisterClass;
import MVlate.mbelt.client.menu.CreatorSlots;
import MVlate.mbelt.item.BeltItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;
import java.util.function.Supplier;

import static MVlate.mbelt.item.BeltItem.getEquippedBelt;


public class BeltInventoryPacket {

    public BeltInventoryPacket() {}

    public BeltInventoryPacket(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            ItemStack belt = getEquippedBelt(player);

            if (belt == null || belt.isEmpty()) return;

            if (!BeltItem.hasUpgrades(belt)) {
                player.displayClientMessage(Component.translatable("tooltip.mbelt.belt_no_upgrades"), true);
            } else {
                NetworkHooks.openScreen(player, new SimpleMenuProvider(
                        (id, playerInv, plyr) -> new CreatorSlots(id, playerInv, belt),
                        belt.getHoverName()
                ), buf -> {
                    buf.writeItem(belt);
                });
            }
        });

        context.setPacketHandled(true);
    }
}