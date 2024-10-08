package net.pedroricardo;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.block.ShapeContext;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.EndPlatformFeature;
import net.pedroricardo.appledrness.Appledrness;
import net.pedroricardo.content.AppleDrDimension;
import net.pedroricardo.content.AppleDrItems;
import net.pedroricardo.content.AppleDrStatistics;
import net.pedroricardo.content.entity.AIEntityComponent;
import net.pedroricardo.content.worldgen.TheAppleEndBiomeSource;
import net.pedroricardo.loot.AppleDrLootConditions;
import net.pedroricardo.loot.AppledrnessLootConditionType;
import net.pedroricardo.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

public class AppleDrMod implements DedicatedServerModInitializer {
	public static final String MOD_ID = "appledrmod";
	public static final UUID APPLEDR_UUID = UUID.fromString("3bd4c790-aea5-47da-8963-7f907539889c");
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeServer() {
		AppleDrConfig.reload();

		AppleDrLootConditions.init();
		AppleDrItems.init();
		AppleDrStatistics.init();

		Appledrness.register("having_apple_in_name", (world, player) -> player.getName().getString().toLowerCase(Locale.ROOT).contains("apple") ? 50 : 0);
		Appledrness.register("being_appledr", (world, player) -> player.getName().getString().equals("AppleDr") ? 100 : 0);
		Appledrness.register("being_near_appledr", (world, player) -> {
			PlayerEntity appleDrPlayer = world.getPlayerByUuid(APPLEDR_UUID);
			if (appleDrPlayer == null) return 0;
			return player.distanceTo(appleDrPlayer) <= 50 ? 100 : 0;
		});
		Appledrness.register("having_apples_in_inventory", (world, player) -> player.getInventory().count(Items.APPLE) * 5);
		Appledrness.register("having_golden_apples_in_inventory", (world, player) -> player.getInventory().count(Items.GOLDEN_APPLE) * 20);
		Appledrness.register("having_enchanted_golden_apples_in_inventory", (world, player) -> player.getInventory().count(Items.ENCHANTED_GOLDEN_APPLE) * 30);
		Appledrness.register("wearing_apple_greathelm", (world, player) -> {
			if (player.getInventory().getArmorStack(EquipmentSlot.HEAD.getEntitySlotId()).isIn(AppleDrTags.Items.APPLE_GREATHELMS)) {
				return 250;
			}
			return 0;
		});
		Appledrness.register("eating_apples", (world, player) -> player.getStatHandler().getStat(Stats.USED, Items.APPLE));
		Appledrness.register("eating_golden_apples", (world, player) -> player.getStatHandler().getStat(Stats.USED, Items.GOLDEN_APPLE) * 5);
		Appledrness.register("eating_enchanted_golden_apples", (world, player) -> player.getStatHandler().getStat(Stats.USED, Items.ENCHANTED_GOLDEN_APPLE) * 20);
		Appledrness.register("eating_apple_pies", (world, player) -> player.getStatHandler().getStat(Stats.USED, AppleDrItems.APPLE_PIE) * 20);
		Appledrness.register("having_appledrs_grace", (world, player) -> player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(AppleDrStatistics.APPLEDRS_GRACE)) * 50);
		Appledrness.register("being_in_apple_end", (world, player) -> player.getWorld().getRegistryKey() == AppleDrDimension.WORLD ? 50 : 0);
		Appledrness.register("accepting_appledraltar_offers", (world, player) -> player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(AppleDrStatistics.APPLEDRALTAR_OFFERS_ACCEPTED)) * 10);
		Appledrness.register("rejecting_appledraltar_offers", (world, player) -> -player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(AppleDrStatistics.APPLEDRALTAR_OFFERS_REJECTED)) * 10);
		Appledrness.register("having_rotten_apples_in_inventory", (world, player) -> -player.getInventory().count(AppleDrItems.ROTTEN_APPLE) * 5);
		Appledrness.register("eating_rotten_apples", (world, player) -> -player.getStatHandler().getStat(Stats.USED, AppleDrItems.ROTTEN_APPLE));

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

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("appledrness")
					.executes(c -> {
						ServerPlayerEntity player = c.getSource().getPlayerOrThrow();
						int appledrness = Appledrness.getAppledrness(player.getWorld(), player);
						c.getSource().sendMessage(Text.translatable(Appledrlevels.getAppledrlevel(Appledrness.getAppledrness(player.getWorld(), player)).getAppledrnessTranslationKey(), Text.translatable("appledrmod.appledr_the_appledrful").formatted(Formatting.RED), appledrness, Text.translatable(Appledrlevels.getAppledrlevel(appledrness).getTranslationKey()).formatted(Formatting.GOLD)));
						return Command.SINGLE_SUCCESS;
					})
					.then(RequiredArgumentBuilder.<ServerCommandSource, EntitySelector>argument("player", EntityArgumentType.player())
							.executes(c -> {
								ServerPlayerEntity player = EntityArgumentType.getPlayer(c, "player");
								int appledrness = Appledrness.getAppledrness(player.getWorld(), player);
								c.getSource().sendMessage(Text.translatable(Appledrlevels.getAppledrlevel(Appledrness.getAppledrness(player.getWorld(), player)).getAppledrnessTranslationKey(), Text.translatable("appledrmod.appledr_the_appledrful").formatted(Formatting.RED), appledrness, Text.translatable(Appledrlevels.getAppledrlevel(appledrness).getTranslationKey()).formatted(Formatting.GOLD)));
								return Command.SINGLE_SUCCESS;
							})));
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("reload_appledr_settings")
					.requires(c -> c.hasPermissionLevel(2))
					.executes(c -> {
						AppleDrConfig.reload();
						return Command.SINGLE_SUCCESS;
					}));
		});

		ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
			if (message.isSenderMissing() || sender instanceof FakePlayer) return;
			AppleDrAI.findAIEntities(sender.getServer(), entity -> {
				if (message.getContent().getString().startsWith(AppleDrConfig.aiIgnorePrefix)) return false;
				return entity.getComponent(AIEntityComponent.COMPONENT).getPattern().matcher(message.getContent().getString()).find() || (entity.getComponent(AIEntityComponent.COMPONENT).respondWhenNear() && entity.getWorld() == sender.getServerWorld() && sender.distanceTo(entity) <= 32.0f);
			}).forEach(entity -> AppleDrAI.reply(entity, message));
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("ai")
					.requires(source -> source.hasPermissionLevel(2))
					.then(LiteralArgumentBuilder.<ServerCommandSource>literal("set")
							.then(RequiredArgumentBuilder.<ServerCommandSource, EntitySelector>argument("entity", EntityArgumentType.entity())
									.executes(c -> {
										Entity entity = EntityArgumentType.getEntity(c, "entity");
										Pattern pattern = AIEntityComponent.DEFAULT_PATTERN;
										String context = AIEntityComponent.DEFAULT_CONTEXT;
										boolean respondWhenNear = AIEntityComponent.DEFAULT_RESPOND_WHEN_NEAR;
										if (entity instanceof ServerPlayerEntity player) {
											if (AppleDrConfig.replacedPlayers.containsKey(player.getUuid())) {
												ReplacedPlayer replacedPlayer = AppleDrConfig.replacedPlayers.get(player.getUuid());
												pattern = replacedPlayer.pattern();
												context = replacedPlayer.context();
												respondWhenNear = replacedPlayer.respondWhenNear();
											}
											if (!(entity instanceof EntityPlayerMPFake)) {
												AppleDrAI.createPlayer(player, c.getSource().getServer(), pattern, context, respondWhenNear, UUID.randomUUID());
												c.getSource().sendMessage(Text.translatable("commands.appledr.create.success"));
												return Command.SINGLE_SUCCESS;
											}
										}

										AppleDrAI.create(EntityArgumentType.getEntity(c, "entity"), pattern, context, respondWhenNear);
										c.getSource().sendMessage(Text.translatable("commands.ai.create.success"));
										return Command.SINGLE_SUCCESS;
									})
									.then(RequiredArgumentBuilder.<ServerCommandSource, Boolean>argument("respondWhenNear", BoolArgumentType.bool())
											.executes(c -> {
												Entity entity = EntityArgumentType.getEntity(c, "entity");
												Pattern pattern = AIEntityComponent.DEFAULT_PATTERN;
												String context = AIEntityComponent.DEFAULT_CONTEXT;
												if (entity instanceof ServerPlayerEntity player) {
													if (AppleDrConfig.replacedPlayers.containsKey(player.getUuid())) {
														ReplacedPlayer replacedPlayer = AppleDrConfig.replacedPlayers.get(player.getUuid());
														pattern = replacedPlayer.pattern();
														context = replacedPlayer.context();
													}
													if (!(entity instanceof EntityPlayerMPFake)) {
														AppleDrAI.createPlayer(player, c.getSource().getServer(), pattern, context, BoolArgumentType.getBool(c, "respondWhenNear"), UUID.randomUUID());
														c.getSource().sendMessage(Text.translatable("commands.appledr.create.success"));
														return Command.SINGLE_SUCCESS;
													}
												}

												AppleDrAI.create(EntityArgumentType.getEntity(c, "entity"), pattern, context, BoolArgumentType.getBool(c, "respondWhenNear"));
												c.getSource().sendMessage(Text.translatable("commands.ai.create.success"));
												return Command.SINGLE_SUCCESS;
											})
											.then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("pattern", StringArgumentType.string())
													.executes(c -> {
														Entity entity = EntityArgumentType.getEntity(c, "entity");
														String context = AIEntityComponent.DEFAULT_CONTEXT;
														if (entity instanceof ServerPlayerEntity player) {
															if (AppleDrConfig.replacedPlayers.containsKey(player.getUuid())) {
																ReplacedPlayer replacedPlayer = AppleDrConfig.replacedPlayers.get(player.getUuid());
																context = replacedPlayer.context();
															}
															if (!(entity instanceof EntityPlayerMPFake)) {
																AppleDrAI.createPlayer(player, c.getSource().getServer(), Pattern.compile(StringArgumentType.getString(c, "pattern"), Pattern.CASE_INSENSITIVE), context, BoolArgumentType.getBool(c, "respondWhenNear"), UUID.randomUUID());
																c.getSource().sendMessage(Text.translatable("commands.appledr.create.success"));
																return Command.SINGLE_SUCCESS;
															}
														}

														AppleDrAI.create(EntityArgumentType.getEntity(c, "entity"), Pattern.compile(StringArgumentType.getString(c, "pattern"), Pattern.CASE_INSENSITIVE), AIEntityComponent.DEFAULT_CONTEXT, BoolArgumentType.getBool(c, "respondWhenNear"));
														c.getSource().sendMessage(Text.translatable("commands.ai.create.success"));
														return Command.SINGLE_SUCCESS;
													})
													.then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("context", StringArgumentType.greedyString())
															.executes(c -> {
																Entity entity = EntityArgumentType.getEntity(c, "entity");
																if (entity instanceof ServerPlayerEntity player && !(entity instanceof EntityPlayerMPFake)) {
																	AppleDrAI.createPlayer(player, c.getSource().getServer(), Pattern.compile(StringArgumentType.getString(c, "pattern"), Pattern.CASE_INSENSITIVE), StringArgumentType.getString(c, "context"), BoolArgumentType.getBool(c, "respondWhenNear"), UUID.randomUUID());
																	c.getSource().sendMessage(Text.translatable("commands.appledr.create.success"));
																	return Command.SINGLE_SUCCESS;
																}

																AppleDrAI.create(EntityArgumentType.getEntity(c, "entity"), Pattern.compile(StringArgumentType.getString(c, "pattern"), Pattern.CASE_INSENSITIVE), StringArgumentType.getString(c, "context"), BoolArgumentType.getBool(c, "respondWhenNear"));
																c.getSource().sendMessage(Text.translatable("commands.ai.create.success"));
																return Command.SINGLE_SUCCESS;
															}))))))
					.then(LiteralArgumentBuilder.<ServerCommandSource>literal("remove")
							.then(RequiredArgumentBuilder.<ServerCommandSource, EntitySelector>argument("entity", EntityArgumentType.entity())
									.executes(c -> {
										AppleDrAI.removeAI(EntityArgumentType.getEntity(c, "entity"));
										c.getSource().sendMessage(Text.translatable("commands.ai.remove.success"));
										return Command.SINGLE_SUCCESS;
									}))));
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("stat")
					.requires(source -> source.hasPermissionLevel(2))
					.then(RequiredArgumentBuilder.<ServerCommandSource, EntitySelector>argument("player", EntityArgumentType.player())
							.then(RequiredArgumentBuilder.<ServerCommandSource, Identifier>argument("stat", IdentifierArgumentType.identifier())
									.then(LiteralArgumentBuilder.<ServerCommandSource>literal("set")
											.then(RequiredArgumentBuilder.<ServerCommandSource, Integer>argument("value", IntegerArgumentType.integer())
													.executes(c -> {
														ServerPlayerEntity player = EntityArgumentType.getPlayer(c, "player");
														Identifier id = IdentifierArgumentType.getIdentifier(c, "stat");
														int value = IntegerArgumentType.getInteger(c, "value");
														try {
															player.getStatHandler().setStat(player, Stats.CUSTOM.getOrCreateStat(id), value);
														} catch (Exception e) {
															e.printStackTrace();
														}
														c.getSource().sendMessage(Text.translatable("commands.stat.set", id.toString(), value));
														return Command.SINGLE_SUCCESS;
													}))))));
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("grace")
					.requires(source -> source.hasPermissionLevel(2))
					.then(RequiredArgumentBuilder.<ServerCommandSource, EntitySelector>argument("player", EntityArgumentType.player())
							.executes(c -> {
								ServerPlayerEntity player = EntityArgumentType.getPlayer(c, "player");
								if (player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(AppleDrStatistics.APPLEDRS_GRACE)) > 0) {
									c.getSource().sendError(Text.translatable("commands.grace.error"));
									return 0;
								} else {
									player.incrementStat(Stats.CUSTOM.getOrCreateStat(AppleDrStatistics.APPLEDRS_GRACE));
									c.getSource().sendMessage(Text.translatable("commands.grace.success", player.getName()));
									return Command.SINGLE_SUCCESS;
								}
							})));
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("unstuck")
					.executes(c -> {
						ServerPlayerEntity player = c.getSource().getPlayerOrThrow();
						if (player.getWorld().getRegistryKey() == AppleDrDimension.WORLD || player.getWorld().getRegistryKey() == AppleDrDimension.WORLD_1E8BF29C9C6240B2A7AAE7D226DF8486) {
							player.teleportTo(new TeleportTarget(c.getSource().getServer().getWorld(World.OVERWORLD), player, TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)));
						}
						return Command.SINGLE_SUCCESS;
					}));
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("appleend")
					.requires(source -> source.hasPermissionLevel(2))
					.then(RequiredArgumentBuilder.<ServerCommandSource, EntitySelector>argument("player", EntityArgumentType.player())
							.executes(c -> {
								ServerPlayerEntity player = EntityArgumentType.getPlayer(c, "player");
                                if (player.getWorld().getRegistryKey() == AppleDrDimension.WORLD) {
									player.teleportTo(new TeleportTarget(c.getSource().getServer().getWorld(World.OVERWORLD), player, TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)));
									c.getSource().sendMessage(Text.translatable("commands.appleend.success.overworld", player.getName()));
									return Command.SINGLE_SUCCESS;
								} else if (Math.abs(Appledrness.getAppledrness(player.getWorld(), player)) >= AppleDrConfig.appleEndAppledrness) {
									EndPlatformFeature.generate(c.getSource().getServer().getWorld(AppleDrDimension.WORLD), BlockPos.ORIGIN.add(0, 60, 0).down(), true);
									player.teleportTo(new TeleportTarget(c.getSource().getServer().getWorld(AppleDrDimension.WORLD), BlockPos.ORIGIN.add(0, 60, 0).toCenterPos(), Vec3d.ZERO, 0.0f, 0.0f, TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)));
									c.getSource().sendMessage(Text.translatable("commands.appleend.success", player.getName()));
									return Command.SINGLE_SUCCESS;
								}
								c.getSource().sendError(Text.translatable("commands.appleend.error", player.getName()));
								return 0;
							})));
		});

		ServerTickEvents.START_SERVER_TICK.register(server -> {
			server.getWorlds().forEach(world -> {
				ServerPlayerEntity player = world.getRandomAlivePlayer();
				if (player == null) {
					return;
				}
				int appledrness = Appledrness.getAppledrness(world, player);
				Appledrlevel appledrlevel = Appledrlevels.getAppledrlevel(appledrness);
				if (appledrlevel != Appledrlevels.SPROUT && world.getRandom().nextDouble() < (1.0/16384.0) * MathHelper.sqrt((MathHelper.abs(appledrlevel.getLevel())) / 2000.0f)) {
					world.spawnEntity(new ItemEntity(world, player.getX(), Math.min(world.raycast(new RaycastContext(player.getPos(), player.getPos().add(0.0, 30.0, 0.0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, ShapeContext.absent())).getBlockPos().down().getY(), player.getY() + 30.0), player.getZ(), new ItemStack(appledrlevel.getLevel() < 0 ? AppleDrItems.ROTTEN_APPLE : Items.APPLE)));
				}
			});
		});

		Registry.register(Registries.BIOME_SOURCE, Identifier.of(MOD_ID, "the_apple_end"), TheAppleEndBiomeSource.CODEC);

		ResourcePackUtil.bootstrap();
		PolymerResourcePackUtils.addModAssets(MOD_ID);
		PolymerResourcePackUtils.markAsRequired();
	}
}