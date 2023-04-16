package ru.nern.playerladder.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
    @Inject(method = "rideableUnderWater", at = @At("TAIL"), cancellable = true)
    private void rideableUnderWater(CallbackInfoReturnable<Boolean> cir)
    {
        LivingEntity entity = (LivingEntity) (Object) this;
        if(entity instanceof Player) cir.setReturnValue(true);
    }
}
