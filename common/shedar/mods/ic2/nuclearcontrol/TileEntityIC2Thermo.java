package shedar.mods.ic2.nuclearcontrol;

import java.util.List;
import java.util.Vector;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Facing;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import ic2.api.INetworkClientTileEntityEventListener;
import ic2.api.INetworkDataProvider;
import ic2.api.INetworkUpdateListener;
import ic2.api.IReactor;
import ic2.api.IReactorChamber;
import ic2.api.IWrenchable;
import ic2.api.NetworkHelper;

public class TileEntityIC2Thermo extends TileEntity implements 
        INetworkDataProvider, INetworkUpdateListener, 
        INetworkClientTileEntityEventListener, IWrenchable,
        ITextureHelper
{
    protected boolean init;
    private int prevHeatLevel;
    public int heatLevel;
    private int mappedHeatLevel;
    private int prevOnFire;
    public int onFire;
    private short prevFacing;
    public short facing;
    private boolean prevInvertRedstone;
    private boolean invertRedstone;

    protected int updateTicker;
    protected int tickRate;

    public TileEntityIC2Thermo()
    {
        init = false;
        onFire = 0;
        prevOnFire = 0;
        facing = 0;
        prevFacing = 0;
        mappedHeatLevel = 500;
        prevHeatLevel = 500;
        heatLevel = 500;
        updateTicker = 0;
        tickRate = -1;
        prevInvertRedstone = false;
        invertRedstone = false;
    }

    protected void initData()
    {
    	if(worldObj.isRemote)
    	{
    		NetworkHelper.requestInitialData(this);
    	}
    	else
    	{
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
    	}
        init = true;
    }

    public boolean isInvertRedstone()
    {
        return invertRedstone;
    }
    
    public void setInvertRedstone(boolean value)
    {
        invertRedstone = value;
        if(prevInvertRedstone !=value)
        {
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
            NetworkHelper.updateTileEntityField(this, "invertRedstone");
        }
        prevInvertRedstone = value;
    }
    
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
    public List<String> getNetworkedFields()
    {
        Vector<String> vector = new Vector<String>(3);
        vector.add("heatLevel");
        vector.add("onFire");
        vector.add("facing");
        vector.add("invertRedstone");
        return vector;
    }
    
    @Override
    public void onNetworkUpdate(String field)
    {
        if (field.equals("heatLevel") && prevHeatLevel != heatLevel)
        {
            worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
            prevHeatLevel = heatLevel;
        }
        if (field.equals("facing") && prevFacing != facing)
        {
            worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
            prevFacing = facing;
        }
        if (field.equals("onFire") && prevOnFire != onFire)
        {
            worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
            prevOnFire = onFire;
        }
        if (field.equals("invertRedstone") && prevInvertRedstone != invertRedstone)
        {
            worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
            prevInvertRedstone = invertRedstone;
        }
    }

    public void onNetworkEvent(EntityPlayer entityplayer, int i)
    {
        if(i < 0)
        {
            switch (i)
            {
            case -1:
                setInvertRedstone(false);
                break;
            case -2:
                setInvertRedstone(true);
                break;
            default:
                break;
            }
        }
        else
        {
            setHeatLevel(i);
        }
        
    }
    
    public void setOnFire(int f)
    {
        onFire = f;
        if (prevOnFire != f)
        {
            NetworkHelper.updateTileEntityField(this, "onFire");
        }
        prevOnFire = onFire;
    }
    
    public int getOnFire()
    {
        return onFire;
    }
    
    public void setHeatLevel(int h)
    {
        heatLevel = h;
        if (prevHeatLevel != h)
        {
            NetworkHelper.updateTileEntityField(this, "heatLevel");
        }
        prevHeatLevel = heatLevel;
        mappedHeatLevel = h;
    }    

    public void setHeatLevelWithoutNotify(int h)
    {
    	heatLevel = h;
        prevHeatLevel = heatLevel;
        mappedHeatLevel = h;
    }
    
    public Integer getHeatLevel()
    {
    	return heatLevel;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        if(nbttagcompound.hasKey("heatLevel")){
        	int heat = nbttagcompound.getInteger("heatLevel");
        	setHeatLevelWithoutNotify(heat);
        	prevFacing = facing =  nbttagcompound.getShort("facing");
        	prevInvertRedstone = invertRedstone = nbttagcompound.getBoolean("invert"); 
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setInteger("heatLevel", getHeatLevel());
        nbttagcompound.setShort("facing", facing);
        nbttagcompound.setBoolean("invert", isInvertRedstone());
    }

    protected void checkStatus()
    {
    	byte fire;
    	IReactorChamber chamber = NuclearHelper.getReactorChamberAroundCoord(worldObj, xCoord, yCoord, zCoord);
        IReactor reactor = null;
        if(chamber != null){
        	reactor = chamber.getReactor();
        }
        if(reactor == null){
        	reactor = NuclearHelper.getReactorAroundCoord(worldObj, xCoord, yCoord, zCoord);
        }
        if(reactor != null){
        	if(tickRate == -1)
        	{
        		tickRate = reactor.getTickRate() / 2;
        		if(tickRate == 0)
    				tickRate = 1;
        		updateTicker = tickRate;
        	}
        	int reactorHeat = reactor.getHeat();
            if (reactorHeat >= mappedHeatLevel)
            {
                fire = 1;
            } 
            else
            {
                fire = 0;
            }
        }
        else
        {
            fire = -1;
        }
        if(fire != getOnFire()){
        	setOnFire(fire);
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
        }
    }

    @Override
    public void updateEntity()
    {
        if (!init)
        {
            initData();
        }
        super.updateEntity();
        if (!worldObj.isRemote)
        {
            if (tickRate != -1 && updateTicker-- > 0)
                return;
            updateTicker = tickRate;
            checkStatus();
        }

    }
    
    @Override
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side)
    {
        return false;
    }

    @Override
    public boolean wrenchCanRemove(EntityPlayer entityPlayer)
    {
        return true;
    }

    @Override
    public float getWrenchDropRate()
    {
        return 1;
    }
    
    @Override
    public int modifyTextureIndex(int texture)
    {
        if(texture != 0)
            return texture;
        int fireState = getOnFire();
        switch (fireState)
        {
            case 1:
                texture = 16;
                break;
            case 0:
                texture = 0;
                break;
            default:
                texture = 32;
                break;
        }
        return texture;
    }
}
