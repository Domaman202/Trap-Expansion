package party.lemons.trapexpansion.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.tick.OrderedTick;
import party.lemons.trapexpansion.init.TrapExpansionBlocks;
import party.lemons.trapexpansion.init.TrapExpansionSounds;
import party.lemons.trapexpansion.misc.SpikeDamageSource;

import java.util.List;
import java.util.Random;

public class SpikeTrapFloorBlock extends Block {
    protected static final VoxelShape AABB_UP = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.1, 1.0);
    protected static final VoxelShape AABB_DOWN = VoxelShapes.cuboid(0.0, 0.9, 0.0, 1.0, 1.0, 1.0);
    public static final IntProperty OUT = IntProperty.of("out", 0, 2);
    public static final DirectionProperty DIRECTION = DirectionProperty.of("direction", (f) -> f.getAxis().isVertical());
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public SpikeTrapFloorBlock(Block.Settings settings) {
        super(settings.nonOpaque());
        this.setDefaultState(this.stateManager.getDefaultState().with(OUT, 0).with(DIRECTION, Direction.UP).with(WATERLOGGED, false));
    }

    public SpikeTrapFloorBlock(Block.Settings settings, boolean child) {
        super(settings);
    }

    @Override
    public FluidState getFluidState(BlockState var1) {
        return var1.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(var1);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState var1, Direction var2, BlockState var3, WorldAccess var4, BlockPos var5, BlockPos var6) {
        if (var1.get(WATERLOGGED))
            var4.getFluidTickScheduler().scheduleTick(new OrderedTick<>(Fluids.WATER, var5, Fluids.WATER.getTickRate(var4), var4.getTickOrder()));
        return super.getStateForNeighborUpdate(var1, var2, var3, var4, var5, var6);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView bv, BlockPos pos, ShapeContext ctx) {
        return this.getCollisionShape(state, bv, pos, ctx);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient && !entity.isRemoved()) {
            int i = state.get(OUT);
            if (i == 0)
                this.updateState(world, pos, state, i);
            if (i == 2 && world.getTime() % 5L == 0L)
                entity.damage(SpikeDamageSource.SPIKE, 3.0F);
        }
    }

    /** @deprecated */
    @Deprecated
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block var4, BlockPos var5, boolean var6) {
        world.getBlockTickScheduler().scheduleTick(new OrderedTick<>(this, pos, world.getLevelProperties().getTime() + 5, world.getTickOrder()));
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!world.isClient) {
            int i = state.get(OUT);
            if (i > 0 || world.isReceivingRedstonePower(pos))
                this.updateState(world, pos, state, i);
        }
    }

    /** @deprecated */
    @Deprecated
    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState state2, boolean bool) {
        if (state.get(OUT) > 0 || world.isReceivingRedstonePower(pos))
            world.getBlockTickScheduler().scheduleTick(new OrderedTick<>(this, pos, world.getLevelProperties().getTime() + 5, world.getTickOrder()));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(DIRECTION) == Direction.UP ? AABB_UP : AABB_DOWN;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        boolean isWater = ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER;
        if (ctx.getSide() == Direction.DOWN)
            return this.getDefaultState().with(DIRECTION, Direction.DOWN).with(WATERLOGGED, isWater);
        else return switch (ctx.getSide()) {
            case NORTH, SOUTH, WEST, EAST -> TrapExpansionBlocks.SPIKE_TRAP_WALL.getDefaultState().with(SpikeTrapWallBlock.DIRECTION_WALL, ctx.getSide()).with(WATERLOGGED, isWater);
            default -> this.getDefaultState().with(WATERLOGGED, isWater);
        };
    }

    /** @deprecated */
    @Deprecated
    @Override
    public boolean hasComparatorOutput(BlockState var1) {
        return true;
    }

    /** @deprecated */
    @Deprecated
    @Override
    public int getComparatorOutput(BlockState var1, World var2, BlockPos var3) {
        return var1.get(OUT);
    }

    protected void updateState(World world, BlockPos pos, BlockState state, int outValue) {
        int change = 0;
        boolean powered = world.isReceivingRedstonePower(pos);
        if (!powered && !this.hasEntity(world, pos, state)) {
            change = -1;
        } else if (outValue < 2) {
            change = 1;
        }

        int endValue = Math.max(0, outValue + change);
        if (change != 0) {
            SoundEvent sound = TrapExpansionSounds.SOUND_SPIKE_1;
            if (endValue == 2)
                sound = TrapExpansionSounds.SOUND_SPIKE_2;
            world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0F, 0.5F + world.random.nextFloat() / 2.0F);
        }

        var n = state.with(OUT, endValue);
        world.setBlockState(pos, n);
        world.scheduleBlockRerenderIfNeeded(pos, state, n);
        if (endValue != 2 || !powered)
            world.getBlockTickScheduler().scheduleTick(new OrderedTick<>(this, pos, world.getLevelProperties().getTime() + 5, world.getTickOrder()));
    }

    protected boolean hasEntity(World worldIn, BlockPos pos, BlockState state) {
        List<? extends Entity> list = worldIn.getEntitiesByClass(Entity.class, (new Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)).offset(pos), (e) -> true);
        if (!list.isEmpty()) {
            for (Entity entity : list) {
                if (!entity.canAvoidTraps()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> st) {
        st.add(new Property[]{OUT}).add(new Property[]{DIRECTION}).add(WATERLOGGED);
    }
}
