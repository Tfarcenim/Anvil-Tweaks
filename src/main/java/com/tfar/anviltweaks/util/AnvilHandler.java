package com.tfar.anviltweaks.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public class AnvilHandler extends ItemStackHandler {
  public AnvilHandler(int i) {
    super(i);
  }

  public List<ItemStack> getContents(){
    return this.stacks;
  }



}
