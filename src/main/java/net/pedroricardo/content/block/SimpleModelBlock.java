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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.pedroricardo.util.ResourcePackUtil;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class SimpleModelBlock extends Block implements FactoryBlock {
    private final BlockState polymerState;

    public SimpleModelBlock(Settings settings, BlockState polymerState) {
        super(settings);
        this.polymerState = polymerState;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return this.polymerState;
    }

    @Override
    @Nullable
    public ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return new Model();
    }

    public ItemStack getModel() {
        return ResourcePackUtil.MODELS.get(this).asStack();
    }

    public final class Model extends BlockModel {
        private final ItemDisplayElement main;

        public Model() {
            this.main = ItemDisplayElementUtil.createSimple(SimpleModelBlock.this.getModel());
            this.main.setDisplaySize(1, 1);
            this.main.setScale(new Vector3f(2));
            this.addElement(this.main);
        }

        @Override
        public void notifyUpdate(HolderAttachment.UpdateType updateType) {
            if (updateType == BlockAwareAttachment.BLOCK_STATE_UPDATE) {
                this.main.setItem(SimpleModelBlock.this.getModel());

                this.tick();
            }
        }
    }
}
