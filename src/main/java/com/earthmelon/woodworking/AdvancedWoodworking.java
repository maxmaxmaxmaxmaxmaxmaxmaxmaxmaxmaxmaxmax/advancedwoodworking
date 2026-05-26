package com.earthmelon.woodworking;

import com.earthmelon.woodworking.blocks.*;
import com.earthmelon.woodworking.items.*;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AdvancedWoodworking.MODID)
public class AdvancedWoodworking
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "advancedwoodworking";
    // Directly reference a slf4j logger
    static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "advancedwoodworking" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    public static final DeferredRegister<Item> VANILLA_OVERRIDES = DeferredRegister.create(ForgeRegistries.ITEMS, "minecraft");

    // Create a Deferred Register to hold Items which will all be registered under the "advancedwoodworking" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "advancedwoodworking" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<Block> BRICK_MOULD = BLOCKS.register("brick_mould", () -> new BrickMould(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Item> BRICK_MOULD_ITEM = ITEMS.register("brick_mould", () -> new BlockItem(BRICK_MOULD.get(), new Item.Properties()));
    public static final RegistryObject<Block> BRICK_MOULD_FILLED = BLOCKS.register("brick_mould_filled", () -> new BrickMouldFilled(BlockBehaviour.Properties.of()));

    public static final RegistryObject<Block> LARGE_BARK = BLOCKS.register("large_bark", () -> new LargeBark(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).ignitedByLava()));
    public static final RegistryObject<Item> LARGE_BARK_ITEM = ITEMS.register("large_bark", () -> new BlockItem(LARGE_BARK.get(), new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Block> CARPET_GRIPPER = BLOCKS.register("carpet_gripper", () -> new CarpetGripper(BlockBehaviour.Properties.of().ignitedByLava()));
    public static final RegistryObject<Item> CARPET_GRIPPER_ITEM = ITEMS.register("carpet_gripper", () -> new BlockItem(CARPET_GRIPPER.get(), new Item.Properties()));

    public static final RegistryObject<Item> COPPER_NAIL = ITEMS.register("copper_nail", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COPPER_NUGGET = ITEMS.register("copper_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MOD_CLAY = VANILLA_OVERRIDES.register("clay_ball", () -> new ModClay(new Item.Properties()));

    public static final RegistryObject<Item> OAK_PLANKS = ITEMS.register("oak_plank", () -> new SingularPlank(new Item.Properties(), WoodType.OAK));
//    public static final RegistryObject<Item> BIRCH_PLANKS = ITEMS.register("birch_plank", () -> new SingularPlank(new Item.Properties(), WoodType.BIRCH));

    public static final RegistryObject<CreativeModeTab> ADVANCED_WOODWORKING_CREATIVE_TAB = CREATIVE_MODE_TABS.register("advancedwoodworking_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> COPPER_NAIL.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(COPPER_NAIL.get());
                output.accept(COPPER_NUGGET.get());
                output.accept(LARGE_BARK_ITEM.get());
                output.accept(CARPET_GRIPPER_ITEM.get());
                output.accept(OAK_PLANKS.get());
//                output.accept(BIRCH_PLANKS.get());
                output.accept(BRICK_MOULD_ITEM.get());
                output.accept(MOD_CLAY.get());
            }).build());

    public AdvancedWoodworking(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so mod content get registered
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        VANILLA_OVERRIDES.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(LARGE_BARK_ITEM);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }

    // doesn't work, check if statement for both conditions and see if anything works.
    @SubscribeEvent
    public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();

        // Only run on the main server thread
        if (event.getLevel().isClientSide()) return;

        // Make sure the player clicked with the main hand to avoid firing twice
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        // Check if the clicked block is a specific block (e.g., Dirt)
        Block clickedBlock = event.getLevel().getBlockState(event.getPos()).getBlock();
        if (/*clickedBlock.equals(BRICK_MOULD.get()) &&*/ heldItem.getItem() == Items.CLAY_BALL) {
            event.getLevel().setBlock(event.getPos(), Blocks.DIRT.defaultBlockState(), 3);
            if (!player.isCreative()) {
                heldItem.shrink(1);
            }

            // Optional: cancel the event so it stops vanilla behavior (like placing blocks)
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }
}
