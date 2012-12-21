package shedar.mods.ic2.nuclearcontrol;

import ic2.api.IEnergyStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class ItemKitEnergySensor extends ItemSensorKitBase
{

    public ItemKitEnergySensor(int i, int iconIndex)
    {
        super(i);
        this.iconIndex = iconIndex;
    }

    @Override
    protected ChunkCoordinates getTargetCoordinates(World world, int x, int y, int z, ItemStack stack)
    {
        IEnergyStorage storage = EnergyStorageHelper.getStorageAt(world, x, y, z);
        if (storage != null)
        {
            return new ChunkCoordinates(x, y, z);
        }
        return null;
    }

    @Override
    protected ItemStack getItemStackByDamage(int damage)
    {
        return new ItemStack(IC2NuclearControl.instance.itemEnergySensorLocationCard, 1, 0);
    }

}
