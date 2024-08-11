package net.pedroricardo.content.item;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.pedroricardo.appledrness.Appledrness;

public class ApplePieItem extends AppleDrBlockItem {
    public ApplePieItem(Block block, Settings settings, Item virtualItem) {
        super(block, settings, virtualItem);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack itemStack = super.finishUsing(stack, world, user);
        int appledrness = 0;
        if (user instanceof ServerPlayerEntity player) {
            appledrness = Appledrness.getAppledrness(world, player);
        }
        if (appledrness < 0) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 800));
            if (appledrness <= -100) {
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 800));
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 800));
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 800));
            }
        } else {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 800));
            if (appledrness >= 2000) {
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 800, 1));
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 800));
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 800, 1));
            }
        }
        return itemStack;
    }
}
