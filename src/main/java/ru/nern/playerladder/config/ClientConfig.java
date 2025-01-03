package ru.nern.playerladder.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import ru.nern.playerladder.PlayerLadder;

@EventBusSubscriber(modid = PlayerLadder.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue ALLOW_INTERACTIONS = BUILDER
            .comment("Allows interacting with the world while there's an entity on top of you.")
            .define("allowInteractions", true);


    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean allowInteractions;


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if(event.getConfig().getType() == ModConfig.Type.CLIENT) {
            allowInteractions = ALLOW_INTERACTIONS.get();
        }
    }
}
