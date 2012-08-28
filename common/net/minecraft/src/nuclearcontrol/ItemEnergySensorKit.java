package net.minecraft.src.nuclearcontrol;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.World;
import net.minecraft.src.mod_IC2NuclearControl;
import net.minecraft.src.ic2.api.IEnergyStorage;

public class ItemEnergySensorKit extends ItemSensorKitBase
{

    public ItemEnergySensorKit(int i, int iconIndex)
    {
        super(i, iconIndex, mod_IC2NuclearControl.itemEnergySensorLocationCard);
    }

    @Override
    protected ChunkCoordinates getTargetCoordinates(World world, int x, int y, int z)
    {
        IEnergyStorage storage = EnergyStorageHelper.getStorageAt(world, x, y, z);
        if (storage != null)
        {
            ChunkCoordinates coordinates = new ChunkCoordinates();
            coordinates.posX = x;
            coordinates.posY = y;
            coordinates.posZ = z;
            return coordinates;
        }
        return null;
    }

}
