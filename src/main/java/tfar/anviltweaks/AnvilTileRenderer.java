package tfar.anviltweaks;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.AnvilBlock;
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
  public void render(AnvilTile tileEntity, float p_225616_2_, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int int_1, int int_2) {
    if (!tileEntity.handler.getContents().isEmpty() && this.renderDispatcher.renderInfo != null
						&& tileEntity.getDistanceSq(this.renderDispatcher.renderInfo.getProjectedView().x, this.renderDispatcher.renderInfo.getProjectedView().y, this.renderDispatcher.renderInfo.getProjectedView().z) < 64d) {
      matrixStack.translate(0,1.0625, 0);
      drawItemStack(tileEntity,matrixStack,0,int_1,iRenderTypeBuffer);
      drawItemStack(tileEntity,matrixStack,1,int_1,iRenderTypeBuffer);
    }
  }

  public void drawItemStack(AnvilTile tile,MatrixStack matrixStack,int index, int int_1, IRenderTypeBuffer iRenderTypeBuffer) {
    if (!tile.handler.getStackInSlot(index).isEmpty()) {
      ItemStack item = tile.handler.getStackInSlot(index);

      Direction.Axis axis = tile.getBlockState().get(AnvilBlock.FACING).getAxis();
      //pushmatrix
      matrixStack.push();
      //func_227861_a_ = translate x,y,z
      matrixStack.rotate(Vector3f.XN.rotationDegrees(90));

      switch (axis){
        case X:
          matrixStack.translate(.75 - .5 * index,-.5,0);break;
        case Z:
          matrixStack.translate(.5,-.25 - .5 * index,0);
      }

      //func_227862_a = scale x,y,z
      matrixStack.scale(0.25F, 0.25F, 0.25F);
      Minecraft.getInstance().getItemRenderer().renderItem(item, ItemCameraTransforms.TransformType.FIXED, int_1, OverlayTexture.DEFAULT_LIGHT, matrixStack, iRenderTypeBuffer);
      //popmatrix
      matrixStack.pop();
    }
  }
}