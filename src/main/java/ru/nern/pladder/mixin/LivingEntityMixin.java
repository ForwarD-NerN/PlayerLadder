package ru.nern.pladder.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
	@Inject(method = "canBeRiddenInWater", at = @At("HEAD"), cancellable = true)
	private void canBeRiddenInWater(CallbackInfoReturnable<Boolean> cir)
	{
		Entity entity = (Entity) (Object) this;
		if(entity instanceof PlayerEntity) cir.setReturnValue(true);
	}
}
