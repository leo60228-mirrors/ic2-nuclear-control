package shedar.mods.ic2.nuclearcontrol.panel;

import java.util.List;

import shedar.mods.ic2.nuclearcontrol.TileEntityInfoPanel;

import net.minecraft.src.ItemStack;

public interface IPanelDataSource
{
    void update(TileEntityInfoPanel panel, ItemStack stack, int range);
    
    void networkUpdate(String fieldName, int value, ItemStack stack);
    
    List<PanelString> getStringData(int displaySettings, ItemStack itemStack, boolean showLabels);
    
    List<PanelSetting> getSettingsList();
    
    String getTitle(ItemStack stack);

    void setTitle(ItemStack stack, String title);

    int getCardType();

}
