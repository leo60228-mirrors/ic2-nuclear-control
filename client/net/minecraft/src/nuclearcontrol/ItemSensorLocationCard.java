package net.minecraft.src.nuclearcontrol;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.StatCollector;
import net.minecraft.src.nuclearcontrol.panel.PanelSetting;
import net.minecraft.src.nuclearcontrol.panel.PanelString;

public class ItemSensorLocationCard extends ItemSensorLocationCardBase
{

    public ItemSensorLocationCard(int i, int iconIndex)
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
        String text;
        PanelString line;
        if((displaySettings & DISPLAY_HEAT) > 0)
        {
            line = new PanelString();
            line.textLeft = String.format(StatCollector.translateToLocal("msg.nc.InfoPanelHeat"), nbtTagCompound.getInteger("heat"));
            result.add(line);
        }
        if((displaySettings & DISPLAY_MAXHEAT) > 0)
        {
            line = new PanelString();
            line.textLeft = String.format(StatCollector.translateToLocal("msg.nc.InfoPanelMaxHeat"), nbtTagCompound.getInteger("maxHeat"));
            result.add(line);
        }
        if((displaySettings & DISPLAY_MELTING) > 0)
        {
            line = new PanelString();
            line.textLeft = String.format(StatCollector.translateToLocal("msg.nc.InfoPanelMelting"), nbtTagCompound.getInteger("maxHeat")*85/100);
            result.add(line);
        }
        if((displaySettings & DISPLAY_OUTPUT) > 0)
        {
            line = new PanelString();
            line.textLeft = String.format(StatCollector.translateToLocal("msg.nc.InfoPanelOutput"), nbtTagCompound.getInteger("output"));
            result.add(line);
        }
        int timeLeft = nbtTagCompound.getInteger("timeLeft");
        if((displaySettings & DISPLAY_TIME) > 0)
        {
            int hours = timeLeft / 3600;
            int minutes = (timeLeft % 3600) / 60;
            int seconds = timeLeft % 60;
            line = new PanelString();

            String time = String.format("%d:%02d:%02d", hours, minutes, seconds);                
            line.textLeft = String.format(StatCollector.translateToLocal("msg.nc.InfoPanelTimeRemaining"), time);
            result.add(line);
        }

        int txtColor = 0;
        if((displaySettings & DISPLAY_ONOFF) > 0)
        {
            boolean reactorPowered = nbtTagCompound.getInteger("reactorPowered")==1;
            if(reactorPowered)
            {
                txtColor = 0x00ff00;
                text = StatCollector.translateToLocal("msg.nc.InfoPanelOn");
            }
            else
            {
                txtColor = 0xff0000;
                text = StatCollector.translateToLocal("msg.nc.InfoPanelOff");
            }
            if(result.size()>0)
            {
                PanelString firstLine = result.get(0);
                firstLine.textRight = text;
                firstLine.colorRight = txtColor;
            }
            else
            {
                line = new PanelString();
                line.textLeft = text;
                line.colorLeft = txtColor;
                result.add(line);
            }
        }        
        return result;
    }

    @Override
    public List<PanelSetting> getSettingsList()
    {
        List<PanelSetting> result = new ArrayList<PanelSetting>(6);
        result.add(new PanelSetting(StatCollector.translateToLocal("msg.nc.cbInfoPanelOnOff"), DISPLAY_ONOFF, CARD_TYPE));
        result.add(new PanelSetting(StatCollector.translateToLocal("msg.nc.cbInfoPanelHeat"), DISPLAY_HEAT, CARD_TYPE));
        result.add(new PanelSetting(StatCollector.translateToLocal("msg.nc.cbInfoPanelMaxHeat"), DISPLAY_MAXHEAT, CARD_TYPE));
        result.add(new PanelSetting(StatCollector.translateToLocal("msg.nc.cbInfoPanelMelting"), DISPLAY_MELTING, CARD_TYPE));
        result.add(new PanelSetting(StatCollector.translateToLocal("msg.nc.cbInfoPanelOutput"), DISPLAY_OUTPUT, CARD_TYPE));
        result.add(new PanelSetting(StatCollector.translateToLocal("msg.nc.cbInfoPanelTimeRemaining"), DISPLAY_TIME, CARD_TYPE));
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
