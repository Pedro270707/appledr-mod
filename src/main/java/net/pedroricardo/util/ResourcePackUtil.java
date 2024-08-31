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
        item(AppleDrItems.ROTTEN_APPLE);
        item(AppleDrItems.APPLE_PIE);
        blockItem(AppleDrBlocks.APPLEDRALTAR);
        item(AppleDrItems.APPLE_PIE);
        block(AppleDrBlocks.APPLE_PIE);
        item(AppleDrItems.IRON_KEY);
        item(AppleDrItems.GOLDEN_KEY);
        item(AppleDrItems.DIAMOND_KEY);
        item(AppleDrItems.NETHERITE_KEY);
        item(AppleDrItems.APPLECAXE);
        item(AppleDrItems.APPLECAXE_II);
        item(AppleDrItems.APPLECAXE_III);
        item(AppleDrItems.APPLECAXE_IV);
        item(AppleDrItems.APPLECAXE_V);
        item(AppleDrItems.APPLECAXE_VI);
        item(AppleDrItems.APPLECAXE_VII);
        item(AppleDrItems.APPLECAXE_VIII);
        item(AppleDrItems.APPLECAXE_IX);
        item(AppleDrItems.APPLECAXE_X);

        BLOCK_MODELS.put(AppleDrBlocks.APPLE_BRICKS, PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK, PolymerBlockModel.of(Registries.BLOCK.getId(AppleDrBlocks.APPLE_BRICKS).withPrefixedPath("block/"))));
        BLOCK_MODELS.put(AppleDrBlocks.IRON_LOCK_APPLE_BRICKS, PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK, PolymerBlockModel.of(Registries.BLOCK.getId(AppleDrBlocks.IRON_LOCK_APPLE_BRICKS).withPrefixedPath("block/"))));
        BLOCK_MODELS.put(AppleDrBlocks.GOLDEN_LOCK_APPLE_BRICKS, PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK, PolymerBlockModel.of(Registries.BLOCK.getId(AppleDrBlocks.GOLDEN_LOCK_APPLE_BRICKS).withPrefixedPath("block/"))));
        BLOCK_MODELS.put(AppleDrBlocks.DIAMOND_LOCK_APPLE_BRICKS, PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK, PolymerBlockModel.of(Registries.BLOCK.getId(AppleDrBlocks.DIAMOND_LOCK_APPLE_BRICKS).withPrefixedPath("block/"))));
        BLOCK_MODELS.put(AppleDrBlocks.NETHERITE_LOCK_APPLE_BRICKS, PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK, PolymerBlockModel.of(Registries.BLOCK.getId(AppleDrBlocks.NETHERITE_LOCK_APPLE_BRICKS).withPrefixedPath("block/"))));
        BLOCK_MODELS.put(AppleDrBlocks.APPLE_STONE, PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK, PolymerBlockModel.of(Registries.BLOCK.getId(AppleDrBlocks.APPLE_STONE).withPrefixedPath("block/"))));
        blockItem(AppleDrBlocks.APPLE_BRICKS);
        blockItem(AppleDrBlocks.IRON_LOCK_APPLE_BRICKS);
        blockItem(AppleDrBlocks.GOLDEN_LOCK_APPLE_BRICKS);
        blockItem(AppleDrBlocks.DIAMOND_LOCK_APPLE_BRICKS);
        blockItem(AppleDrBlocks.NETHERITE_LOCK_APPLE_BRICKS);
        blockItem(AppleDrBlocks.APPLE_STONE);
    }

    private static void block(Block block) {
        MODELS.put(block, PolymerResourcePackUtils.requestModel(Items.STONE, Registries.BLOCK.getId(block).withPrefixedPath("block/")));
    }

    private static void item(ItemConvertible item) {
        MODELS.put(item.asItem(), PolymerResourcePackUtils.requestModel(Items.PAPER, Registries.ITEM.getId(item.asItem()).withPrefixedPath("item/")));
    }

    private static void blockItem(Block block) {
        MODELS.put(block.asItem(), PolymerResourcePackUtils.requestModel(Items.STONE, Registries.BLOCK.getId(block).withPrefixedPath("block/")));
    }
}
