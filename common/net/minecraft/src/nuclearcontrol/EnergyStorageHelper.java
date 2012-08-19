package net.minecraft.src.nuclearcontrol;

import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.ic2.api.IEnergyStorage;

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
