package net.pedroricardo.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

public record OpenAILogprobs(Optional<Token> content, Optional<Token> refusal) {
    public static final Codec<OpenAILogprobs> CODEC = RecordCodecBuilder.create(instance -> instance.group(Token.CODEC.optionalFieldOf("content").forGetter(OpenAILogprobs::content), Token.CODEC.optionalFieldOf("refusal").forGetter(OpenAILogprobs::refusal)).apply(instance, OpenAILogprobs::new));

    public record Token(String token, double logprob, Optional<List<Integer>> bytes, Optional<List<Token>> topLogprobs) {
        public static final Codec<Token> SIMPLE_CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.STRING.fieldOf("token").forGetter(Token::token), Codec.DOUBLE.fieldOf("logprob").forGetter(Token::logprob), Codec.INT.listOf().optionalFieldOf("bytes").forGetter(Token::bytes)).apply(instance, Token::new));
        public static final Codec<Token> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.STRING.fieldOf("token").forGetter(Token::token), Codec.DOUBLE.fieldOf("logprob").forGetter(Token::logprob), Codec.INT.listOf().optionalFieldOf("bytes").forGetter(Token::bytes), SIMPLE_CODEC.listOf().optionalFieldOf("top_logprobs").forGetter(Token::topLogprobs)).apply(instance, Token::new));

        public Token(String token, double logprob, Optional<List<Integer>> bytes) {
            this(token, logprob, bytes, Optional.empty());
        }
    }
}
