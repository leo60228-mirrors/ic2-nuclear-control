package shedar.mods.ic2.nuclearcontrol.items;

import shedar.mods.ic2.nuclearcontrol.BlockNuclearControlMain;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
            case BlockNuclearControlMain.DAMAGE_ADVANCED_PANEL:
                return "tile.blockAdvancedInfoPanel";
            case BlockNuclearControlMain.DAMAGE_ADVANCED_EXTENDER:
                return "tile.blockAdvancedInfoPanelExtender";
            case BlockNuclearControlMain.DAMAGE_ENERGY_COUNTER:
                return "tile.blockEnergyCounter";
            case BlockNuclearControlMain.DAMAGE_AVERAGE_COUNTER:
                return "tile.blockAverageCounter";
            case BlockNuclearControlMain.DAMAGE_RANGE_TRIGGER:
                return "tile.blockRangeTrigger";
        
        }
        return "";
    }
    
    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
       if (!world.setBlockAndMetadataWithNotify(x, y, z, getBlockID(), metadata & 0xff))
       {
               return false;
       }

       if (world.getBlockId(x, y, z) == getBlockID())
       {
           if(Block.blocksList[getBlockID()] instanceof BlockNuclearControlMain)
               ((BlockNuclearControlMain)Block.blocksList[getBlockID()]).onBlockPlacedBy(world, x, y, z, player, metadata);
           else
               Block.blocksList[getBlockID()].onBlockPlacedBy(world, x, y, z, player);
           Block.blocksList[getBlockID()].onPostBlockPlaced(world, x, y, z, metadata);
       }

       return true;
    }

    

    @SideOnly(Side.CLIENT)

    /**
     * Returns true if the given ItemBlock can be placed on the given side of the given block position.
     */
    @Override
    public boolean canPlaceItemBlockOnSide(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack item)
    {
        int var8 = world.getBlockId(x, y, z);

        if (var8 == Block.snow.blockID)
        {
            side = 1;
        }
        else if (var8 != Block.vine.blockID && var8 != Block.tallGrass.blockID && var8 != Block.deadBush.blockID
                && (Block.blocksList[var8] == null || !Block.blocksList[var8].isBlockReplaceable(world, x, y, z)))
        {
            if (side == 0)
            {
                --y;
            }

            if (side == 1)
            {
                ++y;
            }

            if (side == 2)
            {
                --z;
            }

            if (side == 3)
            {
                ++z;
            }

            if (side == 4)
            {
                --x;
            }

            if (side == 5)
            {
                ++x;
            }
        }

        return BlockNuclearControlMain.canPlaceBlockOnSide(world, x, y, z, side, item.getItemDamage());
    }
    
}
