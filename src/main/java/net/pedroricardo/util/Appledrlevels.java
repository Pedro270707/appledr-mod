package net.pedroricardo.util;

import net.minecraft.util.Identifier;
import net.pedroricardo.AppleDrMod;

import java.util.Map;
import java.util.OptionalInt;
import java.util.TreeMap;

public class Appledrlevels {
    private static final TreeMap<Integer, Appledrlevel> LEVELS = new TreeMap<>();
    public static final Appledrlevel DEFAULT_LEVEL = new Appledrlevel(Identifier.of(AppleDrMod.MOD_ID, "sprout"));

    static {
        LEVELS.put(-6000, new Appledrlevel(Identifier.of(AppleDrMod.MOD_ID, "antiappledr")));
        LEVELS.put(-1000, new Appledrlevel(Identifier.of(AppleDrMod.MOD_ID, "antiappledrmaster")));
        LEVELS.put(-600, new Appledrlevel(Identifier.of(AppleDrMod.MOD_ID, "appledrenemy")));
        LEVELS.put(-300, new Appledrlevel(Identifier.of(AppleDrMod.MOD_ID, "appledrhater")));
        LEVELS.put(-100, new Appledrlevel(Identifier.of(AppleDrMod.MOD_ID, "appledrdisliker")));
        LEVELS.put(100, new Appledrlevel(Identifier.of(AppleDrMod.MOD_ID, "appledrliker")));
        LEVELS.put(300, new Appledrlevel(Identifier.of(AppleDrMod.MOD_ID, "appledrlover")));
        LEVELS.put(600, new Appledrlevel(Identifier.of(AppleDrMod.MOD_ID, "appledrfriend")));
        LEVELS.put(1000, new Appledrlevel(Identifier.of(AppleDrMod.MOD_ID, "appledrmaster")));
        LEVELS.put(6000, new Appledrlevel(Identifier.of(AppleDrMod.MOD_ID, "appledr")));
    }

    public static Appledrlevel getAppledrlevel(int appledrness) {
        Map.Entry<Integer, Appledrlevel> level = appledrness >= 0 ? LEVELS.floorEntry(appledrness) : LEVELS.ceilingEntry(appledrness);
        if (level == null || Math.signum(level.getKey()) != Math.signum(appledrness)) {
            return DEFAULT_LEVEL;
        }
        return level.getValue();
    }

    /**
     * Use {@link Appledrlevel#getLevel()} instead.
     */
    protected static OptionalInt getAppledrness(Appledrlevel appledrlevel) {
        for (Map.Entry<Integer, Appledrlevel> entry : LEVELS.entrySet()) {
            if (entry.getValue() == appledrlevel) {
                return OptionalInt.of(entry.getKey());
            }
        }
        return OptionalInt.empty();
    }
}
