package net.pedroricardo.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringIdentifiable;

import java.util.List;
import java.util.Optional;

public record OpenAIResponse(String id, List<Choice> choices, int created, String model, Optional<String> serviceTier, String systemFingerprint, String object, Usage usage) {
    public static final Codec<OpenAIResponse> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.STRING.fieldOf("id").forGetter(OpenAIResponse::id), Choice.CODEC.listOf().fieldOf("choices").forGetter(OpenAIResponse::choices), Codec.INT.fieldOf("created").forGetter(OpenAIResponse::created), Codec.STRING.fieldOf("model").forGetter(OpenAIResponse::model), Codec.STRING.optionalFieldOf("service_tier").forGetter(OpenAIResponse::serviceTier), Codec.STRING.fieldOf("system_fingerprint").forGetter(OpenAIResponse::systemFingerprint), Codec.STRING.fieldOf("object").forGetter(OpenAIResponse::object), Usage.CODEC.fieldOf("usage").forGetter(OpenAIResponse::usage)).apply(instance, OpenAIResponse::new));
    
    public record Choice(FinishReason finishReason, int index, AppleDrAI.Message message, Optional<OpenAILogprobs> logprobs) {
        public static final Codec<Choice> CODEC = RecordCodecBuilder.create(instance -> instance.group(FinishReason.CODEC.fieldOf("finish_reason").forGetter(Choice::finishReason), Codec.INT.fieldOf("index").forGetter(Choice::index), AppleDrAI.Message.CODEC.fieldOf("message").forGetter(Choice::message), OpenAILogprobs.CODEC.optionalFieldOf("logprobs").forGetter(Choice::logprobs)).apply(instance, Choice::new));

        public enum FinishReason implements StringIdentifiable {
            STOP("stop"),
            LENGTH("length"),
            CONTENT_FILTER("content_filter"),
            TOOL_CALLS("tool_calls"),
            FUNCTION_CALL("function_call");

            public static final Codec<FinishReason> CODEC = StringIdentifiable.createCodec(FinishReason::values);

            private final String id;

            FinishReason(String id) {
                this.id = id;
            }

            @Override
            public String asString() {
                return id;
            }
        }
    }

    public record Usage(int completionTokens, int promptTokens, int totalTokens) {
        public static final Codec<Usage> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.INT.fieldOf("completion_tokens").forGetter(Usage::completionTokens), Codec.INT.fieldOf("prompt_tokens").forGetter(Usage::promptTokens), Codec.INT.fieldOf("total_tokens").forGetter(Usage::totalTokens)).apply(instance, Usage::new));
    }
}
