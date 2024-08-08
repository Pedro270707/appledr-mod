package net.pedroricardo.content;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.pedroricardo.AppleDrMod;

public class AppleDrEntityTypes {
    public static final EntityType<AppleDrEntity> APPLEDR = register("appledr", EntityType.Builder.<AppleDrEntity>create(AppleDrEntity::new, SpawnGroup.MISC).dimensions(0.6f, 1.8f).eyeHeight(1.62f).vehicleAttachment(PlayerEntity.VEHICLE_ATTACHMENT_POS).maxTrackingRange(32).trackingTickInterval(2).build());

    public static <T extends Entity> EntityType<T> register(String id, EntityType<T> entityType) {
        Registry.register(Registries.ENTITY_TYPE, Identifier.of(AppleDrMod.MOD_ID, id), entityType);
        PolymerEntityUtils.registerType(entityType);
        return entityType;
    }

    public static void init() {
        AppleDrMod.LOGGER.debug("Registering entity types");
        FabricDefaultAttributeRegistry.register(APPLEDR, AppleDrEntity.createAppleDrAttributes());
    }
}
