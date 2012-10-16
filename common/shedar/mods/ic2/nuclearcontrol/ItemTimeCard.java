package shedar.mods.ic2.nuclearcontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.src.TileEntity;
import shedar.mods.ic2.nuclearcontrol.api.CardState;
import shedar.mods.ic2.nuclearcontrol.api.ICardWrapper;
import shedar.mods.ic2.nuclearcontrol.api.PanelSetting;
import shedar.mods.ic2.nuclearcontrol.api.PanelString;
import shedar.mods.ic2.nuclearcontrol.utils.StringUtils;
import cpw.mods.fml.client.FMLClientHandler;

public class ItemTimeCard extends ItemCardBase
{

    public static final UUID CARD_TYPE =  new UUID(0, 1);
    
    public ItemTimeCard(int i, int iconIndex)
    {
        super(i, iconIndex);
    }

    @Override
    public CardState update(TileEntity panel, ICardWrapper card, int range)
    {
        return CardState.OK;
    }

    @Override
    public boolean isDamageable()
    {
        return false;
    }
    
    @Override
    public List<PanelString> getStringData(int displaySettings, ICardWrapper card, boolean showLabels)
    {
        List<PanelString> result = new ArrayList<PanelString>(1);
        PanelString item = new PanelString();
        result.add(item);
        int time = (int)((FMLClientHandler.instance().getClient().theWorld.getWorldTime() - 18000) % 24000);
        int hours = time / 1000;
        int minutes = (time % 1000)*6/100;
        item.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelTime", String.format("%02d:%02d", hours, minutes), showLabels);  
        return result;
    }

    @Override
    public List<PanelSetting> getSettingsList()
    {
        return null;
    }

    @Override
    public UUID getCardType()
    {
        return CARD_TYPE;
    }

}
