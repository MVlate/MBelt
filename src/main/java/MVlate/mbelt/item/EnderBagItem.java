package MVlate.mbelt.item;

import MVlate.mbelt.network.OpenEnderBagPacket;
import MVlate.mbelt.network.PacketHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EnderBagItem extends Item {
    public EnderBagItem(Item.Properties properties) {
    super(properties);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack bagStack = player.getItemInHand(hand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            PlayerEnderChestContainer enderChest = serverPlayer.getEnderChestInventory();

            serverPlayer.openMenu(new SimpleMenuProvider(
                    (id, playerInv, p) -> ChestMenu.threeRows(id, playerInv, enderChest),
                    Component.translatable("container.enderchest")
            ));
        }

        return InteractionResultHolder.sidedSuccess(bagStack, level.isClientSide());
    }

}
