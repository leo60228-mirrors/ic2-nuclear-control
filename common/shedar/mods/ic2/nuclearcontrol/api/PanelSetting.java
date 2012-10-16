package shedar.mods.ic2.nuclearcontrol.api;

import java.util.UUID;

public class PanelSetting
{
    public String title;
    public int displayBit;
    public UUID cardType;
    
    public PanelSetting(String title, int displayBit, UUID cardType)
    {
        this.title = title;
        this.displayBit = displayBit;
        this.cardType = cardType;
    }
}
