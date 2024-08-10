package net.pedroricardo.content;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.pedroricardo.AppleDrMod;
import net.pedroricardo.content.item.AppleDrBlockItem;
import net.pedroricardo.content.item.GreathelmItem;
import net.pedroricardo.content.item.RottenAppleItem;

public class AppleDrItems {
    public static final Item ROTTEN_APPLE = register("rotten_apple", new RottenAppleItem(new Item.Settings().food(FoodComponents.APPLE), Items.ROTTEN_FLESH));
    public static final Item APPLE_GREATHELM = register("apple_greathelm", new GreathelmItem(new Item.Settings().rarity(Rarity.EPIC), "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmU2OTg4YWMzYzIzMGNhZDUzMDA4NjBlOTY1NjgxNWYyMzkwMDZkZmE3YzVmYzdhMmRkMjliODI2MzQzNWJiOSJ9fX0="));
    public static final Item TECHNO_GREATHELM = register("techno_greathelm", new GreathelmItem(new Item.Settings().rarity(Rarity.EPIC), "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNhNTM4Zjc4NzA0OGRiYTI3ZGNkYmJjYjcyZDJmNTc4Zjg1NzczMTY4ZDcyNDY2MjY2ZTc1NWY0NzFjODkifX19"));
    public static final Item LEGACY_APPLE_GREATHELM = register("legacy_apple_greathelm", new GreathelmItem(new Item.Settings().rarity(Rarity.EPIC), "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTdjMWFlYzJhYTBkZTlhZGQ4NjM2MTM1NDRjZTYzNDBlYWQ2M2RkZmFiNGUzYmY0ZjEwYzgwMDZhY2Q2NzY3NSJ9fX0="));
    public static final Item APPLEDRALTAR = register("appledraltar", new AppleDrBlockItem(AppleDrBlocks.APPLEDRALTAR, new Item.Settings().rarity(Rarity.EPIC), Items.ENCHANTING_TABLE));

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
            entries.add(APPLEDRALTAR);
        }).build());
        registerItemGroup("appledrmod.operator", PolymerItemGroupUtils.builder().special().icon(() -> new ItemStack(APPLE_GREATHELM)).displayName(Text.translatable("itemGroup.appledrmod.operator")).entries((ctx, entries) -> {
            entries.add(APPLE_GREATHELM);
            entries.add(TECHNO_GREATHELM);
            entries.add(LEGACY_APPLE_GREATHELM);
        }).build());
    }
}
