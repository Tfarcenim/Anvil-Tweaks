package com.tfar.anviltweaks;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.client.ForgeHooksClient;

import java.util.List;

public class AnvilTileSpecialRenderer extends TileEntityRenderer<AnvilTile> {

  private ItemRenderer itemRenderer;

  private static float[][] shifts = {{0.25F, 1.060F, 0.5F}, {0.75F, 1.060F, 0.5F}, {0.5F, 1.060F, 0.25F}, {0.5F, 1.060F, 0.75F}};


  @Override
  public void render(AnvilTile tileEntity, double x, double y, double z, float partialTicks, int destroyStage) {
    if (!tileEntity.handler.getContents().isEmpty() && this.rendererDispatcher.renderInfo != null && tileEntity.getDistanceSq(this.rendererDispatcher.renderInfo.getProjectedView().x, this.rendererDispatcher.renderInfo.getProjectedView().y, this.rendererDispatcher.renderInfo.getProjectedView().z) < 128d) {
      float shiftX;
      float shiftY;
      float shiftZ;
      int shift = 0;
      Direction dir = tileEntity.getBlockState().get(AnvilBlockv2.FACING);
      if (dir == Direction.NORTH || dir == Direction.SOUTH) {
        shift += 2;
      }
      float blockScale = 0.75F;

      GlStateManager.translated(x,y,z);

      List<ItemStack> contents = tileEntity.handler.getContents();
      for (int i = 0, contentsSize = contents.size(); i < contentsSize; i++) {
        ItemStack item = contents.get(i);

        if (item.isEmpty()) continue;

        shiftX = shifts[shift][0];
        shiftY = shifts[shift][1];
        shiftZ = shifts[shift][2];
        shift++;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.translatef(shiftX, shiftY, shiftZ);
        GlStateManager.rotatef(90, 1, 0, 0);
        GlStateManager.rotatef(90 * tileEntity.angles[i], 0, 0, 1);
        switch (tileEntity.angles[i]) {
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

        if (this.itemRenderer == null) {
          this.itemRenderer = new ItemRenderer(Minecraft.getInstance().textureManager, Minecraft.getInstance().getModelManager(), Minecraft.getInstance().getItemColors());
        }
        IBakedModel ibakedmodel = this.itemRenderer.getItemModelWithOverrides(item, tileEntity.getWorld(), null);
        IBakedModel transformedModel = ForgeHooksClient.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GROUND, false);
        this.itemRenderer.renderItem(item, transformedModel);
        GlStateManager.popMatrix();
      }
    }
  }
}

