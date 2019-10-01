package com.tfar.anviltweaks;

import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
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
import java.util.Random;
import java.util.stream.IntStream;

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

  protected void onStartFalling(FallingBlockEntity p_149829_1_) {
    super.onStartFalling(p_149829_1_);

  }

  @Override
  public void onReplaced(BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
    if (!state.getBlock().isIn(ANVIL)) {
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

  private static final Random RANDOM = new Random();

  public static void spawnItemStack(World worldIn, double x, double y, double z, ItemStack stack) {
    double d0 = EntityType.ITEM.getWidth();
    double d1 = 1.0D - d0;
    double d2 = d0 / 2.0D;
    double d3 = Math.floor(x) + RANDOM.nextDouble() * d1 + d2;
    double d4 = Math.floor(y) + RANDOM.nextDouble() * d1;
    double d5 = Math.floor(z) + RANDOM.nextDouble() * d1 + d2;

    while(!stack.isEmpty()) {
      ItemEntity itementity = new ItemEntity(worldIn, d3, d4, d5, stack.split(RANDOM.nextInt(21) + 10));
      float f = 0.05F;
      itementity.setMotion(RANDOM.nextGaussian() * f, RANDOM.nextGaussian() * f + 0.2, RANDOM.nextGaussian() * f);
      worldIn.addEntity(itementity);
    }
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new AnvilTile(AnvilTweaks.Stuff.anvil_tile);
  }
}
