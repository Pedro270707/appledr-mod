package net.pedroricardo.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.pedroricardo.content.entity.AppleDrEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NameTagItem.class)
public class DoNotNameTagAppleDrMixin {
    @Inject(method = "useOnEntity", at = @At("HEAD"), cancellable = true)
    private void appledrmod$doNotNameAppleDr(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (entity instanceof AppleDrEntity) cir.setReturnValue(ActionResult.PASS);
    }
}
