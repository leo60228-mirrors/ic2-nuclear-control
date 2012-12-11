package shedar.mods.ic2.nuclearcontrol;

import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;

public class LiquidStorageHelper {

	
    public static ILiquidTank getStorageAt(World world, int x, int y, int z) 
    {
        if(world == null)
            return null;
        TileEntity entity = world.getBlockTileEntity(x, y, z);
        if (entity!=null && entity instanceof ITankContainer)
        {
            ILiquidTank[] tanks = ((ITankContainer)entity).getTanks(ForgeDirection.UP);
            if(tanks == null || tanks.length == 0)
                return null;
            return tanks[0];
        }
        return null;
    }    
    
}
