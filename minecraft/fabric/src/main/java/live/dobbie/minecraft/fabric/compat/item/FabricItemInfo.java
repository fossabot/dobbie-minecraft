package live.dobbie.minecraft.fabric.compat.item;

import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import live.dobbie.minecraft.compat.item.MinecraftItemEnchantment;
import live.dobbie.minecraft.compat.item.MinecraftItemInfo;
import live.dobbie.minecraft.fabric.compat.nbt.FabricNbtConvertible;
import live.dobbie.minecraft.util.TextUtil;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

@SuperBuilder
public class FabricItemInfo extends MinecraftItemInfo implements FabricNbtConvertible {
    @Override
    public @NonNull CompoundTag toCompoundTag(@NonNull MinecraftIdConverter converter) {
        // TODO support more item nbt
        CompoundTag item = new CompoundTag();
        item.putString("id", converter.convertItemId(getId()));
        item.putByte("Count", (byte) getCount());
        if (getEnchantments() != null) {
            ListTag enchantmentsTag = new ListTag();
            for (MinecraftItemEnchantment.MinecraftItemEnchantmentBuilder enchantmentBuilder : getEnchantments()) {
                MinecraftItemEnchantment enchantment = enchantmentBuilder.build();
                if (enchantment instanceof FabricItemEnchantment) {
                    enchantmentsTag.add(((FabricItemEnchantment) enchantment).toCompoundTag(converter));
                } else {
                    throw new RuntimeException("enchantments must be created using " + FabricItemInfoFactory.class);
                }
            }
            item.put("Enchantments", enchantmentsTag);
        }
        if (getDisplay() != null || getDisplayColor() != null || getDisplayLoreList() != null) {
            CompoundTag displayTag = new CompoundTag();
            if (getDisplay() != null) {
                displayTag.putString("Name", convertDisplayName(getDisplay()));
            }
            if (getDisplayColor() != null) {
                displayTag.putString("color", getDisplayColor());
            }
            if (getDisplayLoreList() != null && !getDisplayLoreList().isEmpty()) {
                ListTag loreListTag = new ListTag();
                for (String lore : getDisplayLoreList()) {
                    loreListTag.add(StringTag.of(convertDisplayName(lore)));
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
        if (!(item instanceof FabricItemInfo)) {
            throw new IllegalArgumentException("you should use " + FabricItemInfoFactory.class + " to create item info");
        }
        if (item instanceof FabricNativeItemInfo) {
            return ((FabricNativeItemInfo) item).getNativeItemStack();
        }
        return ItemStack.fromTag(((FabricItemInfo) item).toCompoundTag(converter));
    }
}
