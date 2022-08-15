package party.lemons.trapexpansion;

import net.fabricmc.api.ModInitializer;
import party.lemons.trapexpansion.init.TrapExpansionBlockEntities;
import party.lemons.trapexpansion.init.TrapExpansionBlocks;
import party.lemons.trapexpansion.init.TrapExpansionItems;
import party.lemons.trapexpansion.init.TrapExpansionSounds;

public class TrapExpansion implements ModInitializer {
    public void onInitialize() {
        TrapExpansionBlocks.init();
        TrapExpansionItems.init();
        TrapExpansionSounds.init();
        TrapExpansionBlockEntities.init();
    }
}
