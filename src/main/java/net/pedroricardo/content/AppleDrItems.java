package net.pedroricardo.content;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.pedroricardo.AppleDrMod;
import net.pedroricardo.content.item.*;

public class AppleDrItems {
    public static final Item ROTTEN_APPLE = register("rotten_apple", new RottenAppleItem(new Item.Settings().food(FoodComponents.APPLE), Items.PAPER));
    public static final Item APPLE_GREATHELM = register("apple_greathelm", new GreathelmItem(new Item.Settings().rarity(Rarity.EPIC), "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmU2OTg4YWMzYzIzMGNhZDUzMDA4NjBlOTY1NjgxNWYyMzkwMDZkZmE3YzVmYzdhMmRkMjliODI2MzQzNWJiOSJ9fX0="));
    public static final Item TECHNO_GREATHELM = register("techno_greathelm", new GreathelmItem(new Item.Settings().rarity(Rarity.EPIC), "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNhNTM4Zjc4NzA0OGRiYTI3ZGNkYmJjYjcyZDJmNTc4Zjg1NzczMTY4ZDcyNDY2MjY2ZTc1NWY0NzFjODkifX19"));
    public static final Item LEGACY_APPLE_GREATHELM = register("legacy_apple_greathelm", new GreathelmItem(new Item.Settings().rarity(Rarity.EPIC), "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTdjMWFlYzJhYTBkZTlhZGQ4NjM2MTM1NDRjZTYzNDBlYWQ2M2RkZmFiNGUzYmY0ZjEwYzgwMDZhY2Q2NzY3NSJ9fX0="));
    public static final Item APPLEDRALTAR = register("appledraltar", new AppleDrBlockItem(AppleDrBlocks.APPLEDRALTAR, new Item.Settings().rarity(Rarity.EPIC), Items.STONE));
    public static final Item APPLE_PIE = register("apple_pie", new ApplePieItem(AppleDrBlocks.APPLE_PIE, new Item.Settings().food(FoodComponents.PUMPKIN_PIE), Items.PAPER));
    public static final Item APPLE_BRICKS = register("apple_bricks", new AppleDrBlockItem(AppleDrBlocks.APPLE_BRICKS, new Item.Settings(), Items.STONE));
    public static final Item IRON_LOCK_APPLE_BRICKS = register("iron_lock_apple_bricks", new AppleDrBlockItem(AppleDrBlocks.IRON_LOCK_APPLE_BRICKS, new Item.Settings(), Items.STONE));
    public static final Item GOLDEN_LOCK_APPLE_BRICKS = register("golden_lock_apple_bricks", new AppleDrBlockItem(AppleDrBlocks.GOLDEN_LOCK_APPLE_BRICKS, new Item.Settings(), Items.STONE));
    public static final Item DIAMOND_LOCK_APPLE_BRICKS = register("diamond_lock_apple_bricks", new AppleDrBlockItem(AppleDrBlocks.DIAMOND_LOCK_APPLE_BRICKS, new Item.Settings(), Items.STONE));
    public static final Item NETHERITE_LOCK_APPLE_BRICKS = register("netherite_lock_apple_bricks", new AppleDrBlockItem(AppleDrBlocks.NETHERITE_LOCK_APPLE_BRICKS, new Item.Settings(), Items.STONE));
    public static final Item IRON_KEY = register("iron_key", new AppleDrItem(new Item.Settings(), Items.PAPER));
    public static final Item GOLDEN_KEY = register("golden_key", new AppleDrItem(new Item.Settings(), Items.PAPER));
    public static final Item DIAMOND_KEY = register("diamond_key", new AppleDrItem(new Item.Settings(), Items.PAPER));
    public static final Item NETHERITE_KEY = register("netherite_key", new AppleDrItem(new Item.Settings(), Items.PAPER));
    public static final Item APPLE_STONE = register("apple_stone", new AppleDrBlockItem(AppleDrBlocks.APPLE_STONE, new Item.Settings(), Items.STONE));
    public static final Item APPLECAXE = register("applecaxe", new AppleDrItem(new Item.Settings().rarity(Rarity.EPIC).food(FoodComponents.APPLE).maxCount(1), Items.PAPER));
    public static final Item APPLECAXE_II = register("applecaxe_ii", new AppleDrItem(new Item.Settings().rarity(Rarity.EPIC).food(FoodComponents.APPLE).maxCount(1), Items.PAPER));
    public static final Item APPLECAXE_III = register("applecaxe_iii", new AppleDrItem(new Item.Settings().rarity(Rarity.EPIC).food(FoodComponents.APPLE).maxCount(1), Items.PAPER));
    public static final Item APPLECAXE_IV = register("applecaxe_iv", new AppleDrItem(new Item.Settings().rarity(Rarity.EPIC).food(FoodComponents.APPLE).maxCount(1), Items.PAPER));
    public static final Item APPLECAXE_V = register("applecaxe_v", new AppleDrItem(new Item.Settings().rarity(Rarity.EPIC).food(FoodComponents.APPLE).maxCount(1), Items.PAPER));
    public static final Item APPLECAXE_VI = register("applecaxe_vi", new AppleDrItem(new Item.Settings().rarity(Rarity.EPIC).food(FoodComponents.APPLE).maxCount(1), Items.PAPER));
    public static final Item APPLECAXE_VII = register("applecaxe_vii", new AppleDrItem(new Item.Settings().rarity(Rarity.EPIC).food(FoodComponents.APPLE).maxCount(1), Items.PAPER));
    public static final Item APPLECAXE_VIII = register("applecaxe_viii", new AppleDrItem(new Item.Settings().rarity(Rarity.EPIC).food(FoodComponents.APPLE).maxCount(1), Items.PAPER));
    public static final Item APPLECAXE_IX = register("applecaxe_ix", new AppleDrItem(new Item.Settings().rarity(Rarity.EPIC).food(FoodComponents.APPLE).maxCount(1), Items.PAPER));
    public static final Item APPLECAXE_X = register("applecaxe_x", new AppleDrItem(new Item.Settings().rarity(Rarity.EPIC).food(FoodComponents.APPLE).maxCount(1), Items.PAPER));
    public static final Item CORE = register("core", new AppleDrBlockItem(AppleDrBlocks.CORE, new Item.Settings(), Items.STONE));
    public static final Item AI_CHIP = register("ai_chip", new AIChipItem(new Item.Settings().rarity(Rarity.EPIC).maxCount(1), Items.PAPER));

