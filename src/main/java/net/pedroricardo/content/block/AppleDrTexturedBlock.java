package net.pedroricardo.content.block;

import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.pedroricardo.util.ResourcePackUtil;

public class AppleDrTexturedBlock extends Block implements PolymerTexturedBlock {
    public AppleDrTexturedBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return ResourcePackUtil.BLOCK_MODELS.get(this);
    }
}
