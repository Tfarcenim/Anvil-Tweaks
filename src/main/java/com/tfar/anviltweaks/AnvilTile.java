package com.tfar.anviltweaks;

import com.tfar.anviltweaks.util.AnvilHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class AnvilTile extends TileEntity implements INamedContainerProvider {

  public AnvilHandler handler;

  public final Random rand = new Random();
  public int[] angles = {0,0};

  public AnvilTile(TileEntityType<?> tileEntityType) {
    super(tileEntityType);
    this.handler = new AnvilHandler(2);
  }

  @Nonnull
  @Override
  public CompoundNBT write(CompoundNBT tag) {
    CompoundNBT compound = this.handler.serializeNBT();
    tag.put("inv", compound);
    return super.write(tag);
  }

  @Override
  public void read(CompoundNBT tag) {
    CompoundNBT invTag = tag.getCompound("inv");
    handler.deserializeNBT(invTag);
    super.read(tag);
  }

  @Override
  public ITextComponent getDisplayName() {
    return new TranslationTextComponent("container.repair");
  }

  @Nullable
  @Override
  public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity player) {
     return new RepairContainerv2(i, world, pos, playerInventory, player);
    }

  @Override
  public void markDirty() {
    super.markDirty();
  }

  @Override
  public CompoundNBT getUpdateTag()
  {
    return write(new CompoundNBT());    // okay to send entire inventory on chunk load
  }

  @Override
  public SUpdateTileEntityPacket getUpdatePacket()
  {
    CompoundNBT nbt = new CompoundNBT();
    this.write(nbt);

    return new SUpdateTileEntityPacket(getPos(), 1, nbt);
  }

  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet)
  {
    this.read(packet.getNbtCompound());
  }
}
