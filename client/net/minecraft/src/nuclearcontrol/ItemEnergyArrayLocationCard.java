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

public class ItemEnergyArrayLocationCard extends ItemEnergyArrayLocationCardBase
{
    public ItemEnergyArrayLocationCard(int i, int iconIndex)
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
        int state = nbtTagCompound.getInteger("state");
        if(state != STATE_OK)
            return StringUtils.getStateMessage(state);
        List<PanelString> result = new LinkedList<PanelString>();
        PanelString line;
        int totalEnergy = 0;
        int totalStorage = 0;
        boolean showEach = (displaySettings & DISPLAY_EACH) > 0;
        boolean showSummary = (displaySettings & DISPLAY_TOTAL) > 0;
        boolean showEnergy = (displaySettings & DISPLAY_ENERGY) > 0;
        boolean showFree = (displaySettings & DISPLAY_FREE) > 0;
        boolean showStorage = (displaySettings & DISPLAY_STORAGE) > 0;
        boolean showPercentage = (displaySettings & DISPLAY_PERCENTAGE) > 0;
        int cardCount = getCardCount(itemStack);
        String title = nbtTagCompound.getString("title");
        if(title!=null && !title.isEmpty())
        {
            line = new PanelString();
            line.textCenter = title; 
            result.add(line);
        }
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
                    if(showLabels)
                        line.textLeft = String.format(StatCollector.translateToLocal("msg.nc.InfoPanelEnergyN"), i+1, StringUtils.getFormatted("", energy, false));
                    else
                        line.textLeft = StringUtils.getFormatted("", energy, false);
                    result.add(line);
                }
                if(showFree)
                {
                    line = new PanelString();
                    if(showLabels)
                        line.textLeft = String.format(StatCollector.translateToLocal("msg.nc.InfoPanelEnergyFreeN"), i+1, StringUtils.getFormatted("", storage - energy, false));
                    else
                        line.textLeft = StringUtils.getFormatted("", storage - energy, false);

                    result.add(line);
                }
                if(showStorage)
                {
                    line = new PanelString();
                    if(showLabels)
                        line.textLeft = String.format(StatCollector.translateToLocal("msg.nc.InfoPanelEnergyStorageN"), i+1, StringUtils.getFormatted("", storage, false));
                    else
                        line.textLeft = StringUtils.getFormatted("", storage, false);
                    result.add(line);
                }
                if(showPercentage)
                {
                    line = new PanelString();
                    if(showLabels)
                        line.textLeft = String.format(StatCollector.translateToLocal("msg.nc.InfoPanelEnergyPercentageN"), i+1, StringUtils.getFormatted("", storage==0? 100:(energy*100/storage), false));
                    else
                        line.textLeft = StringUtils.getFormatted("", storage==0? 100:(energy*100/storage), false);
                    result.add(line);
                }                
            }
        }
        if(showSummary)
        {
            if(showEnergy)
            {
                line = new PanelString();
                line.textLeft =  StringUtils.getFormatted("msg.nc.InfoPanelEnergy", totalEnergy, showLabels);
                result.add(line);
            }
            if(showFree)
            {
                line = new PanelString();
                line.textLeft =  StringUtils.getFormatted("msg.nc.InfoPanelEnergyFree", totalStorage - totalEnergy, showLabels);
                result.add(line);
            }
            if(showStorage)
            {
                line = new PanelString();
                line.textLeft =  StringUtils.getFormatted("msg.nc.InfoPanelEnergyStorage", totalStorage, showLabels);
                result.add(line);
            }
            if(showPercentage)
            {
                line = new PanelString();
                line.textLeft =  StringUtils.getFormatted("msg.nc.InfoPanelEnergyPercentage", totalStorage==0? 100:(totalEnergy*100/totalStorage), showLabels);
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
        result.add(new PanelSetting(StatCollector.translateToLocal("msg.nc.cbInfoPanelEnergyPercentage"), DISPLAY_PERCENTAGE, CARD_TYPE));
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
            NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
            String title = nbtTagCompound.getString("title");
            if(title != null && !title.isEmpty())
            {
                info.add(title);
            }
            String hint = String.format(StatCollector.translateToLocal("msg.nc.EnergyCardQuantity"), cardCount);
            info.add(hint);
        }
    }
    
}
