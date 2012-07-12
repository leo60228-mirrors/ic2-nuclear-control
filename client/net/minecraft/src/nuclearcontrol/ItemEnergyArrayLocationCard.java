package net.minecraft.src.nuclearcontrol;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.StatCollector;
import net.minecraft.src.nuclearcontrol.panel.PanelSetting;
import net.minecraft.src.nuclearcontrol.panel.PanelString;

public class ItemEnergyArrayLocationCard extends ItemEnergyArrayLocationCardBase
{
    public ItemEnergyArrayLocationCard(int i, int iconIndex)
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
        int totalEnergy = 0;
        int totalStorage = 0;
        boolean showEach = (displaySettings & DISPLAY_EACH) > 0;
        boolean showSummary = (displaySettings & DISPLAY_TOTAL) > 0;
        boolean showEnergy = (displaySettings & DISPLAY_ENERGY) > 0;
        boolean showFree = (displaySettings & DISPLAY_FREE) > 0;
        boolean showStorage = (displaySettings & DISPLAY_STORAGE) > 0;
        int cardCount =  getCardCount(itemStack);
        for(int i=0; i<cardCount; i++)
        {
            int energy =  nbtTagCompound.getInteger(String.format("_%denergy",i));
            int storage =  nbtTagCompound.getInteger(String.format("_%dmaxStorage",i));
            if(showSummary)
            {
                totalEnergy += energy;
                totalStorage += storage;
            }
            
            if(showEach)
            {
                if(showEnergy)
                {
                    line = new PanelString();
                    line.textLeft = String.format(StatCollector.translateToLocal("msg.nc.InfoPanelEnergyN"), i+1, energy);
                    result.add(line);
                }
                if(showFree)
                {
                    line = new PanelString();
                    line.textLeft = String.format(StatCollector.translateToLocal("msg.nc.InfoPanelEnergyFreeN"), i+1, storage - energy);
                    result.add(line);
                }
                if(showStorage)
                {
                    line = new PanelString();
                    line.textLeft = String.format(StatCollector.translateToLocal("msg.nc.InfoPanelEnergyStorageN"), i+1, storage);
                    result.add(line);
                }
            }
        }
        if(showSummary)
        {
            if(showEnergy)
            {
                line = new PanelString();
                line.textLeft = String.format(StatCollector.translateToLocal("msg.nc.InfoPanelEnergy"), totalEnergy);
                result.add(line);
            }
            if(showFree)
            {
                line = new PanelString();
                line.textLeft = String.format(StatCollector.translateToLocal("msg.nc.InfoPanelEnergyFree"), totalStorage - totalEnergy);
                result.add(line);
            }
            if(showStorage)
            {
                line = new PanelString();
                line.textLeft = String.format(StatCollector.translateToLocal("msg.nc.InfoPanelEnergyStorage"), totalStorage);
                result.add(line);
            }
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
        result.add(new PanelSetting(StatCollector.translateToLocal("msg.nc.cbInfoPanelEnergyEach"), DISPLAY_EACH, CARD_TYPE));
        result.add(new PanelSetting(StatCollector.translateToLocal("msg.nc.cbInfoPanelEnergyTotal"), DISPLAY_TOTAL, CARD_TYPE));
        return result;
    }

    @Override
    public void addInformation(ItemStack itemStack, List info) 
    {
        int cardCount = getCardCount(itemStack);
        if(cardCount > 0)
        {
            String hint = String.format(StatCollector.translateToLocal("msg.nc.EnergyCardQuantity"), cardCount);
            info.add(hint);
        }
    }
    
}
