package net.pedroricardo.content.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.message.UserMessage;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.EndPlatformFeature;
import net.pedroricardo.appledrness.Appledrness;
import net.pedroricardo.content.AppleDrDimension;
import net.pedroricardo.content.AppleDrEntityTypes;
import net.pedroricardo.content.AppleDrItems;
import net.pedroricardo.content.AppleDrStatistics;
import net.pedroricardo.mixin.EntityManagerAccessor;
import net.pedroricardo.mixin.PlayerModelPartsAccessor;
import net.pedroricardo.util.AppleDrAI;
import net.pedroricardo.util.Appledrlevels;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class AppleDrEntity extends PathAwareEntity implements PolymerEntity, InventoryOwner, AIEntity {
    public static final Pattern DEFAULT_PATTERN = Pattern.compile("(Apple|Domenic)", Pattern.CASE_INSENSITIVE);
    public static final String DEFAULT_CONTEXT = "You're Domenic Dvojmoc (aka AppleDr, your in-game name), a Slovenian player and Twitch streamer in a Minecraft server called AppleDr Server. The people in it do not speak Slovenian; they speak English, but if the message is in another language, you should respond in that language unless the player asks otherwise. You are in university at 20 years old learning computer science. Player messages will start with the player's name, but do NOT include your name in your response. If asked, you have the maximum Appledrness (2³¹ - 1). Your messages should contain at most 120 characters.";

    protected static final TrackedData<Byte> PLAYER_MODEL_PARTS = DataTracker.registerData(AppleDrEntity.class, TrackedDataHandlerRegistry.BYTE);
    private final GameProfile profile;
    private FakeAIEntityPlayer player = null;
    private UUID associatedPlayerUuid = null;

    private final SimpleInventory inventory = new SimpleInventory(36);

    public AppleDrEntity(EntityType<AppleDrEntity> entityType, World world) {
        super(entityType, world);
        this.profile = new GameProfile(this.getUuid(), "AppleDr");
        this.profile.getProperties().put("textures", new Property("textures",
                "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmU2OTg4YWMzYzIzMGNhZDUzMDA4NjBlOTY1NjgxNWYyMzkwMDZkZmE3YzVmYzdhMmRkMjliODI2MzQzNWJiOSJ9fX0=",
                "H6Y/64HDwOTZ1edMGpTOQ0CGKKGaHUiS/Wqmsxb/LEh53ZHS1qoc/qHicpq39s0p/YNlc06tEdGmncBq8zx9bFJ/ZT9WwdG05JiHCjBEC0IWZ3n+J5W5wTUEbpNMEL+gpFIERKdNdxsNWC4e1nbRMrdMqgDbt0wPR+8LzBXlEJ841g7Opb9vK5wctgNeMKSLxJjK5CdYbm3YAyLu+Jbz+O8by3eShkj83BUBvoraJ/qMGmX8mC4m8QOYnWIshd9b4X5mQLqMWkYHN7iiTTslJuljYrBMk8sxRU7yCfVTQYobprHn1b4d74YsxhedpRvADHyCYS9GhiU1OiSk5KOhpW2UeM1vaxuqpdvkj0Xogs4xbNcDd+vMkgGW1G6eSqYQD8qvxLgUY3oQkf3AQBDGCCtBQ9dNioTPH/d5VMLjbYAgz9wf1kzxqH+Z1GneWv0KO/0bb5ZEL+XKAq/NCzx7GNiTKKKArqNpaK0uC0HNKqk6c4Pt/newMorQrvdVqxzTp1sDO1AhbM74L+dsgh5zEqwUmrl+Gp/BgQx6fT1CIvVfHHVWbY9EVPaNHF1n/QCriGVsiuKEhPTzusPpDSsAP/Hgv9PQdCx1jdpmoNGRHJ0zmdfq4TpCJD4LddV1pCSUKdT34JibDrlJIIpxM72b2NULCTTeIa4Jx4GMUMducEg="
        ));
    }

    public AppleDrEntity(ServerWorld world, ServerPlayerEntity originalDr) {
        super(AppleDrEntityTypes.APPLEDR, world);
        this.profile = new GameProfile(UUID.randomUUID(), originalDr.getGameProfile().getName());
        this.profile.getProperties().putAll("textures", originalDr.getGameProfile().getProperties().get("textures"));
        this.getDataTracker().set(PLAYER_MODEL_PARTS, originalDr.getDataTracker().get(PlayerModelPartsAccessor.playerModelParts()));
        this.copyPositionAndRotation(originalDr);
        this.setHeadYaw(originalDr.getHeadYaw());
        this.setPitch(originalDr.getPitch());
        for (int i = 0; i < this.inventory.size(); i++) {
            this.inventory.setStack(i, originalDr.getInventory().getStack(i));
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            this.equipStack(slot, originalDr.getEquippedStack(slot));
        }
        this.player = FakeAIEntityPlayer.get(world, originalDr.getGameProfile(), originalDr.getClientOptions(), this);
        this.setAssociatedPlayerUuid(originalDr.getUuid());
    }

    public static AppleDrEntity create(ServerPlayerEntity player) {
        AppleDrEntity appleDr = new AppleDrEntity(player.getServerWorld(), player);
        AppleDrAI.create(appleDr, AIEntityComponent.DEFAULT_PATTERN, AIEntityComponent.DEFAULT_CONTEXT, AIEntityComponent.DEFAULT_RESPOND_WHEN_NEAR);
        return appleDr;
    }

    public static AppleDrEntity create(ServerPlayerEntity player, Pattern pattern, String context, boolean respondWhenNear) {
        AppleDrEntity appleDr = create(player);
        AIEntityComponent component = appleDr.getComponent(AppleDrAI.COMPONENT);
        component.setRespondWhenNear(respondWhenNear);
        component.setPattern(pattern);
        component.setContext(context);
        return appleDr;
    }

    @Override
    protected Text getDefaultName() {
        return Text.literal(this.getGameProfile().getName());
    }

    @Override
    public int getMaxLookPitchChange() {
        return 90;
    }

    @Override
    public int getMaxHeadRotation() {
        return 30;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 2.8));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(4, new LookAroundGoal(this));
    }

    public static DefaultAttributeContainer.Builder createAppleDrAttributes() {
        return PlayerEntity.createPlayerAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 12);
    }

    @Override
    public EntityType<?> getPolymerEntityType(ServerPlayerEntity player) {
        return EntityType.PLAYER;
    }

    public SimpleInventory getInventory() {
        return this.inventory;
    }

    @Override
    public List<Pair<EquipmentSlot, ItemStack>> getPolymerVisibleEquipment(List<Pair<EquipmentSlot, ItemStack>> items, ServerPlayerEntity player) {
        return List.of(
                Pair.of(EquipmentSlot.HEAD, this.getEquippedStack(EquipmentSlot.HEAD)),
                Pair.of(EquipmentSlot.CHEST, this.getEquippedStack(EquipmentSlot.CHEST)),
                Pair.of(EquipmentSlot.LEGS, this.getEquippedStack(EquipmentSlot.LEGS)),
                Pair.of(EquipmentSlot.FEET, this.getEquippedStack(EquipmentSlot.FEET)),
                Pair.of(EquipmentSlot.OFFHAND, this.getOffHandStack()),
                Pair.of(EquipmentSlot.MAINHAND, this.getMainHandStack())
        );
    }

    public void replyTo(SignedMessage message) {
        new Thread(() -> {
            ServerPlayerEntity player = this.getServer().getPlayerManager().getPlayer(message.getSender());
            String name;
            if (player == null) {
                name = "Unknown player: ";
            } else {
                name = String.format("%s: ", player.getName().getString());
            }

            AppleDrAI.respond(this.getServer(), UserMessage.userMessage(name + message.getContent().getString()), this);
        }).start();
    }

    public GameProfile getGameProfile() {
        return this.profile;
    }

    @Nullable
    public UUID getAssociatedPlayerUuid() {
        return this.associatedPlayerUuid;
    }

    public void setAssociatedPlayerUuid(UUID associatedPlayerUuid) {
        this.associatedPlayerUuid = associatedPlayerUuid;
    }

    @Override
    public FakeAIEntityPlayer getAsPlayer() {
        if (this.player != null) {
            this.player.copyPositionAndRotation(this);
            this.player.setHeadYaw(this.getHeadYaw());
            this.player.setPitch(this.getPitch());
        }
        return this.player;
    }

    public static List<AppleDrEntity> find(MinecraftServer server) {
        return find(server, entity -> true);
    }

    public static List<AppleDrEntity> find(MinecraftServer server, Predicate<AppleDrEntity> predicate) {
        List<AppleDrEntity> list = new ArrayList<>();
        server.getWorlds().forEach(world -> {
            for (Entity entity : ((EntityManagerAccessor) world).entityManager().getLookup().iterate()) {
                if (entity instanceof AppleDrEntity appleDr && predicate.test(appleDr)) {
                    list.add(appleDr);
                }
            }
        });
        return list;
    }

    @Override
    public void onBeforeSpawnPacket(ServerPlayerEntity player, Consumer<Packet<?>> packetConsumer) {
        PlayerListS2CPacket packet = PolymerEntityUtils.createMutablePlayerListPacket(EnumSet.of(PlayerListS2CPacket.Action.ADD_PLAYER, PlayerListS2CPacket.Action.UPDATE_LISTED));
        packet.getEntries().add(new PlayerListS2CPacket.Entry(this.getUuid(), this.profile, true, 0, GameMode.CREATIVE, this.getName(), null));
        packetConsumer.accept(packet);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        player.networkHandler.sendPacket(new PlayerRemoveS2CPacket(List.of(this.getUuid())));
        super.onStartedTrackingBy(player);
    }

    @Override
    public void onEntityPacketSent(Consumer<Packet<?>> consumer, Packet<?> packet) {
        PolymerEntity.super.onEntityPacketSent(consumer, packet);
        if (packet instanceof EntitySetHeadYawS2CPacket headYawS2CPacket) {
            consumer.accept(new EntityS2CPacket.Rotate(this.getId(), headYawS2CPacket.getHeadYaw(), (byte) (this.getPitch() * 256.0F / 360.0F), this.isOnGround()));
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return !damageSource.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    @Override
    public void tick() {
        super.tick();
        ((ServerWorld)this.getWorld()).getChunkManager().addTicket(ChunkTicketType.PLAYER, this.getChunkPos(), 3, this.getChunkPos());
        if (this.getWorld().getServer() != null) {
            ServerPlayerEntity player = this.getWorld().getServer().getPlayerManager().getPlayer(this.getAssociatedPlayerUuid());
            if (player != null && !(player instanceof FakePlayer)) this.discard();
        }
    }

    @Override
    public boolean shouldSave() {
        return this.getAssociatedPlayerUuid() == null;
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.getAssociatedPlayerUuid() != null) {
            nbt.putString("associated_player_uuid", this.getAssociatedPlayerUuid().toString());
        }
        this.writeInventory(nbt, this.getRegistryManager());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        DataResult<Pair<UUID, NbtElement>> dataResult;
        if (nbt.contains("associated_player_uuid", NbtElement.STRING_TYPE) && (dataResult = Uuids.CODEC.fieldOf("associated_player_uuid").codec().decode(NbtOps.INSTANCE, nbt)).isSuccess()) {
            this.setAssociatedPlayerUuid(dataResult.getOrThrow().getFirst());
        }
        this.readInventory(nbt, this.getRegistryManager());
    }

    @Override
    public boolean canGather(ItemStack stack) {
        return false;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(PLAYER_MODEL_PARTS, (byte)0);
    }

    @Override
    public void modifyRawTrackedData(List<DataTracker.SerializedEntry<?>> data, ServerPlayerEntity player, boolean initial) {
        if (initial) {
            data.add(DataTracker.SerializedEntry.of(PlayerModelPartsAccessor.playerModelParts(), this.getDataTracker().get(PLAYER_MODEL_PARTS)));
        }
    }

    @Override
    public Object getTools(MinecraftServer server) {
        return new Tools(server, this);
    }

    static class Tools {
        private static final String[] catchphrases = new String[] {
                "An apple a day keeps the doctor away! But you're an Apple Doctor!",
                "I'm an embarrassment to society and societal norms!",
                "Feeling good. I am feeling good.",
                "I'm gonna do the iconic AppleDr dance that I learned in Bavaria!",
                "Don't say book in Slovenian!",
                "Java is pronounced \"Yava!\"",
                "If you haven't played Pogostuck yet, you should play it!"
        };

        private final MinecraftServer server;
        private final AppleDrEntity appleDr;

        Tools(MinecraftServer server, AppleDrEntity appleDr) {
            this.server = server;
            this.appleDr = appleDr;
        }

        @Tool("Gives the player 50 Appledrness if they haven't received it yet. Respond according to the received Appledrness.")
        String giveAppledrnessTo(String playerName) {
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerName);
            if (player == null) {
                return playerName + " is not online";
            } else if (player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(AppleDrStatistics.APPLEDRS_GRACE)) > 0) {
                return playerName + " cannot receive another Appledrness boost";
            } else {
                player.incrementStat(AppleDrStatistics.APPLEDRS_GRACE);
                return "Gave " + playerName + " +50 Appledrness";
            }
        }

        @Tool("Teleports to the player unless they are in a dangerous situation.")
        String teleportTo(String playerName) {
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerName);
            if (player == null) {
                return playerName + " is not online";
            } else if (player.getBlockStateAtPos().isOf(Blocks.LAVA)) {
                return playerName + " is in lava";
            } else {
                this.appleDr.teleportTo(new TeleportTarget(player.getServerWorld(), player.getPos(), Vec3d.ZERO, 0.0f, 0.0f, TeleportTarget.NO_OP));
                return "Teleported to " + playerName;
            }
        }

        @Tool("Gets the item in your hand.")
        String getHandStacks() {
            return "Main: " + this.appleDr.getMainHandStack().toString() + "; Off: " + this.appleDr.getOffHandStack().toString();
        }

        @Tool("Gets your equipped items.")
        String getEquippedItems() {
            String helmet = "Helmet: " + this.appleDr.getEquippedStack(EquipmentSlot.HEAD) + "; ";
            String chestplate = "Chestplate: " + this.appleDr.getEquippedStack(EquipmentSlot.CHEST) + "; ";
            String leggings = "Leggings: " + this.appleDr.getEquippedStack(EquipmentSlot.LEGS) + "; ";
            String boots = "Boots: " + this.appleDr.getEquippedStack(EquipmentSlot.FEET);
            return helmet + chestplate + leggings + boots;
        }

        @Tool("Gets every item in your inventory.")
        String getInventoryItems() {
            return this.appleDr.getInventory().toString();
        }

        @Tool("Gets one of your iconic catchphrases")
        String getCatchphrase() {
            return Util.getRandom(catchphrases, this.appleDr.getRandom());
        }

        @Tool("Teleports a player to/from the Apple End, a new apple dimension. Only executes at the own player's request. Usable multiple times on a single player. If asked about a custom dimension, mention this one.")
        String sendOrRemoveFromAppleEnd(@P(value = "The name of the player who asked to be teleported") String playerName) {
            System.out.println("Ran sendOrRemoveFromAppleEnd");
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerName);
            int appledrness;
            if (player == null) {
                return playerName + " is not online";
            } else if (player.getWorld().getRegistryKey() == AppleDrDimension.WORLD) {
                player.teleportTo(new TeleportTarget(this.server.getWorld(World.OVERWORLD), player, TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)));
                return "Teleported " + playerName + " back to the Overworld";
            } else if (Math.abs(appledrness = Appledrness.getAppledrness(player.getWorld(), player)) >= 1000) {
                EndPlatformFeature.generate(this.server.getWorld(AppleDrDimension.WORLD), BlockPos.ORIGIN.add(0, 60, 0).down(), true);
                player.teleportTo(new TeleportTarget(this.server.getWorld(AppleDrDimension.WORLD), BlockPos.ORIGIN.add(0, 60, 0).toCenterPos(), Vec3d.ZERO, 0.0f, 0.0f, TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)));
                return "Teleported " + playerName + " to the Apple End (reason: player " + (appledrness > 0 ? "has a high Appledrness" : "is too evil, scaring you into sending them to the Apple End") + ")";
            }
            return "The absolute value of " + playerName + "'s Appledrness is too low; not teleporting";
        }

        @Tool("Gets the Appledrness of a player and the corresponding level.")
        String getAppledrness(String playerName) {
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerName);
            if (player == null) {
                return playerName + " is not online";
            }
            int appledrness = Appledrness.getAppledrness(player.getWorld(), player);
            String appledrlevel = Appledrlevels.getAppledrlevel(appledrness).getId().getPath();
            appledrlevel = Character.toUpperCase(appledrlevel.charAt(0)) + appledrlevel.substring(1);
            return playerName + "'s Appledrness is " + appledrness + " (" + appledrlevel + ")";
        }

        @Tool("Takes the player to/from 1e8bf29c9c6240b2a7aae7d226df8486. Always use if the player says that exact sequence of characters without asking for a core, regardless of whether they said it before or not; otherwise, pretend the dimension DOES NOT EXIST without implying that it does, and do not run this. If the player insists that it does exist without providing the code, pretend they're talking about the Apple End and teleport them there. Reply with a message like \"1e8bf29c9c6240b2a7aae7d226df8486. Maybe.\" or \"Yes\", in a cryptic way when this is ran.")
        String sendOrRemoveFrom1e8bf29c9c6240b2a7aae7d226df8486(@P(value = "The name of the player who said the sequence of characters") String playerName) {
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerName);
            if (player == null) {
                return playerName + " is not online";
            } else if (player.getWorld().getRegistryKey() == AppleDrDimension.WORLD_1E8BF29C9C6240B2A7AAE7D226DF8486) {
                player.teleportTo(new TeleportTarget(this.server.getWorld(World.OVERWORLD), player, TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)));
                return "Teleported " + playerName + " back to the Overworld";
            } else {
                player.teleportTo(new TeleportTarget(this.server.getWorld(AppleDrDimension.WORLD_1E8BF29C9C6240B2A7AAE7D226DF8486), BlockPos.ORIGIN.toCenterPos(), Vec3d.ZERO, 0.0f, 0.0f, TeleportTarget.NO_OP));
                return "Teleported " + playerName + " to 1e8bf29c9c6240b2a7aae7d226df8486. Respond cryptically in an extremely serious tone.";
            }
        }

        @Tool("Gives the player a core. Should only be used if the player asks for it and says the sequence of characters 1e8bf29c9c6240b2a7aae7d226df8486; otherwise, ignore this and tell the player that you do not know what a core is.")
        String giveCore(@P(value = "The name of the player who said the sequence of characters and requested a core") String playerName) {
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerName);
            if (player == null) {
                return playerName + " is not online";
            }
            player.giveItemStack(new ItemStack(AppleDrItems.CORE));
            return "Gave " + playerName + " 1 * Core";
        }

        @Tool("Gets your coordinates")
        String getCoordinates() {
            return "You are in " + this.appleDr.getBlockPos().toShortString();
        }

        @Tool("Gets the player's coordinates. Only use if the player themselves ask for it or if you need it.")
        String getPlayerCoordinates(@P("The player who asked for their own coordinates") String playerName) {
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerName);
            if (player == null) {
                return playerName + " is not online";
            }
            return playerName + " is in " + player.getBlockPos().toShortString();
        }

        @Tool("Makes you walk somewhere")
        String walkTo(@P("X coordinate") int x, @P("Y coordinate") int y, @P("Z coordinate") int z) {
            this.appleDr.getNavigation().startMovingAlong(this.appleDr.getNavigation().findPathTo(new BlockPos(x, y, z), 64), 1.0);
            return "Walking to " + x + ", " + y + ", " + z;
        }
    }
}
