package com.tfar.anviltweaks.util;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Random;

public class InventoryHelper {
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
}