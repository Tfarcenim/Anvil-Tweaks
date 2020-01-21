package com.tfar.anviltweaks;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class Jei implements IModPlugin {

  @Override
  public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
    registration.addRecipeTransferHandler(RepairContainerv2.class, VanillaRecipeCategoryUid.ANVIL, 1, 9, 10, 36);
  }

  @Override
  public ResourceLocation getPluginUid() {
    return new ResourceLocation(AnvilTweaks.MODID, AnvilTweaks.MODID);
  }
}
