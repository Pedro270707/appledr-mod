package net.pedroricardo;

import net.fabricmc.api.DedicatedServerModInitializer;

import net.pedroricardo.content.AppleDrAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppleDrMod implements DedicatedServerModInitializer {
	public static final String MOD_ID = "appledrmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeServer() {
		AppleDrAttributes.init();

	}
}