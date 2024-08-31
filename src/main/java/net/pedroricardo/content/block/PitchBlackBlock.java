package net.pedroricardo.content.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PitchBlackBlock extends AppleDrTexturedBlock {
    public PitchBlackBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        for (Direction direction : new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}) {
            if (world.getBlockState(pos.offset(direction)).isAir()) {
                world.setBlockState(pos.offset(direction), state, Block.NOTIFY_ALL);
            }
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        if (world.getBlockState(sourcePos).isAir() && sourcePos.getY() == pos.getY()) {
            world.setBlockState(sourcePos, this.getDefaultState(), Block.NOTIFY_ALL);
        }
    }
}
