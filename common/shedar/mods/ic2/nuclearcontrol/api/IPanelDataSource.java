package shedar.mods.ic2.nuclearcontrol.api;

import java.util.List;
import java.util.UUID;

import net.minecraft.src.TileEntity;

public interface IPanelDataSource
{
    CardState update(TileEntity panel, ICardWrapper card, int maxRange);
    
    List<PanelString> getStringData(int displaySettings, ICardWrapper card, boolean showLabels);
    
    List<PanelSetting> getSettingsList();
    
    UUID getCardType();

}
