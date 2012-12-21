package shedar.mods.ic2.nuclearcontrol;

import ic2.api.IEnergyStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class EnergyStorageHelper {

	
    public static IEnergyStorage getStorageAt(World world, int x, int y, int z) 
    {
        if(world == null)
            return null;
        TileEntity entity = world.getBlockTileEntity(x, y, z);
        if (entity!=null && entity instanceof IEnergyStorage)
        {
            return (IEnergyStorage)entity;
        }
        return null;
    }    
    
}
