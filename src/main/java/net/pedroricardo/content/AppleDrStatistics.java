package net.pedroricardo.content;

import eu.pb4.polymer.core.api.other.PolymerStat;
import net.minecraft.stat.StatFormatter;
import net.minecraft.util.Identifier;
import net.pedroricardo.AppleDrMod;

public class AppleDrStatistics {
    public static final Identifier APPLEDRALTAR_OFFERS_ACCEPTED = register("appledraltar_offers_accepted", StatFormatter.DEFAULT);
    public static final Identifier APPLEDRALTAR_OFFERS_REJECTED = register("appledraltar_offers_rejected", StatFormatter.DEFAULT);
    public static final Identifier APPLEDRS_GRACE = register("appledrs_grace", StatFormatter.DEFAULT); // probably wrong use of statistics? this is only incremented once, but it is the easiest way to do this.
    public static final Identifier APPLE_PIES_EATEN = register("apple_pies_eaten", StatFormatter.DEFAULT);

    public static Identifier register(String id, StatFormatter formatter) {
        return PolymerStat.registerStat(Identifier.of(AppleDrMod.MOD_ID, id), formatter);
    }

    public static void init() {
        AppleDrMod.LOGGER.debug("Registering statistics");
    }
}
