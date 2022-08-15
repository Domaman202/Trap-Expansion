package party.lemons.trapexpansion.init;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;
import party.lemons.trapexpansion.block.*;

public class TrapExpansionBlocks {
    public static Block SLIPPERY_STONE;
    public static Block SPIKE_TRAP;
    public static Block SPIKE_TRAP_WALL;
    public static Block FAN;
    public static Block DETECTOR;

    public TrapExpansionBlocks() {
    }

    public static void init() {
        SLIPPERY_STONE = registerBlock(new SpiderProofBlock(Block.Settings.of(Material.STONE).strength(0.5F, 1.5F)), "slippery_stone");
        SPIKE_TRAP = registerBlock(new SpikeTrapFloorBlock(Block.Settings.of(Material.METAL).strength(0.5F, 1.5F)), "spike_trap");
        SPIKE_TRAP_WALL = registerBlock(new SpikeTrapWallBlock(Block.Settings.of(Material.METAL).strength(0.5F, 1.5F).dropsLike(SPIKE_TRAP)), "spike_trap_wall", false);
        FAN = registerBlock(new FanBlock(Block.Settings.of(Material.STONE).strength(0.5F, 1.5F)), "fan");
        DETECTOR = registerBlock(new DetectorBlock(Block.Settings.of(Material.STONE).strength(0.5F, 1.5F)), "detector");
    }

    private static Block registerBlock(Block block, String name) {
        return registerBlock(block, name, true);
    }

    private static Block registerBlock(Block block, String name, boolean doItem) {
        Registry.register(Registry.BLOCK, "trapexpansion:" + name, block);
        if (doItem) {
            BlockItem item = new BlockItem(block, (new Item.Settings()).group(ItemGroup.REDSTONE));
            item.appendBlocks(Item.BLOCK_ITEMS, item);
            TrapExpansionItems.registerItem(item, name);
        }

        return block;
    }
}
