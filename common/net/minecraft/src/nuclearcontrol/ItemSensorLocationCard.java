package net.minecraft.src.nuclearcontrol;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.forge.ITextureProvider;

public class ItemSensorLocationCard extends Item implements ITextureProvider
{
    private static final String HINT_TEMPLATE = "x: %d, y: %d, z: %d";

    public ItemSensorLocationCard(int i, int iconIndex)
    {
        super(i);
        setIconIndex(iconIndex);
        setMaxStackSize(1);
    }

    @Override
    public boolean isDamageable()
    {
        return true;
    }
    
    public String getTextureFile()
    {
        return "/img/texture_thermo.png";
    }
    
    public static int[] getCoordinates(ItemStack itemStack)
    {
        if(!(itemStack.getItem() instanceof ItemSensorLocationCard))
            return null;
        NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
        if (nbtTagCompound == null)
        {
            return null;
        }
        int[] coordinates = new int[]{
            nbtTagCompound.getInteger("x"),  
            nbtTagCompound.getInteger("y"),  
            nbtTagCompound.getInteger("z")  
        };
        return coordinates;
    }
    
    public static void setCoordinates(ItemStack itemStack, int x, int y, int z)
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        itemStack.setTagCompound(nbttagcompound);
        nbttagcompound.setInteger("x", x);
        nbttagcompound.setInteger("y", y);
        nbttagcompound.setInteger("z", z);
    }

    public void addInformation(ItemStack itemStack, List info) 
    {
        int[] coordinates = getCoordinates(itemStack);
        if(coordinates!=null)
        {
            String hint = String.format(HINT_TEMPLATE, coordinates[0], coordinates[1], coordinates[2]);
            info.add(hint);
        }
    }
    
    @Override
    public void addCreativeItems(ArrayList arraylist)
    {
        //should not be created via creative inventory
    }
}
