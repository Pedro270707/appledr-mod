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
        blockWithItem(AppleDrBlocks.APPLEDRALTAR);
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
        item(AppleDrItems.AI_CHIP);
        item(AppleDrItems.MUSIC_DISC_SKIBIDI);
        item(AppleDrItems.MUSIC_DISC_THE_VIDEO);

        blockWithItem(AppleDrBlocks.APPLE_BRICKS);
        blockWithItem(AppleDrBlocks.IRON_LOCK_APPLE_BRICKS);
        blockWithItem(AppleDrBlocks.GOLDEN_LOCK_APPLE_BRICKS);
        blockWithItem(AppleDrBlocks.DIAMOND_LOCK_APPLE_BRICKS);
        blockWithItem(AppleDrBlocks.NETHERITE_LOCK_APPLE_BRICKS);
        blockWithItem(AppleDrBlocks.APPLE_STONE);
        blockWithItem(AppleDrBlocks.PITCH_BLACK_BLOCK);
        blockWithItem(AppleDrBlocks.CORE);
    }

    private static void block(Block block) {
        MODELS.put(block, PolymerResourcePackUtils.requestModel(Items.STONE, Registries.BLOCK.getId(block).withPrefixedPath("block/")));
    }

    private static void item(ItemConvertible item) {
        MODELS.put(item.asItem(), PolymerResourcePackUtils.requestModel(Items.PAPER, Registries.ITEM.getId(item.asItem()).withPrefixedPath("item/")));
    }

    private static void blockWithItem(Block block) {
        BLOCK_MODELS.put(block, PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK, PolymerBlockModel.of(Registries.BLOCK.getId(block).withPrefixedPath("block/"))));
        MODELS.put(block.asItem(), PolymerResourcePackUtils.requestModel(Items.STONE, Registries.BLOCK.getId(block).withPrefixedPath("block/")));
    }
}
