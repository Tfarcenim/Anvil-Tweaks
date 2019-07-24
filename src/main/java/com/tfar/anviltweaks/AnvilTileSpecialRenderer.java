package com.tfar.anviltweaks;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

public class AnvilTileSpecialRenderer extends TileEntityRenderer<AnvilTile> {

  private ItemRenderer itemRenderer;

  private static double level = 1.015;

  private static double[][] shifts = {{0.25F, level, 0.5F}, {0.75F, level, 0.5F}, {0.5F, level, 0.25F}, {0.5F, level, 0.75F}};


  @Override
  public void render(AnvilTile tileEntity, double x, double y, double z, float partialTicks, int destroyStage) {
    if (!tileEntity.handler.getContents().isEmpty() && this.rendererDispatcher.renderInfo != null && tileEntity.getDistanceSq(this.rendererDispatcher.renderInfo.getProjectedView().x, this.rendererDispatcher.renderInfo.getProjectedView().y, this.rendererDispatcher.renderInfo.getProjectedView().z) < 128d) {

      double shiftX;
      double shiftY;
      double shiftZ;
      int shift = 0;

      Direction dir = tileEntity.getBlockState().get(AnvilBlockv2.FACING);
      if (dir == Direction.NORTH || dir == Direction.SOUTH) {
        shift += 2;
      }
      if (this.itemRenderer == null) {
        this.itemRenderer = new ItemRenderer(Minecraft.getInstance().textureManager, Minecraft.getInstance().getModelManager(), Minecraft.getInstance().getItemColors());
      }
      float blockScale = 0.75F;

      if (!tileEntity.handler.getStackInSlot(0).isEmpty()) {
        ItemStack item = tileEntity.handler.getStackInSlot(0);
        shiftX = shifts[shift][0];
        shiftY = shifts[shift][1];
        shiftZ = shifts[shift][2];
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        if (item.getItem() instanceof BlockItem)shiftY += .0775;
        GlStateManager.translated(x + shiftX, y + shiftY, z + shiftZ);
        GlStateManager.rotatef(90, 1, 0, 0);
        GlStateManager.rotatef(90 * tileEntity.angles[0], 0, 0, 1);
        switch (tileEntity.angles[0]) {
          case 0: {
            GlStateManager.translatef(0, -0.0625f, 0);
            break;
          }
          case 1: {
            GlStateManager.translatef(0, -0.125f, 0);
            break;
          }
          case 2: {
            GlStateManager.translatef(0, -0.0625f, 0);
            break;
          }
          case 3: {
            GlStateManager.translatef(0, -0.0625f, 0);
            break;
          }
        }
        GlStateManager.scalef(blockScale, blockScale, blockScale);

        this.itemRenderer.renderItem(item, ItemCameraTransforms.TransformType.GROUND);
        GlStateManager.popMatrix();
      }
      if (!tileEntity.handler.getStackInSlot(1).isEmpty()) {
        ItemStack item = tileEntity.handler.getStackInSlot(1);
        shift++;
        shiftX = shifts[shift][0];
        shiftY = shifts[shift][1];
        shiftZ = shifts[shift][2];
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        if (item.getItem() instanceof BlockItem)shiftY += .0775;
          GlStateManager.translated(x + shiftX, y + shiftY, z + shiftZ);
        GlStateManager.rotatef(90, 1, 0, 0);
        GlStateManager.rotatef(90 * tileEntity.angles[1], 0, 0, 1);
        switch (tileEntity.angles[1]) {
          case 0: {
            GlStateManager.translatef(0, -0.0625f, 0);
            break;
          }
          case 1: {
            GlStateManager.translatef(0, -0.125f, 0);
            break;
          }
          case 2: {
            GlStateManager.translatef(0, -0.0625f, 0);
            break;
          }
          case 3: {
            GlStateManager.translatef(0, -0.0625f, 0);
            break;
          }
        }
        GlStateManager.scalef(blockScale, blockScale, blockScale);

        this.itemRenderer.renderItem(item, ItemCameraTransforms.TransformType.GROUND);
        GlStateManager.popMatrix();
      }
    }
  }
}


