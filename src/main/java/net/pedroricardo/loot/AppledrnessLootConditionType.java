package net.pedroricardo.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.StringIdentifiable;
import net.pedroricardo.appledrness.Appledrness;

public record AppledrnessLootConditionType(NumberRange.IntRange range, Source source) implements LootCondition {
    public static final MapCodec<AppledrnessLootConditionType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(NumberRange.IntRange.CODEC.fieldOf("range").forGetter(type -> type.range), Source.CODEC.fieldOf("source").forGetter(type -> type.source)).apply(instance, AppledrnessLootConditionType::new));

    @Override
    public LootConditionType getType() {
        return AppleDrLootConditions.APPLEDRNESS;
    }

    @Override
    public boolean test(LootContext lootContext) {
        if (!lootContext.hasParameter(LootContextParameters.THIS_ENTITY)) {
            return false;
        }

        Entity entity = lootContext.get(LootContextParameters.THIS_ENTITY);
        if (!(entity instanceof ServerPlayerEntity player)) {
            return false;
        }
        int appledrness = Appledrness.getAppledrness(lootContext.getWorld(), player);
        return this.range().test(appledrness);
    }

    public static LootCondition.Builder builder(NumberRange.IntRange range, Source source) {
        return () -> new AppledrnessLootConditionType(range, source);
    }

    public enum Source implements StringIdentifiable {
        THIS("this", LootContextParameters.THIS_ENTITY),
        ATTACKING_ENTITY("attacking_entity", LootContextParameters.ATTACKING_ENTITY),
        LAST_DAMAGE_PLAYER("last_damage_player", LootContextParameters.LAST_DAMAGE_PLAYER),
        BLOCK_ENTITY("block_entity", LootContextParameters.BLOCK_ENTITY);

        public static final Codec<Source> CODEC;
        private final String name;
        final LootContextParameter<?> parameter;

        private Source(String name, LootContextParameter<?> parameter) {
            this.name = name;
            this.parameter = parameter;
        }

        @Override
        public String asString() {
            return this.name;
        }

        static {
            CODEC = StringIdentifiable.createCodec(Source::values);
        }
    }
}
