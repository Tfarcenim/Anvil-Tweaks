package com.tfar.anviltweaks;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.client.ForgeHooksClient;

import java.util.List;

public class AnvilTileSpecialRenderer extends TileEntityRenderer<AnvilTile> {

  private static ItemEntity customItem;

  private ItemRenderer itemRenderer;

  private static float[][] shifts = {{0.25F, 0.45F, 0.5F}, {0.75F, 0.45F, 0.5F}, {0.5F, 0.45F, 0.25F}, {0.5F, 0.45F, 0.75F}};


  @Override
  public void render(AnvilTile tileEntity, double x, double y, double z, float partialTicks, int destroyStage) {
    GlStateManager.enableDepthTest();
    GlStateManager.depthFunc(515);
    GlStateManager.depthMask(true);

    if (this.rendererDispatcher.renderInfo != null) {
      if (tileEntity.getDistanceSq(this.rendererDispatcher.renderInfo.getProjectedView().x, this.rendererDispatcher.renderInfo.getProjectedView().y, this.rendererDispatcher.renderInfo.getProjectedView().z) < 128d) {
      //  this.random.setSeed(254L);
        float shiftX;
        float shiftY;
        float shiftZ;
        int shift = 0;
        boolean northSouth = false;
        Direction dir = tileEntity.getBlockState().get(AnvilBlockv2.FACING);
        if (dir == Direction.NORTH || dir == Direction.SOUTH) {
          shift += 2;
          northSouth = true;
        }
        float blockScale = 0.75F;

        GlStateManager.pushMatrix();
        GlStateManager.translatef((float) x, (float) y, (float) z);

        if (customItem == null) {
          customItem = new ItemEntity(EntityType.ITEM, this.getWorld());
        }

        List<ItemStack> contents = tileEntity.handler.getContents();
        for (int i = 0, contentsSize = contents.size(); i < contentsSize; i++) {
          ItemStack item = contents.get(i);

          if (item.isEmpty()) continue;
          shiftX = shifts[shift][0];
          shiftY = shifts[shift][1];
          shiftZ = shifts[shift][2];
          shift++;
          GlStateManager.pushMatrix();
          GlStateManager.translatef(shiftX, shiftY, shiftZ);
          GlStateManager.translatef(0,  item.getItem() instanceof BlockItem ? .6f : .575f, -.125f);
          GlStateManager.rotatef(90, 1, 0, 0);
          GlStateManager.rotatef(tileEntity.angles[i], 0, 0, 1);

          //   GlStateManager.rotatef(0, .5, 0, 0);
          GlStateManager.scalef(blockScale, blockScale, blockScale);

          customItem.setItem(item);

          if (this.itemRenderer == null) {
            this.itemRenderer = new ItemRenderer(Minecraft.getInstance().textureManager, Minecraft.getInstance().getModelManager(), Minecraft.getInstance().getItemColors());
          }
          IBakedModel ibakedmodel = this.itemRenderer.getItemModelWithOverrides(item, tileEntity.getWorld(), null);
          IBakedModel transformedModel = ForgeHooksClient.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GROUND, false);

          GlStateManager.enableBlend();
          this.itemRenderer.renderItem(item, transformedModel);

          GlStateManager.popMatrix();
        }
          GlStateManager.popMatrix();
        }
      }
    }
  }

