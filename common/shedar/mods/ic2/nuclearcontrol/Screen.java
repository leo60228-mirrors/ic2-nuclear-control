package shedar.mods.ic2.nuclearcontrol;

import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class Screen
{
    public int minX;
    public int minY;
    public int minZ;
    public int maxX;
    public int maxY;
    public int maxZ;
    private int coreX;
    private int coreY;
    private int coreZ;
    public World coreWorld;
    private boolean powered = false;

    public TileEntityInfoPanel getCore()
    {
        TileEntity tileEntity = coreWorld.getBlockTileEntity(coreX, coreY, coreZ);
        if(tileEntity == null || !(tileEntity instanceof TileEntityInfoPanel))
            return null;
        return (TileEntityInfoPanel)tileEntity;
    }
    
    public void setCore(TileEntityInfoPanel core)
    {
        coreWorld = core.worldObj;
        coreX = core.xCoord;
        coreY = core.yCoord;
        coreZ = core.zCoord;
        powered = core.powered;
    }
    
    public boolean isBlockNearby(TileEntity tileEntity)
    {
        int x = tileEntity.xCoord;
        int y = tileEntity.yCoord;
        int z = tileEntity.zCoord;
        return  (x == minX-1 && y>=minY && y<=maxY && z>=minZ && z<=maxZ) ||
                (x == maxX+1 && y>=minY && y<=maxY && z>=minZ && z<=maxZ) ||
                (x >= minX && x<=maxX && y==minY-1 && z>=minZ && z<=maxZ) ||
                (x >= minX && x<=maxX && y==maxY+1 && z>=minZ && z<=maxZ) ||
                (x >= minX && x<=maxX && y>=minY && y<=maxY && z==minZ-1) ||
                (x >= minX && x<=maxX && y>=minY && y<=maxY && z==maxZ+1);
    }
    
    public boolean isBlockPartOf(TileEntity tileEntity)
    {
        int x = tileEntity.xCoord;
        int y = tileEntity.yCoord;
        int z = tileEntity.zCoord;
        return  x >= minX && x <= maxX &&
                y >= minY && y <= maxY &&
                z >= minZ && z <= maxZ;
    }
    
    public void init()
    {
        for(int x = minX; x<=maxX; x++)
        {
            for(int y = minY; y<=maxY; y++)
            {
                for(int z = minZ; z<=maxZ; z++)
                {
                    TileEntity tileEntity = coreWorld.getBlockTileEntity(x, y, z);
                    if(tileEntity == null || !(tileEntity instanceof IScreenPart))
                        continue;
                    ((IScreenPart)tileEntity).setScreen(this); 
                    if(powered)
                    {
                        coreWorld.markBlockNeedsUpdate(x, y, z);
                        coreWorld.updateAllLightTypes(x, y, z);
                    }
                }
            }
        }
    }
    
    
    public void destroy()
    {
        for(int x = minX; x<=maxX; x++)
        {
            for(int y = minY; y<=maxY; y++)
            {
                for(int z = minZ; z<=maxZ; z++)
                {
                    TileEntity tileEntity = coreWorld.getBlockTileEntity(x, y, z);
                    if(tileEntity == null || !(tileEntity instanceof IScreenPart))
                        continue;
                    ((IScreenPart)tileEntity).setScreen(null); 
                    if(powered)
                    {
                        coreWorld.markBlockNeedsUpdate(x, y, z);
                        coreWorld.updateAllLightTypes(x, y, z);
                    }
                }
            }
        }
    }
    
    public void turnPower(boolean on)
    {
        if(powered!=on)
        {
            powered = on;
            for(int x = minX; x<=maxX; x++)
            {
                for(int y = minY; y<=maxY; y++)
                {
                    for(int z = minZ; z<=maxZ; z++)
                    {
                        coreWorld.markBlockNeedsUpdate(x, y, z);
                        coreWorld.updateAllLightTypes(x, y, z);
                    }
                }
            }
        }
    }
    
}
