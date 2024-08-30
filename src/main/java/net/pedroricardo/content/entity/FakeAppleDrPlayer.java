package net.pedroricardo.content.entity;

import com.google.common.collect.MapMaker;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;
import java.util.Objects;

public class FakeAppleDrPlayer extends FakePlayer {
    private final AppleDrEntity appleDr;

    protected FakeAppleDrPlayer(ServerWorld world, GameProfile profile, AppleDrEntity appleDr) {
        super(world, profile);
        this.appleDr = appleDr;
    }

    public static FakeAppleDrPlayer get(ServerWorld world, GameProfile profile, AppleDrEntity appleDr) {
        Objects.requireNonNull(world, "World may not be null.");
        Objects.requireNonNull(profile, "Game profile may not be null.");
        Objects.requireNonNull(appleDr, "AppleDr may not be null.");

        return FAKE_APPLEDR_PLAYER_MAP.computeIfAbsent(new FakePlayerKey(world, profile, appleDr), key -> new FakeAppleDrPlayer(key.world, key.profile, key.appleDr));
    }

    private record FakePlayerKey(ServerWorld world, GameProfile profile, AppleDrEntity appleDr) {}
    private static final Map<FakePlayerKey, FakeAppleDrPlayer> FAKE_APPLEDR_PLAYER_MAP = new MapMaker().weakValues().makeMap();

    public AppleDrEntity getAppleDr() {
        return this.appleDr;
    }
}
