package net.minecraft.src.nuclearcontrol;

import java.util.List;

import net.minecraft.src.ItemStack;
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
    {}

    @Override
    public List<PanelString> getStringData(int displaySettings, ItemStack itemStack, boolean showLabels)
    {
        return null;
    }
    
    @Override
    public List<PanelSetting> getSettingsList()
    {
        return null;
    }
}
