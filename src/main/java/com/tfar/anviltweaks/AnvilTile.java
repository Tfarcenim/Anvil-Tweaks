package com.tfar.anviltweaks;

import com.tfar.anviltweaks.util.AnvilHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AnvilTile extends TileEntity implements INamedContainerProvider {

  public AnvilHandler handler;
  public String savedName = "";

  public AnvilTile(TileEntityType<?> tileEntityType) {
    super(tileEntityType);
    this.handler = new AnvilHandler(2);
  }

  @Nonnull
  @Override
  public CompoundNBT write(CompoundNBT tag) {
    CompoundNBT compound = this.handler.serializeNBT();
    tag.put("inv", compound);
    if (savedName == null) savedName = "";
    tag.putString("savedName", savedName);
    return super.write(tag);
  }

  @Override
  public void read(CompoundNBT tag) {
    CompoundNBT invTag = tag.getCompound("inv");
    handler.deserializeNBT(invTag);
    savedName = tag.getString("savedName");
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
}
