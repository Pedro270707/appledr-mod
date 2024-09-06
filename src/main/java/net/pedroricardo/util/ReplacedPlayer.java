package net.pedroricardo.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;

import java.util.UUID;
import java.util.regex.Pattern;

public record ReplacedPlayer(UUID uuid, Pattern pattern, String context, boolean respondWhenNear) {
    public static final Codec<ReplacedPlayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(Uuids.CODEC.fieldOf("uuid").forGetter(ReplacedPlayer::uuid), Codecs.REGULAR_EXPRESSION.fieldOf("pattern").forGetter(ReplacedPlayer::pattern), Codec.STRING.fieldOf("context").forGetter(ReplacedPlayer::context), Codec.BOOL.fieldOf("respond_when_near").forGetter(ReplacedPlayer::respondWhenNear)).apply(instance, (uuid, pattern, ctx, respondWhenNear) -> new ReplacedPlayer(uuid, Pattern.compile(pattern.pattern(), Pattern.CASE_INSENSITIVE), ctx, respondWhenNear)));
}
