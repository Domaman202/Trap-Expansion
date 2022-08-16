package party.lemons.trapexpansion.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import party.lemons.trapexpansion.block.DetectorBlock;
import party.lemons.trapexpansion.init.TrapExpansionBlockEntities;

import java.util.List;

public class DetectorBlockEntity extends BlockEntity {
    public DetectorBlockEntity(BlockPos pos, BlockState state) {
        super(TrapExpansionBlockEntities.DETECTOR_BE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, DetectorBlockEntity blockEntity) {
        if (blockEntity.world.getTime() % 4L == 0L && !blockEntity.world.isClient) {
            if (!(state.getBlock() instanceof DetectorBlock)) {
                return;
            }

            Direction facing = state.get(DetectorBlock.FACING);
            Box bb = (new Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)).offset(blockEntity.pos.offset(facing)).expand(facing.getOffsetX() * 5, facing.getOffsetY() * 5, facing.getOffsetZ() * 5);
            List<Entity> entities = blockEntity.world.getOtherEntities(null, bb, (ex) -> true);
            int entityCount = entities.size();
            boolean hasEntity = entityCount > 0;
            if (hasEntity) {
                for (Entity e : entities) {
                    int xCheck = facing.getOffsetX() * (MathHelper.floor(e.getX()) - blockEntity.pos.getX());
                    int yCheck = facing.getOffsetY() * (MathHelper.floor(e.getY()) - blockEntity.pos.getY());
                    int zCheck = facing.getOffsetZ() * (MathHelper.floor(e.getZ()) - blockEntity.pos.getZ());

                    for (int b = 1; b < Math.abs(xCheck + yCheck + zCheck); ++b) {
                        if (blockEntity.world.getBlockState(blockEntity.pos.offset(facing, b)).isOpaque()) {
                            --entityCount;
                            if (entityCount <= 0) {
                                hasEntity = false;
                                break;
                            }
                        }
                    }
                }
            }

            boolean powered = state.get(DetectorBlock.POWERED);
            if (powered != hasEntity) {
                blockEntity.world.setBlockState(blockEntity.pos, state.with(DetectorBlock.POWERED, hasEntity));
            }
        }

    }
}
