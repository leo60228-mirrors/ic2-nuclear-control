package shedar.mods.ic2.nuclearcontrol;

import ic2.api.IWrenchable;
import ic2.api.network.INetworkDataProvider;
import ic2.api.network.INetworkUpdateListener;
import ic2.api.network.NetworkHelper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Facing;
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
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
        if(texture!=47)
            return texture;
        if(screen!=null)
        {
            TileEntityInfoPanel core = screen.getCore();
            if(core!=null)
            {
                return core.modifyTextureIndex(texture, xCoord, yCoord, zCoord);
            }
        }
        return texture;
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
        {
            TileEntityInfoPanel core = screen.getCore(); 
            if(core != null)
                core.rotate();
        }
    }

    @Override
    public int getRotation()
    {
        if(screen!=null)
        {
            TileEntityInfoPanel core = screen.getCore(); 
            if(core != null)
                return core.rotation;
        }
        return 0;
    }

    @Override
    public void setRotation(int rotation)
    {
        if(screen!=null)
        {
            TileEntityInfoPanel core = screen.getCore(); 
            if(core != null)
                core.setRotation(rotation);
        }
    }

    @Override
    public ItemStack getWrenchDrop(EntityPlayer entityPlayer)
    {
        return new ItemStack(IC2NuclearControl.instance.blockNuclearControlMain.blockID, 1, BlockNuclearControlMain.DAMAGE_INFO_PANEL_EXTENDER);
    }
}
