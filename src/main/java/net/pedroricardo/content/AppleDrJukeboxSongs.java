package net.pedroricardo.content;

import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.pedroricardo.AppleDrMod;

public class AppleDrJukeboxSongs {
    public static final RegistryKey<JukeboxSong> MUSIC_DISC_SKIBIDI = of("skibidi");
    public static final RegistryKey<JukeboxSong> MUSIC_DISC_THE_VIDEO = of("the_video");
    public static final RegistryKey<JukeboxSong> MUSIC_DISC_DRINKING_SONG = of("drinking_song");
    public static final RegistryKey<JukeboxSong> MUSIC_DISC_DRIP = of("drip");
    public static final RegistryKey<JukeboxSong> MUSIC_DISC_STEAMED_HAMS_BLIZZCON = of("steamed_hams_blizzcon");
    public static final RegistryKey<JukeboxSong> MUSIC_DISC_WARM_CATS = of("warm_cats");

    private static RegistryKey<JukeboxSong> of(String id) {
        return RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(AppleDrMod.MOD_ID, id));
    }
}
