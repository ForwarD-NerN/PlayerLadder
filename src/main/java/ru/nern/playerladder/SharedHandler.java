package ru.nern.playerladder;

import com.google.common.collect.Sets;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ru.nern.playerladder.PlayerLadder.config;

public class SharedHandler {
    private static final Set<EntityType<?>> entityTypesToExclude = Sets.newHashSet();
    private static final Set<TagKey<EntityType<?>>> entityTagsToExclude = Sets.newHashSet();

    public static InteractionResult rideEntity(Player player, Entity newVehicle, Level level, InteractionHand hand) {
        if(!level.isClientSide() && hand == InteractionHand.MAIN_HAND && canPickUpOrRideLiving(newVehicle) && player.getItemInHand(hand).isEmpty()) {
            Entity vehicle = getHighestOrSelf(newVehicle, player, config().server.stepUpLimit);

            if(vehicle == null) return InteractionResult.FAIL;
            player.startRiding(vehicle);

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult pickUpEntity(Player player, Entity newPassenger, Level level, InteractionHand hand) {
        if(!level.isClientSide() && hand == InteractionHand.MAIN_HAND && canPickUpOrRideLiving(newPassenger) && player.getItemInHand(hand).isEmpty()) {
            Entity vehicle = getHighestOrSelf(player, newPassenger, config().server.pickUpLimit);

            if(vehicle == null) return InteractionResult.FAIL;
            newPassenger.startRiding(vehicle);

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private static Entity getHighestOrSelf(Entity vehicle, Entity newPassenger, int limit) {
        int count = -1;
        while (vehicle.isVehicle()) {
            count++;
            vehicle = vehicle.getFirstPassenger();
            if(vehicle == newPassenger || count >= limit) return null;
        }
        return vehicle;
    }

    private static boolean canPickUpOrRideLiving(Entity entity) {
        if(entity instanceof Player) {
            return config().server.allowPlayers;
        }

        return config().server.interactWithAnyLiving &&
                !entityTypesToExclude.contains(entity.getType()) &&
                entityTagsToExclude.stream().noneMatch(tag -> entity.getType().is(tag));
    }

    public static void onMount(Entity vehicle, Entity passenger) {
        if(!vehicle.level().isClientSide && vehicle instanceof Player) {
            ((ServerPlayer)vehicle).connection.send(new ClientboundSetPassengersPacket(vehicle));
        }
    }

    public static void onDismount(Entity vehicle) {
        if(!vehicle.level().isClientSide && vehicle instanceof Player)
            ((ServerPlayer) vehicle).connection.send(new ClientboundSetPassengersPacket(vehicle));
    }

    public static void onPlayerTick(Player player) {
        if(!player.level().isClientSide && player.onGround() && player.isVehicle() && player.isCrouching())
            player.getFirstPassenger().stopRiding();
    }

    public static void onLogOut(Player player) {
        if(player.isPassenger() && player.getVehicle() instanceof Player)
            player.stopRiding();
    }

    public static void onGameModeChange(Player player) {
        if(player.isVehicle() && (config().server.dismountOnGameModeChange || player.gameMode() == GameType.SPECTATOR))
            player.getFirstPassenger().stopRiding();
    }

    private static void addExcludedEntityType(String entity) {
        try {
            Optional<EntityType<?>> type = EntityType.byString(entity);
            type.ifPresent(entityTypesToExclude::add);
        } catch (ResourceLocationException ignored) {}
    }

    private static void addExcludedEntityTag(String tag) {
        try {
            TagKey<EntityType<?>> tagKey = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(tag.substring(1)));
            entityTagsToExclude.add(tagKey);
        } catch (ResourceLocationException ignored) {}
    }

    public static void setExcludedEntries(List<String> entries) {
        entityTagsToExclude.clear();
        entityTypesToExclude.clear();

        for(String entry : entries) {
            if(entry.isEmpty()) continue;

            if(entry.startsWith("#")) {
                SharedHandler.addExcludedEntityTag(entry);
            }else{
                SharedHandler.addExcludedEntityType(entry);
            }
        }
    }
}
