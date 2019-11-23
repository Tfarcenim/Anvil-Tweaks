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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

import static com.tfar.anviltweaks.Configs.ServerConfig.*;

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
        tileEntity.angles[0] = tileEntity.rand.nextInt(4);
      }
    });
    this.addSlot(new SlotItemHandler(tileEntity.handler, 1, 76, 47){
      @Override
      public void onSlotChanged() {
        updateRepairOutput();
        tileEntity.angles[1] = tileEntity.rand.nextInt(4);
      }
    });
    this.addSlot(new Slot(this.outputSlot, 2, 134, 47) {
      /**
       * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
       */
      @Override
      public boolean isItemValid(ItemStack stack) {
        return false;
      }

      /**
       * Return whether this slot's stack can be taken from this slot.
       */
      @Override
      public boolean canTakeStack(PlayerEntity playerIn) {
        return (playerIn.abilities.isCreativeMode || playerIn.experienceLevel >= RepairContainerv2.this.maximumCost.get()) && RepairContainerv2.this.maximumCost.get() > 0 && this.getHasStack();
      }

      @Override
      public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
        if (!thePlayer.abilities.isCreativeMode) {
          thePlayer.addExperienceLevel(-RepairContainerv2.this.maximumCost.get());
        }

        double breakChance = net.minecraftforge.common.ForgeHooks.onAnvilRepair(thePlayer, stack, tileEntity.handler.getStackInSlot(0), tileEntity.handler.getStackInSlot(1)) * (damage_chance.get() / .12);

        if (!damageable.get()) breakChance = 0;

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
  @Override
  public void onCraftMatrixChanged(IInventory inventoryIn) {
    super.onCraftMatrixChanged(inventoryIn);
      this.updateRepairOutput();
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
      ItemStack stack = itemstack.copy();
      ItemStack stack2 = tileEntity.handler.getStackInSlot(1);
      Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
      j = j + getRepairCost(itemstack) + (stack2.isEmpty() ? 0 : getRepairCost(stack2));
      this.materialCost = 0;
      boolean flag = false;

      if (!stack2.isEmpty()) {
        if (!onAnvilChange(this, itemstack, stack2, outputSlot, this.repairedItemName, j)) return;
        flag = stack2.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(stack2).isEmpty();
        if (stack.isDamageable() && stack.getItem().getIsRepairable(itemstack, stack2)) {
          int l2 = Math.min(stack.getDamage(), stack.getMaxDamage() / 4);
          if (l2 <= 0) {
            this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
            this.maximumCost.set(0);
            return;
          }

          int i3;
          for (i3 = 0; l2 > 0 && i3 < stack2.getCount(); ++i3) {
            int j3 = stack.getDamage() - l2;
            stack.setDamage(j3);
            ++i;
            l2 = Math.min(stack.getDamage(), stack.getMaxDamage() / 4);
          }

          this.materialCost = i3;
        } else {
          if (!flag && (stack.getItem() != stack2.getItem() || !stack.isDamageable())) {
            this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
            this.maximumCost.set(0);
            return;
          }

          if (stack.isDamageable() && !flag) {
            int l = itemstack.getMaxDamage() - itemstack.getDamage();
            int i1 = stack2.getMaxDamage() - stack2.getDamage();
            int j1 = i1 + stack.getMaxDamage() * 12 / 100;
            int k1 = l + j1;
            int l1 = stack.getMaxDamage() - k1;
            if (l1 < 0) {
              l1 = 0;
            }

            if (l1 < stack.getDamage()) {
              stack.setDamage(l1);
              i += 2;
            }
          }

          Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(stack2);
          boolean flag2 = false;
          boolean flag3 = false;

          for (Enchantment enchantment1 : map1.keySet()) {
            if (enchantment1 != null) {
              int i2 = map.getOrDefault(enchantment1, 0);
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
                //can't anvil stacked items?
                if (itemstack.getCount() > 1) {
                  i = repair_cost_cap.get();
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
          stack.clearCustomName();
        }
      } else if (!this.repairedItemName.equals(itemstack.getDisplayName().getString())) {
        k = 1;
        i += k;
        stack.setDisplayName(new StringTextComponent(this.repairedItemName));
      }
      if (flag && !stack.isBookEnchantable(stack2)) stack = ItemStack.EMPTY;

      this.maximumCost.set(j + i);
      if (i <= 0) {
        stack = ItemStack.EMPTY;
      }

      //renaming only?
      if (k == i && k > 0 && (this.maximumCost.get() >= repair_cost_cap.get() || cheap_renaming.get())) {
        this.maximumCost.set(cheap_renaming.get() ? 1: repair_cost_cap.get() - 1);
        System.out.println();
      }

      if (this.maximumCost.get() >= repair_cost_cap.get() && !this.player.abilities.isCreativeMode) {
        stack = ItemStack.EMPTY;
      }

      if (!stack.isEmpty()) {
        int k2 = getRepairCost(stack);
        if (!stack2.isEmpty() && k2 < getRepairCost(stack2)) {
          k2 = getRepairCost(stack2);
        }

        if (k != i || k == 0) {
          k2 = func_216977_d(k2);
        }

        if (prior_work_penalty.get())
        stack.setRepairCost(k2);
        EnchantmentHelper.setEnchantments(map, stack);
      }

      this.outputSlot.setInventorySlotContents(0, stack);
      this.detectAndSendChanges();
    }
  }

  public static int getRepairCost(ItemStack stack){
    return prior_work_penalty.get() ? stack.getRepairCost() : 0;
  }

  public static int func_216977_d(int p_216977_0_) {
    return p_216977_0_ * 2 + 1;
  }

  /**
   * Called when the container is closed.
   */
  @Override
  public void onContainerClosed(PlayerEntity playerIn) {
  }

  /**
   * Determines whether supplied player can use this container
   */
  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    return  field_216980_g.getBlockState(pos)
            .isIn(BlockTags.ANVIL) && playerIn.getDistanceSq(pos.getX() + 0.5D,
            pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64;
  }

  /**
   * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
   * inventory and the other inventory(s).
   */
  @Override
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
        itemstack.clearCustomName();
      } else {
        itemstack.setDisplayName(new StringTextComponent(this.repairedItemName));
      }
    }
    this.updateRepairOutput();
  }

  public int getMaxCost() {
    return this.maximumCost.get();
  }

  /** unused*/
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

