package ru.nern.playerladder.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
    @Inject(method = "canBeRiddenInWater", at = @At("TAIL"), cancellable = true)
    private void canBeRiddenInWater(CallbackInfoReturnable<Boolean> cir)
    {
        LivingEntity entity = (LivingEntity) (Object) this;
        if(entity instanceof PlayerEntity) cir.setReturnValue(true);
    }
}
