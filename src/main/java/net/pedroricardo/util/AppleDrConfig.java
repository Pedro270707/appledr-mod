package net.pedroricardo.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;

public class AppleDrConfig {
    public static String getValue(String setting, String fallback) {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("appledr.json");
        try {
            JsonElement config = JsonParser.parseReader(new FileReader(path.toFile()));
            if (config.isJsonObject()) {
                JsonElement element = config.getAsJsonObject().get(setting);
                if (element.isJsonPrimitive()) {
                    return element.getAsString();
                }
            }
        } catch (FileNotFoundException ignored) {
        }
        return fallback;
    }
}
