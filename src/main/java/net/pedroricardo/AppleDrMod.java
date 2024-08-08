package net.pedroricardo;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.pedroricardo.appledrness.Appledrness;
import net.pedroricardo.content.AppleDrEntityTypes;
import net.pedroricardo.content.AppleDrItems;
import net.pedroricardo.loot.AppleDrLootConditions;
import net.pedroricardo.loot.AppledrnessLootConditionType;
import net.pedroricardo.util.AppleDrAI;
import net.pedroricardo.util.AppleDrConfig;
import net.pedroricardo.util.AppleDrTags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class AppleDrMod implements DedicatedServerModInitializer {
	public static final String MOD_ID = "appledrmod";
	public static final UUID APPLEDR_UUID = UUID.fromString("3bd4c790-aea5-47da-8963-7f907539889c");
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static HashMap<PlayerEntity, Appledrness> playersDrness;

	@Override
	public void onInitializeServer() {
		AppleDrLootConditions.init();
		AppleDrItems.init();
		AppleDrEntityTypes.init();

		LootTableEvents.MODIFY.register((key, builder, source) -> {
			if (source.isBuiltin() && key == EntityType.PLAYER.getLootTableId()) {
				LootPool.Builder applePool = LootPool.builder()
						.with(ItemEntry.builder(Items.APPLE).weight(1)
						.conditionally(AppledrnessLootConditionType.builder(NumberRange.IntRange.atLeast(300), AppledrnessLootConditionType.Source.THIS))
								.conditionally(AppledrnessLootConditionType.builder(NumberRange.IntRange.atMost(-100), AppledrnessLootConditionType.Source.ATTACKING_ENTITY)))
						.with(EmptyEntry.builder().weight(10));
				LootPool.Builder rottenApplePool = LootPool.builder()
						.with(ItemEntry.builder(AppleDrItems.ROTTEN_APPLE).weight(1)
								.conditionally(AppledrnessLootConditionType.builder(NumberRange.IntRange.atMost(-300), AppledrnessLootConditionType.Source.THIS))
								.conditionally(AppledrnessLootConditionType.builder(NumberRange.IntRange.atLeast(100), AppledrnessLootConditionType.Source.ATTACKING_ENTITY)))
						.with(EmptyEntry.builder().weight(10));

				NbtCompound nbt = new NbtCompound();
				nbt.putUuid(PlayerEntity.UUID_KEY, APPLEDR_UUID);
				LootPool.Builder appleGreathelmPool = LootPool.builder()
						.with(ItemEntry.builder(AppleDrItems.APPLE_GREATHELM).weight(1)
								.conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().nbt(new NbtPredicate(nbt)))))
						.with(EmptyEntry.builder().weight(10));

				builder.pool(applePool).pool(rottenApplePool).pool(appleGreathelmPool);
			}
		});

		Appledrness.register("having_apple_in_name", (world, player) -> player.getName().getString().toLowerCase(Locale.ROOT).contains("apple") ? 50 : 0);
		Appledrness.register("being_appledr", (world, player) -> player.getName().getString().equals("AppleDr") ? 100 : 0);
		Appledrness.register("having_apples_in_inventory", (world, player) -> player.getInventory().count(Items.APPLE) * 5);
		Appledrness.register("wearing_apple_greathelm", (world, player) -> {
			if (player.getInventory().getArmorStack(EquipmentSlot.HEAD.getEntitySlotId()).isIn(AppleDrTags.Items.APPLE_GREATHELMS)) {
				return 250;
			}
			return 0;
		});
		Appledrness.register("having_rotten_apples_in_inventory", (world, player) -> -player.getInventory().count(AppleDrItems.ROTTEN_APPLE) * 5);

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

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("appledr")
					.then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("message", StringArgumentType.greedyString())
							.executes(c -> {
								String key = AppleDrConfig.getValue("openai_api_key", "");

								if (key.isEmpty()) {
									return 0;
								}

								new Thread(() -> {
									try {
										JsonObject object = AppleDrAI.sendMessageRequest(key, StringArgumentType.getString(c, "message"));
										String message = object.getAsJsonArray("choices").get(0).getAsJsonObject().getAsJsonObject("message").get("content").getAsString();
										FakePlayer player = FakePlayer.get(c.getSource().getWorld(), new GameProfile(APPLEDR_UUID, "AppleDr"));
										c.getSource().getServer().getPlayerManager().broadcast(SignedMessage.ofUnsigned(message), player, MessageType.params(MessageType.CHAT, player));
									} catch (IOException ignored) {
									}
								}).start();

                                return Command.SINGLE_SUCCESS;
							})));
		});
	}
}