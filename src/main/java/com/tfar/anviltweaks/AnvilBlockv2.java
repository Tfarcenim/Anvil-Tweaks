package com.tfar.anviltweaks;

import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.IntStream;

import static net.minecraft.inventory.InventoryHelper.spawnItemStack;
import static net.minecraft.tags.BlockTags.ANVIL;

public class AnvilBlockv2 extends AnvilBlock {
  public AnvilBlockv2(Properties p_i48450_1_) {
    super(p_i48450_1_);
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  @Nullable
  public INamedContainerProvider getContainer(BlockState state, World world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    return te instanceof AnvilTile ? (INamedContainerProvider) te : null;
  }

  @Override
  public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
    if (!world.isRemote) {
      TileEntity tileEntity = world.getTileEntity(pos);
      if (tileEntity instanceof INamedContainerProvider) {
        NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getPos());
      }
    }
    return ActionResultType.SUCCESS;
  }

  @Override
  public void onReplaced(BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
    if (!newState.getBlock().isIn(ANVIL)) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof AnvilTile) {
        dropItems((AnvilTile) tileentity,worldIn, pos);
       // worldIn.updateComparatorOutputLevel(pos, this);
      }
      super.onReplaced(state, worldIn, pos, newState, isMoving);
    }
  }

  public static void dropItems(AnvilTile anvil, World world, BlockPos pos) {
    IntStream.range(0, anvil.handler.getSlots()).mapToObj(i -> anvil.handler.getStackInSlot(i)).filter(stack -> !stack.isEmpty()).forEach(stack -> spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack));
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new AnvilTile(AnvilTweaks.Stuff.anvil_tile);
  }
}
