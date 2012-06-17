package net.minecraft.src.nuclearcontrol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.src.Facing;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.mod_IC2NuclearControl;

public class ScreenManager
{
    
    private final Map<World,List<Screen>> screens; 
    private final Map<World,List<TileEntityInfoPanel>> unusedPanels;
    
    
    public ScreenManager()
    {
        screens = new HashMap<World, List<Screen>>();
        unusedPanels = new HashMap<World, List<TileEntityInfoPanel>>();
    }
    
    private boolean isValidExtender(World world, int x, int y, int z, int facing)
    {
        if(world.getBlockId(x, y, z) != mod_IC2NuclearControl.blockNuclearControlMain.blockID)
            return false;
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if(!(tileEntity instanceof TileEntityInfoPanelExtender))
            return false;
        if(((TileEntityInfoPanelExtender)tileEntity).facing != facing)
            return false;
        if(((IScreenPart)tileEntity).getScreen() != null)
            return false;
        return true;
    }
    
    private void updateScreenBound(Screen screen, int dx, int dy, int dz)
    {
        if(dx == 0 && dy == 0 && dz == 0)
            return;
        boolean isMin = dx + dy + dz < 0;
        int dir = isMin?1:-1;
        World world = screen.coreWorld;
        int steps = 0;
        while(steps<20)
        {
            int x, rx;
            int y, ry;
            int z, rz;
            if(isMin)
            {
                x = screen.minX + dx;
                y = screen.minY + dy;
                z = screen.minZ + dz;
            }
            else
            {
                x = screen.maxX + dx;
                y = screen.maxY + dy;
                z = screen.maxZ + dz;
            }
            rx = dx!=0?0:(screen.maxX-screen.minX);
            ry = dy!=0?0:(screen.maxY-screen.minY);
            rz = dz!=0?0:(screen.maxZ-screen.minZ);
            boolean allOk = true;
            for(int interX=0;interX<=rx && allOk;interX++)
            {
                for(int interY=0;interY<=ry && allOk;interY++)
                {
                    for(int interZ=0;interZ<=rz && allOk;interZ++)
                    {
                        allOk = screen.getCore()!=null && isValidExtender(world, x+dir*interX, y+dir*interY, z+dir*interZ, screen.getCore().facing);
                    }
                }
            }
            if(!allOk)
                break;
            if(isMin)
            {
                screen.minX+=dx;
                screen.minY+=dy;
                screen.minZ+=dz;
            }
            else
            {
                screen.maxX+=dx;
                screen.maxY+=dy;
                screen.maxZ+=dz;
            }
            steps++;
        }
    }
    
    private Screen tryBuildFromPanel(TileEntityInfoPanel panel)
    {
        System.out.println("BuildFromPanel panel:"+panel.xCoord+","+panel.yCoord+","+panel.zCoord);
        Screen screen = new Screen();
        screen.maxX = screen.minX = panel.xCoord; 
        screen.maxY = screen.minY = panel.yCoord; 
        screen.maxZ = screen.minZ = panel.zCoord;
        screen.setCore(panel);
        int dx = Facing.offsetsXForSide[panel.facing]!=0?0:-1;
        int dy = Facing.offsetsYForSide[panel.facing]!=0?0:-1;
        int dz = Facing.offsetsZForSide[panel.facing]!=0?0:-1;
        updateScreenBound(screen, dx, 0, 0);
        updateScreenBound(screen, -dx, 0, 0);
        updateScreenBound(screen, 0, dy, 0);
        updateScreenBound(screen, 0, -dy, 0);
        updateScreenBound(screen, 0, 0, dz);
        updateScreenBound(screen, 0, 0, -dz);
        if(screen.minX == screen.maxX && screen.minY == screen.maxY && screen.minZ == screen.maxZ)
            return null;
        screen.init();
        System.out.println("BuildingDone:"+screen.minX+","+screen.minY+","+screen.minZ+":"+screen.maxX+","+screen.maxY+","+screen.maxZ);
        return screen;
    }
    
    private void destroyScreen(Screen screen)
    {
        System.out.println("Destroy screen:"+screen.minX+","+screen.minY+","+screen.minZ+":"+screen.maxX+","+screen.maxY+","+screen.maxZ);
        screens.get(screen.coreWorld).remove(screen);
        screen.destroy();
    }
    
