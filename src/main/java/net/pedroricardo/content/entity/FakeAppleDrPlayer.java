package net.pedroricardo.content.entity;

import com.google.common.collect.MapMaker;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;
import java.util.Objects;

public class FakeAppleDrPlayer extends FakePlayer {
    private final AppleDrEntity appleDr;
    private SyncedClientOptions clientOptions = SyncedClientOptions.createDefault();

    protected FakeAppleDrPlayer(ServerWorld world, GameProfile profile, SyncedClientOptions clientOptions, AppleDrEntity appleDr) {
        super(world, profile);
        this.appleDr = appleDr;
        this.setClientOptions(clientOptions);
    }

    public static FakeAppleDrPlayer get(ServerWorld world, GameProfile profile, SyncedClientOptions clientOptions, AppleDrEntity appleDr) {
        Objects.requireNonNull(world, "World may not be null.");
        Objects.requireNonNull(profile, "Game profile may not be null.");
        Objects.requireNonNull(appleDr, "AppleDr may not be null.");
        if (clientOptions == null) clientOptions = SyncedClientOptions.createDefault();

        return FAKE_APPLEDR_PLAYER_MAP.computeIfAbsent(new FakePlayerKey(world, profile, clientOptions, appleDr), key -> new FakeAppleDrPlayer(key.world, key.profile, key.clientOptions, key.appleDr));
    }

    private record FakePlayerKey(ServerWorld world, GameProfile profile, SyncedClientOptions clientOptions, AppleDrEntity appleDr) {}
    private static final Map<FakePlayerKey, FakeAppleDrPlayer> FAKE_APPLEDR_PLAYER_MAP = new MapMaker().weakValues().makeMap();

    public AppleDrEntity getAppleDr() {
        return this.appleDr;
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
