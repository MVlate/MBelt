package MVlate.mbelt.client.events;

import MVlate.mbelt.Mbelt;
import MVlate.mbelt.client.screen.BeltHudOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Mbelt.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientForgeEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && BeltHudOverlay.displayTicks > 0) {
            BeltHudOverlay.displayTicks--;
        }
    }

}