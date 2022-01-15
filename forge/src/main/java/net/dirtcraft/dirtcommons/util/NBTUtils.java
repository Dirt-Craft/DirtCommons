package net.dirtcraft.dirtcommons.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class NBTUtils {

    public static int getInt(CompoundNBT data, String tag, int ifAbsent){
        return data.contains(tag)? data.getInt(tag) : ifAbsent;
    }

    public static String getString(CompoundNBT data, String tag, String ifAbsent){
        return data.contains(tag)? data.getString(tag) : ifAbsent;
    }

    public static Item getItem(CompoundNBT data, String tag, Item ifAbsent){
        String identifier = NBTUtils.getString(data, tag, "");
        if (identifier.isEmpty()) return ifAbsent;
        Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(identifier));
        return item == null? ifAbsent : item;
    }

    public static void setItem(CompoundNBT data, String tag, Item val) {
        ResourceLocation location = val.getRegistryName();
        String id = location == null? "" : location.toString();
        data.putString(tag, id);
    }

    public static ItemStack getItemStackXS(CompoundNBT data, String tag) {
        CompoundNBT itemData = data.getCompound(tag);
        Item type = NBTUtils.getItem(itemData, "type", Items.AIR);
        int quantity = NBTUtils.getInt(itemData, "amount", 0);
        CompoundNBT tagData = itemData.getCompound("tag");
        return new ItemStack(type, quantity, tagData.isEmpty()? null: tagData);
    }

    @SuppressWarnings("ConstantConditions")
    public static void setItemStackXS(CompoundNBT data, String tag, ItemStack stack) {
        CompoundNBT itemData = new CompoundNBT();
        setItem(itemData, "type", stack.getItem());
        itemData.putInt("amount", stack.getCount());
        if (stack.hasTag()) itemData.put("tag", stack.getTag());
        data.put(tag, itemData);
    }

    public static void setItemStack(CompoundNBT data, String tag, ItemStack stack){
        data.put(tag, stack.save(new CompoundNBT()));
    }

    public static ItemStack getItemStack(CompoundNBT data, String tag) {
        return ItemStack.of(data.getCompound(tag));
    }
}
