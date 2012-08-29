package net.minecraft.src.nuclearcontrol;

import java.util.List;

import net.minecraft.src.ItemStack;
import net.minecraft.src.nuclearcontrol.panel.PanelSetting;
import net.minecraft.src.nuclearcontrol.panel.PanelString;

public class ItemTimeCard extends ItemCardBase
{

    public static final int CARD_TYPE = 1;
    
    public ItemTimeCard(int i, int iconIndex)
    {
        super(i, iconIndex);
    }

    @Override
    public void update(TileEntityInfoPanel panel, ItemStack stack, int range)
    {
    }

    @Override
    public void networkUpdate(String fieldName, int value, ItemStack stack)
    {
    }

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

    @Override
    public int getCardType()
    {
        return CARD_TYPE;
    }

}
