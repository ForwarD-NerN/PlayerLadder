package ru.nern.playerladder.config;

import com.llamalad7.mixinextras.sugar.Share;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import ru.nern.playerladder.PlayerLadder;
import ru.nern.playerladder.SharedHandler;

import java.util.List;


@EventBusSubscriber(modid = PlayerLadder.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ServerConfig
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.EnumValue<ClickMode> MODE = BUILDER
            .comment("Action that occurs when you click on a player/entity.")
            .defineEnum("rightClickMode", ClickMode.RIDE);

    private static final ModConfigSpec.IntValue PICK_UP_LIMIT = BUILDER
            .comment("Limits how many entities a player can pick up.")
            .defineInRange("pickUpLimit", 16, 1, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue STEP_UP_LIMIT = BUILDER
        .comment("Limits how many entities up a player can go.")
        .defineInRange("stepUpLimit", 16, 1, Integer.MAX_VALUE);

    private static final ModConfigSpec.BooleanValue ALLOW_LIVING_ENTITIES = BUILDER
            .comment("Allows riding or picking up any living entity.")
            .define("allowLivingEntities", false);

    private static final ModConfigSpec.BooleanValue ALLOW_PLAYERS = BUILDER
            .comment("Allows riding or picking up any player.")
            .define("allowPlayers", true);

    private static final ModConfigSpec.ConfigValue<List<? extends String>> EXCLUDED_LIVING_ENTITIES = BUILDER
            .comment("The list of living entities that can't be ridden/picked up. Supports entity tags e.g: #minecraft:dismounts_underwater")
            .defineList("excludedLivingEntities",
                    () -> List.of("minecraft:wither", "minecraft:ender_dragon", "minecraft:minecart", "#minecraft:boat", "#minecraft:dismounts_underwater"), object -> true);

    private static final ModConfigSpec.BooleanValue RIDE_EXTENSION = BUILDER
            .comment("Allows the /ride command to mount entities on top of players.")
            .define("rideCommandExtension", true);


    public static final ModConfigSpec SPEC = BUILDER.build();

    public static ClickMode mode;
    public static int pickUpLimit;
    public static int stepUpLimit;
    public static boolean allowLivingEntities;
    public static boolean allowPlayers;
    public static boolean rideExtension;


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if(event.getConfig().getType() == ModConfig.Type.COMMON) {
            mode = MODE.get();
            pickUpLimit = PICK_UP_LIMIT.get();
            stepUpLimit = STEP_UP_LIMIT.get();
            allowLivingEntities = ALLOW_LIVING_ENTITIES.get();
            allowPlayers = ALLOW_PLAYERS.get();
            rideExtension = RIDE_EXTENSION.get();

            SharedHandler.setExcludedEntries((List<String>) EXCLUDED_LIVING_ENTITIES.get());
        }
    }

    public enum ClickMode {
        RIDE,
        PICK_UP,
        DO_NOTHING
    }
}
