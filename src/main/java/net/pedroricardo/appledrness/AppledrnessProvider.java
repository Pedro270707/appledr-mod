package net.pedroricardo.appledrness;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

@FunctionalInterface
public interface AppledrnessProvider {
    int getAppledrness(World world, PlayerEntity player);
}
