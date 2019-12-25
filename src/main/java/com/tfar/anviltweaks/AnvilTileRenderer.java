package com.tfar.anviltweaks;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

public class AnvilTileRenderer extends TileEntityRenderer<AnvilTile> {

  public AnvilTileRenderer(TileEntityRendererDispatcher p_i226006_1_) {
    super(p_i226006_1_);
  }


  @Override
  public void func_225616_a_(AnvilTile tileEntity, float p_225616_2_, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int int_1, int int_2) {
    if (!tileEntity.handler.getContents().isEmpty() && this.field_228858_b_.renderInfo != null && tileEntity.getDistanceSq(this.field_228858_b_.renderInfo.getProjectedView().x, this.field_228858_b_.renderInfo.getProjectedView().y, this.field_228858_b_.renderInfo.getProjectedView().z) < 64d) {
      matrixStack.func_227861_a_(0,1.0625, 0);
      drawItemStack(tileEntity,matrixStack,0,int_1,iRenderTypeBuffer);
      drawItemStack(tileEntity,matrixStack,1,int_1,iRenderTypeBuffer);
    }
  }

  public void drawItemStack(AnvilTile tile,MatrixStack matrixStack,int index, int int_1, IRenderTypeBuffer iRenderTypeBuffer) {
    if (!tile.handler.getStackInSlot(index).isEmpty()) {
      ItemStack item = tile.handler.getStackInSlot(index);

      Direction.Axis axis = tile.getBlockState().get(AnvilBlockv2.FACING).getAxis();
      //pushmatrix
      matrixStack.func_227860_a_();
      //func_227861_a_ = translate x,y,z
      matrixStack.func_227863_a_(Vector3f.field_229178_a_.func_229187_a_(90));

      switch (axis){
        case X:
          matrixStack.func_227861_a_(.75 - .5 * index,-.5,0);break;
        case Z:
          matrixStack.func_227861_a_(.5,-.25 - .5 * index,0);
      }

      //func_227862_a = scale x,y,z
      matrixStack.func_227862_a_(0.25F, 0.25F, 0.25F);
      Minecraft.getInstance().getItemRenderer().func_229110_a_(item, ItemCameraTransforms.TransformType.FIXED, int_1, OverlayTexture.field_229196_a_, matrixStack, iRenderTypeBuffer);
      //popmatrix
      matrixStack.func_227865_b_();
    }
  }
}