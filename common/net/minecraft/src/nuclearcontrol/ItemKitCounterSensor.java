package net.minecraft.src.nuclearcontrol;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.mod_IC2NuclearControl;

public class ItemKitCounterSensor extends ItemSensorKitBase
{

    public ItemKitCounterSensor(int i, int iconIndex)
    {
        super(i, iconIndex, mod_IC2NuclearControl.itemCounterSensorLocationCard);
    }

    @Override
    protected ChunkCoordinates getTargetCoordinates(World world, int x, int y, int z)
    {
        TileEntity entity = world.getBlockTileEntity(x, y, z);
        if (entity != null && entity instanceof TileEntityEnergyCounter)
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
