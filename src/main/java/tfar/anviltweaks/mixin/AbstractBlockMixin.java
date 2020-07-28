package tfar.anviltweaks.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.anviltweaks.AnvilTile;
import tfar.anviltweaks.Hooks;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {

	@Inject(method = "onReplaced",at = @At("HEAD"))
	private void dropItems(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving, CallbackInfo ci){
		TileEntity tileentity = world.getTileEntity(pos);
		if (tileentity instanceof AnvilTile) {
			Hooks.dropItems((AnvilTile) tileentity,world, pos);
			// world.updateComparatorOutputLevel(pos, this);
		}
	}
}
