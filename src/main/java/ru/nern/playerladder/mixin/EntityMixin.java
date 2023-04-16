package ru.nern.playerladder.mixin;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Entity.class)
public abstract class EntityMixin
{

    @Shadow public Level level;

    @Shadow public abstract boolean hasPassenger(Entity p_20364_);

    @Shadow public abstract double getY();

    @Shadow public abstract double getX();

    @Shadow public abstract double getZ();

    @Shadow public abstract double getPassengersRidingOffset();


    @Inject(method = "removePassenger", at = @At("TAIL"))
    private void removePassenger(Entity passenger, CallbackInfo ci)
    {
        Entity entity = (Entity) (Object) this;


        if(!entity.level.isClientSide && entity instanceof Player && passenger instanceof Player)
            ((ServerPlayer) entity).connection.send(new ClientboundSetPassengersPacket(entity));
    }

    @Inject(method = "addPassenger", at = @At("TAIL"))
    private void onAddPassenger(Entity entity, CallbackInfo ci)
    {
        Entity vehicle = (Entity) (Object) this;
        if(!entity.level.isClientSide && entity instanceof Player && vehicle instanceof Player)
        {
            ((ServerPlayer)vehicle).connection.send(new ClientboundSetPassengersPacket(vehicle));
            System.out.println("SEND");
        }
    }

    /**
     * @author ForwarD_NerN
     * @reason To make passengers sit a little higher in first person
     */
    @Overwrite
    private void positionRider(Entity passenger, Entity.MoveFunction positionUpdater) {
        if (!this.hasPassenger(passenger)) return;

        double d = this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset();

        if(level.isClientSide)
            d = getDOffset(passenger);


        positionUpdater.accept(passenger, this.getX(), d, this.getZ());
    }

    @OnlyIn(Dist.CLIENT)
    private double getDOffset(Entity passenger)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.options.getCameraType().isFirstPerson() && passenger instanceof Player && passenger.getVehicle() == mc.player)
            return this.getY() + this.getPassengersRidingOffset();

        return this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset();
    }
}
