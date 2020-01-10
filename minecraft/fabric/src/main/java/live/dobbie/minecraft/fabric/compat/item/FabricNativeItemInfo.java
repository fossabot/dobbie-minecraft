package live.dobbie.minecraft.fabric.compat.item;

import live.dobbie.minecraft.compat.converter.MinecraftIdConverter;
import live.dobbie.minecraft.fabric.compat.nbt.FabricNbtConvertible;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class FabricNativeItemInfo extends FabricItemInfo implements FabricNbtConvertible {
    private final @NonNull @Getter ItemStack nativeItemStack;

    FabricNativeItemInfo(@NonNull ItemStack nativeItemStack) {
        super(fromItem(nativeItemStack));
        this.nativeItemStack = nativeItemStack;
    }

    @Override
    public @NonNull CompoundTag toCompoundTag(@NonNull MinecraftIdConverter converter) {
        return nativeItemStack.toTag(new CompoundTag());
    }

    @NonNull
    public static FabricItemInfoBuilder fromItem(@NonNull ItemStack itemStack) {
        // TODO support more item nbt
        Item item = itemStack.getItem();
        FabricItemInfoBuilder b = FabricItemInfo.builder()
                .id(Registry.ITEM.getId(item).toString())
                .count(itemStack.getCount());
        processEnchantments(b, itemStack);
        return b;
    }

    private static void processEnchantments(FabricItemInfoBuilder b, ItemStack itemStack) {
        for (Tag tag : itemStack.getEnchantments()) {
            if (!(tag instanceof CompoundTag)) {
                throw new RuntimeException("expected " + CompoundTag.class + " as enchantment, but got: " + tag);
            }
            CompoundTag c = (CompoundTag) tag;
            String id = c.getString("id");
            short lvl = (short) c.getInt("lvl");
            b.enchantment(FabricItemEnchantment.builder().id(id).lvl(lvl));
        }
    }

    @Nullable
    public static FabricNativeItemInfo from(@Nullable ItemStack nativeItemStack) {
        if (nativeItemStack == null || nativeItemStack.getItem().equals(Items.AIR)) {
            return null;
        }
        return new FabricNativeItemInfo(nativeItemStack);
    }
}
