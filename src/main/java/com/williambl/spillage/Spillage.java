package com.williambl.spillage;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("spillage")
public class Spillage
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String MODID = "spillage";

    public Spillage() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
    }

    private void processIMC(final InterModProcessEvent event)
    {
    }

    @SubscribeEvent
    public void onPlayerTick(final LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            if (player.getHeldItemMainhand().getItem() instanceof BucketItem)
                spillBucket(player.getHeldItemMainhand(), player);
            if (player.getHeldItemOffhand().getItem() instanceof BucketItem)
                spillBucket(player.getHeldItemOffhand(), player);
        }
    }

    private void spillBucket(ItemStack stack, PlayerEntity player) {
        CompoundNBT tag = stack.getOrCreateChildTag("Spillage");
        World world = player.world;
        BucketItem bucket = (BucketItem) stack.getItem();
        int timesSpilled = tag.getInt("TimesSpilled");
        System.out.println(timesSpilled);
        if (
                world.rand.nextDouble() < 0.001 / (timesSpilled + 1)
                        || player.isSprinting() && world.rand.nextDouble() < (0.002 / timesSpilled + 1)
                        || player.isAirBorne && world.rand.nextDouble() < 0.003 / (timesSpilled + 1)
        ) {
            tag.putInt("TimesSpilled", ++timesSpilled);
            if (world.getBlockState(player.getPosition()).isAir()) {
                world.setBlockState(player.getPosition(), bucket.getFluid().getDefaultState().getBlockState());
            }
        }
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
    }
}