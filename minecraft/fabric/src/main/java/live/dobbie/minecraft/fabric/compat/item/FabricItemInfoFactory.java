package live.dobbie.minecraft.fabric.compat.item;

import live.dobbie.minecraft.compat.item.MinecraftItemInfoFactory;

public class FabricItemInfoFactory extends MinecraftItemInfoFactory {
    @Override
    public FabricItemInfo.FabricItemInfoBuilder builder() {
        return FabricItemInfo.builder();
    }

    @Override
    public FabricItemEnchantment.FabricItemEnchantmentBuilder enchantment() {
        return FabricItemEnchantment.builder();
    }
}
