package net.pedroricardo.content;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.pedroricardo.AppleDrMod;

public class AppleDrDimension {
    public static final RegistryKey<World> WORLD = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(AppleDrMod.MOD_ID, "apple_end"));
}
