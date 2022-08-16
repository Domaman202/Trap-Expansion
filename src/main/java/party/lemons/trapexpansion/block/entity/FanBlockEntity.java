package party.lemons.trapexpansion.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import party.lemons.trapexpansion.block.FanBlock;
import party.lemons.trapexpansion.init.TrapExpansionBlockEntities;

import java.util.ArrayList;
import java.util.List;

public class FanBlockEntity extends BlockEntity {
    public FanBlockEntity(BlockPos pos, BlockState state) {
        super(TrapExpansionBlockEntities.FAN_BE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, FanBlockEntity blockEntity) {
        if (world.isClient)
            return;

        if (!(Boolean)state.get(FanBlock.POWERED)) {
            return;
        }

        Direction facing = state.get(FanBlock.FACING);
        Box bb = (new Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)).offset(blockEntity.pos.offset(facing)).expand((float)facing.getOffsetX() * 8.0F, (float)facing.getOffsetY() * 8.0F, (double)((float)facing.getOffsetZ() * 8.0F));
        List<Entity> entities = blockEntity.world.getOtherEntities(null, bb, (ex) -> true);

        for (Entity entity : entities) {
            int xCheck = facing.getOffsetX() * (MathHelper.floor(entity.getX()) - blockEntity.pos.getX());
            int yCheck = facing.getOffsetY() * (MathHelper.floor(entity.getY()) - blockEntity.pos.getY());
            int zCheck = facing.getOffsetZ() * (MathHelper.floor(entity.getZ()) - blockEntity.pos.getZ());

            for (int b = 1; b < Math.abs(xCheck + yCheck + zCheck); ++b) {
                if (blockEntity.world.getBlockState(blockEntity.pos.offset(facing, b)).isOpaque()) {
                    return;
                }
            }

            double distance = entity.getPos().distanceTo(new Vec3d(blockEntity.pos.getX(), blockEntity.pos.getY(), blockEntity.pos.getZ()));
            float distanceDecay = Math.max(0.0F, (float) ((8.0 - distance) / 64.0));
            float speed = 1.0F;
            if (facing == Direction.UP || facing == Direction.DOWN) {
                ++speed;
            }

            float velX = speed * (float) facing.getOffsetX() * distanceDecay;
            float velY = speed * (float) facing.getOffsetY() * distanceDecay;
            float velZ = speed * (float) facing.getOffsetZ() * distanceDecay;
            entity.addVelocity(velX, velY, velZ);
            if (entity instanceof ServerPlayerEntity player)
                player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(entity));
            entity.fallDistance = Math.max(0.0F, entity.fallDistance - 1.0F);
        }
    }
}
