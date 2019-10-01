package com.tfar.anviltweaks;

import com.tfar.anviltweaks.network.Message;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AnvilTweaks.MODID)
public class AnvilTweaks
{
  // Directly reference a log4j logger.

  public static final String MODID = "anviltweaks";

  public AnvilTweaks() {
    // Register the setup method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configs.COMMON_SPEC);
  }

  private void setup(final FMLCommonSetupEvent event) {
    Message.registerMessages(AnvilTweaks.MODID);
  }

  // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
  // Event bus for receiving Registry Events)
  @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
  public static class RegistryEvents {
    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
      if (ModList.get().isLoaded("apothesis"))return;
      Block.Properties anvil = Block.Properties.create(Material.ANVIL, MaterialColor.IRON)
              .hardnessAndResistance(5.0F, 1200.0F).sound(SoundType.ANVIL);
      registerItemBlock(new AnvilBlockv2(anvil),"anvil",event.getRegistry());
      registerItemBlock(new AnvilBlockv2(anvil),"chipped_anvil",event.getRegistry());
      registerItemBlock(new AnvilBlockv2(anvil),"damaged_anvil",event.getRegistry());
    }

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
      if (ModList.get().isLoaded("apothesis"))return;
      registerItemBlock(new BlockItem(Blocks.ANVIL,new Item.Properties().group(ItemGroup.DECORATIONS)), Blocks.ANVIL.getRegistryName().toString()
              ,event.getRegistry());
      registerItemBlock(new BlockItem(Blocks.CHIPPED_ANVIL,new Item.Properties().group(ItemGroup.DECORATIONS)), Blocks.CHIPPED_ANVIL.getRegistryName().toString()
              ,event.getRegistry());
      registerItemBlock(new BlockItem(Blocks.DAMAGED_ANVIL,new Item.Properties().group(ItemGroup.DECORATIONS)), Blocks.DAMAGED_ANVIL.getRegistryName().toString()
              ,event.getRegistry());
    }

    @SubscribeEvent
    public static void registerTiles(final RegistryEvent.Register<TileEntityType<?>> event) {
      registerObject(TileEntityType.Builder.create(() -> new AnvilTile(Stuff.anvil_tile), Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL).build(null),"anvil_tile",event.getRegistry());
    }

    @SubscribeEvent
    public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
      registerObject(IForgeContainerType.create((windowId, inv, data) -> new RepairContainerv2(windowId, inv.player.world, data.readBlockPos(), inv, inv.player)),"anvil_container_v2",event.getRegistry());
    }

    private static <T extends IForgeRegistryEntry<T>> void registerItemBlock(T obj, String name, IForgeRegistry<T> registry) {
      registry.register(obj.setRegistryName(new ResourceLocation(name)));
    }

    private static <T extends IForgeRegistryEntry<T>> void registerObject(T obj, String name, IForgeRegistry<T> registry) {
      registry.register(obj.setRegistryName(new ResourceLocation(MODID, name)));
    }
  }

  @ObjectHolder(MODID)
  public static class Stuff {

    public static final ContainerType<RepairContainerv2> anvil_container_v2 = null;
    public static final TileEntityType<AnvilTile> anvil_tile = null;
  }
}
