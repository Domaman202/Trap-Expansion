package party.lemons.trapexpansion.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import party.lemons.trapexpansion.block.entity.FanBlockEntity;
import party.lemons.trapexpansion.init.TrapExpansionBlockEntities;

import java.util.Random;

public class FanBlock extends BlockWithEntity {
    public static final BooleanProperty POWERED;
    public static final DirectionProperty FACING;

    public FanBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, false).with(FACING, Direction.SOUTH));
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(POWERED) && random.nextInt(3) == 0) {
            Direction facing = state.get(FACING);
            double xPos = (float)pos.offset(facing).getX() + random.nextFloat();
            double yPos = (float)pos.offset(facing).getY() + random.nextFloat();
            double zPos = (float)pos.offset(facing).getZ() + random.nextFloat();
            world.addParticle(ParticleTypes.CLOUD, xPos, yPos, zPos, (float)facing.getOffsetX() / 2.0F, (float)facing.getOffsetY() / 2.0F, (float)facing.getOffsetZ() / 2.0F);
        }

    }

    @Override
    public BlockRenderType getRenderType(BlockState var1) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos pos2, boolean boolean_1) {
        boolean powered = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
        if (powered) {
            world.getBlockTickScheduler().schedule(pos, this, 5);
            world.setBlockState(pos, state.with(POWERED, true));
        } else if (state.get(POWERED)) {
            world.getBlockTickScheduler().schedule(pos, this, 5);
            world.setBlockState(pos, state.with(POWERED, false));
        }

    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean bool) {
        if (world.isReceivingRedstonePower(pos)) {
            world.getBlockTickScheduler().schedule(pos, this, 5);
            world.setBlockState(pos, (BlockState)state.with(POWERED, true));
        }

    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> st) {
        st.add(new Property[]{FACING}).add(POWERED);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FanBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : FanBlock.checkType(type, TrapExpansionBlockEntities.FAN_BE, FanBlockEntity::tick);
    }

    static {
        POWERED = Properties.POWERED;
        FACING = Properties.FACING;
    }
}
