package net.pedroricardo.content;

import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.pedroricardo.AppleDrMod;
import net.pedroricardo.content.block.ApplePieBlock;
import net.pedroricardo.content.block.AppledraltarBlock;

public class AppleDrBlocks {
    public static final Block APPLEDRALTAR = register("appledraltar", new AppledraltarBlock(AbstractBlock.Settings.create().mapColor(MapColor.RED).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(Blocks::never).solidBlock(Blocks::never).suffocates(Blocks::never).blockVision(Blocks::never).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0f)));
    public static final Block APPLE_PIE = register("apple_pie", new ApplePieBlock(AbstractBlock.Settings.create().mapColor(MapColor.RED).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(Blocks::never).solidBlock(Blocks::never).suffocates(Blocks::never).blockVision(Blocks::never).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0f), Blocks.SNOW.getDefaultState().with(Properties.LAYERS, 2)));

    public static Block register(String id, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(AppleDrMod.MOD_ID, id), block);
    }
}