    public static void registerItemGroup(String id, ItemGroup group) {
        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(AppleDrMod.MOD_ID, id), group);
    }

    public static Item register(String id, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(AppleDrMod.MOD_ID, id), item);
    }

    public static void init() {
        AppleDrMod.LOGGER.debug("Registering items");

        registerItemGroup("appledrmod", PolymerItemGroupUtils.builder().icon(() -> new ItemStack(Items.APPLE)).displayName(Text.translatable("itemGroup.appledrmod")).entries((ctx, entries) -> {
            entries.add(ROTTEN_APPLE);
            entries.add(APPLE_PIE);
            entries.add(APPLEDRALTAR);
            entries.add(APPLE_BRICKS);
            entries.add(IRON_LOCK_APPLE_BRICKS);
            entries.add(GOLDEN_LOCK_APPLE_BRICKS);
            entries.add(DIAMOND_LOCK_APPLE_BRICKS);
            entries.add(NETHERITE_LOCK_APPLE_BRICKS);
            entries.add(IRON_KEY);
            entries.add(GOLDEN_KEY);
            entries.add(DIAMOND_KEY);
            entries.add(NETHERITE_KEY);
            entries.add(APPLE_STONE);
            entries.add(APPLECAXE);
            entries.add(APPLECAXE_II);
            entries.add(APPLECAXE_III);
            entries.add(APPLECAXE_IV);
            entries.add(APPLECAXE_V);
            entries.add(APPLECAXE_VI);
            entries.add(APPLECAXE_VII);
            entries.add(APPLECAXE_VIII);
            entries.add(APPLECAXE_IX);
            entries.add(APPLECAXE_X);
        }).build());
        registerItemGroup("appledrmod.operator", PolymerItemGroupUtils.builder().special().icon(() -> new ItemStack(APPLE_GREATHELM)).displayName(Text.translatable("itemGroup.appledrmod.operator")).entries((ctx, entries) -> {
            entries.add(APPLE_GREATHELM);
            entries.add(TECHNO_GREATHELM);
            entries.add(LEGACY_APPLE_GREATHELM);
        }).build());
    }
}
