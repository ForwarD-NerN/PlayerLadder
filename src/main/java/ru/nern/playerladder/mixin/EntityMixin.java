package ru.nern.playerladder.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin
{

    @Shadow public abstract World getWorld();

    @Inject(method = "removePassenger", at = @At("TAIL"))
    private void onRemovePassenger(Entity passenger, CallbackInfo callbackInfo)
    {
        Entity entity = (Entity) (Object) this;

        if(!entity.world.isClient && entity instanceof PlayerEntity)
            ((ServerPlayerEntity) entity).networkHandler.sendPacket(new EntityPassengersSetS2CPacket(entity));
    }

    @Inject(method = "startRiding(Lnet/minecraft/entity/Entity;Z)Z", at = @At("TAIL"))
    private void onStartRiding(Entity entity, boolean force, CallbackInfoReturnable<Boolean> cir)
    {
        if(!entity.world.isClient && entity instanceof PlayerEntity)
            ((ServerPlayerEntity)entity).networkHandler.sendPacket(new EntityPassengersSetS2CPacket(entity));
    }

    @ModifyVariable(method = "updatePassengerPosition(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity$PositionUpdater;)V",
            at = @At(value = "STORE"), ordinal = 0, require = 0)
    private double offsetPassengersClientSide(double d, Entity passenger) {
        return getWorld().isClient && passenger instanceof PlayerEntity ? d-getRidingOffset(passenger) : d;
    }
    @Environment(EnvType.CLIENT)
    private double getRidingOffset(Entity passenger)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.options.getPerspective().isFirstPerson() && passenger.getVehicle() == mc.player ? passenger.getHeightOffset() : 0;
    }
}
