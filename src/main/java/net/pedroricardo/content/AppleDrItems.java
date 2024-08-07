package net.pedroricardo.content;

import net.minecraft.component.type.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.pedroricardo.AppleDrMod;

public class AppleDrItems {
    public static final Item ROTTEN_APPLE = register("rotten_apple", new RottenAppleItem(new Item.Settings().food(FoodComponents.APPLE), Items.ROTTEN_FLESH));
    public static final Item APPLE_GREATHELM = register("apple_greathelm", new GreatHelmItem(new Item.Settings().rarity(Rarity.EPIC), "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmU2OTg4YWMzYzIzMGNhZDUzMDA4NjBlOTY1NjgxNWYyMzkwMDZkZmE3YzVmYzdhMmRkMjliODI2MzQzNWJiOSJ9fX0="));
    public static final Item TECHNO_GREATHELM = register("techno_greathelm", new GreatHelmItem(new Item.Settings().rarity(Rarity.EPIC), "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNhNTM4Zjc4NzA0OGRiYTI3ZGNkYmJjYjcyZDJmNTc4Zjg1NzczMTY4ZDcyNDY2MjY2ZTc1NWY0NzFjODkifX19"));

    public static Item register(String id, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(AppleDrMod.MOD_ID, id), item);
    }

    public static void init() {
        AppleDrMod.LOGGER.debug("Registering items");
    }
}
