package tfar.anviltweaks.mixin;

import tfar.anviltweaks.Configs;
import net.minecraft.client.gui.screen.inventory.AnvilScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin {
	@ModifyConstant(method = "drawGuiContainerForegroundLayer",constant = @Constant(intValue = 40))
	private int uncapRepairCost(int value){
		return Configs.ServerConfig.repair_cost_cap.get();
	}
}
