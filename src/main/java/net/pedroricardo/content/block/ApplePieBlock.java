package net.pedroricardo.content.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.content.AppleDrItems;
import net.pedroricardo.content.AppleDrStatistics;

public class ApplePieBlock extends SimpleModelBlock {
    public ApplePieBlock(Settings settings, BlockState polymerState) {
        super(settings, polymerState);
    }

    @Override
    public BlockState getPolymerBreakEventBlockState(BlockState state, ServerPlayerEntity player) {
        return Blocks.CAKE.getDefaultState();
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!player.canConsume(false)) {
            return ActionResult.PASS;
        }
        world.removeBlock(pos, false);
        world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        // This is quite a mess... I sure hope Apple Dr does not see this!
        ItemStack stack = new ItemStack(AppleDrItems.APPLE_PIE);
        world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, stack), pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0, 0.05, 0.0);
        AppleDrItems.APPLE_PIE.finishUsing(stack, world, player);
        player.eatFood(world, stack, AppleDrItems.APPLE_PIE.getComponents().getOrDefault(DataComponentTypes.FOOD, FoodComponents.PUMPKIN_PIE));
        return ActionResult.SUCCESS;
    }
}
