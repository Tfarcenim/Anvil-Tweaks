package com.tfar.anviltweaks;

import com.tfar.anviltweaks.util.InventoryHelper;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AnvilBlockv2 extends AnvilBlock {
  public AnvilBlockv2(Properties p_i48450_1_) {
    super(p_i48450_1_);
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public INamedContainerProvider getContainer(BlockState state, World world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    return te instanceof AnvilTile ? (INamedContainerProvider) te : null;
  }

  @Override
  public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
    if (!world.isRemote) {
      TileEntity tileEntity = world.getTileEntity(pos);
      if (tileEntity instanceof INamedContainerProvider) {
        NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getPos());
      } else {
        throw new IllegalStateException("Our named container provider is missing!");
      }
    }
    return true;
  }

  @Nullable
  public static BlockState damage(BlockState p_196433_0_) {
    Block lvt_1_1_ = p_196433_0_.getBlock();
    if (lvt_1_1_ == AnvilTweaks.Anvils.anvil) {
      return AnvilTweaks.Anvils.chipped_anvil.getDefaultState().with(FACING, p_196433_0_.get(FACING));
    } else {
      return lvt_1_1_ == AnvilTweaks.Anvils.chipped_anvil ? AnvilTweaks.Anvils.damaged_anvil.getDefaultState().with(FACING, p_196433_0_.get(FACING)) : null;
    }
  }

  protected void onStartFalling(FallingBlockEntity p_149829_1_) {
    super.onStartFalling(p_149829_1_);

  }

  @Override
  public void onReplaced(BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
    if (state.getBlock() != newState.getBlock()) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof AnvilTile) {
        dropItems((AnvilTile) tileentity,worldIn, pos);
        worldIn.updateComparatorOutputLevel(pos, this);
      }
      super.onReplaced(state, worldIn, pos, newState, isMoving);
    }
  }

  public static void dropItems(AnvilTile barrel, World world, BlockPos pos) {

    for (int i = 0; i < barrel.handler.getSlots(); ++i) {
      ItemStack stack = barrel.handler.getStackInSlot(i);

      if (!stack.isEmpty()) {
        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
      }
    }
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new AnvilTile(AnvilTweaks.Stuff.anvil_tile);
  }
}
