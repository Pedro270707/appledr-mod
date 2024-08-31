package net.pedroricardo.content;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.pedroricardo.AppleDrMod;

public class AppleDrDimension {
    public static final RegistryKey<World> WORLD = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(AppleDrMod.MOD_ID, "apple_end"));
    public static final RegistryKey<World> WORLD_1E8BF29C9C6240B2A7AAE7D226DF8486 = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(AppleDrMod.MOD_ID, "1e8bf29c9c6240b2a7aae7d226df8486"));
}
