package ru.nern.playerladder;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(PlayerLadder.MODID)
public class PlayerLadder
{
    public static final String MODID = "playerladder";


    public PlayerLadder()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {}

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.EntityInteract event)
    {
        Player player = event.getEntity();
        Level level = event.getLevel();
        Entity entity = event.getTarget();

        if(!level.isClientSide && event.getHand() == InteractionHand.MAIN_HAND && entity instanceof Player && player.getItemInHand(event.getHand()).isEmpty())
        {
            ServerPlayer passenger = (ServerPlayer) entity;

            while (passenger.getFirstPassenger() != null && passenger.getFirstPassenger() != player)
                passenger = (ServerPlayer) passenger.getFirstPassenger();

            player.startRiding(passenger);

            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        Player player = event.getEntity();

        if(player.isPassenger() && player.getVehicle() instanceof Player)
            player.stopRiding();
    }

    @SubscribeEvent
    public void onPlayerChangeGamemode(PlayerEvent.PlayerChangeGameModeEvent event)
    {
        Player player = event.getEntity();

        if(player.isVehicle()) player.getFirstPassenger().stopRiding();

    }
}
