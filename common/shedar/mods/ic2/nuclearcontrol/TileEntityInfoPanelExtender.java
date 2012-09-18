package shedar.mods.ic2.nuclearcontrol;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Facing;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.ic2.api.INetworkDataProvider;
import net.minecraft.src.ic2.api.INetworkUpdateListener;
import net.minecraft.src.ic2.api.IWrenchable;
import net.minecraft.src.ic2.api.NetworkHelper;
import cpw.mods.fml.common.FMLCommonHandler;


public class TileEntityInfoPanelExtender extends TileEntity implements 
    INetworkDataProvider, INetworkUpdateListener, 
    IWrenchable, ITextureHelper, IScreenPart, IRotation
{

    protected boolean init;
  
    private Screen screen;
    private short prevFacing;
    public short facing;

    @Override
    public short getFacing()
    {
        return (short)Facing.faceToSide[facing];
    }
    
    @Override
    public void setFacing(short f)
    {
        setSide((short)Facing.faceToSide[f]);
    
    }

    private void setSide(short f)
    {
        facing = f;

        if (prevFacing != f)
        {
            NetworkHelper.updateTileEntityField(this, "facing");
        }

        prevFacing = f;
    }
     
    @Override
    public void onNetworkUpdate(String field)
    {
        if (field.equals("facing") && prevFacing != facing)
        {
            if(FMLCommonHandler.instance().getEffectiveSide().isClient())
            {
                IC2NuclearControl.instance.screenManager.unregisterScreenPart(this);
                IC2NuclearControl.instance.screenManager.registerInfoPanelExtender(this);
            }
            worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
            prevFacing = facing;
        }
    }
    
    public TileEntityInfoPanelExtender()
    {
        super();
        init = false;
        facing = 0;
        prevFacing = 0;
        screen = null;
    }
    
    @Override
    public List<String> getNetworkedFields()
    {
        List<String> list = new ArrayList<String>(1);
        list.add("facing");
        return list;
    }
    
    protected void initData()
    {
        if(worldObj.isRemote){
            NetworkHelper.requestInitialData(this);
        }
        if(FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            IC2NuclearControl.instance.screenManager.registerInfoPanelExtender(this);
        }
        init = true;
    }
    
    @Override
    public void updateEntity()
    {
        if (!init)
        {
            initData();
        }
        super.updateEntity();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        prevFacing = facing =  nbttagcompound.getShort("facing");
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        if(FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            IC2NuclearControl.instance.screenManager.unregisterScreenPart(this);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setShort("facing", facing);
    }


    @Override
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int face) {
        return !entityPlayer.isSneaking() && getFacing() != face;
    };

    @Override
    public float getWrenchDropRate()
    {
        return 1;
    }

    @Override
    public boolean wrenchCanRemove(EntityPlayer entityPlayer)
    {
        return !entityPlayer.isSneaking();
    }
    
    @Override
    public int modifyTextureIndex(int texture)
    {
        if(texture!=80)
            return texture;
        if(screen == null || screen.getCore() == null)
            return texture + 15;
        return screen.getCore().modifyTextureIndex(texture, xCoord, yCoord, zCoord);
    }

    @Override
    public void setScreen(Screen screen)
    {
        this.screen = screen;
    }

    @Override
    public Screen getScreen()
    {
        return screen;
    }

    @Override
    public void rotate()
    {
        if(screen!=null)
            screen.getCore().rotate();
    }

    @Override
    public int getRotation()
    {
        if(screen!=null)
            return screen.getCore().rotation;
        return 0;
    }

    @Override
    public void setRotation(int rotation)
    {
        if(screen!=null)
            screen.getCore().setRotation(rotation);
    }
}