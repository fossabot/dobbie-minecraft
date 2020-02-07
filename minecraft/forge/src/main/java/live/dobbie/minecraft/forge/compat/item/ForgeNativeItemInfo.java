package live.dobbie.minecraft.forge.compat.item;

import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class ForgeNativeItemInfo extends ForgeItemInfo {
    private final @NonNull
    @Getter
    ItemStack nativeItemStack;

    ForgeNativeItemInfo(@NonNull ItemStack nativeItemStack) {
        super(fromItem(nativeItemStack));
        this.nativeItemStack = nativeItemStack;
    }

    @Override
    public @NonNull CompoundNBT toCompoundNBT(@NonNull MinecraftIdConverter converter) {
        return nativeItemStack.serializeNBT();
    }

    @NonNull
    public static ForgeItemInfoBuilder fromItem(@NonNull ItemStack itemStack) {
        // TODO support more item nbt
        Item item = itemStack.getItem();
        ForgeItemInfoBuilder b = ForgeItemInfo.builder()
                .id(Registry.ITEM.getKey(item).toString())
                .count(itemStack.getCount());
        processEnchantments(b, itemStack);
        return b;
    }

    private static void processEnchantments(ForgeItemInfoBuilder b, ItemStack itemStack) {
        for (INBT tag : itemStack.getEnchantmentTagList()) {
            if (!(tag instanceof CompoundNBT)) {
                throw new RuntimeException("expected " + CompoundNBT.class + " as enchantment, but got: " + tag);
            }
            CompoundNBT c = (CompoundNBT) tag;
            String id = c.getString("id");
            short lvl = (short) c.getInt("lvl");
            b.enchantment(ForgeItemEnchantment.builder().id(id).lvl(lvl));
        }
    }

    @Nullable
    public static ForgeNativeItemInfo from(@Nullable ItemStack nativeItemStack) {
        if (nativeItemStack == null || nativeItemStack.getItem().equals(Items.AIR)) {
            return null;
        }
        return new ForgeNativeItemInfo(nativeItemStack);
    }
}
