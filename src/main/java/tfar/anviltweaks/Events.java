package tfar.anviltweaks;

import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Events {
	@SubscribeEvent
	public static void anvil(AnvilRepairEvent e) {
		if (!Configs.ServerConfig.damageable.get()){
			e.setBreakChance(0);
		} else {
			e.setBreakChance(Configs.ServerConfig.damage_chance.get().floatValue());
		}
	}
}
