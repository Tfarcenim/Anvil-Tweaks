package com.tfar.anviltweaks;

import com.mojang.blaze3d.platform.GlStateManager;
import com.tfar.anviltweaks.network.Message;
import com.tfar.anviltweaks.network.PacketAnvilRename;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnvilScreenv2 extends ContainerScreen<RepairContainerv2> implements IContainerListener {
  private static final ResourceLocation ANVIL_RESOURCE = new ResourceLocation("textures/gui/container/anvil.png");
  private TextFieldWidget nameField;

  public AnvilScreenv2(RepairContainerv2 p_i51103_1_, PlayerInventory p_i51103_2_, ITextComponent p_i51103_3_) {
    super(p_i51103_1_, p_i51103_2_, p_i51103_3_);
  }

  protected void init() {
    super.init();
    this.minecraft.keyboardListener.enableRepeatEvents(true);
    int lvt_1_1_ = (this.width - this.xSize) / 2;
    int lvt_2_1_ = (this.height - this.ySize) / 2;
    this.nameField = new TextFieldWidget(this.font, lvt_1_1_ + 62, lvt_2_1_ + 24, 103, 12, I18n.format("container.repair"));
    this.nameField.setCanLoseFocus(false);
    this.nameField.changeFocus(true);
    this.nameField.setTextColor(-1);
    this.nameField.setDisabledTextColour(-1);
    this.nameField.setEnableBackgroundDrawing(false);
    this.nameField.setMaxStringLength(35);
    this.nameField.func_212954_a(this::func_214075_a);
    this.children.add(this.nameField);
    this.container.addListener(this);
    this.func_212928_a(this.nameField);
  }

  public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
    String lvt_4_1_ = this.nameField.getText();
    this.init(p_resize_1_, p_resize_2_, p_resize_3_);
    this.nameField.setText(lvt_4_1_);
  }

  public void removed() {
    super.removed();
    this.minecraft.keyboardListener.enableRepeatEvents(false);
    this.container.removeListener(this);
  }

  public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
    if (p_keyPressed_1_ == 256) {
      this.minecraft.player.closeScreen();
    }

    return this.nameField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) || this.nameField.func_212955_f() || super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
  }

  protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
    GlStateManager.disableLighting();
    GlStateManager.disableBlend();

    this.font.drawString(this.title.getFormattedText(), 60.0F, 6.0F, 0x404040);
    int lvt_3_1_ = this.container.func_216976_f();
    if (!container.repairedItemName.equals(this.nameField.getText()) && !nameField.getText().isEmpty()){
      this.container.updateItemName(this.nameField.getText());
      Message.INSTANCE.sendToServer(new PacketAnvilRename(this.nameField.getText()));

    }
    if (lvt_3_1_ > 0) {
      int lvt_4_1_ = 8453920;
      boolean lvt_5_1_ = true;
      String lvt_6_1_ = I18n.format("container.repair.cost", lvt_3_1_);
      if (lvt_3_1_ >= Configs.ServerConfig.repair_cost_cap.get() && !this.minecraft.player.abilities.isCreativeMode) {
        lvt_6_1_ = I18n.format("container.repair.expensive");
        lvt_4_1_ = 16736352;
      } else if (!this.container.getSlot(2).getHasStack()) {
        lvt_5_1_ = false;
      } else if (!this.container.getSlot(2).canTakeStack(this.playerInventory.player)) {
        lvt_4_1_ = 16736352;
      }

      if (lvt_5_1_) {
        int lvt_7_1_ = this.xSize - 8 - this.font.getStringWidth(lvt_6_1_) - 2;
        fill(lvt_7_1_ - 2, 67, this.xSize - 8, 79, 0x4f000000);
        this.font.drawStringWithShadow(lvt_6_1_, (float)lvt_7_1_, 69.0F, lvt_4_1_);
      }
    }

    GlStateManager.enableLighting();
  }

  private void func_214075_a(String newName) {
    if (!newName.isEmpty()) {
      String name = newName;
      Slot slot = this.container.getSlot(0);
      if (slot.getHasStack() && !slot.getStack().hasDisplayName() && newName.equals(slot.getStack().getDisplayName().getString())) {
        name = "";
      }

      this.container.updateItemName(name);
      Message.INSTANCE.sendToServer(new PacketAnvilRename(name));
    }
  }

  public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
    this.renderBackground();
    super.render(p_render_1_, p_render_2_, p_render_3_);
    this.renderHoveredToolTip(p_render_1_, p_render_2_);
    GlStateManager.disableLighting();
    GlStateManager.disableBlend();
    this.nameField.render(p_render_1_, p_render_2_, p_render_3_);
  }

  protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.minecraft.getTextureManager().bindTexture(ANVIL_RESOURCE);
    int lvt_4_1_ = (this.width - this.xSize) / 2;
    int lvt_5_1_ = (this.height - this.ySize) / 2;
    this.blit(lvt_4_1_, lvt_5_1_, 0, 0, this.xSize, this.ySize);
    this.blit(lvt_4_1_ + 59, lvt_5_1_ + 20, 0, this.ySize + (this.container.getSlot(0).getHasStack() ? 0 : 16), 110, 16);
    if ((this.container.getSlot(0).getHasStack() || this.container.getSlot(1).getHasStack()) && !this.container.getSlot(2).getHasStack()) {
      this.blit(lvt_4_1_ + 99, lvt_5_1_ + 45, this.xSize, 0, 28, 21);
    }

  }

  public void sendAllContents(Container p_71110_1_, NonNullList<ItemStack> p_71110_2_) {
    this.sendSlotContents(p_71110_1_, 0, p_71110_1_.getSlot(0).getStack());
  }

  public void sendSlotContents(Container p_71111_1_, int p_71111_2_, ItemStack toRename) {
    if (p_71111_2_ == 0) {
      String s = toRename.isEmpty() ? "" : container.tileEntity.savedName != null && !container.tileEntity.savedName.isEmpty() ? container.tileEntity.savedName : toRename.getDisplayName().getString();
      this.nameField.setText(s);
      this.nameField.setEnabled(!toRename.isEmpty());

      this.container.updateItemName(s);
    }
  }

  public void sendWindowProperty(Container p_71112_1_, int p_71112_2_, int p_71112_3_) {
  }
}
