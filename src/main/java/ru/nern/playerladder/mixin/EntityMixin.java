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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin
{
    @Shadow public abstract boolean hasPassenger(Entity passenger);

    @Shadow public abstract double getMountedHeightOffset();

    @Shadow public abstract double getY();

    @Shadow public abstract double getX();

    @Shadow public abstract double getZ();

    @Shadow public World world;

    @Inject(method = "removePassenger", at = @At("TAIL"))
    private void onRemovePassenger(Entity passenger, CallbackInfo callbackInfo)
    {
        Entity entity = (Entity) (Object) this;


        if(!entity.world.isClient && entity instanceof PlayerEntity && passenger instanceof PlayerEntity)
            ((ServerPlayerEntity) entity).networkHandler.sendPacket(new EntityPassengersSetS2CPacket(entity));
    }

    @Inject(method = "startRiding(Lnet/minecraft/entity/Entity;Z)Z", at = @At("TAIL"))
    private void onStartRiding(Entity entity, boolean force, CallbackInfoReturnable<Boolean> cir)
    {
        if(!entity.world.isClient && entity instanceof PlayerEntity)
            ((ServerPlayerEntity)entity).networkHandler.sendPacket(new EntityPassengersSetS2CPacket(entity));
    }


    /**
     * @author ForwarD_NerN
     * @reason To make passengers sit a little higher in first person
     */
    @Overwrite
    private void updatePassengerPosition(Entity passenger, Entity.PositionUpdater positionUpdater) {
        if (!this.hasPassenger(passenger)) return;

        double d = this.getY() + this.getMountedHeightOffset() + passenger.getHeightOffset();

        if(world.isClient)
            d = getDOffset(passenger);


        positionUpdater.accept(passenger, this.getX(), d, this.getZ());
    }

    @Environment(EnvType.CLIENT)
    private double getDOffset(Entity passenger)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc.options.getPerspective().isFirstPerson() && passenger instanceof PlayerEntity && passenger.getVehicle() == mc.player)
            return this.getY() + this.getMountedHeightOffset();

        return this.getY() + this.getMountedHeightOffset() + passenger.getHeightOffset();
    }
}
