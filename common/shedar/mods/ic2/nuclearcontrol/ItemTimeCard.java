package shedar.mods.ic2.nuclearcontrol;

import java.util.ArrayList;
import java.util.List;

import shedar.mods.ic2.nuclearcontrol.panel.PanelSetting;
import shedar.mods.ic2.nuclearcontrol.panel.PanelString;

import net.minecraft.src.ItemStack;
import cpw.mods.fml.client.FMLClientHandler;

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
        List<PanelString> result = new ArrayList<PanelString>(1);
        PanelString item = new PanelString();
        result.add(item);
        int time = (int)((FMLClientHandler.instance().getClient().theWorld.getWorldTime() - 18000) % 24000);
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
