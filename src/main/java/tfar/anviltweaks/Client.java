package tfar.anviltweaks;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = AnvilTweaks.MODID,bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
public class Client {
  @SubscribeEvent
  public static void doClientStuff(final FMLClientSetupEvent event) {
    ClientRegistry.bindTileEntityRenderer(AnvilTweaks.Stuff.anvil_tile, AnvilTileRenderer::new);
  }
}
