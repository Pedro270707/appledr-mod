package net.pedroricardo.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.server.command.ServerCommandSource;
import net.pedroricardo.content.entity.AppleDrEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(ServerCommandSource.class)
public class AddAppleDrToCommandSuggestionsMixin {
    @ModifyReturnValue(method = "getPlayerNames", at = @At("RETURN"))
    private Collection<String> addToPlayerNames(Collection<String> original) {
        List<AppleDrEntity> list = AppleDrEntity.find(((ServerCommandSource)(Object) this).getServer(), appleDr -> appleDr.getAssociatedPlayerUuid() != null);
        List<String> newList = new ArrayList<>(original);
        for (AppleDrEntity appleDr : list) {
            if (original.contains(appleDr.getAsPlayer().getName().getString())) continue;
            newList.add(appleDr.getAsPlayer().getName().getString());
        }
        return newList;
    }
}
