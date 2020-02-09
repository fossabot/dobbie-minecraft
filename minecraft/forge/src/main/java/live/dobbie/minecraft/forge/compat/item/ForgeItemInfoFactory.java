package live.dobbie.minecraft.forge.compat.item;

import live.dobbie.minecraft.compat.item.MinecraftItemInfoFactory;

public class ForgeItemInfoFactory extends MinecraftItemInfoFactory {
    @Override
    public ForgeItemInfo.ForgeItemInfoBuilder builder() {
        return ForgeItemInfo.builder();
    }

    @Override
    public ForgeItemEnchantment.ForgeItemEnchantmentBuilder enchantment() {
        return ForgeItemEnchantment.builder();
    }
}
