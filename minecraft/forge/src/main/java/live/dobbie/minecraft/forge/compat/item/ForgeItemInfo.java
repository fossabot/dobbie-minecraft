package live.dobbie.minecraft.forge.compat.item;

import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import live.dobbie.minecraft.compat.item.MinecraftItemEnchantment;
import live.dobbie.minecraft.compat.item.MinecraftItemInfo;
import live.dobbie.minecraft.forge.compat.nbt.ForgeNbtConvertible;
import live.dobbie.minecraft.util.TextUtil;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;

@SuperBuilder
public class ForgeItemInfo extends MinecraftItemInfo implements ForgeNbtConvertible {
    @Override
    public @NonNull CompoundNBT toCompoundNBT(@NonNull MinecraftIdConverter converter) {
        // TODO support more item nbt
        CompoundNBT item = new CompoundNBT();
        item.putString("id", converter.convertItemId(getId()));
        item.putByte("Count", (byte) getCount());
        if (getEnchantments() != null) {
            ListNBT enchantmentsTag = new ListNBT();
            for (MinecraftItemEnchantment.MinecraftItemEnchantmentBuilder enchantmentBuilder : getEnchantments()) {
                MinecraftItemEnchantment enchantment = enchantmentBuilder.build();
                if (enchantment instanceof ForgeItemEnchantment) {
                    enchantmentsTag.add(((ForgeItemEnchantment) enchantment).toCompoundNBT(converter));
                } else {
                    throw new RuntimeException("enchantments must be created using " + ForgeItemInfoFactory.class);
                }
            }
            item.put("Enchantments", enchantmentsTag);
        }
        if (getDisplay() != null || getDisplayColor() != null || getDisplayLoreList() != null) {
            CompoundNBT displayTag = new CompoundNBT();
            if (getDisplay() != null) {
                displayTag.putString("Name", convertDisplayName(getDisplay()));
            }
            if (getDisplayColor() != null) {
                displayTag.putString("color", getDisplayColor());
            }
            if (getDisplayLoreList() != null && !getDisplayLoreList().isEmpty()) {
                ListNBT loreListTag = new ListNBT();
                for (String lore : getDisplayLoreList()) {
                    loreListTag.add(StringNBT.valueOf(convertDisplayName(lore)));
                }
                displayTag.put("Lore", loreListTag);
            }
            item.put("display", displayTag);
        }
        if (getSkullOwner() != null) {
            item.putString("SkullOwner", getSkullOwner());
        }
        return item;
    }

    private static String convertDisplayName(String name) {
        return TextUtil.toJsonText(name);
    }

    public static ItemStack toItemStack(@NonNull MinecraftItemInfo item, @NonNull MinecraftIdConverter converter) {
        if (!(item instanceof ForgeItemInfo)) {
            throw new IllegalArgumentException("you should use " + ForgeItemInfoFactory.class + " to create item info");
        }
        if (item instanceof ForgeNativeItemInfo) {
            return ((ForgeNativeItemInfo) item).getNativeItemStack();
        }
        return ItemStack.read(((ForgeItemInfo) item).toCompoundNBT(converter));
    }
}
