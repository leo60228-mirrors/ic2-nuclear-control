package shedar.mods.ic2.nuclearcontrol;

import java.lang.reflect.Method;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;

public class LiquidStorageHelper {

    private static ILiquidTank getRailcraftIronTank(TileEntity entity)
    {
        try
        {
            Method method = entity.getClass().getMethod("getTank");
            if(method.getReturnType().equals(ILiquidTank.class))
            {
                return (ILiquidTank)method.invoke(entity);
            }
            return null;
        } 
        catch (Exception e)
        {
            return null;
        }
    }
	
    public static ILiquidTank getStorageAt(World world, int x, int y, int z) 
    {
        if(world == null)
            return null;
        TileEntity entity = world.getBlockTileEntity(x, y, z);
        if (entity!=null && entity instanceof ITankContainer)
        {
            ILiquidTank[] tanks = ((ITankContainer)entity).getTanks(ForgeDirection.UP);
            if(tanks == null || tanks.length == 0)
                return null;
            return tanks[0];
        }
        return getRailcraftIronTank(entity);
    }    
    
}
