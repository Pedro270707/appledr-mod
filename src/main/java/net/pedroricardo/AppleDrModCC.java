package net.pedroricardo;

import net.minecraft.entity.Entity;
import net.pedroricardo.content.entity.AIEntityComponent;
import net.pedroricardo.util.AppleDrAI;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;

public class AppleDrModCC implements EntityComponentInitializer {
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(Entity.class, AppleDrAI.COMPONENT, AIEntityComponent::new);
    }
}
