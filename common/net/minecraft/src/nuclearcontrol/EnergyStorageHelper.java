package net.minecraft.src.nuclearcontrol;

import java.lang.reflect.Field;

import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class EnergyStorageHelper {
	
    private static String storagePackage = null;
    private static Class storageTileEntityClass = null;
    
    private static Field energyField = null;
    private static Field outputField = null;
    private static Field maxStorageField = null;

    private static String getStoragePackage()
    {
        if (storagePackage != null)
        {
            return storagePackage;
        }

        Package rPackage = (net.minecraft.src.ic2.api.Ic2Recipes.class).getPackage();
        String result;

        if (rPackage != null)
        {
            result = rPackage.getName().substring(0, rPackage.getName().lastIndexOf('.'));
        } 
        else
        {
            result = "net.minecraft.src.ic2";
        }
        result += ".common";
        storagePackage = result;
        return result;
    }

	private static Class getStorageTileEntityClass()
	{
        try
        {
			if(storageTileEntityClass==null)
			    storageTileEntityClass = Class.forName(getStoragePackage() +".TileEntityElectricBlock");
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }
		return storageTileEntityClass;
	}
	
    private static Field getEnergyField() throws NoSuchFieldException
    {
        if(energyField == null)
            energyField = getStorageTileEntityClass().getField("energy");
        return energyField;
    }
    
    private static Field getOutputField() throws NoSuchFieldException
    {
        if(outputField == null)
            outputField = getStorageTileEntityClass().getField("output");
        return outputField;
    }
    
    private static Field getMaxStorageField() throws NoSuchFieldException
    {
        if(maxStorageField == null)
            maxStorageField = getStorageTileEntityClass().getField("maxStorage");
        return maxStorageField;
    }
	
    public static TileEntity getStorageAt(World world, int x, int y, int z) 
    {
        if(world == null)
            return null;
        TileEntity entity = world.getBlockTileEntity(x, y, z);
        if (entity!=null && getStorageTileEntityClass().isInstance(entity))
        {
            return entity;
        }
        return null;
    }    
    
    public static int getStorageEnergy(TileEntity storage)
    {
        try
        {
            return getEnergyField().getInt(storage);
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }
    
    public static int getStorageOutput(TileEntity storage)
    {
        try
        {
            return getOutputField().getInt(storage);
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }

    public static int getStorageMaxStorage(TileEntity storage)
    {
        try
        {
            return getMaxStorageField().getInt(storage);
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }

}
