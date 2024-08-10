package net.pedroricardo.content.block;

import eu.pb4.polymer.virtualentity.api.elements.SimpleEntityElement;
import eu.pb4.polymer.virtualentity.api.tracker.EntityTrackedData;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.pedroricardo.mixin.ItemStackTrackerAccessor;

public class DroppedItemElement extends SimpleEntityElement {
    public DroppedItemElement(ItemStack stack) {
        super(EntityType.ITEM);
        this.setStack(stack);
        this.dataTracker.set(EntityTrackedData.NO_GRAVITY, true);
    }

    public void setStack(ItemStack stack) {
        this.dataTracker.set(ItemStackTrackerAccessor.stack(), stack);
    }
}
