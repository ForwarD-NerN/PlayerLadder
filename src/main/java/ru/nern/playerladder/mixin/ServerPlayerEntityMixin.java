package ru.nern.playerladder.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin
{
    @Redirect(method = "changeGameMode",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;dropShoulderEntities()V"))
    private void changeGameMode(ServerPlayerEntity player)
    {
        if(player.hasPassengers()) player.getFirstPassenger().stopRiding();
    }
}
