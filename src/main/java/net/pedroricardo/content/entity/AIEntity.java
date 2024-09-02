package net.pedroricardo.content.entity;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public interface AIEntity {
    @Nullable
    Object getTools(MinecraftServer server);

    FakeAIEntityPlayer getAsPlayer();
}
