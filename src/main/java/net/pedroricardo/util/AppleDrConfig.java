package net.pedroricardo.util;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class AppleDrConfig {
    private static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

    public static String getValue(String setting, @NotNull String fallback) {
        File file = FabricLoader.getInstance().getConfigDir().resolve("appledr.json").toFile();
        try {
            if (!file.exists() && !file.createNewFile()) {
                return fallback;
            }

            JsonElement config = new JsonObject();
            try {
                config = JsonParser.parseReader(new FileReader(file));
            } catch (IOException ignored) {
            }
            if (!config.isJsonObject()) {
                config = new JsonObject();
            }

            JsonElement element = config.getAsJsonObject().get(setting);
            if (element == null || !element.isJsonPrimitive()) {
                config.getAsJsonObject().addProperty(setting, fallback);
                element = new JsonPrimitive(fallback);
            }
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(PRETTY_GSON.toJson(config));
            }
            return element.getAsString();
        } catch (IOException ignored) {
        }
        return fallback;
    }
}
