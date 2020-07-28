package tfar.anviltweaks.mixin;

import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import org.spongepowered.asm.mixin.Mixin;
import tfar.anviltweaks.AnvilTile;
import tfar.anviltweaks.AnvilTweaks;

import javax.annotation.Nullable;

@Mixin(AnvilBlock.class)
public class AnvilBlockMixin extends FallingBlock {

	//these come from default interfaces
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new AnvilTile(AnvilTweaks.Stuff.anvil_tile);
	}


	public AnvilBlockMixin(Properties properties) {
		super(properties);
	}
}
