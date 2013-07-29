package shedar.mods.ic2.nuclearcontrol.utils;

import java.lang.reflect.Method;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class LiquidStorageHelper {

    private static FluidTankInfo getRailcraftIronTank(TileEntity entity)
    {
        try
        {
            Method method = entity.getClass().getMethod("getTank");
            if(FluidTankInfo.class.isAssignableFrom(method.getReturnType()))
            {
                return (FluidTankInfo)method.invoke(entity);
            }
            return null;
        } 
        catch (Exception e)
        {
            return null;
        }
    }
	
    public static FluidTankInfo getStorageAt(World world, int x, int y, int z) 
    {
        if(world == null)
            return null;
        TileEntity entity = world.getBlockTileEntity(x, y, z);
        if (entity!=null && entity instanceof IFluidHandler)
        {
            FluidTankInfo[] tanks = ((IFluidHandler)entity).getTankInfo(ForgeDirection.UNKNOWN);
            if(tanks == null || tanks.length == 0)
                return null;
            return tanks[0];
        }
        return getRailcraftIronTank(entity);
    }    
    
}
