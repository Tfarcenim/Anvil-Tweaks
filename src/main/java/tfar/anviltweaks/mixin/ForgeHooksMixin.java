package tfar.anviltweaks.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.anviltweaks.AnvilTweaks;

@Mixin(ForgeHooks.class)
public class ForgeHooksMixin {
	@Inject(method = "getDefaultCreatorModId",at = @At("RETURN"),remap = false,cancellable = true)
	private static void changeCreatorId(ItemStack itemStack, CallbackInfoReturnable<String> cir){
		if (itemStack.getItem() == Items.ANVIL || itemStack.getItem() == Items.CHIPPED_ANVIL
						|| itemStack.getItem() == Items.DAMAGED_ANVIL){
			cir.setReturnValue(AnvilTweaks.MODID);
		}
	}
}
