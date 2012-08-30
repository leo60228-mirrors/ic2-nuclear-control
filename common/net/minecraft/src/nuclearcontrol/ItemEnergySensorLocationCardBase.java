package net.minecraft.src.nuclearcontrol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.mod_IC2NuclearControl;
import net.minecraft.src.ic2.api.IEnergyStorage;
import net.minecraft.src.nuclearcontrol.panel.PanelSetting;
import net.minecraft.src.nuclearcontrol.panel.PanelString;
import net.minecraft.src.nuclearcontrol.utils.ItemStackUtils;

public abstract class ItemEnergySensorLocationCardBase extends ItemCardBase
{
    public static final int DISPLAY_ENERGY = 1;
    public static final int DISPLAY_FREE = 2;
    public static final int DISPLAY_STORAGE = 4;
    public static final int DISPLAY_PERCENTAGE = 8;
    
    public static final int CARD_TYPE = 2;

    public ItemEnergySensorLocationCardBase(int i, int iconIndex)
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
            setField("state", STATE_INVALID_CARD, nbtTagCompound, panel, updateSet);
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
                setField("state", STATE_OUT_OF_RANGE, nbtTagCompound, panel, updateSet);
            }
            else
            {
                IEnergyStorage storage = EnergyStorageHelper.getStorageAt(panel.worldObj, 
                        coordinates[0], coordinates[1], coordinates[2]);
                if(storage != null){
                    setField("state", STATE_OK, nbtTagCompound, panel, updateSet);
                    setField("energy", storage.getStored(), nbtTagCompound, panel, updateSet);
                    setField("maxStorage", storage.getCapacity(), nbtTagCompound, panel, updateSet);
                }
                else
                {
                    setField("state", STATE_NO_TARGET, nbtTagCompound, panel, updateSet);
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
