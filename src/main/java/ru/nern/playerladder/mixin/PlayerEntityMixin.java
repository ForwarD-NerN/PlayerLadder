package ru.nern.playerladder.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin
{

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo callbackInfo)
    {
        PlayerEntity entity = (PlayerEntity) (Object) this;

        if(!entity.getWorld().isClient && entity.hasPassengers() && entity.isSneaking() && entity.isOnGround())
            entity.getFirstPassenger().stopRiding();
    }
}
