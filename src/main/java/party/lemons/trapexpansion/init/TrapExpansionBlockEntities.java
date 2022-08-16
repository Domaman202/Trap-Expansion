package party.lemons.trapexpansion.init;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;
import party.lemons.trapexpansion.block.entity.DetectorBlockEntity;
import party.lemons.trapexpansion.block.entity.FanBlockEntity;

public class TrapExpansionBlockEntities {
    public static BlockEntityType<FanBlockEntity> FAN_BE;
    public static BlockEntityType<DetectorBlockEntity> DETECTOR_BE;

    public TrapExpansionBlockEntities() {
    }

    public static void init() {
        FAN_BE = Registry.register(Registry.BLOCK_ENTITY_TYPE, "trapexpansion:fan", FabricBlockEntityTypeBuilder.create(FanBlockEntity::new, TrapExpansionBlocks.FAN).build());
        DETECTOR_BE = Registry.register(Registry.BLOCK_ENTITY_TYPE, "trapexpansion:detector", FabricBlockEntityTypeBuilder.create(DetectorBlockEntity::new, TrapExpansionBlocks.DETECTOR).build());;
    }
}
