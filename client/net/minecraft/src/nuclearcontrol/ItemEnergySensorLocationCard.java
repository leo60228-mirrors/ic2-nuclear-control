package net.minecraft.src.nuclearcontrol;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.StatCollector;
import net.minecraft.src.nuclearcontrol.panel.PanelSetting;
import net.minecraft.src.nuclearcontrol.panel.PanelString;

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
        NBTTagCompound nbtTagCompound = getTagCompound(itemStack);
        nbtTagCompound.setInteger(fieldName, value);
    }
    
        @Override
    public List<PanelString> getStringData(int displaySettings, ItemStack itemStack)
    {
        NBTTagCompound nbtTagCompound = getTagCompound(itemStack);
        boolean activeData = nbtTagCompound.getInteger("activeData")==1;
        if(!activeData)
            return null;
        List<PanelString> result = new LinkedList<PanelString>();
        PanelString line;
        int energy =  nbtTagCompound.getInteger("energy");
        int storage =  nbtTagCompound.getInteger("maxStorage");
        DecimalFormat formatter = new DecimalFormat("#,###.###");
        DecimalFormatSymbols smb = new DecimalFormatSymbols();
        smb.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(smb);
        if((displaySettings & DISPLAY_ENERGY) > 0)
        {
            line = new PanelString();
            line.textLeft = String.format(StatCollector.translateToLocal("msg.nc.InfoPanelEnergy"), formatter.format(energy));
            result.add(line);
        }
        if((displaySettings & DISPLAY_FREE) > 0)
        {
            line = new PanelString();
            line.textLeft = String.format(StatCollector.translateToLocal("msg.nc.InfoPanelEnergyFree"), formatter.format(storage - energy));
            result.add(line);
        }
        if((displaySettings & DISPLAY_STORAGE) > 0)
        {
            line = new PanelString();
            line.textLeft = String.format(StatCollector.translateToLocal("msg.nc.InfoPanelEnergyStorage"), formatter.format(storage));
            result.add(line);
        }
        if((displaySettings & DISPLAY_PERCENTAGE) > 0)
        {
            line = new PanelString();
            line.textLeft = String.format(StatCollector.translateToLocal("msg.nc.InfoPanelEnergyPercentage"), formatter.format(storage==0? 100:(energy*100/storage)));
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
            String hint = String.format(HINT_TEMPLATE, coordinates[0], coordinates[1], coordinates[2]);
            info.add(hint);
        }
    }
    
}
