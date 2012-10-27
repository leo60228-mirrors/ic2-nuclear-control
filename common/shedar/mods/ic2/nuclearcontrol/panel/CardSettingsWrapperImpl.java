package shedar.mods.ic2.nuclearcontrol.panel;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import shedar.mods.ic2.nuclearcontrol.GuiInfoPanel;
import shedar.mods.ic2.nuclearcontrol.NuclearNetworkHelper;
import shedar.mods.ic2.nuclearcontrol.api.ICardSettingsWrapper;
import shedar.mods.ic2.nuclearcontrol.api.IPanelDataSource;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;

public class CardSettingsWrapperImpl implements ICardSettingsWrapper
{
    private ItemStack card;
    private TileEntity panel;
    private Map<String, Object> updateSet;
    private GuiInfoPanel gui;
    
    public CardSettingsWrapperImpl(ItemStack card, TileEntity panel, GuiInfoPanel gui)
    {
        if(!(card.getItem() instanceof IPanelDataSource))
        {
            FMLLog.severe("CardHelper sould be used for IPanelDataSource items.");
        }
        this.card = card;
        this.panel = panel;
        updateSet = new HashMap<String, Object>();
        this.gui = gui;
    }
    
    @Override
    public void setInt(String name, Integer value)
    {
        updateSet.put(name, value);
    }
    
    @Override
    public void setLong(String name, Long value)
    {
        updateSet.put(name, value);
    }
    
    @Override
    public void setString(String name, String value)
    {
        updateSet.put(name, value);
    }
    
    @Override
    public void setBoolean(String name, Boolean value)
    {
        updateSet.put(name, value);
    }
    
    @Override
    public void commit()
    {
        if(!updateSet.isEmpty())
            NuclearNetworkHelper.setCardSettings(card, panel, updateSet);
        gui.prevCard = null;
        FMLClientHandler.instance().getClient().displayGuiScreen(gui);
    }

}
