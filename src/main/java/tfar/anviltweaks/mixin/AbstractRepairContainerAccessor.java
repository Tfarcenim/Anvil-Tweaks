package tfar.anviltweaks.mixin;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.AbstractRepairContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractRepairContainer.class)
public interface AbstractRepairContainerAccessor {
	@Accessor IInventory getField_234643_d_();//fuck mcp and it's shitty system
}
