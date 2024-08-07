package net.pedroricardo.util;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public record Appledrlevel(Identifier id, Supplier<Text> prefixSupplier) {
    public Appledrlevel(Identifier id) {
        this(id, () -> Text.translatable(id.toTranslationKey("appledrlevel")));
    }
}
