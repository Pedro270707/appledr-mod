package net.pedroricardo.content.block;

import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.BlockAwareAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class AppledraltarBlock extends Block implements FactoryBlock {
    public AppledraltarBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return Blocks.BARRIER.getDefaultState();
    }

    @Override
    public void onPolymerBlockSend(BlockState blockState, BlockPos.Mutable pos, ServerPlayerEntity player) {
        FactoryBlock.super.onPolymerBlockSend(blockState, pos, player);
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return new Model(initialBlockState);
    }

    public ItemStack getModel(BlockState state) {
        return ItemDisplayElementUtil.getModel(this.asItem());
    }

    @Override
    public BlockState getPolymerBreakEventBlockState(BlockState state, ServerPlayerEntity player) {
        return Blocks.RED_STAINED_GLASS.getDefaultState();
    }

    public final class Model extends BlockModel {
        private final ItemDisplayElement main;
        private final DroppedItemElement item;

        public Model(BlockState state) {
            this.main = ItemDisplayElementUtil.createSimple(getModel(state));
            this.main.setDisplaySize(1, 1);
            this.main.setScale(new Vector3f(2));
            this.addElement(this.main);
            this.item = new DroppedItemElement(new ItemStack(Items.APPLE));
            this.item.setOffset(new Vec3d(0.0, 0.5, 0.0));
            this.addElement(this.item);
        }

        @Override
        public void notifyUpdate(HolderAttachment.UpdateType updateType) {
            if (updateType == BlockAwareAttachment.BLOCK_STATE_UPDATE) {
                var state = this.blockState();
                this.main.setItem(getModel(state));

                this.tick();
            }
        }
    }
}
