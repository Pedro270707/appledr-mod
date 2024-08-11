package net.pedroricardo;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
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
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.pedroricardo.appledrness.Appledrness;
import net.pedroricardo.content.AppleDrEntityTypes;
import net.pedroricardo.content.AppleDrItems;
import net.pedroricardo.content.AppleDrStatistics;
import net.pedroricardo.content.entity.AppleDrEntity;
import net.pedroricardo.loot.AppleDrLootConditions;
import net.pedroricardo.loot.AppledrnessLootConditionType;
import net.pedroricardo.mixin.EntityManagerAccessor;
import net.pedroricardo.util.AppleDrTags;
import net.pedroricardo.util.Appledrlevels;
import net.pedroricardo.util.ResourcePackUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppleDrMod implements DedicatedServerModInitializer {
	public static final String MOD_ID = "appledrmod";
	public static final UUID APPLEDR_UUID = UUID.fromString("3bd4c790-aea5-47da-8963-7f907539889c");
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeServer() {
		AppleDrLootConditions.init();
		AppleDrItems.init();
		AppleDrEntityTypes.init();
		AppleDrStatistics.init();

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
		Appledrness.register("eating_apples", (world, player) -> player.getStatHandler().getStat(Stats.USED, Items.APPLE));
		Appledrness.register("accepting_appledraltar_offers", (world, player) -> player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(AppleDrStatistics.APPLEDRALTAR_OFFERS_ACCEPTED)) * 10);
		Appledrness.register("rejecting_appledraltar_offers", (world, player) -> -player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(AppleDrStatistics.APPLEDRALTAR_OFFERS_REJECTED)) * 10);
		Appledrness.register("having_rotten_apples_in_inventory", (world, player) -> -player.getInventory().count(AppleDrItems.ROTTEN_APPLE) * 5);
		Appledrness.register("eating_rotten_apples", (world, player) -> -player.getStatHandler().getStat(Stats.USED, AppleDrItems.ROTTEN_APPLE));

		// Lambda of CommandRegistrationCallback: void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment).
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("appledrness")
					.then(RequiredArgumentBuilder.<ServerCommandSource, EntitySelector>argument("player", EntityArgumentType.player())
							.executes(c -> {
								ServerPlayerEntity player = EntityArgumentType.getPlayer(c, "player");
								c.getSource().sendMessage(Text.translatable(Appledrlevels.getAppledrlevel(Appledrness.getAppledrness(player.getWorld(), player)).getAppledrnessTranslationKey(), Text.translatable("appledrmod.appledr_the_appledrful").formatted(Formatting.RED), Appledrness.getAppledrness(player.getWorld(), player)));
								return Command.SINGLE_SUCCESS;
							})));
		});

		ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
			if (message.getSender().equals(APPLEDR_UUID) || message.isSenderMissing() || sender instanceof FakePlayer) return;
			Pattern pattern = Pattern.compile("(Apple|Domenic)", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(message.getContent().getString());
			if (matcher.find()) {
				for (Entity entity : ((EntityManagerAccessor) sender.getServerWorld()).entityManager().getLookup().iterate()) {
					if (entity instanceof AppleDrEntity appleDr) {
						appleDr.messagesReceived.add(message);
					}
				}
			} else {
				for (AppleDrEntity entity : sender.getWorld().getEntitiesByType(TypeFilter.equals(AppleDrEntity.class), sender.getBoundingBox().expand(32.0f), (appleDr) -> true)) {
					entity.messagesReceived.add(message);
				}
			}
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("appledr")
					.requires(source -> source.isExecutedByPlayer() && source.hasPermissionLevel(2))
					.executes(c -> {
						c.getSource().getWorld().spawnEntity(new AppleDrEntity(c.getSource().getPlayer().getServerWorld(), c.getSource().getPlayer()));
						return Command.SINGLE_SUCCESS;
					})
					.then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("context", StringArgumentType.greedyString())
							.executes(c -> {
								AppleDrEntity appleDr = new AppleDrEntity(c.getSource().getPlayer().getServerWorld(), c.getSource().getPlayer());
								appleDr.setInitialMessageContext(StringArgumentType.getString(c, "context"));
								c.getSource().getWorld().spawnEntity(appleDr);
								return Command.SINGLE_SUCCESS;
							})));
		});

		ResourcePackUtil.bootstrap();
		PolymerResourcePackUtils.addModAssets(MOD_ID);
		PolymerResourcePackUtils.markAsRequired();
	}
}