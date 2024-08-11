package net.pedroricardo.content.item;

import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.pedroricardo.util.ResourcePackUtil;
import org.jetbrains.annotations.Nullable;

public class AppleDrItem extends SimplePolymerItem {
    public AppleDrItem(Settings settings, Item virtualItem) {
        super(settings, virtualItem);
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return ResourcePackUtil.MODELS.get(this).value();
    }
}
