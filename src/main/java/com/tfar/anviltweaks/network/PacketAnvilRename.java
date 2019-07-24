package com.tfar.anviltweaks.network;

import com.tfar.anviltweaks.RepairContainerv2;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketAnvilRename {

  private String name;
  private int length;

  public PacketAnvilRename() {}

  public PacketAnvilRename(String newName) {
    this.name = newName;
  }

 public PacketAnvilRename(PacketBuffer buf) {
    length = buf.readInt();
   name = buf.readString(length);
  }

  public void encode(PacketBuffer buf) {
    buf.writeInt(name.length());
    buf.writeString(name);
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      if (ctx.get() == null || ctx.get().getSender() == null)return;
      Container anvil = ctx.get().getSender().openContainer;
      if (anvil instanceof RepairContainerv2){
        ((RepairContainerv2)anvil).updateItemName(name);}
    });
    ctx.get().setPacketHandled(true);
  }
}
