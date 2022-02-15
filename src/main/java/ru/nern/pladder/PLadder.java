package ru.nern.pladder;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PLadder implements ModInitializer
{

	public static final Logger LOGGER = LoggerFactory.getLogger("modid");

	@Override
	public void onInitialize()
	{
		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) ->
		{
			if(entity instanceof PlayerEntity && !world.isClient)
			{
				if(!entity.hasPlayerRider() && entity.distanceTo(player) < 2 && hand == Hand.MAIN_HAND && !player.isHolding(Items.SHIELD))
				{

					ServerPlayerEntity serverPlayer = (ServerPlayerEntity) entity;

					serverPlayer.networkHandler.sendPacket(new EntityPassengersSetS2CPacket(player));

					player.startRiding(entity);

					serverPlayer.networkHandler.sendPacket(new EntityPassengersSetS2CPacket(entity));
				}
			}
			return ActionResult.PASS;
		});
	}
}
