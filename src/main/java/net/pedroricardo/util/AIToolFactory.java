package net.pedroricardo.util;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;

public abstract class AIToolFactory {
    public abstract AITools create(Entity entity, MinecraftServer server);
}
