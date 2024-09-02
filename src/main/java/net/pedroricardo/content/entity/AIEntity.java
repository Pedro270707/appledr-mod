package net.pedroricardo.content.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import dev.langchain4j.data.message.UserMessage;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.pedroricardo.content.AppleDrEntityTypes;
import net.pedroricardo.mixin.EntityManagerAccessor;
import net.pedroricardo.util.AppleDrAI;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class AIEntity extends PathAwareEntity implements PolymerEntity {
    public static final Pattern DEFAULT_PATTERN = Pattern.compile(".*", Pattern.CASE_INSENSITIVE);
    public static final String DEFAULT_CONTEXT = "You're a typical %s in Minecraft. Your messages should contain at most 120 characters.";

    private final EntityType<?> polymerEntityType;

    private String initialMessageContext;
    private Pattern pattern = DEFAULT_PATTERN;

    public AIEntity(EntityType<? extends AIEntity> entityType, World world) {
        super(entityType, world);
        this.polymerEntityType = EntityType.PLAYER;
    }

    public AIEntity(World world, LivingEntity livingEntity) {
        this(AppleDrEntityTypes.AI_ENTITY, world, livingEntity.getType());
        this.copyPositionAndRotation(livingEntity);
        this.setHeadYaw(livingEntity.getHeadYaw());
        this.setPitch(livingEntity.getPitch());
    }

    public AIEntity(EntityType<? extends AIEntity> entityType, World world, EntityType<?> polymerType) {
        super(entityType, world);
        this.polymerEntityType = polymerType;
        this.initialMessageContext = String.format(DEFAULT_CONTEXT, polymerType.getName().getString());
    }

    @Override
    protected Text getDefaultName() {
        return this.polymerEntityType.getName();
    }

    @Override
    public int getMaxLookPitchChange() {
        return 90;
    }

    @Override
    public int getMaxHeadRotation() {
        return 30;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 12);
    }

    @Override
    public EntityType<?> getPolymerEntityType(ServerPlayerEntity player) {
        return this.polymerEntityType;
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

    public FakeAIEntityPlayer getAsPlayer() {
        FakeAIEntityPlayer player = FakeAIEntityPlayer.get((ServerWorld) this.getWorld(), new GameProfile(UUID.randomUUID(), this.getName().getString()), SyncedClientOptions.createDefault(), this);
        player.copyPositionAndRotation(this);
        player.setHeadYaw(this.getHeadYaw());
        player.setPitch(this.getPitch());
        return player;
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

    public String getInitialMessageContext() {
        return this.initialMessageContext;
    }

    public void setInitialMessageContext(String value) {
        this.initialMessageContext = value;
    }

    public Pattern getPattern() {
        return this.pattern;
    }

    public void setPattern(String string) {
        try {
            this.setPattern(Pattern.compile(string, Pattern.CASE_INSENSITIVE));
        } catch (PatternSyntaxException e) {
            this.setPattern(DEFAULT_PATTERN);
        }
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public static List<AIEntity> find(MinecraftServer server) {
        return find(server, entity -> true);
    }

    public static List<AIEntity> find(MinecraftServer server, Predicate<AIEntity> predicate) {
        List<AIEntity> list = new ArrayList<>();
        server.getWorlds().forEach(world -> {
            for (Entity entity : ((EntityManagerAccessor) world).entityManager().getLookup().iterate()) {
                if (entity instanceof AIEntity aiEntity && predicate.test(aiEntity)) {
                    list.add(aiEntity);
                }
            }
        });
        return list;
    }

    @Override
    public void onEntityPacketSent(Consumer<Packet<?>> consumer, Packet<?> packet) {
        PolymerEntity.super.onEntityPacketSent(consumer, packet);
        if (packet instanceof EntitySetHeadYawS2CPacket headYawS2CPacket) {
            consumer.accept(new EntityS2CPacket.Rotate(this.getId(), headYawS2CPacket.getHeadYaw(), (byte) (this.getPitch() * 256.0F / 360.0F), this.isOnGround()));
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("initial_message_context", this.getInitialMessageContext());
        nbt.putString("message_pattern", this.getPattern().pattern());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setInitialMessageContext(nbt.getString("initial_message_context"));
        this.setPattern(nbt.getString("message_pattern"));
    }

    @Nullable
    public Object getTools(MinecraftServer server) {
        return null;
    }
}
