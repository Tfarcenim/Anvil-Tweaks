package com.tfar.anviltweaks;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class AnvilBlockItem extends BlockItem {
  public AnvilBlockItem(Block blockIn, Properties builder) {
    super(blockIn, builder);
  }

  @Nullable
  @Override
  public String getCreatorModId(ItemStack itemStack) {
    return AnvilTweaks.MODID;
  }
}
