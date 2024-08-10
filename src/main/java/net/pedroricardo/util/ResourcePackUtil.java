package net.pedroricardo.util;

import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.pedroricardo.content.AppleDrItems;

import java.util.HashMap;
import java.util.Map;

public class ResourcePackUtil {
    public static final Map<Item, PolymerModelData> MODELS = new HashMap<>();

    public static void bootstrap() {
        MODELS.clear();
        MODELS.put(AppleDrItems.ROTTEN_APPLE, PolymerResourcePackUtils.requestModel(Items.ROTTEN_FLESH, Registries.ITEM.getId(AppleDrItems.ROTTEN_APPLE).withPrefixedPath("item/")));
        MODELS.put(AppleDrItems.APPLEDRALTAR, PolymerResourcePackUtils.requestModel(Items.ENCHANTING_TABLE, Registries.ITEM.getId(AppleDrItems.APPLEDRALTAR).withPrefixedPath("block/")));
    }
}
