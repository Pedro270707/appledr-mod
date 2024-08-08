package net.pedroricardo.appledrness;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.pedroricardo.AppleDrMod;

public class Appledrness {
    public static final RegistryKey<Registry<AppledrnessProvider>> REGISTRY_KEY = RegistryKey.ofRegistry(Identifier.of(AppleDrMod.MOD_ID, "appledrness_provider"));
    public static SimpleRegistry<AppledrnessProvider> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable(), false)).buildAndRegister();

    public static int getAppledrness(World world, ServerPlayerEntity player) {
        if (player.getUuid().equals(AppleDrMod.APPLEDR_UUID)) return Integer.MAX_VALUE;
        int appledrness = 0;
        for (AppledrnessProvider provider : REGISTRY.stream().toList()) {
            appledrness += provider.getAppledrness(world, player);
        }
        return appledrness;
    }

    public static AppledrnessProvider register(String id, AppledrnessProvider provider) {
        return Registry.register(REGISTRY, Identifier.of(AppleDrMod.MOD_ID, id), provider);
    }

    /*

    private int levelOfDrness;

    Appledrness() {

        this.levelOfDrness = 0;

    }



    public void resetAppleDrness() {

        levelOfDrness = 0;

    }

    public void decreaseAppleDrness(int amount) {

        levelOfDrness -= amount;

    }

    public void increaseAppleDrness(int amount) {

        levelOfDrness += amount;

    }

    public int getAppledrness(World world, PlayerEntity player) {

        return levelOfDrness;

    }

     */

}
