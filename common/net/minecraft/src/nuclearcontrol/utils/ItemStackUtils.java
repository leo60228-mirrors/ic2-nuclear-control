package net.minecraft.src.nuclearcontrol.utils;

import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;

public class ItemStackUtils
{
    
    public static NBTTagCompound getTagCompound(ItemStack itemStack)
    {
        NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
        if (nbtTagCompound == null)
        {
            nbtTagCompound = new NBTTagCompound();
            itemStack.setTagCompound(nbtTagCompound);
        }
        return nbtTagCompound;
    }
}
