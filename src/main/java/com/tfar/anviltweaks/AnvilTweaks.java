package com.tfar.anviltweaks;

import com.tfar.anviltweaks.network.Message;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AnvilTweaks.MODID)
public class AnvilTweaks
{
  // Directly reference a log4j logger.

  public static final String MODID = "anviltweaks";

  private static final Logger LOGGER = LogManager.getLogger();

  public AnvilTweaks() {
    // Register the setup method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    // Register the doClientStuff method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

    // Register ourselves for server and other game events we are interested in
    MinecraftForge.EVENT_BUS.register(this);
  }

  private void setup(final FMLCommonSetupEvent event) {
    Message.registerMessages(AnvilTweaks.MODID);
  }

  private void doClientStuff(final FMLClientSetupEvent event) {
    ScreenManager.registerFactory(Stuff.anvil_container_v2, AnvilScreenv2::new);
    ClientRegistry.bindTileEntitySpecialRenderer(AnvilTile.class, new AnvilTileSpecialRenderer());

  }

  // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
  // Event bus for receiving Registry Events)
  @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
  public static class RegistryEvents {
    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
      Block.Properties anvil = Block.Properties.create(Material.ANVIL, MaterialColor.IRON)
              .hardnessAndResistance(5.0F, 1200.0F).sound(SoundType.ANVIL);
      registerBlock(new AnvilBlockv2(anvil),"anvil",event.getRegistry());
      registerBlock(new AnvilBlockv2(anvil),"chipped_anvil",event.getRegistry());
      registerBlock(new AnvilBlockv2(anvil),"damaged_anvil",event.getRegistry());
    }

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
      registerItem(new BlockItem(Anvils.anvil,new Item.Properties().group(ItemGroup.DECORATIONS)), Anvils.anvil.getRegistryName().toString()
              ,event.getRegistry());
      registerItem(new BlockItem(Anvils.chipped_anvil,new Item.Properties().group(ItemGroup.DECORATIONS)), Anvils.chipped_anvil.getRegistryName().toString()
              ,event.getRegistry());
      registerItem(new BlockItem(Anvils.damaged_anvil,new Item.Properties().group(ItemGroup.DECORATIONS)), Anvils.damaged_anvil.getRegistryName().toString()
              ,event.getRegistry());
    }

    @SubscribeEvent
    public static void registerTiles(final RegistryEvent.Register<TileEntityType<?>> event) {
      event.getRegistry().register(TileEntityType.Builder.create(() -> new AnvilTile(Stuff.anvil_tile), Anvils.anvil, Anvils.chipped_anvil, Anvils.damaged_anvil).build(null).setRegistryName("anvil_tile"));
    }

    @SubscribeEvent
    public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
      event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new RepairContainerv2(windowId, inv.player.world, data.readBlockPos(), inv, inv.player)).setRegistryName("anvil_container_v2"));
    }

    private static void registerItem(Item item, String name, IForgeRegistry<Item> registry) {
      registry.register(item.setRegistryName(new ResourceLocation(name)));
    }
    private static void registerBlock(Block block, String name, IForgeRegistry<Block> registry) {
      registry.register(block.setRegistryName(new ResourceLocation(name)));
    }
  }
  @ObjectHolder("minecraft")
  public static class Anvils {

    public static final Block anvil = null;
    public static final Block chipped_anvil = null;
    public static final Block damaged_anvil = null;
  }

  @ObjectHolder(MODID)
  public static class Stuff {

    public static final ContainerType<RepairContainerv2> anvil_container_v2 = null;
    public static final TileEntityType<AnvilTile> anvil_tile = null;
  }
}
