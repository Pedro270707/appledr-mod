package net.pedroricardo;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class AppleDrModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			AppleDrMod.LOGGER.info("Running on an IDE! :D");
		}
	}
}