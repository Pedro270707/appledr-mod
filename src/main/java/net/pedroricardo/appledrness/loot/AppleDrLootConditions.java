package net.pedroricardo.appledrness.loot;

import com.mojang.serialization.MapCodec;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.pedroricardo.AppleDrMod;

public class AppleDrLootConditions {
    public static final LootConditionType APPLEDRNESS = register("appledrness", AppledrnessLootConditionType.CODEC);

    public static LootConditionType register(String id, MapCodec<? extends LootCondition> codec) {
        return Registry.register(Registries.LOOT_CONDITION_TYPE, Identifier.of(AppleDrMod.MOD_ID, id), new LootConditionType(codec));
    }

    public static void init() {
        AppleDrMod.LOGGER.debug("Registering loot conditions");
    }
}
