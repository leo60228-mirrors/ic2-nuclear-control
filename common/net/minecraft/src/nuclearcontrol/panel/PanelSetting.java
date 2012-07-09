package net.minecraft.src.nuclearcontrol.panel;

public class PanelSetting
{
    public String title;
    public int displayBit;
    public int cardType;
    
    public PanelSetting(String title, int displayBit, int cardType)
    {
        this.title = title;
        this.displayBit = displayBit;
        this.cardType = cardType;
    }
}
