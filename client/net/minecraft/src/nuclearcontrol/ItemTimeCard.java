package net.minecraft.src.nuclearcontrol;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
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
    public List<PanelString> getStringData(int displaySettings, ItemStack itemStack)
    {
        List<PanelString> result = new ArrayList<PanelString>(1);
        PanelString item = new PanelString();
        result.add(item);
        int time = (int)((ModLoader.getMinecraftInstance().theWorld.getWorldTime() - 18000) % 24000);
        int hours = time / 1000;
        int minutes = (time % 1000)*6/100;
        item.textLeft = String.format("%02d:%02d", hours, minutes);
        return result;
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
