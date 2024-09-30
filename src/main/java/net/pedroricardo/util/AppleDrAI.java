package net.pedroricardo.util;

import carpet.patches.EntityPlayerMPFake;
import carpet.patches.FakeClientConnection;
import com.mojang.authlib.GameProfile;
import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.common.function.FunctionExecutor;
import io.github.sashirestela.openai.common.tool.Tool;
import io.github.sashirestela.openai.domain.chat.Chat;
import io.github.sashirestela.openai.domain.chat.ChatMessage;
import io.github.sashirestela.openai.domain.chat.ChatRequest;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.UserCache;
import net.minecraft.util.Uuids;
import net.pedroricardo.content.entity.AIEntity;
import net.pedroricardo.content.entity.AIEntityComponent;
import net.pedroricardo.mixin.EntityAccessor;
import net.pedroricardo.mixin.EntityManagerAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@SuppressWarnings("ReferenceToMixin")
public class AppleDrAI {
    public static final Map<Entity, List<ChatMessage>> CHAT_MEMORY_MAP = new HashMap<>();
    public static final SimpleOpenAI openAI = SimpleOpenAI.builder().apiKey(AppleDrConfig.openAIApiKey).build();

    public static ChatMessage.ResponseMessage respondSilently(ChatMessage message, Entity entity) {
        return respondSilently(message, entity, null);
    }

    public static ChatMessage.ResponseMessage respondSilently(ChatMessage message, Entity entity, AITools tools) {
        final List<ChatMessage> memory = CHAT_MEMORY_MAP.computeIfAbsent(entity, e -> new ArrayList<>());
        memory.add(message);
        FunctionExecutor functionExecutor = new FunctionExecutor();
        if (tools != null) {
            functionExecutor.enrollFunctions(tools.getTools());
        }
        List<Tool> toolList = functionExecutor.getToolFunctions();
        Chat chat = openAI.chatCompletions().create(ChatRequest.builder()
                .model("gpt-4o-mini")
                .message(ChatMessage.SystemMessage.of(entity.getComponent(AIEntityComponent.COMPONENT).getContext()))
                .messages(memory)
                .tools(toolList)
                .build()).join();

        ChatMessage.ResponseMessage response = chat.firstMessage();
        if (response.getToolCalls() != null) {
            memory.add(response);
            response.getToolCalls().forEach(call -> {
                var result = functionExecutor.execute(call.getFunction());
                memory.add(ChatMessage.ToolMessage.of(result.toString(), call.getId()));
            });
        }
        chat = openAI.chatCompletions().create(ChatRequest.builder()
                .model("gpt-4o-mini")
                .message(ChatMessage.SystemMessage.of(entity.getComponent(AIEntityComponent.COMPONENT).getContext()))
                .messages(memory)
                .build()).join();
        ChatMessage.ResponseMessage finalResponse = chat.firstMessage();
        memory.add(finalResponse);
        return finalResponse;
    }

    public static ChatMessage.ResponseMessage respond(MinecraftServer server, ChatMessage message, Entity entity, AITools tools) {
        ChatMessage.ResponseMessage response = respondSilently(message, entity, tools);
        String str = response.getContent();
        FakePlayer player = entity instanceof AIEntity aiEntity ? aiEntity.getAsPlayer() : FakePlayer.get((ServerWorld) entity.getWorld(), new GameProfile(entity.getUuid(), entity.getName().getString()));
        server.getPlayerManager().broadcast(SignedMessage.ofUnsigned(str), player, MessageType.params(MessageType.CHAT, player));
        return response;
    }

    public static void reply(Entity entity, SignedMessage message) {
        if (!entity.getComponent(AIEntityComponent.COMPONENT).shouldRespond()) return;
        new Thread(() -> {
            ServerPlayerEntity player = entity.getServer().getPlayerManager().getPlayer(message.getSender());
            String name;
            if (player == null) {
                name = "Unknown player: ";
            } else {
                name = String.format("%s: ", player.getName().getString());
            }

            AppleDrAI.respond(entity.getServer(), ChatMessage.UserMessage.of(name + message.getContent().getString()), entity, entity instanceof EntityPlayerMPFake ? new PlayerAITools((EntityPlayerMPFake) entity, entity.getServer()) : null);
        }).start();
    }

