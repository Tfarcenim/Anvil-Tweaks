package tfar.anviltweaks;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import tfar.anviltweaks.util.AnvilHandler;

import javax.annotation.Nonnull;
import java.util.Random;

public class AnvilTile extends TileEntity  {

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

  public void save(IInventory inv) {
    handler.setStackInSlot(0,inv.getStackInSlot(0).copy());
    handler.setStackInSlot(1,inv.getStackInSlot(1).copy());
    markDirty();
  }

  public void load(IInventory inv) {
    inv.setInventorySlotContents(0,handler.getStackInSlot(0));
    inv.setInventorySlotContents(1,handler.getStackInSlot(1));
  }

  @Override
  public void read(CompoundNBT tag) {
    CompoundNBT invTag = tag.getCompound("inv");
    handler.deserializeNBT(invTag);
    super.read(tag);
  }

  @Override
  public void markDirty() {
    super.markDirty();
    world.notifyBlockUpdate(pos,getBlockState(),getBlockState(),3);
  }

  @Override
  public CompoundNBT getUpdateTag() {
    return write(new CompoundNBT());    // okay to send entire inventory on chunk load
  }

  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
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
