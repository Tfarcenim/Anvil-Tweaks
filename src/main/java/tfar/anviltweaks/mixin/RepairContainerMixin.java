package tfar.anviltweaks.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.AbstractRepairContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.IWorldPosCallable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.anviltweaks.AnvilTile;
import tfar.anviltweaks.Configs;
import net.minecraft.inventory.container.RepairContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import tfar.anviltweaks.Hooks;
import tfar.anviltweaks.RepairContainerDuck;

import javax.annotation.Nullable;

@Mixin(RepairContainer.class)
abstract class RepairContainerMixin extends AbstractRepairContainer implements RepairContainerDuck {

	private AnvilTile anvilTile;

	private boolean loading = false;

	public RepairContainerMixin(@Nullable ContainerType<?> p_i231587_1_, int p_i231587_2_, PlayerInventory p_i231587_3_, IWorldPosCallable p_i231587_4_) {
		super(p_i231587_1_, p_i231587_2_, p_i231587_3_, p_i231587_4_);
	}

	//negate prior work penalty
	@Inject(method = "getNewRepairCost",at = @At("HEAD"),cancellable = true)
	private static void removePriorWorkPenalty(int oldRepairCost, CallbackInfoReturnable<Integer> cir){
		if (!Configs.ServerConfig.prior_work_penalty.get())cir.setReturnValue(0);
	}

	@Override
	public boolean isLoading() {
		return loading;
	}

	@Override
	public boolean isClient() {
		return field_234645_f_.world.isRemote;
	}

	//change the rate at which prior work penalty scales
	@Inject(method = "getNewRepairCost",at = @At("RETURN"),cancellable = true)
	private static void scalePriorWorkPenalty(int oldRepairCost, CallbackInfoReturnable<Integer> cir){
		if (!Configs.ServerConfig.prior_work_penalty.get() && !Configs.ServerConfig.prior_work_penalty_scale.get().equals(2d)){
			cir.setReturnValue((int) (Configs.ServerConfig.prior_work_penalty_scale.get() * oldRepairCost + 1));
		}
	}

	//load blockentity data
	@Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/util/IWorldPosCallable;)V",at = @At("RETURN"))
	private void addInventory(int id, PlayerInventory playerInventory, IWorldPosCallable posCallable, CallbackInfo ci){
		posCallable.consume((world, pos) -> {
			anvilTile = (AnvilTile) world.getTileEntity(pos);
			loading = true;
			anvilTile.load(field_234643_d_);
			loading = false;
		});
	}

	@Override
	public AnvilTile getAnvilTile() {
		return anvilTile;
	}

	//allow cheap renaming
	@ModifyConstant(method = "updateRepairOutput",constant = @Constant(intValue = 39))
	private int cheapRename(int cost) {
		return Configs.ServerConfig.cheap_renaming.get() ? 1 : 39;
	}

	//change hardcoded level 40 cap
	@ModifyConstant(method = "updateRepairOutput",constant = @Constant(intValue = 40),
					slice = @Slice(from = @At(value = "INVOKE",target = "Lnet/minecraft/util/IntReferenceHolder;get()I",ordinal = 1)))
	private int uncapRepairCost(int cost) {
		return Configs.ServerConfig.repair_cost_cap.get();
	}

}
