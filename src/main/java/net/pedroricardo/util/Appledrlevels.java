package net.pedroricardo.util;

import net.minecraft.util.Identifier;
import net.pedroricardo.AppleDrMod;

import java.util.Map;
import java.util.OptionalInt;
import java.util.TreeMap;

public class Appledrlevels {
    private static final TreeMap<Integer, Appledrlevel> LEVELS = new TreeMap<>();

    public static final Appledrlevel ANTIAPPLEDR = register(-6000, new Appledrlevel(Identifier.of(AppleDrMod.MOD_ID, "antiappledr")));
    public static final Appledrlevel ANTIAPPLEDRMASTER = register(-1000, new Appledrlevel(Identifier.of(AppleDrMod.MOD_ID, "antiappledrmaster")));
    public static final Appledrlevel APPLEDRENEMY = register(-600, new Appledrlevel(Identifier.of(AppleDrMod.MOD_ID, "appledrenemy")));
    public static final Appledrlevel APPLEDRHATER = register(-300, new Appledrlevel(Identifier.of(AppleDrMod.MOD_ID, "appledrhater")));
    public static final Appledrlevel APPLEDRDISLIKER = register(-100, new Appledrlevel(Identifier.of(AppleDrMod.MOD_ID, "appledrdisliker")));
    public static final Appledrlevel SPROUT = new Appledrlevel(Identifier.of(AppleDrMod.MOD_ID, "sprout"));
    public static final Appledrlevel APPLEDRLIKER = register(100, new Appledrlevel(Identifier.of(AppleDrMod.MOD_ID, "appledrliker")));
    public static final Appledrlevel APPLEDRLOVER = register(300, new Appledrlevel(Identifier.of(AppleDrMod.MOD_ID, "appledrlover")));
    public static final Appledrlevel APPLEDRFRIEND = register(600, new Appledrlevel(Identifier.of(AppleDrMod.MOD_ID, "appledrfriend")));
    public static final Appledrlevel APPLEDRMASTER = register(1000, new Appledrlevel(Identifier.of(AppleDrMod.MOD_ID, "appledrmaster")));
    public static final Appledrlevel APPLEDR = register(6000, new Appledrlevel(Identifier.of(AppleDrMod.MOD_ID, "appledr")));

    public static Appledrlevel register(int value, Appledrlevel appledrlevel) {
        return LEVELS.put(value, appledrlevel);
    }

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
            return SPROUT;
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
