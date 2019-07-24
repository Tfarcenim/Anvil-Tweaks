package com.tfar.anviltweaks;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class RepairContainerv2 extends Container {

  protected IItemHandler playerInventory;
  private final IInventory outputSlot = new CraftResultInventory();
  private final IntReferenceHolder maximumCost = IntReferenceHolder.single();
  private final World field_216980_g;
  public AnvilTile tileEntity;
  public int materialCost;
  String repairedItemName;
  private final PlayerEntity player;
  private final BlockPos pos;

  public RepairContainerv2(int windowId, World world, BlockPos readBlockPos, PlayerInventory inv, PlayerEntity player) {

    super(AnvilTweaks.Stuff.anvil_container_v2, windowId);
    this.field_216980_g = world;
    this.player = player;
    this.pos = readBlockPos;
    this.trackInt(this.maximumCost);
    this.playerInventory = new InvWrapper(inv);
    tileEntity = (AnvilTile) world.getTileEntity(readBlockPos);
    this.addSlot(new SlotItemHandler(tileEntity.handler, 0, 27, 47){
      @Override
      public void onSlotChanged() {
        if (this.getStack().isEmpty())repairedItemName = "";
        updateRepairOutput();
      }
    });
    this.addSlot(new SlotItemHandler(tileEntity.handler, 1, 76, 47){
      @Override
      public void onSlotChanged() {
        updateRepairOutput();
      }
    });
    this.addSlot(new Slot(this.outputSlot, 2, 134, 47) {
      /**
       * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
       */
      public boolean isItemValid(ItemStack stack) {
        return false;
      }

      /**
       * Return whether this slot's stack can be taken from this slot.
       */
      public boolean canTakeStack(PlayerEntity playerIn) {
        return (playerIn.abilities.isCreativeMode || playerIn.experienceLevel >= RepairContainerv2.this.maximumCost.get()) && RepairContainerv2.this.maximumCost.get() > 0 && this.getHasStack();
      }

      public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
        if (!thePlayer.abilities.isCreativeMode) {
          thePlayer.addExperienceLevel(-RepairContainerv2.this.maximumCost.get());
        }

        float breakChance = net.minecraftforge.common.ForgeHooks.onAnvilRepair(thePlayer, stack, tileEntity.handler.getStackInSlot(0), tileEntity.handler.getStackInSlot(1));

        tileEntity.handler.setStackInSlot(0, ItemStack.EMPTY);
        if (RepairContainerv2.this.materialCost > 0) {
          ItemStack itemstack = tileEntity.handler.getStackInSlot(1);
          if (!itemstack.isEmpty() && itemstack.getCount() > RepairContainerv2.this.materialCost) {
            itemstack.shrink(RepairContainerv2.this.materialCost);
            tileEntity.handler.setStackInSlot(1, itemstack);
          } else {
            tileEntity.handler.setStackInSlot(1, ItemStack.EMPTY);
          }
        } else {
          tileEntity.handler.setStackInSlot(1, ItemStack.EMPTY);
        }

        RepairContainerv2.this.maximumCost.set(0);
          BlockState blockstate = world.getBlockState(readBlockPos);
          if (!thePlayer.abilities.isCreativeMode && blockstate.isIn(BlockTags.ANVIL) && thePlayer.getRNG().nextFloat() < breakChance) {
            BlockState blockstate1 = AnvilBlockv2.damage(blockstate);
            if (blockstate1 == null) {
              world.removeBlock(readBlockPos, false);
              world.playEvent(1029, readBlockPos, 0);
            } else {
              world.setBlockState(readBlockPos, blockstate1, 2);
              world.playEvent(1030, readBlockPos, 0);
            }
          } else {
            world.playEvent(1030, readBlockPos, 0);
          }

        return stack;
      }
    });

    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
      }
    }

    for (int k = 0; k < 9; ++k) {
      this.addSlot(new Slot(inv, k, 8 + k * 18, 142));
    }
   // updateRepairOutput();
  }



  /**
   * Callback for when the crafting matrix is changed.
   */
  public void onCraftMatrixChanged(IInventory inventoryIn) {
    super.onCraftMatrixChanged(inventoryIn);
  //  if (inventoryIn == this.tileEntity.handler) {
      this.updateRepairOutput();
   // }

  }

  /**
   * called when the Anvil Input Slot changes, calculates the new result and puts it in the output slot
   */
  public void updateRepairOutput() {
    ItemStack itemstack = tileEntity.handler.getStackInSlot(0);
    this.maximumCost.set(1);
    int i = 0;
    int j = 0;
    int k = 0;
    if (itemstack.isEmpty()) {
      this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
      this.maximumCost.set(0);
    } else {
      ItemStack itemstack1 = itemstack.copy();
      ItemStack itemstack2 = tileEntity.handler.getStackInSlot(1);
      Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack1);
      j = j + itemstack.getRepairCost() + (itemstack2.isEmpty() ? 0 : itemstack2.getRepairCost());
      this.materialCost = 0;
      boolean flag = false;

      if (!itemstack2.isEmpty()) {
        if (!onAnvilChange(this, itemstack, itemstack2, outputSlot, this.repairedItemName, j)) return;
        flag = itemstack2.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(itemstack2).isEmpty();
        if (itemstack1.isDamageable() && itemstack1.getItem().getIsRepairable(itemstack, itemstack2)) {
          int l2 = Math.min(itemstack1.getDamage(), itemstack1.getMaxDamage() / 4);
          if (l2 <= 0) {
            this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
            this.maximumCost.set(0);
            return;
          }

          int i3;
          for (i3 = 0; l2 > 0 && i3 < itemstack2.getCount(); ++i3) {
            int j3 = itemstack1.getDamage() - l2;
            itemstack1.setDamage(j3);
            ++i;
            l2 = Math.min(itemstack1.getDamage(), itemstack1.getMaxDamage() / 4);
          }

          this.materialCost = i3;
        } else {
          if (!flag && (itemstack1.getItem() != itemstack2.getItem() || !itemstack1.isDamageable())) {
            this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
            this.maximumCost.set(0);
            return;
          }

          if (itemstack1.isDamageable() && !flag) {
            int l = itemstack.getMaxDamage() - itemstack.getDamage();
            int i1 = itemstack2.getMaxDamage() - itemstack2.getDamage();
            int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
            int k1 = l + j1;
            int l1 = itemstack1.getMaxDamage() - k1;
            if (l1 < 0) {
              l1 = 0;
            }

            if (l1 < itemstack1.getDamage()) {
              itemstack1.setDamage(l1);
              i += 2;
            }
          }

          Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack2);
          boolean flag2 = false;
          boolean flag3 = false;

          for (Enchantment enchantment1 : map1.keySet()) {
            if (enchantment1 != null) {
              int i2 = map.containsKey(enchantment1) ? map.get(enchantment1) : 0;
              int j2 = map1.get(enchantment1);
              j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
              boolean flag1 = enchantment1.canApply(itemstack);
              if (this.player.abilities.isCreativeMode || itemstack.getItem() == Items.ENCHANTED_BOOK) {
                flag1 = true;
              }

              for (Enchantment enchantment : map.keySet()) {
                if (enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment)) {
                  flag1 = false;
                  ++i;
                }
              }

              if (!flag1) {
                flag3 = true;
              } else {
                flag2 = true;
                if (j2 > enchantment1.getMaxLevel()) {
                  j2 = enchantment1.getMaxLevel();
                }

                map.put(enchantment1, j2);
                int k3 = 0;
                switch (enchantment1.getRarity()) {
                  case COMMON:
                    k3 = 1;
                    break;
                  case UNCOMMON:
                    k3 = 2;
                    break;
                  case RARE:
                    k3 = 4;
                    break;
                  case VERY_RARE:
                    k3 = 8;
                }

                if (flag) {
                  k3 = Math.max(1, k3 / 2);
                }

                i += k3 * j2;
                if (itemstack.getCount() > 1) {
                  i = 40;
                }
              }
            }
          }

          if (flag3 && !flag2) {
            this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
            this.maximumCost.set(0);
            return;
          }
        }
      }

      if (StringUtils.isBlank(this.repairedItemName)) {
        if (itemstack.hasDisplayName()) {
          k = 1;
          i += k;
          itemstack1.clearCustomName();
        }
      } else if (!this.repairedItemName.equals(itemstack.getDisplayName().getString())) {
        k = 1;
        i += k;
        itemstack1.setDisplayName(new StringTextComponent(this.repairedItemName));
      }
      if (flag && !itemstack1.isBookEnchantable(itemstack2)) itemstack1 = ItemStack.EMPTY;

      this.maximumCost.set(j + i);
      if (i <= 0) {
        itemstack1 = ItemStack.EMPTY;
      }

      if (k == i && k > 0 && this.maximumCost.get() >= 40) {
        this.maximumCost.set(39);
      }

      if (this.maximumCost.get() >= 40 && !this.player.abilities.isCreativeMode) {
        itemstack1 = ItemStack.EMPTY;
      }

      if (!itemstack1.isEmpty()) {
        int k2 = itemstack1.getRepairCost();
        if (!itemstack2.isEmpty() && k2 < itemstack2.getRepairCost()) {
          k2 = itemstack2.getRepairCost();
        }

        if (k != i || k == 0) {
          k2 = func_216977_d(k2);
        }

        itemstack1.setRepairCost(k2);
        EnchantmentHelper.setEnchantments(map, itemstack1);
      }

      this.outputSlot.setInventorySlotContents(0, itemstack1);
      this.detectAndSendChanges();
    }
  }

  public static int func_216977_d(int p_216977_0_) {
    return p_216977_0_ * 2 + 1;
  }

  /**
   * Called when the container is closed.
   */
  public void onContainerClosed(PlayerEntity playerIn) {
    tileEntity.savedName = this.repairedItemName;
  }

  /**
   * Determines whether supplied player can use this container
   */
  public boolean canInteractWith(PlayerEntity playerIn) {
    return  field_216980_g.getBlockState(pos)
            .isIn(BlockTags.ANVIL) && playerIn.getDistanceSq(pos.getX() + 0.5D,
            pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64;
  }

  /**
   * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
   * inventory and the other inventory(s).
   */
  public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.inventorySlots.get(index);
    if (slot != null && slot.getHasStack()) {
      ItemStack itemstack1 = slot.getStack();
      itemstack = itemstack1.copy();
      if (index == 2) {
        if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
          return ItemStack.EMPTY;
        }

        slot.onSlotChange(itemstack1, itemstack);
      } else if (index != 0 && index != 1) {
        if (index < 39 && !this.mergeItemStack(itemstack1, 0, 2, false)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
        return ItemStack.EMPTY;
      }

      if (itemstack1.isEmpty()) {
        slot.putStack(ItemStack.EMPTY);
      } else {
        slot.onSlotChanged();
      }

      if (itemstack1.getCount() == itemstack.getCount()) {
        return ItemStack.EMPTY;
      }

      slot.onTake(playerIn, itemstack1);
    }

    return itemstack;
  }

  /**
   * used by the Anvil GUI to update the Item Name being typed by the player
   */
  public void updateItemName(String newName) {
    this.repairedItemName = newName;
    if (this.getSlot(2).getHasStack()) {
      ItemStack itemstack = this.getSlot(2).getStack();
      if (StringUtils.isBlank(newName)) {
        itemstack.setDisplayName(new StringTextComponent(this.tileEntity.savedName));
      } else {
        itemstack.setDisplayName(new StringTextComponent(this.repairedItemName));
      }
    }
    this.updateRepairOutput();
  }

  @OnlyIn(Dist.CLIENT)
  public int func_216976_f() {
    return this.maximumCost.get();
  }

  /** does nothing*/
  public void setMaximumCost(int value) {
    this.maximumCost.set(value);
  }

  public static boolean onAnvilChange(RepairContainerv2 container, ItemStack left, ItemStack right, IInventory outputSlot, String name, int baseCost) {
    AnvilUpdateEvent e = new AnvilUpdateEvent(left, right, name, baseCost);
    if (MinecraftForge.EVENT_BUS.post(e)) return false;
    if (e.getOutput().isEmpty()) return true;

    outputSlot.setInventorySlotContents(0, e.getOutput());
    container.maximumCost.set(e.getCost());
    container.materialCost = e.getMaterialCost();
    return false;
  }
}

