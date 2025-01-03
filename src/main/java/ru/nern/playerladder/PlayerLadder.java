package ru.nern.playerladder;

import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.InteractionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nern.fconfiglib.v1.ConfigManager;
import ru.nern.fconfiglib.v1.api.annotations.validation.ConfigValidators;
import ru.nern.fconfiglib.v1.api.annotations.validation.ValidateField;
import ru.nern.fconfiglib.v1.json.JsonConfigManager;
import ru.nern.fconfiglib.v1.log.Sl4jLoggerWrapper;
import ru.nern.fconfiglib.v1.utils.ValueReference;
import ru.nern.fconfiglib.v1.validation.FieldValidator;
import ru.nern.fconfiglib.v1.validation.FieldsConfigValidator;
import ru.nern.fconfiglib.v1.validation.VersionConfigValidator;

import java.util.List;

public class PlayerLadder implements ModInitializer {
	public static final String MOD_ID = "playerladder";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static ConfigManager<Config, JsonObject> configManager = JsonConfigManager
			.builderOf(Config.class)
			.modId(MOD_ID)
			.logger(Sl4jLoggerWrapper.createFrom(LOGGER))
			.version(2)
			.create();


	@Override
	public void onInitialize() {
		configManager.init();

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
				SharedHandler.onLogOut(handler.player));

		UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> switch (PlayerLadder.config().server.mode) {
            case RIDE -> SharedHandler.rideEntity(player, entity, level, hand);
            case PICK_UP -> SharedHandler.pickUpEntity(player, entity, level, hand);
            case DO_NOTHING -> InteractionResult.PASS;
        });
	}

	public static Config config() {
		return configManager.config();
	}

	@ConfigValidators({
			VersionConfigValidator.class,
			FieldsConfigValidator.class,
	})
	public static class Config {
		public Server server = new Server();
		public Client client = new Client();

		public static class Server {
			public ClickMode mode = ClickMode.RIDE;
			public int pickUpLimit = 16;
			public int stepUpLimit = 16;
			public boolean interactWithAnyLiving = false;
			public boolean allowPlayers = true;

			@ValidateField(ExcludedLivingEntitiesValidator.class)
			public List<String> excludedLivingEntities = List.of("minecraft:wither", "minecraft:ender_dragon", "minecraft:minecart", "#minecraft:boat", "#minecraft:dismounts_underwater");

			public boolean rideExtension = true;
		}

		public static class Client {
			public boolean allowInteractions = true;
		}
	}

	public enum ClickMode {
		RIDE,
		PICK_UP,
		DO_NOTHING
	}

	static class ExcludedLivingEntitiesValidator implements FieldValidator<List<String>, Config> {

		@Override
		public void validate(ValueReference<List<String>> reference, Config instance) {
			SharedHandler.setExcludedEntries(reference.get());
		}
	}
}

