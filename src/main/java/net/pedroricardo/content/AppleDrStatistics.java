package net.pedroricardo.content;

import eu.pb4.polymer.core.api.other.PolymerStat;
import net.minecraft.stat.StatFormatter;
import net.minecraft.util.Identifier;
import net.pedroricardo.AppleDrMod;

public class AppleDrStatistics {
    public static final Identifier APPLEDRALTAR_OFFERS_ACCEPTED = register("appledraltar_offers_accepted", StatFormatter.DEFAULT);
    public static final Identifier APPLEDRALTAR_OFFERS_REJECTED = register("appledraltar_offers_rejected", StatFormatter.DEFAULT);

    public static Identifier register(String id, StatFormatter formatter) {
        return PolymerStat.registerStat(Identifier.of(AppleDrMod.MOD_ID, id), formatter);
    }

    public static void init() {
        AppleDrMod.LOGGER.debug("Registering statistics");
    }
}
