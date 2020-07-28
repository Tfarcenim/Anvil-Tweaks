package tfar.anviltweaks.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.AbstractRepairContainer;
import net.minecraft.inventory.container.RepairContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractRepairContainer.class)
public class AbstractRepairContainerMixin {

	@Inject(method = "onContainerClosed",at = @At("HEAD"),cancellable = true)
	private void noDrop(PlayerEntity playerIn, CallbackInfo ci) {
		if ((Object)this instanceof RepairContainer)ci.cancel();
	}
}
