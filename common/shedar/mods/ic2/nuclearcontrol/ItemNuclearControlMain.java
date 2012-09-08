package shedar.mods.ic2.nuclearcontrol;

import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

public class ItemNuclearControlMain extends ItemBlock
{
    public ItemNuclearControlMain(int blockId)
    {
        super(blockId);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    /**
     * Returns the metadata of the block which this Item (ItemBlock) can place
     */
    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public String getItemNameIS(ItemStack item)
    {
        switch(item.getItemDamage())
        {
            case BlockNuclearControlMain.DAMAGE_THERMAL_MONITOR:
                return "tile.blockThermalMonitor";
            case BlockNuclearControlMain.DAMAGE_INDUSTRIAL_ALARM:
                return "tile.blockIndustrialAlarm";
            case BlockNuclearControlMain.DAMAGE_HOWLER_ALARM:
                return "tile.blockHowlerAlarm";
            case BlockNuclearControlMain.DAMAGE_REMOTE_THERMO:
                return "tile.blockRemoteThermo";
            case BlockNuclearControlMain.DAMAGE_INFO_PANEL:
                return "tile.blockInfoPanel";
            case BlockNuclearControlMain.DAMAGE_INFO_PANEL_EXTENDER:
                return "tile.blockInfoPanelExtender";
            case BlockNuclearControlMain.DAMAGE_ENERGY_COUNTER:
                return "tile.blockEnergyCounter";
        
        }
        return "";
    }

}
