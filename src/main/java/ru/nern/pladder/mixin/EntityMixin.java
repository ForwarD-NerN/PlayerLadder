package ru.nern.pladder.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin
{
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

	@Inject(method = "setSneaking", at = @At("HEAD"))
	private void onSneak(boolean sneaking, CallbackInfo callbackInfo)
	{
		Entity entity = (Entity) (Object) this;

		if(entity instanceof PlayerEntity && !entity.world.isClient && !((PlayerEntity) entity).isFallFlying())
		{
			if(!entity.hasVehicle() && entity.hasPassengers()) entity.getFirstPassenger().dismountVehicle();
		}
	}
}
