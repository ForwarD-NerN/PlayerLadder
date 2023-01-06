package ru.nern.pladder.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin
{
    @Inject(method = "changeGameMode",
            at = @At(value = "HEAD", target = "Lnet/minecraft/server/network/ServerPlayerEntity;stopRiding()V"))
    private void onChangeGameMode(GameMode gameMode, CallbackInfoReturnable<Boolean> cir)
    {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if(player.hasPassengers() && gameMode == GameMode.SPECTATOR) player.getFirstPassenger().stopRiding();
    }
}
