package ru.nern.pladder.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin
{
	@Shadow private EntityDimensions dimensions;

	@Inject(method = "removePassenger", at = @At("TAIL"))
	private void onRemovePassenger(Entity passenger, CallbackInfo callbackInfo)
	{
		Entity entity = (Entity) (Object) this;
		if(entity instanceof PlayerEntity && !entity.world.isClient && passenger instanceof PlayerEntity)
		{
			ServerPlayerEntity serverPlayer = (ServerPlayerEntity) entity;
			serverPlayer.networkHandler.sendPacket(new EntityPassengersSetS2CPacket(entity));
		}
	}

	@Redirect(method = "updatePassengerPosition(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity$PositionUpdater;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getMountedHeightOffset()D"))
	private double getMountedHeightOffset(Entity entity)
	{
		if(!entity.world.isClient) return entity.getMountedHeightOffset();
		return isFirstPerson() ? dimensions.height * 0.93 : entity.getMountedHeightOffset();
	}

	@Environment(EnvType.CLIENT)
	private boolean isFirstPerson()
	{
		return MinecraftClient.getInstance().options.getPerspective() == Perspective.FIRST_PERSON;
	}

	@Inject(method = "setSneaking", at = @At("HEAD"))
	private void onSneak(boolean sneaking, CallbackInfo callbackInfo)
	{
		Entity entity = (Entity) (Object) this;

		if(entity instanceof PlayerEntity && !entity.world.isClient && !((PlayerEntity) entity).isFallFlying())
			if(!entity.hasVehicle() && entity.hasPassengers()) entity.getFirstPassenger().dismountVehicle();
	}
}
