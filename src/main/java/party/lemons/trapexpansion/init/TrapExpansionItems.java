package party.lemons.trapexpansion.init;

import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class TrapExpansionItems {
    public TrapExpansionItems() {
    }

    public static void init() {
    }

    public static Item registerItem(Item item, String name) {
        Registry.register(Registry.ITEM, "trapexpansion:" + name, item);
        return item;
    }
}