package net.pedroricardo.content.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.pedroricardo.content.entity.AIEntityComponent;
import net.pedroricardo.util.AppleDrAI;

public class AIChipItem extends AppleDrItem {
    public AIChipItem(Settings settings, Item virtualItem) {
        super(settings, virtualItem);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!entity.getComponent(AIEntityComponent.COMPONENT).shouldRespond()) {
            AppleDrAI.create(entity, AIEntityComponent.DEFAULT_PATTERN, AIEntityComponent.DEFAULT_CONTEXT, AIEntityComponent.DEFAULT_RESPOND_WHEN_NEAR);
            stack.decrementUnlessCreative(1, user);
            if (entity instanceof MobEntity mob) mob.setPersistent();
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }
}
