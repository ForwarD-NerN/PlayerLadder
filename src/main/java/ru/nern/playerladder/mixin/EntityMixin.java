package ru.nern.playerladder.mixin;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Entity.class)
public abstract class EntityMixin
{


    @Shadow public abstract Level getLevel();

    @Inject(method = "removePassenger", at = @At("TAIL"))
    private void removePassenger(Entity passenger, CallbackInfo ci)
    {
        Entity entity = (Entity) (Object) this;


        if(!entity.level.isClientSide && entity instanceof Player)
            ((ServerPlayer) entity).connection.send(new ClientboundSetPassengersPacket(entity));
    }

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;)Z", at = @At("TAIL"))
    private void onStartRiding(Entity entity, CallbackInfoReturnable<Boolean> cir)
    {
        if(!entity.level.isClientSide && entity instanceof Player)
            ((ServerPlayer)entity).connection.send(new ClientboundSetPassengersPacket(entity));
    }

    @ModifyVariable(method = "positionRider(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity$MoveFunction;)V",
            at = @At(value = "STORE"), ordinal = 0, require = 0)
    private double offsetPassengersClientSide(double d, Entity passenger) {
        return getLevel().isClientSide && passenger instanceof Player ? d-getRidingOffset(passenger) : d;
    }
    @OnlyIn(Dist.CLIENT)
    private double getRidingOffset(Entity passenger)
    {
        Minecraft mc = Minecraft.getInstance();
        return mc.options.getCameraType().isFirstPerson() && passenger.getVehicle() == mc.player ? passenger.getMyRidingOffset() : 0;
    }
}
