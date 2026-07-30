package MVlate.mbelt.item;
import MVlate.mbelt.MBeltConstants;
import MVlate.mbelt.client.menu.CreatorSlots;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.CuriosApi;
import com.google.common.collect.Multimap;
import com.google.common.collect.LinkedHashMultimap;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BeltItem extends Item implements ICurioItem {

    private final int extraSlot;

    public BeltItem(Properties properties, int extraSlot) {
        super(properties);
        this.extraSlot = extraSlot;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> modifiers = LinkedHashMultimap.create();

        CuriosApi.addSlotModifier(modifiers, "mbelt_extra_slot", uuid, this.extraSlot, AttributeModifier.Operation.ADDITION);

        return modifiers;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("tooltip.mbelt.indication").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);

        if (stack.hasTag()) {
            CompoundTag nbt = stack.getTag();

            int quickSlots = nbt.getInt(MBeltConstants.NBT_QUICK_SLOT);
            if (quickSlots > 0) {
                tooltipComponents.add(Component.translatable("tooltip.mbelt.quick_slots", quickSlots).withStyle(ChatFormatting.GREEN));
            }

            int bagSize = nbt.getInt(MBeltConstants.NBT_BAG_SIZE);
            if (bagSize > 0) {
                if (bagSize <= 4) tooltipComponents.add(Component.translatable("tooltip.mbelt.small_bag").withStyle(ChatFormatting.GOLD));
                else if (bagSize <= 8) tooltipComponents.add(Component.translatable("tooltip.mbelt.medium_bag").withStyle(ChatFormatting.GOLD));
                else tooltipComponents.add(Component.translatable("tooltip.mbelt.large_bag").withStyle(ChatFormatting.GOLD));
            }
            if(nbt.contains(MBeltConstants.NBT_ENDER_BAG)){
                tooltipComponents.add(Component.translatable("tooltip.mbelt.ender_bag").withStyle(ChatFormatting.DARK_PURPLE));
            }
        }
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack belt = player.getItemInHand(hand);

        if (!level.isClientSide) {
            if (!hasUpgrades(belt)) {
                player.displayClientMessage(Component.translatable("tooltip.mbelt.belt_no_upgrades"), true);
                return InteractionResultHolder.pass(belt);
            } else {
                if (player instanceof ServerPlayer serverPlayer) {

                    NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider(
                            (id, playerInv, plyr) -> new CreatorSlots(id, playerInv, belt),
                            belt.getHoverName()
                    ), buf -> {
                        buf.writeItem(belt);
                    });

                }
            }
        }
        return InteractionResultHolder.sidedSuccess(belt, level.isClientSide);
    }

    public static ItemStack getEquippedBelt(Player player) {
        Optional<SlotResult> curioResult = CuriosApi.getCuriosHelper().findFirstCurio(player,
                stack -> stack.getItem() instanceof BeltItem
        );

        if (curioResult.isPresent()) {
            return curioResult.get().stack();
        }

        return ItemStack.EMPTY;
    }

    public static boolean hasUpgrades(ItemStack belt) {
        if (!belt.hasTag()) {
            return false;
        }
        CompoundTag tag = belt.getTag();
        return tag.getInt(MBeltConstants.NBT_QUICK_SLOT) > 0 ||
                tag.contains(MBeltConstants.NBT_BAG) ||
                tag.contains(MBeltConstants.NBT_ENDER_BAG);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (oldStack.getItem() == newStack.getItem() && !slotChanged) {
            return false;
        }

        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

}