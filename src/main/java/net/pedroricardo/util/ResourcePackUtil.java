package net.pedroricardo.util;

import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.pedroricardo.content.AppleDrBlocks;
import net.pedroricardo.content.AppleDrItems;

import java.util.HashMap;
import java.util.Map;

public class ResourcePackUtil {
    public static final Map<ItemConvertible, PolymerModelData> MODELS = new HashMap<>();
    public static final Map<Block, BlockState> BLOCK_MODELS = new HashMap<>();

    public static void bootstrap() {
        MODELS.clear();
        MODELS.put(AppleDrItems.ROTTEN_APPLE, PolymerResourcePackUtils.requestModel(Items.ROTTEN_FLESH, Registries.ITEM.getId(AppleDrItems.ROTTEN_APPLE).withPrefixedPath("item/")));
        MODELS.put(AppleDrItems.APPLE_PIE, PolymerResourcePackUtils.requestModel(Items.PUMPKIN_PIE, Registries.ITEM.getId(AppleDrItems.APPLE_PIE).withPrefixedPath("item/")));
        MODELS.put(AppleDrItems.APPLEDRALTAR, PolymerResourcePackUtils.requestModel(Items.ENCHANTING_TABLE, Registries.ITEM.getId(AppleDrItems.APPLEDRALTAR).withPrefixedPath("block/")));
        MODELS.put(AppleDrBlocks.APPLE_PIE, PolymerResourcePackUtils.requestModel(Items.CAKE, Registries.BLOCK.getId(AppleDrBlocks.APPLE_PIE).withPrefixedPath("block/")));
        MODELS.put(AppleDrItems.IRON_KEY, PolymerResourcePackUtils.requestModel(Items.PAPER, Registries.ITEM.getId(AppleDrItems.IRON_KEY).withPrefixedPath("item/")));
        MODELS.put(AppleDrItems.GOLDEN_KEY, PolymerResourcePackUtils.requestModel(Items.PAPER, Registries.ITEM.getId(AppleDrItems.GOLDEN_KEY).withPrefixedPath("item/")));
        MODELS.put(AppleDrItems.DIAMOND_KEY, PolymerResourcePackUtils.requestModel(Items.PAPER, Registries.ITEM.getId(AppleDrItems.DIAMOND_KEY).withPrefixedPath("item/")));
        MODELS.put(AppleDrItems.NETHERITE_KEY, PolymerResourcePackUtils.requestModel(Items.PAPER, Registries.ITEM.getId(AppleDrItems.NETHERITE_KEY).withPrefixedPath("item/")));

        BLOCK_MODELS.put(AppleDrBlocks.APPLE_BRICKS, PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK, PolymerBlockModel.of(Registries.BLOCK.getId(AppleDrBlocks.APPLE_BRICKS).withPrefixedPath("block/"))));
        BLOCK_MODELS.put(AppleDrBlocks.IRON_LOCK_APPLE_BRICKS, PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK, PolymerBlockModel.of(Registries.BLOCK.getId(AppleDrBlocks.IRON_LOCK_APPLE_BRICKS).withPrefixedPath("block/"))));
        BLOCK_MODELS.put(AppleDrBlocks.GOLDEN_LOCK_APPLE_BRICKS, PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK, PolymerBlockModel.of(Registries.BLOCK.getId(AppleDrBlocks.GOLDEN_LOCK_APPLE_BRICKS).withPrefixedPath("block/"))));
        BLOCK_MODELS.put(AppleDrBlocks.DIAMOND_LOCK_APPLE_BRICKS, PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK, PolymerBlockModel.of(Registries.BLOCK.getId(AppleDrBlocks.DIAMOND_LOCK_APPLE_BRICKS).withPrefixedPath("block/"))));
        BLOCK_MODELS.put(AppleDrBlocks.NETHERITE_LOCK_APPLE_BRICKS, PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK, PolymerBlockModel.of(Registries.BLOCK.getId(AppleDrBlocks.NETHERITE_LOCK_APPLE_BRICKS).withPrefixedPath("block/"))));
        MODELS.put(AppleDrItems.APPLE_BRICKS, PolymerResourcePackUtils.requestModel(Items.PAPER, Registries.BLOCK.getId(AppleDrBlocks.APPLE_BRICKS).withPrefixedPath("block/")));
        MODELS.put(AppleDrItems.IRON_LOCK_APPLE_BRICKS, PolymerResourcePackUtils.requestModel(Items.PAPER, Registries.BLOCK.getId(AppleDrBlocks.IRON_LOCK_APPLE_BRICKS).withPrefixedPath("block/")));
        MODELS.put(AppleDrItems.GOLDEN_LOCK_APPLE_BRICKS, PolymerResourcePackUtils.requestModel(Items.PAPER, Registries.BLOCK.getId(AppleDrBlocks.GOLDEN_LOCK_APPLE_BRICKS).withPrefixedPath("block/")));
        MODELS.put(AppleDrItems.DIAMOND_LOCK_APPLE_BRICKS, PolymerResourcePackUtils.requestModel(Items.PAPER, Registries.BLOCK.getId(AppleDrBlocks.DIAMOND_LOCK_APPLE_BRICKS).withPrefixedPath("block/")));
        MODELS.put(AppleDrItems.NETHERITE_LOCK_APPLE_BRICKS, PolymerResourcePackUtils.requestModel(Items.PAPER, Registries.BLOCK.getId(AppleDrBlocks.NETHERITE_LOCK_APPLE_BRICKS).withPrefixedPath("block/")));
    }
}
