package net.minecraft.src.nuclearcontrol;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.World;
import net.minecraft.src.mod_IC2NuclearControl;
import net.minecraft.src.ic2.api.IReactor;
import net.minecraft.src.ic2.api.IReactorChamber;

public class ItemRemoteSensorKit extends ItemSensorKitBase
{

    public ItemRemoteSensorKit(int i, int iconIndex)
    {
        super(i, iconIndex, mod_IC2NuclearControl.itemSensorLocationCard);
    }

    @Override
    protected ChunkCoordinates getTargetCoordinates(World world, int x, int y, int z)
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

}
