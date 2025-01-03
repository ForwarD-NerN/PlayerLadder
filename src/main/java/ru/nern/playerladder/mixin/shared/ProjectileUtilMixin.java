package ru.nern.playerladder.mixin.shared;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.nern.playerladder.PlayerLadder;

import java.util.function.Predicate;

@Mixin(ProjectileUtil.class)
public final class ProjectileUtilMixin {

    @Inject(method = "getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;contains(Lnet/minecraft/world/phys/Vec3;)Z", shift = At.Shift.AFTER))
    private static void playerladder$saveOldValue(Entity entity, Vec3 vec3, Vec3 vec32, AABB aABB, Predicate<Entity> predicate, double d, CallbackInfoReturnable<EntityHitResult> cir, @Local(ordinal = 1) Entity entity2, @Share("entity2") LocalRef<Entity> entity2Ref) {
        if(PlayerLadder.config().client.allowInteractions && entity2 != null) {
            entity2Ref.set(entity2);
        }
    }

    @Inject(method = "getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;",
            at = @At(value = "INVOKE", target = "Ljava/util/Optional;orElse(Ljava/lang/Object;)Ljava/lang/Object;"))
    private static void playerladder$restoreOldValue(Entity pShooter, Vec3 vec3, Vec3 vec32, AABB aABB, Predicate<Entity> predicate, double d, CallbackInfoReturnable<EntityHitResult> cir, @Local(ordinal = 1) LocalRef<Entity> entity2, @Share("entity2") LocalRef<Entity> entity2Ref) {
        if(PlayerLadder.config().client.allowInteractions && entity2.get().getVehicle() == pShooter) {
            entity2.set(entity2Ref.get());
        }
    }
}
