package ru.nern.playerladder.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.nern.playerladder.PlayerLadder;
import ru.nern.playerladder.SharedHandler;

import static ru.nern.playerladder.PlayerLadder.config;


public class ClothConfigIntegration {
    public static Screen generateConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("title.playerladder.config"));

        builder.setSavingRunnable(() -> PlayerLadder.configManager.save(PlayerLadder.configManager.getConfigFile()));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory serverCategory = builder.getOrCreateCategory(Component.translatable("server.playerladder.config"));

        serverCategory.addEntry(entryBuilder.startEnumSelector(Component.translatable("rightClickMode.playerladder.config"), PlayerLadder.ClickMode.class, config().server.mode)
                .setTooltip(Component.translatable("rightClickMode.playerladder.description"))
                .setSaveConsumer(clickMode -> config().server.mode = clickMode).build());

        serverCategory.addEntry(entryBuilder.startIntField(Component.translatable("pickUpLimit.playerladder.config"), config().server.pickUpLimit)
                .setMin(1)
                .setTooltip(Component.translatable("pickUpLimit.playerladder.description"))
                .setSaveConsumer(value -> config().server.pickUpLimit = value).build());

        serverCategory.addEntry(entryBuilder.startIntField(Component.translatable("stepUpLimit.playerladder.config"), config().server.stepUpLimit)
                .setMin(1)
                .setTooltip(Component.translatable("stepUpLimit.playerladder.description"))
                .setSaveConsumer(value -> config().server.stepUpLimit = value).build());

        serverCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("allowPlayers.playerladder.config"), config().server.allowPlayers)
                .setTooltip(Component.translatable("allowPlayers.playerladder.description"))
                .setSaveConsumer(value -> config().server.allowPlayers = value).build());

        serverCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("allowLivingEntities.playerladder.config"), config().server.interactWithAnyLiving)
                .setTooltip(Component.translatable("allowLivingEntities.playerladder.description"))
                .setSaveConsumer(value -> config().server.interactWithAnyLiving = value).build());

        serverCategory.addEntry(entryBuilder.startStrList(Component.translatable("excludedLivingEntities.playerladder.config"), config().server.excludedLivingEntities)
                .setTooltip(Component.translatable("excludedLivingEntities.playerladder.description"))
                .setDisplayRequirement(Requirement.isTrue(() -> config().server.interactWithAnyLiving))
                .setSaveConsumer(entries -> {
                    SharedHandler.setExcludedEntries(entries);
                    config().server.excludedLivingEntities = entries;
                }).build());

        serverCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("rideExtension.playerladder.config"), config().server.rideExtension)
                .setTooltip(Component.translatable("rideExtension.playerladder.description"))
                .setSaveConsumer(value -> config().server.rideExtension = value).build());


        ConfigCategory clientCategory = builder.getOrCreateCategory(Component.translatable("client.playerladder.config"));

        clientCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("allowInteractions.playerladder.config"), config().client.allowInteractions)
                .setTooltip(Component.translatable("allowInteractions.playerladder.description"))
                .setSaveConsumer(value -> config().client.allowInteractions = value).build());

        return builder.build();
    }
}
