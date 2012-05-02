package net.minecraft.src.nuclearcontrol;

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
            case 0:
                return "tile.blockThermalMonitor";
            case 1:
                return "tile.blockIndustrialAlarm";
            case 2:
                return "tile.blockHowlerAlarm";
        
        }
        return "";
    }

}
