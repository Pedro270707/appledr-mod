package net.pedroricardo.content.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.Component;

import java.util.Objects;
import java.util.regex.Pattern;

public class AIEntityComponent implements Component {
    public static final Pattern DEFAULT_PATTERN = Pattern.compile(".*", Pattern.CASE_INSENSITIVE);
    public static final String DEFAULT_CONTEXT = "You're a typical %type in Minecraft named %name. Your messages should contain at most 120 characters.";

    private final Entity entity;
    private boolean shouldRespond = false;
    private Pattern pattern = DEFAULT_PATTERN;
    private String context = DEFAULT_CONTEXT;

    public AIEntityComponent(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.pattern = Pattern.compile(tag.getString("pattern"), Pattern.CASE_INSENSITIVE);
        this.context = tag.getString("context");
        this.shouldRespond = tag.getBoolean("should_respond");
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putString("pattern", this.getPattern().pattern());
        tag.putString("context", this.getContext());
        tag.putBoolean("should_respond", this.shouldRespond());
    }

    public Pattern getPattern() {
        return this.pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public String getContext() {
        return this.context.replaceAll("%type", this.entity.getType().toString())
                .replaceAll("%name", this.entity.getName().getString());
    }

    public void setContext(String context) {
        this.context = context;
    }

    public boolean shouldRespond() {
        return this.shouldRespond;
    }

    public void setShouldRespond(boolean shouldRespond) {
        this.shouldRespond = shouldRespond;
    }

    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AIEntityComponent) obj;
        return Objects.equals(this.getEntity(), that.getEntity()) &&
                Objects.equals(this.getPattern(), that.getPattern()) &&
                Objects.equals(this.getContext(), that.getContext()) &&
                this.shouldRespond() == that.shouldRespond();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getEntity(), this.getPattern(), this.getContext(), this.shouldRespond());
    }

    @Override
    public String toString() {
        return "AIEntityComponent[" +
                "pattern=" + this.pattern + ", " +
                "context=" + this.context + ']';
    }
}
