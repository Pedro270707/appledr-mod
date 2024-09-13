package net.pedroricardo.content;

import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.pedroricardo.AppleDrMod;

public class AppleDrJukeboxSongs {
    public static final RegistryKey<JukeboxSong> MUSIC_DISC_SKIBIDI = of("skibidi");
    public static final RegistryKey<JukeboxSong> MUSIC_DISC_THE_VIDEO = of("the_video");

    private static RegistryKey<JukeboxSong> of(String id) {
        return RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(AppleDrMod.MOD_ID, id));
    }
}
