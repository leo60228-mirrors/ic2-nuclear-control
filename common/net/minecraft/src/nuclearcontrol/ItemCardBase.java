package net.minecraft.src.nuclearcontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.forge.ITextureProvider;
import net.minecraft.src.nuclearcontrol.panel.IPanelDataSource;
import net.minecraft.src.nuclearcontrol.panel.PanelSetting;
import net.minecraft.src.nuclearcontrol.panel.PanelString;
import net.minecraft.src.nuclearcontrol.utils.ItemStackUtils;

public abstract class ItemCardBase extends Item implements ITextureProvider, IPanelDataSource
{
    public static final int STATE_OK = 1;
    public static final int STATE_NO_TARGET = 2;
    public static final int STATE_OUT_OF_RANGE = 3;
    public static final int STATE_INVALID_CARD = 4;
    
  
    protected void setField(String name, int value, NBTTagCompound nbtTagCompound, TileEntityInfoPanel panel, Map<String, Integer> updateSet)
    {
        if(nbtTagCompound.hasKey(name))
        {
            int prevValue = nbtTagCompound.getInteger(name);
            if(prevValue != value)
                updateSet.put(name, value);
        }
        nbtTagCompound.setInteger(name, value);
    }
    
    protected void setField(String name, boolean value, NBTTagCompound nbtTagCompound, TileEntityInfoPanel panel, Map<String, Integer> updateSet)
    {
        setField(name, value?1:0, nbtTagCompound, panel, updateSet);
    }

    protected void setField(String name, long value, NBTTagCompound nbtTagCompound, TileEntityInfoPanel panel, Map<String, Integer> updateSet)
    {
        setField(name+"-lo", (int)(value&0xffffffff), nbtTagCompound, panel, updateSet);
        setField(name+"-hi", (int)((value>>32)&0xffffffff), nbtTagCompound, panel, updateSet);
    }

    public ItemCardBase(int i, int iconIndex)
    {
        super(i);
        setIconIndex(iconIndex);
        setMaxStackSize(1);
        canRepair = false;
    }

    @Override
    public boolean isDamageable()
    {
        return true;
    }
    
    @Override
    public String getTextureFile()
    {
        return "/img/texture_thermo.png";
    }
    
    public static int[] getCoordinates(ItemStack itemStack)
    {
        if(!(itemStack.getItem() instanceof ItemCardBase))
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
    
    
    public String getTitle(ItemStack stack)
    {
        if(!(stack.getItem() instanceof ItemCardBase))
            return "";
        NBTTagCompound nbtTagCompound = stack.getTagCompound();
        if (nbtTagCompound == null)
            return "";
        return nbtTagCompound.getString("title");
    }

    public void setTitle(ItemStack stack, String title)
    {
        ItemStackUtils.getTagCompound(stack).setString("title", title);
    }
    
    @Override
    public void addCreativeItems(ArrayList arraylist)
    {
        //should not be created via creative inventory
    }

    abstract public void update(TileEntityInfoPanel panel, ItemStack stack, int range);

    abstract public int getCardType();

    @Override
    abstract public void networkUpdate(String fieldName, int value, ItemStack stack);

    @Override
    abstract public List<PanelString> getStringData(int displaySettings, ItemStack itemStack, boolean showLabels);

    @Override
    abstract public List<PanelSetting> getSettingsList();
}
