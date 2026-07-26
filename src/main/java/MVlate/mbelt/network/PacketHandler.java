package MVlate.mbelt.network;

import MVlate.mbelt.Mbelt;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Mbelt.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;

        INSTANCE.registerMessage(id++,
                BeltSwapPacket.class,
                BeltSwapPacket::toBytes,
                BeltSwapPacket::new,
                BeltSwapPacket::handle);

        INSTANCE.registerMessage(id++,
                BeltInventoryPacket.class,
                BeltInventoryPacket::toBytes,
                BeltInventoryPacket::new,
                BeltInventoryPacket::handle);

        INSTANCE.registerMessage(id++,
                OpenEnderBagPacket.class,
                OpenEnderBagPacket::toBytes,
                OpenEnderBagPacket::new,
                OpenEnderBagPacket::handle);

        INSTANCE.registerMessage(id++,
                RemoveUpgradePacket.class,
                RemoveUpgradePacket::toBytes,
                RemoveUpgradePacket::new,
                RemoveUpgradePacket::handle);
    }

}