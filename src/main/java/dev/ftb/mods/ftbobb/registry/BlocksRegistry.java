package dev.ftb.mods.ftbobb.registry;

import dev.ftb.mods.ftbobb.FTBOBB;
import dev.ftb.mods.ftbobb.blocks.SluiceBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlocksRegistry {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FTBOBB.MODID);

    public static DeferredBlock<SluiceBlock> OAK_SLUICE = BLOCKS.register("oak_sluice", () -> new SluiceBlock());
    public static DeferredBlock<SluiceBlock> IRON_SLUICE = BLOCKS.register("iron_sluice", () -> new SluiceBlock());
    public static DeferredBlock<SluiceBlock> DIAMOND_SLUICE = BLOCKS.register("diamond_sluice", () -> new SluiceBlock());
    public static DeferredBlock<SluiceBlock> NETHERITE_SLUICE = BLOCKS.register("netherite_sluice", () -> new SluiceBlock());

    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
