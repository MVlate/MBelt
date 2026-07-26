package MVlate.mbelt.event;

import MVlate.mbelt.MBeltConstants;
import MVlate.mbelt.Mbelt;
import MVlate.mbelt.RegisterClass;
import MVlate.mbelt.item.EnderBagItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import MVlate.mbelt.item.BagItem;

@Mod.EventBusSubscriber(modid = Mbelt.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AnvilUpgradeHandler {

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeft();
        ItemStack rightItem = event.getRight();

        int maxQuickSlots = 0;
        int maxBagSize = 0;

        if (leftItem.getItem() == RegisterClass.STRING_BELT.get()) {
            maxQuickSlots = 1;
            maxBagSize = 8;
        } else if (leftItem.getItem() == RegisterClass.LEATHER_BELT.get()) {
            maxQuickSlots = 3;
            maxBagSize = 12;
        } else if (leftItem.getItem() == RegisterClass.HARDENED_BELT.get()) {
            maxQuickSlots = 6;
            maxBagSize = 12;
        }

        if (leftItem.getItem() == RegisterClass.STRING_BELT.get() ||
                leftItem.getItem() == RegisterClass.LEATHER_BELT.get() ||
                leftItem.getItem() == RegisterClass.HARDENED_BELT.get()) {

            if (rightItem.getItem() == Items.LEAD) {
                ItemStack upgradedBelt = leftItem.copy();
                CompoundTag nbt = upgradedBelt.getOrCreateTag();
                int currentQuickSlots = nbt.getInt(MBeltConstants.NBT_QUICK_SLOT);

                if (currentQuickSlots < maxQuickSlots) {
                    nbt.putInt(MBeltConstants.NBT_QUICK_SLOT, currentQuickSlots + 1);
                    event.setOutput(upgradedBelt);
                    event.setCost(3);
                    event.setMaterialCost(1);
                }
            }

            else if (rightItem.getItem() instanceof BagItem) {
                BagItem nuevaBolsa = (BagItem) rightItem.getItem();
                int newBagSize = nuevaBolsa.getSlots();

                ItemStack upgradedBelt = leftItem.copy();
                CompoundTag nbt = upgradedBelt.getOrCreateTag();
                int currentBagSize = nbt.getInt(MBeltConstants.NBT_BAG_SIZE);

                if (newBagSize <= maxBagSize && currentBagSize != newBagSize) {
                    nbt.put(MBeltConstants.NBT_BAG, rightItem.save(new CompoundTag()));
                    nbt.putInt(MBeltConstants.NBT_BAG_SIZE, newBagSize);

                    event.setOutput(upgradedBelt);
                    event.setCost(5);
                    event.setMaterialCost(1);
                }
            }
            else if (rightItem.getItem() instanceof EnderBagItem) {
                CompoundTag currentNbt = leftItem.getTag();
                if (currentNbt != null && currentNbt.contains(MBeltConstants.NBT_ENDER_BAG)) {
                    return;
                }

                ItemStack upgradedBelt = leftItem.copy();
                CompoundTag nbt = upgradedBelt.getOrCreateTag();

                nbt.put(MBeltConstants.NBT_ENDER_BAG, rightItem.save(new CompoundTag()));

                event.setOutput(upgradedBelt);


                event.setCost(10);
                event.setMaterialCost(1);
            }
        }
    }

    @SubscribeEvent
    public static void onAnvilRepair(AnvilRepairEvent event) {
        ItemStack leftItem = event.getLeft();
        ItemStack rightItem = event.getRight();

        if ((leftItem.getItem() == RegisterClass.STRING_BELT.get() || leftItem.getItem() == RegisterClass.LEATHER_BELT.get())
                && rightItem.getItem() instanceof BagItem) {

            CompoundTag oldNbt = leftItem.getTag();
            if (oldNbt != null && oldNbt.contains(MBeltConstants.NBT_BAG)) {

                ItemStack oldBag = ItemStack.of(oldNbt.getCompound(MBeltConstants.NBT_BAG));

                if (!oldBag.isEmpty()) {
                    Player player = event.getEntity();
                    if (!player.getInventory().add(oldBag)) {
                        player.drop(oldBag, false);
                    }
                }
            }
        }
    }
}