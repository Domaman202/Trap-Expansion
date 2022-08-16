package party.lemons.trapexpansion.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import party.lemons.trapexpansion.block.FanBlock;
import party.lemons.trapexpansion.init.TrapExpansionBlockEntities;

import java.util.List;

public class FanBlockEntity extends BlockEntity implements Tickable {
    public FanBlockEntity() {
        super(TrapExpansionBlockEntities.FAN_BE);
    }

    @Override
    public void tick() {
        if (this.world.getTime() % 1L == 0L) {
            BlockState state = this.world.getBlockState(this.pos);
            if (!(state.getBlock() instanceof FanBlock)) {
                return;
            }

            if (!(Boolean)state.get(FanBlock.POWERED)) {
                return;
            }

            Direction facing = state.get(FanBlock.FACING);
            Box bb = (new Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)).offset(this.pos.offset(facing)).expand((float)facing.getOffsetX() * 8.0F, (float)facing.getOffsetY() * 8.0F, (double)((float)facing.getOffsetZ() * 8.0F));
            List<Entity> entities = this.world.getEntities((Entity) null, bb, (ex) -> true);

            for (Entity entity : entities) {
                int xCheck = facing.getOffsetX() * (MathHelper.floor(entity.getX()) - this.pos.getX());
                int yCheck = facing.getOffsetY() * (MathHelper.floor(entity.getY()) - this.pos.getY());
                int zCheck = facing.getOffsetZ() * (MathHelper.floor(entity.getZ()) - this.pos.getZ());

                for (int b = 1; b < Math.abs(xCheck + yCheck + zCheck); ++b) {
                    if (this.world.getBlockState(this.pos.offset(facing, b)).isOpaque()) {
                        return;
                    }
                }

                double distance = entity.getPos().distanceTo(new Vec3d(this.pos.getX(), this.pos.getY(), this.pos.getZ()));
                float distanceDecay = Math.max(0.0F, (float) ((8.0 - distance) / 64.0));
                float speed = 1.0F;
                if (facing == Direction.UP || facing == Direction.DOWN) {
                    ++speed;
                }

                float velX = speed * (float) facing.getOffsetX() * distanceDecay;
                float velY = speed * (float) facing.getOffsetY() * distanceDecay;
                float velZ = speed * (float) facing.getOffsetZ() * distanceDecay;
                entity.addVelocity((double) velX, (double) velY, (double) velZ);
                entity.fallDistance = Math.max(0.0F, entity.fallDistance - 1.0F);
            }
        }

    }
}
