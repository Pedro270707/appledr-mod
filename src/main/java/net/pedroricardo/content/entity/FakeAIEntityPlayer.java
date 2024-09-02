package net.pedroricardo.content.entity;

import com.google.common.collect.MapMaker;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;
import java.util.Objects;

public class FakeAIEntityPlayer extends FakePlayer {
    private final Entity aiEntity;
    private SyncedClientOptions clientOptions = SyncedClientOptions.createDefault();

    protected FakeAIEntityPlayer(ServerWorld world, GameProfile profile, SyncedClientOptions clientOptions, Entity aiEntity) {
        super(world, profile);
        this.aiEntity = aiEntity;
        this.setClientOptions(clientOptions);
    }

    public static FakeAIEntityPlayer get(ServerWorld world, GameProfile profile, SyncedClientOptions clientOptions, Entity aiEntity) {
        Objects.requireNonNull(world, "World may not be null.");
        Objects.requireNonNull(profile, "Game profile may not be null.");
        Objects.requireNonNull(aiEntity, "AI entity may not be null.");
        if (clientOptions == null) clientOptions = SyncedClientOptions.createDefault();

        return FAKE_APPLEDR_PLAYER_MAP.computeIfAbsent(new FakePlayerKey(world, profile, clientOptions, aiEntity), key -> new FakeAIEntityPlayer(key.world, key.profile, key.clientOptions, key.aiEntity));
    }

    private record FakePlayerKey(ServerWorld world, GameProfile profile, SyncedClientOptions clientOptions, Entity aiEntity) {}
    private static final Map<FakePlayerKey, FakeAIEntityPlayer> FAKE_APPLEDR_PLAYER_MAP = new MapMaker().weakValues().makeMap();

    public Entity getAIEntity() {
        return this.aiEntity;
    }

    @Override
    public void setClientOptions(SyncedClientOptions settings) {
        this.clientOptions = settings;
    }

    @Override
    public SyncedClientOptions getClientOptions() {
        return this.clientOptions;
    }

    @Override
    public boolean allowsServerListing() {
        return this.getClientOptions().allowsServerListing();
    }
}
