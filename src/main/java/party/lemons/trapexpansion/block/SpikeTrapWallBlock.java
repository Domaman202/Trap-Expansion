package party.lemons.trapexpansion.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class SpikeTrapWallBlock extends SpikeTrapFloorBlock {
    protected static final VoxelShape AABB_NORTH = VoxelShapes.cuboid(0.0, 0.0, 1.0, 1.0, 1.0, 0.9);
    protected static final VoxelShape AABB_SOUTH = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 1.0, 0.1);
    protected static final VoxelShape AABB_WEST = VoxelShapes.cuboid(1.0, 0.0, 0.0, 0.9, 1.0, 1.0);
    protected static final VoxelShape AABB_EAST = VoxelShapes.cuboid(0.0, 0.0, 0.0, 0.1, 1.0, 1.0);
    public static final DirectionProperty DIRECTION_WALL = DirectionProperty.of("direction", (f) -> f.getAxis().isHorizontal());

    public SpikeTrapWallBlock(Block.Settings settings) {
        super(settings, true);
        this.setDefaultState(this.stateManager.getDefaultState().with(OUT, 0).with(DIRECTION_WALL, Direction.NORTH));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, EntityContext context) {
        return switch (state.get(DIRECTION_WALL)) {
            case NORTH -> AABB_NORTH;
            case SOUTH -> AABB_SOUTH;
            case WEST -> AABB_WEST;
            case EAST -> AABB_EAST;
            default -> AABB_EAST;
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> st) {
        st.add(new Property[]{OUT}).add(new Property[]{DIRECTION_WALL}).add(WATERLOGGED);
    }
}
