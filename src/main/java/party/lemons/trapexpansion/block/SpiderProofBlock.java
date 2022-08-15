package party.lemons.trapexpansion.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpiderProofBlock extends Block {
    public SpiderProofBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0.001, 0.001, 0.001, 0.998, 0.998, 0.998);
    }

    /** @deprecated */
    @Deprecated
    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof SpiderEntity) {
            ((SpiderEntity)entity).setClimbingWall(false);
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        TranslatableText text = new TranslatableText("trapexpansion.tip.spiderproof");
        text.setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
        tooltip.add(text);
        super.appendTooltip(stack, world, tooltip, options);
    }
}
