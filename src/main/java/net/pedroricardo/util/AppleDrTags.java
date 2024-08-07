package net.pedroricardo.util;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.pedroricardo.AppleDrMod;

public class AppleDrTags {
    public static class Items {
        public static final TagKey<Item> APPLE_GREATHELMS = TagKey.of(RegistryKeys.ITEM, Identifier.of(AppleDrMod.MOD_ID, "apple_greathelms"));
    }
}
