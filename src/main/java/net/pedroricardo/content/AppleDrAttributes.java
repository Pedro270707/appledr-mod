package net.pedroricardo.content;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.pedroricardo.AppleDrMod;

public class AppleDrAttributes {
    public static final RegistryEntry<EntityAttribute> APPLEDRNESS = registerReference("appledrness", new ClampedEntityAttribute("appledrness", 0.0, 0.0, Double.MAX_VALUE));

    private static RegistryEntry<EntityAttribute> registerReference(String id, EntityAttribute attribute) {
        return Registry.registerReference(Registries.ATTRIBUTE, Identifier.of(AppleDrMod.MOD_ID, id), attribute);
    }

    public static void init() {
        AppleDrMod.LOGGER.debug("Registering statistics");
    }
}
