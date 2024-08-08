package net.pedroricardo.content;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

public class AppleDrEntity extends PathAwareEntity implements PolymerEntity, InventoryOwner {
    private final SimpleInventory inventory = new SimpleInventory(36);

    public AppleDrEntity(EntityType<AppleDrEntity> entityType, World world) {
        super(entityType, world);
    }

    public AppleDrEntity(ServerWorld world, PlayerEntity originalDr) {
        this(AppleDrEntityTypes.APPLEDR, world);
        this.copyPositionAndRotation(originalDr);
        for (int i = 0; i < this.inventory.size(); i++) {
            this.inventory.setStack(i, originalDr.getInventory().getStack(i));
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            this.equipStack(slot, originalDr.getEquippedStack(slot));
        }
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

    @Override
    public void onBeforeSpawnPacket(ServerPlayerEntity player, Consumer<Packet<?>> packetConsumer) {
        var packet = PolymerEntityUtils.createMutablePlayerListPacket(EnumSet.of(PlayerListS2CPacket.Action.ADD_PLAYER, PlayerListS2CPacket.Action.UPDATE_LISTED));
        var profile = new GameProfile(this.getUuid(), "AppleDr");
        profile.getProperties().put("textures", new Property("textures",
                "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmU2OTg4YWMzYzIzMGNhZDUzMDA4NjBlOTY1NjgxNWYyMzkwMDZkZmE3YzVmYzdhMmRkMjliODI2MzQzNWJiOSJ9fX0=",
                "H6Y/64HDwOTZ1edMGpTOQ0CGKKGaHUiS/Wqmsxb/LEh53ZHS1qoc/qHicpq39s0p/YNlc06tEdGmncBq8zx9bFJ/ZT9WwdG05JiHCjBEC0IWZ3n+J5W5wTUEbpNMEL+gpFIERKdNdxsNWC4e1nbRMrdMqgDbt0wPR+8LzBXlEJ841g7Opb9vK5wctgNeMKSLxJjK5CdYbm3YAyLu+Jbz+O8by3eShkj83BUBvoraJ/qMGmX8mC4m8QOYnWIshd9b4X5mQLqMWkYHN7iiTTslJuljYrBMk8sxRU7yCfVTQYobprHn1b4d74YsxhedpRvADHyCYS9GhiU1OiSk5KOhpW2UeM1vaxuqpdvkj0Xogs4xbNcDd+vMkgGW1G6eSqYQD8qvxLgUY3oQkf3AQBDGCCtBQ9dNioTPH/d5VMLjbYAgz9wf1kzxqH+Z1GneWv0KO/0bb5ZEL+XKAq/NCzx7GNiTKKKArqNpaK0uC0HNKqk6c4Pt/newMorQrvdVqxzTp1sDO1AhbM74L+dsgh5zEqwUmrl+Gp/BgQx6fT1CIvVfHHVWbY9EVPaNHF1n/QCriGVsiuKEhPTzusPpDSsAP/Hgv9PQdCx1jdpmoNGRHJ0zmdfq4TpCJD4LddV1pCSUKdT34JibDrlJIIpxM72b2NULCTTeIa4Jx4GMUMducEg="
        ));
        packet.getEntries().add(new PlayerListS2CPacket.Entry(this.getUuid(), profile, true, 0, GameMode.CREATIVE, Text.literal("AppleDr"), null));
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
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        this.writeInventory(nbt, this.getRegistryManager());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.readInventory(nbt, this.getRegistryManager());
    }

    @Override
    public boolean canGather(ItemStack stack) {
        return false;
    }
}
