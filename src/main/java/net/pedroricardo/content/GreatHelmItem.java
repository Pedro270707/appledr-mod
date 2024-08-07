package net.pedroricardo.content;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class GreatHelmItem extends Item implements PolymerItem {
    private final String texture;

    public GreatHelmItem(Settings settings, String texture) {
        super(settings.equipmentSlot((entity, stack) -> EquipmentSlot.HEAD).maxCount(1));
        this.texture = texture;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.PLAYER_HEAD;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipType tooltipType, RegistryWrapper.WrapperLookup lookup, @Nullable ServerPlayerEntity player) {
        ItemStack stack = PolymerItemUtils.createItemStack(itemStack, lookup, player);
        PropertyMap properties = new PropertyMap();
        properties.put("textures", new Property("textures", this.texture));
        stack.set(DataComponentTypes.PROFILE, new ProfileComponent(Optional.empty(), Optional.empty(), properties));
        return stack;
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        Identifier id = Registries.ITEM.getId(this);
        return id.hashCode();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable(this.getTranslationKey(stack) + ".desc").formatted(Formatting.GRAY));
    }
}
