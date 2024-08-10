package net.pedroricardo.content.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
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
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.pedroricardo.AppleDrMod;
import net.pedroricardo.content.AppleDrEntityTypes;
import net.pedroricardo.mixin.PlayerModelPartsAccessor;
import net.pedroricardo.util.AppleDrAI;

import java.util.*;
import java.util.function.Consumer;

public class AppleDrEntity extends PathAwareEntity implements PolymerEntity, InventoryOwner {
    protected static final TrackedData<Byte> PLAYER_MODEL_PARTS = DataTracker.registerData(AppleDrEntity.class, TrackedDataHandlerRegistry.BYTE);
    public final List<SignedMessage> messagesReceived = new ArrayList<>();
    private AppleDrAI.Message initialMessageContext = new AppleDrAI.Message(AppleDrAI.MessageRole.SYSTEM, "You're Domenic Dvojmoc (aka AppleDr, your in-game name), a Slovenian player and Twitch streamer in a Minecraft server called AppleDr Server. The people in it do not speak Slovenian; they speak English, but if the message is in another language, you should respond in that language unless the player asks otherwise. You are in university at 20 years old learning computer science. Player messages will start with some information about the player, such as their name and their Appledrness, but do NOT include that in your response. If asked, you have the maximum Appledrness (2³¹ - 1). Your messages should contain at most 120 characters.");
    private final GameProfile profile;

    private final SimpleInventory inventory = new SimpleInventory(36);

    public AppleDrEntity(EntityType<AppleDrEntity> entityType, World world) {
        super(entityType, world);
        this.profile = new GameProfile(this.getUuid(), "AppleDr");
        this.profile.getProperties().put("textures", new Property("textures",
                "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmU2OTg4YWMzYzIzMGNhZDUzMDA4NjBlOTY1NjgxNWYyMzkwMDZkZmE3YzVmYzdhMmRkMjliODI2MzQzNWJiOSJ9fX0=",
                "H6Y/64HDwOTZ1edMGpTOQ0CGKKGaHUiS/Wqmsxb/LEh53ZHS1qoc/qHicpq39s0p/YNlc06tEdGmncBq8zx9bFJ/ZT9WwdG05JiHCjBEC0IWZ3n+J5W5wTUEbpNMEL+gpFIERKdNdxsNWC4e1nbRMrdMqgDbt0wPR+8LzBXlEJ841g7Opb9vK5wctgNeMKSLxJjK5CdYbm3YAyLu+Jbz+O8by3eShkj83BUBvoraJ/qMGmX8mC4m8QOYnWIshd9b4X5mQLqMWkYHN7iiTTslJuljYrBMk8sxRU7yCfVTQYobprHn1b4d74YsxhedpRvADHyCYS9GhiU1OiSk5KOhpW2UeM1vaxuqpdvkj0Xogs4xbNcDd+vMkgGW1G6eSqYQD8qvxLgUY3oQkf3AQBDGCCtBQ9dNioTPH/d5VMLjbYAgz9wf1kzxqH+Z1GneWv0KO/0bb5ZEL+XKAq/NCzx7GNiTKKKArqNpaK0uC0HNKqk6c4Pt/newMorQrvdVqxzTp1sDO1AhbM74L+dsgh5zEqwUmrl+Gp/BgQx6fT1CIvVfHHVWbY9EVPaNHF1n/QCriGVsiuKEhPTzusPpDSsAP/Hgv9PQdCx1jdpmoNGRHJ0zmdfq4TpCJD4LddV1pCSUKdT34JibDrlJIIpxM72b2NULCTTeIa4Jx4GMUMducEg="
        ));
    }

    public AppleDrEntity(ServerWorld world, PlayerEntity originalDr) {
        super(AppleDrEntityTypes.APPLEDR, world);
        this.copyPositionAndRotation(originalDr);
        this.setHeadYaw(originalDr.getHeadYaw());
        this.setPitch(originalDr.getPitch());
        for (int i = 0; i < this.inventory.size(); i++) {
            this.inventory.setStack(i, originalDr.getInventory().getStack(i));
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            this.equipStack(slot, originalDr.getEquippedStack(slot));
        }
        this.profile = new GameProfile(UUID.randomUUID(), originalDr.getGameProfile().getName());
        this.profile.getProperties().putAll("textures", originalDr.getGameProfile().getProperties().get("textures"));
        this.getDataTracker().set(PLAYER_MODEL_PARTS, originalDr.getDataTracker().get(PlayerModelPartsAccessor.playerModelParts()));
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
        this.goalSelector.add(1, new SendAIChatMessageGoal(this));
        this.goalSelector.add(2, new SwimGoal(this));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 2.8));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(5, new LookAroundGoal(this));
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

    public AppleDrAI.Message getInitialMessageContext() {
        return this.initialMessageContext;
    }

    public void setInitialMessageContext(String value) {
        this.initialMessageContext = new AppleDrAI.Message(AppleDrAI.MessageRole.SYSTEM, value);
    }

    public GameProfile getGameProfile() {
        return this.profile;
    }

    @Override
    public void onBeforeSpawnPacket(ServerPlayerEntity player, Consumer<Packet<?>> packetConsumer) {
        PlayerListS2CPacket packet = PolymerEntityUtils.createMutablePlayerListPacket(EnumSet.of(PlayerListS2CPacket.Action.ADD_PLAYER, PlayerListS2CPacket.Action.UPDATE_LISTED));
        packet.getEntries().add(new PlayerListS2CPacket.Entry(this.getUuid(), this.profile, true, 0, GameMode.CREATIVE, Text.literal("AppleDr"), null));
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
        if (this.getWorld().getServer() != null && this.getWorld().getServer().getPlayerManager().getPlayer(AppleDrMod.APPLEDR_UUID) != null) {
            this.discard();
        }
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("initial_message_context", this.getInitialMessageContext().content());
        this.writeInventory(nbt, this.getRegistryManager());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setInitialMessageContext(nbt.getString("initial_message_context"));
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
}
