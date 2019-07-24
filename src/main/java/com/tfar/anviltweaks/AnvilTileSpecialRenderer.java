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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

import java.util.Random;

public class AnvilTileSpecialRenderer<T extends TileEntity> extends TileEntityRenderer<T> {

  private static ItemEntity customItem;

  private Random random = new Random();

  private ItemRenderer itemRenderer;

  private static float[][] shifts = {{0.3F, 0.45F, 0.5F}, {0.7F, 0.45F, 0.5F}, {0.5F, 0.45F, 0.3F}, {0.5F, 0.45F, 0.7F}};


  @Override
  public void render(T tileEntity, double x, double y, double z, float partialTicks, int destroyStage) {
    GlStateManager.enableDepthTest();
    GlStateManager.depthFunc(515);
    GlStateManager.depthMask(true);

    AnvilTile anvilTile = (AnvilTile) tileEntity;

    if (this.rendererDispatcher.renderInfo != null) {
      if (anvilTile.getDistanceSq(this.rendererDispatcher.renderInfo.getProjectedView().x, this.rendererDispatcher.renderInfo.getProjectedView().y, this.rendererDispatcher.renderInfo.getProjectedView().z) < 128d) {
        this.random.setSeed(254L);
        float shiftX;
        float shiftY;
        float shiftZ;
        int shift = 0;
        Direction dir = anvilTile.getBlockState().get(AnvilBlockv2.FACING);
        if (dir == Direction.NORTH || dir == Direction.SOUTH) shift += 2;
        float blockScale = 0.75F;

        GlStateManager.pushMatrix();
        GlStateManager.translatef((float) x, (float) y, (float) z);

        if (customItem == null) {
          customItem = new ItemEntity(EntityType.ITEM, this.getWorld());
        }

        for (ItemStack item : anvilTile.handler.getContents()) {

          if (item.isEmpty()) continue;

          shiftX = shifts[shift][0];
          shiftY = shifts[shift][1];
          shiftZ = shifts[shift][2];
          shift++;

          GlStateManager.pushMatrix();
          GlStateManager.translatef(shiftX, shiftY, shiftZ);
          GlStateManager.translatef(-.0f, 0, -.1f);
          GlStateManager.translatef(0, item.getItem() instanceof BlockItem ? .6f :.575f, 0);
          GlStateManager.rotatef(90, 90, 0, 0);
          GlStateManager.scalef(blockScale, blockScale, blockScale);

          customItem.setItem(item);

          if (this.itemRenderer == null) {
            this.itemRenderer = new ItemRenderer(Minecraft.getInstance().textureManager, Minecraft.getInstance().getModelManager(),Minecraft.getInstance().getItemColors());
              }
          IBakedModel ibakedmodel = this.itemRenderer.getItemModelWithOverrides(item, tileEntity.getWorld(), null);
          IBakedModel transformedModel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GROUND, false);

          GlStateManager.enableBlend();
          this.itemRenderer.renderItem(item, transformedModel);

            GlStateManager.popMatrix();
          }
          GlStateManager.popMatrix();
        }
      }
    }
  }

