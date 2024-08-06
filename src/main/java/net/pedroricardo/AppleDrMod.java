package net.pedroricardo;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.pedroricardo.appledrness.Appledrness;
import net.pedroricardo.appledrness.loot.AppleDrLootConditions;
import net.pedroricardo.appledrness.loot.AppledrnessLootConditionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;

public class AppleDrMod implements DedicatedServerModInitializer {
	public static final String MOD_ID = "appledrmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static HashMap<PlayerEntity, Appledrness> playersDrness;

	@Override
	public void onInitializeServer() {
		AppleDrLootConditions.init();

		LootTableEvents.MODIFY.register((key, builder, source) -> {
			if (source.isBuiltin() && key == EntityType.PLAYER.getLootTableId()) {
				LootPool.Builder applePool = LootPool.builder()
						.with(ItemEntry.builder(Items.APPLE).weight(1)
						.conditionally(AppledrnessLootConditionType.builder(NumberRange.IntRange.atLeast(300), AppledrnessLootConditionType.Source.THIS))
								.conditionally(AppledrnessLootConditionType.builder(NumberRange.IntRange.atMost(-100), AppledrnessLootConditionType.Source.ATTACKING_ENTITY)))
						.with(EmptyEntry.builder().weight(10));
				builder.pool(applePool);
			}
		});

		Appledrness.register("having_apple_in_name", (world, player) -> player.getName().getString().toLowerCase(Locale.ROOT).contains("apple") ? 100 : 0);
		Appledrness.register("being_appledr", (world, player) -> player.getName().getString().equals("AppleDr") ? 100 : 0);
		Appledrness.register("having_apples_in_inventory", (world, player) -> player.getInventory().count(Items.APPLE));
		// Lambda of CommandRegistrationCallback: void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment).
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