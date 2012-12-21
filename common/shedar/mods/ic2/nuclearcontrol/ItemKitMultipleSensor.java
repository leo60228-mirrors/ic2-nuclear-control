package shedar.mods.ic2.nuclearcontrol;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.liquids.ILiquidTank;

public class ItemKitMultipleSensor extends ItemSensorKitBase
{
    public static final int TYPE_COUNTER = 0;
    public static final int TYPE_LIQUID = 1;

    public ItemKitMultipleSensor(int i)
    {
        super(i);
    }
    
    @Override
    public String getItemNameIS(ItemStack stack) 
    {
        int damage = stack.getItemDamage();
        switch (damage)
        {
        case TYPE_COUNTER:
            return "item.ItemCounterSensorKit";
        case TYPE_LIQUID:
            return "item.ItemLiquidSensorKit";
        }
        return "";
    }

    @Override
    public int getIconFromDamage(int damage)
    {
        switch (damage)
        {
        case TYPE_COUNTER:
            return 68;
        case TYPE_LIQUID:
            return 70;
        }
        return 0;
    }
    
    @Override
    protected ChunkCoordinates getTargetCoordinates(World world, int x, int y, int z, ItemStack stack)
    {
        int damage = stack.getItemDamage();
        
        switch (damage)
        {
        case TYPE_COUNTER:
            TileEntity entity = world.getBlockTileEntity(x, y, z);
            if (entity != null && 
                (entity instanceof TileEntityEnergyCounter ||
                 entity instanceof TileEntityAverageCounter))
            {
                return new ChunkCoordinates(x, y, z);
            }
            break;
        case TYPE_LIQUID:
            ILiquidTank tank = LiquidStorageHelper.getStorageAt(world, x, y, z);
            if(tank!=null)
            {
                return new ChunkCoordinates(x, y, z);
            }
            break;
        default:
            break;
        }
        return null;
    }

    @Override
    protected ItemStack getItemStackByDamage(int damage)
    {
        switch (damage)
        {
        case TYPE_COUNTER:
            return new ItemStack(IC2NuclearControl.instance.itemMultipleSensorLocationCard, 1, TYPE_COUNTER);
        case TYPE_LIQUID:
            return new ItemStack(IC2NuclearControl.instance.itemMultipleSensorLocationCard, 1, TYPE_LIQUID);
        }
        return null;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(par1, 1, TYPE_COUNTER));
        par3List.add(new ItemStack(par1, 1, TYPE_LIQUID));
    }

}
