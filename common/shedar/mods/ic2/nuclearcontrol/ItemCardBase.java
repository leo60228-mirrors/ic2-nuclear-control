package shedar.mods.ic2.nuclearcontrol;

import java.util.List;
import java.util.Map;

import shedar.mods.ic2.nuclearcontrol.panel.IPanelDataSource;
import shedar.mods.ic2.nuclearcontrol.panel.PanelSetting;
import shedar.mods.ic2.nuclearcontrol.panel.PanelString;
import shedar.mods.ic2.nuclearcontrol.utils.ItemStackUtils;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;

public abstract class ItemCardBase extends Item implements  IPanelDataSource
{
    public static final int STATE_OK = 1;
    public static final int STATE_NO_TARGET = 2;
    public static final int STATE_OUT_OF_RANGE = 3;
    public static final int STATE_INVALID_CARD = 4;
    
    public ItemCardBase(int i, int iconIndex)
    {
        super(i);
        setIconIndex(iconIndex);
        setMaxStackSize(1);
        canRepair = false;
    }
  
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
    
    @SuppressWarnings("rawtypes")
    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
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
