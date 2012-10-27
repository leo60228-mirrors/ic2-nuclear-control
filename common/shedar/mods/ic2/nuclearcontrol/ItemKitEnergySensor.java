package shedar.mods.ic2.nuclearcontrol;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.World;
import ic2.api.IEnergyStorage;

public class ItemKitEnergySensor extends ItemSensorKitBase
{

    public ItemKitEnergySensor(int i, int iconIndex)
    {
        super(i, iconIndex, IC2NuclearControl.instance.itemEnergySensorLocationCard);
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
