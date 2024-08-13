package net.pedroricardo.content.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.block.NeighborUpdater;

import java.util.function.Supplier;

public class LockedBrickBlock extends AppleDrTexturedBlock {
    private final Supplier<Item> keyItem;

    public LockedBrickBlock(Settings settings, Supplier<Item> keyItem) {
        super(settings);
        this.keyItem = keyItem;
    }

    public Item getKeyItem() {
        return this.keyItem.get();
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.isOf(this.getKeyItem())) {
            this.unlock(world, pos);
            stack.decrementUnlessCreative(1, player);
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public void unlock(World world, BlockPos pos) {
        if (!world.getBlockState(pos).isOf(this)) return;
        world.breakBlock(pos, false);
        for (Direction direction : NeighborUpdater.UPDATE_ORDER) {
            BlockPos neighborPos = pos.offset(direction);
            if (world.getBlockState(neighborPos).isOf(this)) {
                this.unlock(world, neighborPos);
            }
        }
    }
}
