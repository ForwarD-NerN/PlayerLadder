package ru.nern.playerladder.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin
{

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo callbackInfo)
    {
        Player entity = (Player) (Object) this;

        if(!entity.level().isClientSide && entity.isVehicle() && entity.isCrouching() && entity.onGround())
            entity.getFirstPassenger().stopRiding();
    }
}
