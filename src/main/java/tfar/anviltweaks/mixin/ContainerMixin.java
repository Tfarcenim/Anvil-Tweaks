package tfar.anviltweaks.mixin;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.RepairContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.anviltweaks.Hooks;
import tfar.anviltweaks.RepairContainerDuck;

@Mixin(Container.class)
public class ContainerMixin {

	@Inject(method = "onCraftMatrixChanged",at = @At("HEAD"))
	private void saveContents(IInventory inventoryIn, CallbackInfo ci) {
		if ((Object)this instanceof RepairContainer) {
				Hooks.saveContents((RepairContainer)(Object)this,((RepairContainerDuck)this).isClient(),((RepairContainerDuck)this).isLoading());
			}
	}
}
