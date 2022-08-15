package party.lemons.trapexpansion.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import party.lemons.trapexpansion.block.DetectorBlock;
import party.lemons.trapexpansion.init.TrapExpansionBlockEntities;

import java.util.List;

public class DetectorBlockEntity extends BlockEntity implements Tickable {
    public DetectorBlockEntity() {
        super(TrapExpansionBlockEntities.DETECTOR_BE);
    }

    @Override
    public void tick() {
        if (this.world.getTime() % 4L == 0L && !this.world.isClient) {
            BlockState state = this.world.getBlockState(this.pos);
            if (!(state.getBlock() instanceof DetectorBlock)) {
                return;
            }

            Direction facing = state.get(DetectorBlock.FACING);
            Box bb = (new Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)).offset(this.pos.offset(facing)).expand(facing.getOffsetX() * 5, facing.getOffsetY() * 5, facing.getOffsetZ() * 5);
            List<Entity> entities = this.world.getOtherEntities(null, bb, (ex) -> true);
            int entityCount = entities.size();
            boolean hasEntity = entityCount > 0;
            if (hasEntity) {
                for (Entity e : entities) {
                    int xCheck = facing.getOffsetX() * (MathHelper.floor(e.getX()) - this.pos.getX());
                    int yCheck = facing.getOffsetY() * (MathHelper.floor(e.getY()) - this.pos.getY());
                    int zCheck = facing.getOffsetZ() * (MathHelper.floor(e.getZ()) - this.pos.getZ());

                    for (int b = 1; b < Math.abs(xCheck + yCheck + zCheck); ++b) {
                        if (this.world.getBlockState(this.pos.offset(facing, b)).isOpaque()) {
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
                this.world.setBlockState(this.pos, state.with(DetectorBlock.POWERED, hasEntity));
            }
        }

    }
}
