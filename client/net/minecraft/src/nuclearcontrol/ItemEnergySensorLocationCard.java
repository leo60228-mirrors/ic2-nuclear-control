package net.minecraft.src.nuclearcontrol;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.StatCollector;
import net.minecraft.src.nuclearcontrol.panel.PanelSetting;
import net.minecraft.src.nuclearcontrol.panel.PanelString;
import net.minecraft.src.nuclearcontrol.utils.ItemStackUtils;
import net.minecraft.src.nuclearcontrol.utils.StringUtils;

public class ItemEnergySensorLocationCard extends ItemEnergySensorLocationCardBase
{
    private static final String HINT_TEMPLATE = "x: %d, y: %d, z: %d";

    public ItemEnergySensorLocationCard(int i, int iconIndex)
    {
        super(i, iconIndex);
    }

    @Override
    public void networkUpdate(String fieldName, int value, ItemStack itemStack)
    {
        NBTTagCompound nbtTagCompound = ItemStackUtils.getTagCompound(itemStack);
        nbtTagCompound.setInteger(fieldName, value);
    }
    
        @Override
    public List<PanelString> getStringData(int displaySettings, ItemStack itemStack, boolean showLabels)
    {
        NBTTagCompound nbtTagCompound = ItemStackUtils.getTagCompound(itemStack);
        boolean activeData = nbtTagCompound.getInteger("activeData")==1;
        if(!activeData)
            return null;
        List<PanelString> result = new LinkedList<PanelString>();
        PanelString line;
        int energy =  nbtTagCompound.getInteger("energy");
        int storage =  nbtTagCompound.getInteger("maxStorage");
        String title = nbtTagCompound.getString("title");
        if(title!=null && !title.isEmpty())
        {
            line = new PanelString();
            line.textCenter = title; 
            result.add(line);
        }
        if((displaySettings & DISPLAY_ENERGY) > 0)
        {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelEnergy", energy, showLabels); 
            result.add(line);
        }
        if((displaySettings & DISPLAY_FREE) > 0)
        {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelEnergyFree", storage - energy, showLabels); 
            result.add(line);
        }
        if((displaySettings & DISPLAY_STORAGE) > 0)
        {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelEnergyStorage", storage, showLabels); 
            result.add(line);
        }
        if((displaySettings & DISPLAY_PERCENTAGE) > 0)
        {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelEnergyPercentage", storage==0? 100:(energy*100/storage), showLabels); 
            result.add(line);
        }
        return result;
    }

    @Override
    public List<PanelSetting> getSettingsList()
    {
        List<PanelSetting> result = new ArrayList<PanelSetting>(3);
        result.add(new PanelSetting(StatCollector.translateToLocal("msg.nc.cbInfoPanelEnergyCurrent"), DISPLAY_ENERGY, CARD_TYPE));
        result.add(new PanelSetting(StatCollector.translateToLocal("msg.nc.cbInfoPanelEnergyStorage"), DISPLAY_STORAGE, CARD_TYPE));
        result.add(new PanelSetting(StatCollector.translateToLocal("msg.nc.cbInfoPanelEnergyFree"), DISPLAY_FREE, CARD_TYPE));
        result.add(new PanelSetting(StatCollector.translateToLocal("msg.nc.cbInfoPanelEnergyPercentage"), DISPLAY_PERCENTAGE, CARD_TYPE));
        return result;
    }

    @Override
    public void addInformation(ItemStack itemStack, List info) 
    {
        int[] coordinates = getCoordinates(itemStack);
        if(coordinates!=null)
        {
            NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
            String title = nbtTagCompound.getString("title");
            if(title != null && !title.isEmpty())
            {
                info.add(title);
            }
            String hint = String.format(HINT_TEMPLATE, coordinates[0], coordinates[1], coordinates[2]);
            info.add(hint);
        }
    }
    
}
