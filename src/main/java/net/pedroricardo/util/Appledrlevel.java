package net.pedroricardo.util;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Supplier;

public final class Appledrlevel {
    private final Identifier id;
    private final Supplier<Text> prefixSupplier;
    private String translationKey = null;
    private OptionalInt level = OptionalInt.empty();

    public Appledrlevel(Identifier id, Supplier<Text> prefixSupplier) {
        this.id = id;
        this.prefixSupplier = prefixSupplier;
    }

    public Appledrlevel(Identifier id) {
        this(id, () -> Text.translatable(id.toTranslationKey("appledrlevel")));
    }

    public String getAppledrnessTranslationKey() {
        return this.getTranslationKey() + ".appledrness";
    }

    public String getTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.createTranslationKey("level", this.id());
        }
        return this.translationKey;
    }

    public int getLevel() {
        if (this.level.isEmpty()) {
            this.level = Appledrlevels.getAppledrness(this);
        }
        return this.level.orElse(0);
    }

    public Identifier id() {
        return id;
    }

    public Supplier<Text> prefixSupplier() {
        return prefixSupplier;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Appledrlevel) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.prefixSupplier, that.prefixSupplier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, prefixSupplier);
    }

    @Override
    public String toString() {
        return "Appledrlevel[" +
                "id=" + id + ", " +
                "prefixSupplier=" + prefixSupplier + ']';
    }

}
