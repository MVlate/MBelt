package MVlate.mbelt.item;

import MVlate.mbelt.client.menu.CreatorSlots;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.server.level.ServerPlayer;
public class BagItem extends Item {

    private final int slots;

    public BagItem(Properties properties, int slots) {
        super(properties);
        this.slots = slots;
    }

    public int getSlots() {
        return this.slots;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack bagStack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider(
                    (containerId, playerInventory, playerEntity) -> new CreatorSlots(containerId, playerInventory, bagStack),
                    bagStack.getHoverName()
            ), buf -> {
                buf.writeItem(bagStack);
            });
        }

        return InteractionResultHolder.sidedSuccess(bagStack, level.isClientSide());
    }
}