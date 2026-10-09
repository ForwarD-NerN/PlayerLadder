package ru.nern.playerladder.mixin.shared;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.commands.RideCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.nern.playerladder.PlayerLadder;

@Mixin(RideCommand.class)
public class RideCommandMixin {

    @ModifyExpressionValue(method = "mount",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;is(Ljava/lang/Object;)Z"))
    private static boolean playerladder$rideExtension(boolean isPlayer) {
        return isPlayer && !PlayerLadder.config().server.rideExtension;
    }
}