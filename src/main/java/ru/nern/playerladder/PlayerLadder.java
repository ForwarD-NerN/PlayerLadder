package ru.nern.playerladder;

import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import ru.nern.playerladder.config.ClientConfig;
import ru.nern.playerladder.config.ServerConfig;

@Mod(PlayerLadder.MODID)
public class PlayerLadder
{
    public static final String MODID = "playerladder";

    public PlayerLadder(IEventBus modEventBus, ModContainer modContainer)
    {
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, ServerConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        SharedHandler.onPlayerTick(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.EntityInteract event) {
        event.setCancellationResult(switch (ServerConfig.mode) {
            case RIDE -> SharedHandler.rideEntity(event.getEntity(), event.getTarget(), event.getLevel(), event.getHand());
            case PICK_UP -> SharedHandler.pickUpEntity(event.getEntity(), event.getTarget(), event.getLevel(), event.getHand());
            case DO_NOTHING -> InteractionResult.PASS;
        });
    }

    @SubscribeEvent
    public void onPlayerLogOut(PlayerEvent.PlayerLoggedOutEvent event) {
        SharedHandler.onLogOut(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerGameModeChange(PlayerEvent.PlayerChangeGameModeEvent event) {
        SharedHandler.onGameModeChange(event.getEntity());
    }

}
