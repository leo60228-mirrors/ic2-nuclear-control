package net.minecraft.src.nuclearcontrol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.mod_IC2NuclearControl;
import net.minecraft.src.nuclearcontrol.panel.PanelSetting;
import net.minecraft.src.nuclearcontrol.panel.PanelString;
import net.minecraft.src.nuclearcontrol.utils.ItemStackUtils;

public abstract class ItemCardCounterSensorLocationBase extends ItemCardBase
{
    public static final int DISPLAY_ENERGY = 1;
    public static final int CARD_TYPE = 4;

    public ItemCardCounterSensorLocationBase(int i, int iconIndex)
    {
        super(i, iconIndex);
    }
    
    @Override
    public void update(TileEntityInfoPanel panel, ItemStack stack, int range)
    {
        NBTTagCompound nbtTagCompound = ItemStackUtils.getTagCompound(stack);
        int[] coordinates = getCoordinates(stack);
        Map<String, Integer> updateSet = new HashMap<String, Integer>();
        if(coordinates == null)
        {
            setField("activeData", false, nbtTagCompound, panel, updateSet);
        }
        else
        {
            int dx = coordinates[0] - panel.xCoord;
            int dy = coordinates[1] - panel.yCoord;
            int dz = coordinates[2] - panel.zCoord;
            if(Math.abs(dx) > range || 
                Math.abs(dy) > range || 
                Math.abs(dz) > range)
            {
                setField("activeData", false, nbtTagCompound, panel, updateSet);
            }
            else
            {
                TileEntity tileEntity = panel.worldObj.getBlockTileEntity(coordinates[0], coordinates[1], coordinates[2]);
                if(tileEntity != null && tileEntity instanceof TileEntityEnergyCounter)
                {
                    TileEntityEnergyCounter counter  = (TileEntityEnergyCounter)tileEntity;
                    setField("activeData", true, nbtTagCompound, panel, updateSet);
                    setField("energy", counter.counter, nbtTagCompound, panel, updateSet);
                }
                else
                {
                    setField("activeData", false, nbtTagCompound, panel, updateSet);
                }
            }
        }
        if(!updateSet.isEmpty())
            mod_IC2NuclearControl.setSensorCardField(panel, updateSet);
    }

    @Override
    public int getCardType()
    {
        return CARD_TYPE;
    }

    @Override
    abstract public void networkUpdate(String fieldName, int value, ItemStack stack);

    @Override
    abstract public List<PanelString> getStringData(int displaySettings, ItemStack itemStack, boolean showLabels);

    @Override
    abstract public List<PanelSetting> getSettingsList();
}
