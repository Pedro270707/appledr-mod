package net.pedroricardo;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.api.DedicatedServerModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.pedroricardo.content.Appledrness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppleDrMod implements DedicatedServerModInitializer {
	public static final String MOD_ID = "appledrmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeServer() {
		Registry.register(Appledrness.REGISTRY, Identifier.of(MOD_ID, "being_appledr"), (world, player) -> player.getName().getString().equals("AppleDr") ? 100 : 0);
		Registry.register(Appledrness.REGISTRY, Identifier.of(MOD_ID, "having_apples_in_inventory"), (world, player) -> player.getInventory().count(Items.APPLE));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("appledrness")
					.then(RequiredArgumentBuilder.<ServerCommandSource, EntitySelector>argument("player", EntityArgumentType.player())
							.executes(c -> {
								PlayerEntity player = EntityArgumentType.getPlayer(c, "player");
								c.getSource().sendMessage(Text.literal("<AppleDr, the Appledrful> ").formatted(Formatting.RED)
										.append(Text.literal("Appledrness: ").formatted(Formatting.WHITE)
												.append(Text.literal(String.valueOf(Appledrness.getAppledrness(player.getWorld(), player))))));
								return Command.SINGLE_SUCCESS;
							})));
		});
	}
}