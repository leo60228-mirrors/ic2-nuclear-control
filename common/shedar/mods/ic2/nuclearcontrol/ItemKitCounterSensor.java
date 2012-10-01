package shedar.mods.ic2.nuclearcontrol;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class ItemKitCounterSensor extends ItemSensorKitBase
{

    public ItemKitCounterSensor(int i, int iconIndex)
    {
        super(i, iconIndex, IC2NuclearControl.instance.itemCounterSensorLocationCard);
    }

    @Override
    protected ChunkCoordinates getTargetCoordinates(World world, int x, int y, int z)
    {
        TileEntity entity = world.getBlockTileEntity(x, y, z);
        if (entity != null && 
            (entity instanceof TileEntityEnergyCounter ||
             entity instanceof TileEntityAverageCounter))
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
