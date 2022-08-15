package party.lemons.trapexpansion.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import party.lemons.trapexpansion.block.entity.DetectorBlockEntity;

public class DetectorBlock extends BlockWithEntity {
    public static final BooleanProperty POWERED;
    public static final DirectionProperty FACING;

    public DetectorBlock(Block.Settings var1) {
        super(var1);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView var1) {
        return new DetectorBlockEntity();
    }

    @Override
    public boolean emitsRedstonePower(BlockState var1) {
        return true;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.getWeakRedstonePower(world, pos, direction);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView blockView, BlockPos pos, Direction direction) {
        return state.get(POWERED) && state.get(FACING) == direction ? 15 : 0;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> st) {
        st.add(new Property[]{FACING}).add(POWERED);
    }

    @Override
    public BlockRenderType getRenderType(BlockState var1) {
        return BlockRenderType.MODEL;
    }

    static {
        POWERED = Properties.POWERED;
        FACING = Properties.FACING;
    }
}