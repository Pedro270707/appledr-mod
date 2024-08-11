package net.pedroricardo.content.item;

import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.pedroricardo.util.ResourcePackUtil;
import org.jetbrains.annotations.Nullable;

public class AppleDrBlockItem extends PolymerBlockItem {
    public AppleDrBlockItem(Block block, Settings settings, Item virtualItem) {
        super(block, settings, virtualItem);
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return ResourcePackUtil.MODELS.get(this).value();
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ActionResult actionResult = super.useOnBlock(context);
        return actionResult.isAccepted() ? ActionResult.SUCCESS : ActionResult.CONSUME;
    }
}