    public void unregisterScreenPart(TileEntity part)
    {
        System.out.println("UNRegister:"+part.xCoord+","+part.yCoord+","+part.zCoord+":"+(part instanceof TileEntityInfoPanel));

        if(!screens.containsKey(part.worldObj))
            return;
        if(!unusedPanels.containsKey(part.worldObj))
            return;
        if(!(part instanceof IScreenPart))
            return;
        IScreenPart screenPart = (IScreenPart)part;
        Screen screen = screenPart.getScreen(); 
        if(screen==null)
        {
            if(part instanceof TileEntityInfoPanel &&
               unusedPanels.get(part.worldObj).contains(part))
                unusedPanels.get(part.worldObj).remove(part);
            return;
        }
        TileEntityInfoPanel core = screen.getCore();
        destroyScreen(screen);
        boolean isCoreDestroyed = part instanceof TileEntityInfoPanel;
        if(!isCoreDestroyed && core != null)
        {
            Screen newScreen = tryBuildFromPanel(core);
            if(newScreen == null)
                unusedPanels.get(core.worldObj).add(core);
            else
                screens.get(core.worldObj).add(newScreen);
        }
        
    }
    
    public void registerInfoPanel(TileEntityInfoPanel panel)
    {
        System.out.println("Register panel:"+panel.xCoord+","+panel.yCoord+","+panel.zCoord);

        if(!screens.containsKey(panel.worldObj))
            screens.put(panel.worldObj, new ArrayList<Screen>());
        if(!unusedPanels.containsKey(panel.worldObj))
            unusedPanels.put(panel.worldObj, new ArrayList<TileEntityInfoPanel>());

        System.out.println("screens"+screens.get(panel.worldObj).size()+"-"+unusedPanels.get(panel.worldObj).size());
        for (Screen screen : screens.get(panel.worldObj))
        {
            System.out.println(""+screen.minX+","+screen.minY+","+screen.minZ+":"+screen.maxX+","+screen.maxY+","+screen.maxZ);
        }
        
        for (Screen screen : screens.get(panel.worldObj))
        {
            if(screen.isBlockPartOf(panel))
            {
                //occurs on chunk unloading/loading
                destroyScreen(screen);
                break;
            }
        }        
        Screen screen = tryBuildFromPanel(panel);
        if(screen!=null)
            screens.get(panel.worldObj).add(screen);
        else
            unusedPanels.get(panel.worldObj).add(panel);
    }
    
    public void registerInfoPanelExtender(TileEntityInfoPanelExtender extender)
    {
        System.out.println("Register extender:"+extender.xCoord+","+extender.yCoord+","+extender.zCoord);

        if(!screens.containsKey(extender.worldObj))
            screens.put(extender.worldObj, new ArrayList<Screen>());
        if(!unusedPanels.containsKey(extender.worldObj))
            unusedPanels.put(extender.worldObj, new ArrayList<TileEntityInfoPanel>());
        
        List<TileEntityInfoPanel> rebuildPanels = new ArrayList<TileEntityInfoPanel>();
        List<Screen> screensToDestroy = new ArrayList<Screen>();

        System.out.println("screens"+screens.get(extender.worldObj).size()+"-"+unusedPanels.get(extender.worldObj).size());
        for (Screen screen : screens.get(extender.worldObj))
        {
            System.out.println(""+screen.minX+","+screen.minY+","+screen.minZ+":"+screen.maxX+","+screen.maxY+","+screen.maxZ);
        }
        
        for (Screen screen : screens.get(extender.worldObj))
        {
            if(screen.isBlockNearby(extender) && screen.getCore()!=null && extender.facing == screen.getCore().facing)
            {
                rebuildPanels.add(screen.getCore());
                screensToDestroy.add(screen);
            }
            else if(screen.isBlockPartOf(extender))
            {
                // block is already part of the screen
                // shouldn't be registered twice
                return;
            }
        }
        for(Screen screen : screensToDestroy)
        {
            destroyScreen(screen);
        }
        for (TileEntityInfoPanel panel : unusedPanels.get(extender.worldObj))
        {
            if(((panel.xCoord == extender.xCoord && panel.yCoord  == extender.yCoord && (panel.zCoord == extender.zCoord+1 || panel.zCoord == extender.zCoord-1)) ||
                (panel.xCoord == extender.xCoord && (panel.yCoord  == extender.yCoord+1 || panel.yCoord  == extender.yCoord-1) && panel.zCoord == extender.zCoord) ||
                ((panel.xCoord == extender.xCoord+1 || panel.xCoord == extender.xCoord-1) && panel.yCoord  == extender.yCoord && panel.zCoord == extender.zCoord)) &&
                extender.facing == panel.facing
                )
            {
                rebuildPanels.add(panel);
            }
        }
        for (TileEntityInfoPanel panel : rebuildPanels)
        {
            Screen screen = tryBuildFromPanel(panel);
            if(screen!=null)
            {
                screens.get(extender.worldObj).add(screen);
                if(unusedPanels.get(extender.worldObj).contains(panel))
                    unusedPanels.get(extender.worldObj).remove(panel);
            }
            else
            {
                if(!unusedPanels.get(extender.worldObj).contains(panel))
                    unusedPanels.get(extender.worldObj).add(panel);
            }
        }
    }

}