    public static List<Entity> findAIEntities(MinecraftServer server, Predicate<Entity> predicate) {
        List<Entity> list = new ArrayList<>();
        server.getWorlds().forEach(world -> {
            for (Entity entity : ((EntityManagerAccessor) world).entityManager().getLookup().iterate()) {
                if (entity.getComponent(AIEntityComponent.COMPONENT).shouldRespond() && predicate.test(entity)) {
                    list.add(entity);
                }
            }
        });
        return list;
    }

    public static Entity create(Entity entity, Pattern pattern, String context, boolean respondWhenNear) {
        if (entity instanceof ServerPlayerEntity && !(entity instanceof EntityPlayerMPFake)) {
            throw new IllegalArgumentException("Entity must not be a player! Use createPlayer for players.");
        }
        AIEntityComponent component = entity.getComponent(AIEntityComponent.COMPONENT);
        component.setRespondWhenNear(respondWhenNear);
        component.setPattern(pattern);
        component.setContext(context);
        component.setShouldRespond(true);
        return entity;
    }

    public static void removeAI(Entity entity) {
        entity.getComponent(AIEntityComponent.COMPONENT).setShouldRespond(false);
    }

    public static void createPlayer(final ServerPlayerEntity player, MinecraftServer server, Pattern pattern, String context, boolean respondWhenNear, @Nullable UUID uuid) {
        ServerWorld worldIn = server.getWorld(player.getServerWorld().getRegistryKey());
        UserCache.setUseRemote(false);
        GameProfile profile;
        try {
            profile = server.getUserCache().findByName(player.getGameProfile().getName()).orElse(null);
        } finally {
            UserCache.setUseRemote(server.isDedicated() && server.isOnlineMode());
        }
        if (profile == null) {
            profile = new GameProfile(Uuids.getOfflinePlayerUuid(player.getGameProfile().getName()), player.getGameProfile().getName());
        }
        GameProfile finalGP = profile;
        SkullBlockEntity.fetchProfileByName(profile.getName()).thenAcceptAsync(p -> {
            GameProfile current = finalGP;
            if (p.isPresent()) {
                current = p.get();
            }
            if (uuid != null) {
                GameProfile newProfile = new GameProfile(uuid, current.getName());
                newProfile.getProperties().clear();
                current.getProperties().forEach((s, property) -> newProfile.getProperties().put(s, property));
                current = newProfile;
            }
            EntityPlayerMPFake instance = EntityPlayerMPFake.respawnFake(server, worldIn, current, player.getClientOptions());
            instance.getAttributes().setFrom(player.getAttributes());
            instance.fixStartingPosition = () -> instance.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
            server.getPlayerManager().onPlayerConnect(new FakeClientConnection(NetworkSide.SERVERBOUND), instance, new ConnectedClientData(current, 0, instance.getClientOptions(), false));
            instance.teleport(worldIn, player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
            instance.setHealth(player.getHealth());
            ((EntityAccessor) instance).invokeUnsetRemoved();
            instance.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT).setBaseValue(0.6F);
            instance.interactionManager.changeGameMode(player.interactionManager.getGameMode());
            server.getPlayerManager().sendToDimension(new EntitySetHeadYawS2CPacket(instance, (byte) (instance.headYaw * 256 / 360)), player.getServerWorld().getRegistryKey());
            server.getPlayerManager().sendToDimension(new EntityPositionS2CPacket(instance), player.getServerWorld().getRegistryKey());
//            instance.getDataTracker().set(PlayerModelPartsAccessor.playerModelParts(), player.getDataTracker().get(PlayerModelPartsAccessor.playerModelParts()));
            instance.getAbilities().flying = player.getAbilities().flying;
            create(instance, pattern, context, respondWhenNear);
        }, server);
    }

    public static void createPlayer(final ServerPlayerEntity player, MinecraftServer server, Pattern pattern, String context, boolean respondWhenNear) {
        createPlayer(player, server, pattern, context, respondWhenNear, null);
    }
}
