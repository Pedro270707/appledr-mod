package net.pedroricardo.content;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.pedroricardo.AppleDrMod;
import net.pedroricardo.content.block.AppledraltarBlock;

public class AppleDrBlocks {
    public static final Block APPLEDRALTAR = register("appledraltar", new AppledraltarBlock(AbstractBlock.Settings.create().mapColor(MapColor.RED).sounds(BlockSoundGroup.GLASS).blockVision(Blocks::never).solidBlock(Blocks::always).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0f)));

    public static Block register(String id, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(AppleDrMod.MOD_ID, id), block);
    }
}
