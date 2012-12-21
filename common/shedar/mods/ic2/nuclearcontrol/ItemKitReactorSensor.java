package shedar.mods.ic2.nuclearcontrol;

import ic2.api.IReactor;
import ic2.api.IReactorChamber;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class ItemKitReactorSensor extends ItemSensorKitBase
{

    public ItemKitReactorSensor(int i, int iconIndex)
    {
        super(i);
        this.iconIndex = iconIndex;
    }

    @Override
    protected ChunkCoordinates getTargetCoordinates(World world, int x, int y, int z, ItemStack stack)
    {
        IReactor reactor = NuclearHelper.getReactorAt(world, x, y, z);
        if (reactor == null)
        {
            IReactorChamber chamber = NuclearHelper.getReactorChamberAt(world, x, y, z);
            if(chamber!=null)
            {
                reactor = chamber.getReactor();
            }
        }
        if(reactor!=null)
            return reactor.getPosition();
        return null;
    }

    @Override
    protected ItemStack getItemStackByDamage(int damage)
    {
        return new ItemStack(IC2NuclearControl.instance.itemSensorLocationCard, 1, 0);
    }

}
