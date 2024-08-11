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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.content.AppleDrStatistics;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class AppledraltarBlock extends Block implements FactoryBlock {
    public static final BooleanProperty HAS_APPLE = BooleanProperty.of("has_apple");

    public AppledraltarBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(HAS_APPLE, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HAS_APPLE);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx);
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

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (state.getOrEmpty(HAS_APPLE).orElse(false)) {
            world.setBlockState(pos, state.with(HAS_APPLE, false));
            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            player.incrementStat(AppleDrStatistics.APPLEDRALTAR_OFFERS_ACCEPTED);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (state.getOrEmpty(HAS_APPLE).orElse(false) && !player.isInCreativeMode()) {
            player.incrementStat(AppleDrStatistics.APPLEDRALTAR_OFFERS_REJECTED);
        }
        return super.onBreak(world, pos, state, player);
    }

    public final class Model extends BlockModel {
        private final ItemDisplayElement main;
        private final DroppedItemElement item;

        public Model(BlockState state) {
            this.main = ItemDisplayElementUtil.createSimple(getModel(state));
            this.main.setDisplaySize(1, 1);
            this.main.setScale(new Vector3f(2));
            this.addElement(this.main);
            this.item = new DroppedItemElement(state.get(HAS_APPLE) ? new ItemStack(Items.APPLE) : ItemStack.EMPTY);
            this.item.setOffset(new Vec3d(0.0, 0.5, 0.0));
            this.addElement(this.item);
        }

        @Override
        public void notifyUpdate(HolderAttachment.UpdateType updateType) {
            if (updateType == BlockAwareAttachment.BLOCK_STATE_UPDATE) {
                var state = this.blockState();
                this.main.setItem(getModel(state));
                this.item.setStack(this.blockState().get(HAS_APPLE) ? new ItemStack(Items.APPLE) : ItemStack.EMPTY);

                this.tick();
            }
        }
    }
}
