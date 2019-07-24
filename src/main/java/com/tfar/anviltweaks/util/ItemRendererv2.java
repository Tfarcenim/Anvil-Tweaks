package com.tfar.anviltweaks.util;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemRendererv2 extends EntityRenderer<ItemEntity> {
  private final net.minecraft.client.renderer.ItemRenderer itemRenderer;
  private final Random random = new Random();

  public ItemRendererv2(EntityRendererManager renderManagerIn, net.minecraft.client.renderer.ItemRenderer p_i46167_2_) {
    super(renderManagerIn);
    this.itemRenderer = p_i46167_2_;
    this.shadowSize = 0.15F;
    this.shadowOpaque = 0.75F;
  }

  private int transformModelCount(ItemEntity item, double p_177077_2_, double p_177077_4_, double p_177077_6_, float p_177077_8_, IBakedModel p_177077_9_) {
    ItemStack itemstack = item.getItem();
    if (itemstack.isEmpty()) {
      return 0;
    } else {
      int i = 1;
      float f2 = .5f;//p_177077_9_.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.getY();
      GlStateManager.translatef((float)p_177077_2_, (float)p_177077_4_ + 0.25F * f2, (float)p_177077_6_);
      GlStateManager.color4f(1, 1, 1, 1);
      return i;
    }
  }

  public void doRender(ItemEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
    ItemStack itemstack = entity.getItem();

    int i = itemstack.isEmpty() ? 187 : Item.getIdFromItem(itemstack.getItem()) + itemstack.getDamage();
    this.random.setSeed((long) i);

    boolean didBindTexture = false;

    if (this.bindEntityTexture(entity))
    {
      this.renderManager.textureManager.getTexture(this.getEntityTexture(entity)).setBlurMipmap(false, false);
      didBindTexture = true;
    }

    GlStateManager.enableRescaleNormal();
    GlStateManager.alphaFunc(516, 0.1F);
    GlStateManager.enableBlend();
    RenderHelper.enableStandardItemLighting();
    GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    GlStateManager.pushMatrix();
    IBakedModel ibakedmodel = this.itemRenderer.getItemModelWithOverrides(itemstack, entity.world, null);


    boolean render3D = ibakedmodel.isGui3d();

    /**
     * block.json
     * "ground": {
     *             "rotation": [ 0, 0, 0 ],
     *             "translation": [ 0, 3, 0],
     *             "scale":[ 0.25, 0.25, 0.25 ]
     *         },
     */
    GlStateManager.translated(x, y, z);
   // GlStateManager.rotatef(entity.rotationYaw, 0.0F, 1.0F, 0.0F);
   // GlStateManager.rotatef(entity.rotationPitch, 1.0F, 0.0F, 0.0F);

    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

    if (itemstack.getItem() instanceof BlockItem)
    {
    //  GlStateManager.translated(-0.005, -0.19, -0.005);
    }

    if (this.renderOutlines)
    {
      GlStateManager.enableColorMaterial();
      GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(entity));
    }
    if (render3D)
    {
      GlStateManager.pushMatrix();

      IBakedModel transformedModel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GROUND, false);
      this.itemRenderer.renderItem(itemstack, transformedModel);
      GlStateManager.popMatrix();
    }
    else
    {
      GlStateManager.pushMatrix();

      IBakedModel transformedModel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GROUND, false);
      this.itemRenderer.renderItem(itemstack, transformedModel);
      GlStateManager.popMatrix();
      GlStateManager.translatef(0.0F, 0.0F, 0.09375F);
    }

    if (this.renderOutlines)
    {
      GlStateManager.tearDownSolidRenderingTextureCombine();
      GlStateManager.disableColorMaterial();
    }

    GlStateManager.popMatrix();
    GlStateManager.disableRescaleNormal();
    GlStateManager.disableBlend();
    this.bindEntityTexture(entity);

    if (didBindTexture)
    {
      this.renderManager.textureManager.getTexture(this.getEntityTexture(entity)).restoreLastBlurMipmap();
    }

    super.doRender(entity, x, y, z, entityYaw, partialTicks);
  }

  protected ResourceLocation getEntityTexture(ItemEntity entity) {
    return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
  }

  /*==================================== FORGE START ===========================================*/

  /**
   * @return If items should spread out when rendered in 3D
   */
  public boolean shouldSpreadItems() {
    return false;
  }

  /**
   * @return If items should have a bob effect
   */
  public boolean shouldBob() {
    return false;
  }
  /*==================================== FORGE END =============================================*/
}