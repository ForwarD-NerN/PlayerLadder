package ru.nern.playerladder.mixin.shared;

import net.minecraft.server.commands.RideCommand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.nern.playerladder.PlayerLadder;

@Mixin(RideCommand.class)
public class RideCommandMixin {

    @Redirect(method = "mount",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getType()Lnet/minecraft/world/entity/EntityType;"))
    private static EntityType<?> playerladder$rideExtension(Entity entity) {
        return PlayerLadder.config().server.rideExtension ? null : entity.getType();
    }
}