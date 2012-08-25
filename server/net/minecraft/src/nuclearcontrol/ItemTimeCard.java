package net.minecraft.src.nuclearcontrol;

import java.util.List;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.forge.ITextureProvider;
import net.minecraft.src.nuclearcontrol.panel.IPanelDataSource;
import net.minecraft.src.nuclearcontrol.panel.PanelSetting;
import net.minecraft.src.nuclearcontrol.panel.PanelString;

public class ItemTimeCard extends Item implements ITextureProvider, IPanelDataSource
{

    public static final int CARD_TYPE = 1;
    
    public ItemTimeCard(int i, int iconIndex)
    {
        super(i);
        setIconIndex(iconIndex);
    }

    public String getTextureFile()
    {
        return "/img/texture_thermo.png";
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
