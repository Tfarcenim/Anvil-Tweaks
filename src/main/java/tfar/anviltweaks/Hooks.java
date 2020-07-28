package tfar.anviltweaks;

import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tfar.anviltweaks.mixin.AbstractRepairContainerAccessor;

import java.util.stream.IntStream;

import static net.minecraft.inventory.InventoryHelper.spawnItemStack;

public class Hooks {
	public static void dropItems(AnvilTile anvil, World world, BlockPos pos) {
		IntStream.range(0, anvil.handler.getSlots()).mapToObj(i -> anvil.handler.getStackInSlot(i)).filter(stack -> !stack.isEmpty()).forEach(stack -> spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack));
	}

	public static void saveContents(RepairContainer container,boolean client,boolean loading) {
		if (!client && !loading)
			((RepairContainerDuck)container).getAnvilTile().save(((AbstractRepairContainerAccessor)container).getField_234643_d_());//don't save contents until they're fully deserialized
	}
}
