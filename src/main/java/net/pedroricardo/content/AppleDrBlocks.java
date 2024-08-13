package net.pedroricardo.content;

import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.pedroricardo.AppleDrMod;
import net.pedroricardo.content.block.AppleDrTexturedBlock;
import net.pedroricardo.content.block.ApplePieBlock;
import net.pedroricardo.content.block.AppledraltarBlock;
import net.pedroricardo.content.block.LockedBrickBlock;

public class AppleDrBlocks {
    public static final Block APPLEDRALTAR = register("appledraltar", new AppledraltarBlock(AbstractBlock.Settings.create().mapColor(MapColor.RED).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(Blocks::never).solidBlock(Blocks::never).suffocates(Blocks::never).blockVision(Blocks::never).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0f).dropsNothing()));
    public static final Block APPLE_PIE = register("apple_pie", new ApplePieBlock(AbstractBlock.Settings.create().mapColor(MapColor.YELLOW).sounds(BlockSoundGroup.WOOL).nonOpaque().allowsSpawning(Blocks::never).solidBlock(Blocks::never).suffocates(Blocks::never).blockVision(Blocks::never).strength(2.0f).dropsNothing(), Blocks.SNOW.getDefaultState().with(Properties.LAYERS, 2)));
    public static final Block APPLE_BRICKS = register("apple_bricks", new AppleDrTexturedBlock(AbstractBlock.Settings.create().mapColor(MapColor.RED).sounds(BlockSoundGroup.STONE).instrument(NoteBlockInstrument.XYLOPHONE).strength(-1.0f, 3600000.0f).dropsNothing()));
    public static final Block IRON_LOCK_APPLE_BRICKS = register("iron_lock_apple_bricks", new LockedBrickBlock(AbstractBlock.Settings.create().mapColor(MapColor.RED).sounds(BlockSoundGroup.STONE).instrument(NoteBlockInstrument.XYLOPHONE).strength(-1.0f, 3600000.0f).dropsNothing(), () -> AppleDrItems.IRON_KEY));
    public static final Block GOLDEN_LOCK_APPLE_BRICKS = register("golden_lock_apple_bricks", new LockedBrickBlock(AbstractBlock.Settings.create().mapColor(MapColor.RED).sounds(BlockSoundGroup.STONE).instrument(NoteBlockInstrument.XYLOPHONE).strength(-1.0f, 3600000.0f).dropsNothing(), () -> AppleDrItems.GOLDEN_KEY));
    public static final Block DIAMOND_LOCK_APPLE_BRICKS = register("diamond_lock_apple_bricks", new LockedBrickBlock(AbstractBlock.Settings.create().mapColor(MapColor.RED).sounds(BlockSoundGroup.STONE).instrument(NoteBlockInstrument.XYLOPHONE).strength(-1.0f, 3600000.0f).dropsNothing(), () -> AppleDrItems.DIAMOND_KEY));
    public static final Block NETHERITE_LOCK_APPLE_BRICKS = register("netherite_lock_apple_bricks", new LockedBrickBlock(AbstractBlock.Settings.create().mapColor(MapColor.RED).sounds(BlockSoundGroup.STONE).instrument(NoteBlockInstrument.XYLOPHONE).strength(-1.0f, 3600000.0f).dropsNothing(), () -> AppleDrItems.NETHERITE_KEY));

    public static Block register(String id, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(AppleDrMod.MOD_ID, id), block);
    }
}
