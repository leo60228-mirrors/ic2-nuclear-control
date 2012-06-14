package net.minecraft.src.nuclearcontrol;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Facing;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.TileEntity;
import net.minecraft.src.ic2.api.INetworkClientTileEntityEventListener;
import net.minecraft.src.ic2.api.INetworkDataProvider;
import net.minecraft.src.ic2.api.INetworkUpdateListener;
import net.minecraft.src.ic2.api.IWrenchable;
import net.minecraft.src.ic2.api.Items;
import net.minecraft.src.ic2.api.NetworkHelper;


public class TileEntityInfoPanel extends TileEntity implements 
    IInventory, ISlotItemFilter, INetworkDataProvider, INetworkUpdateListener, 
    INetworkClientTileEntityEventListener, IWrenchable, IRedstoneConsumer,
    ITextureHelper
{
    public static final int DISPLAY_ONOFF = 1;
    public static final int DISPLAY_HEAT = 2;
    public static final int DISPLAY_MAXHEAT = 4;
    public static final int DISPLAY_OUTPUT = 8;
    public static final int DISPLAY_TIME = 16;
    public static final int DISPLAY_MELTING = 32;
    
    public static final int DISPLAY_DEFAULT = 63;
    
    public static final int SLOT_CARD = 0;
    public static final int SLOT_UPGRADE = 1;
    private static final int LOCATION_RANGE = 8;
    
    public int deltaX;
    public int deltaY;
    public int deltaZ;
    protected int updateTicker;
    protected int tickRate;
    protected boolean init;
    private ItemStack inventory[];

    private boolean prevPowered;
    public boolean powered;

    //display settings
    private int prevDisplaySettings;
    public int displaySettings;
    
    //monitorable fields
    private int prevHeat;
    public int heat;
    
    private int prevOutput;
    public int output;
    
    private boolean prevReactorPowered;
    public boolean reactorPowered;
    
    private int prevTimeleft;
    public int timeLeft;
    
    private int prevMaxHeat;
    public int maxHeat;
    
    
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
    public void setPowered(boolean p)
    {
        powered = p;
        if (prevPowered != p)
        {
            NetworkHelper.updateTileEntityField(this, "powered");
        }
        prevPowered = powered;
    }

    @Override
    public boolean getPowered()
    {
        return powered;
    }    
    
    public void setDisplaySettings(int s)
    {
        displaySettings = s;
        if (prevDisplaySettings != s)
        {
            NetworkHelper.updateTileEntityField(this, "displaySettings");
        }
        prevDisplaySettings = displaySettings;
    }
    
    public void setHeat(int h)
    {
        heat = h;
        if (prevHeat != h)
        {
            NetworkHelper.updateTileEntityField(this, "heat");
        }
        prevHeat = heat;
    }
    
    public void setOutput(int o)
    {
        output = o;
        if (prevOutput != o)
        {
            NetworkHelper.updateTileEntityField(this, "output");
        }
        prevOutput = output;
    }
    
    public void setReactorPowered(boolean p)
    {
        reactorPowered = p;
        if (prevReactorPowered != p)
        {
            NetworkHelper.updateTileEntityField(this, "reactorPowered");
        }
        prevReactorPowered = reactorPowered;
    }
    
    public void setTimeLeft(int t)
    {
        timeLeft = t;
        if (prevTimeleft != t)
        {
            NetworkHelper.updateTileEntityField(this, "timeLeft");
        }
        prevTimeleft = timeLeft;
    }
    
    public void setMaxHeat(int h)
    {
        maxHeat = h;
        if (prevMaxHeat != h)
        {
            NetworkHelper.updateTileEntityField(this, "maxHeat");
        }
        prevMaxHeat = maxHeat;
    }
    
    
    @Override
    public void onNetworkUpdate(String field)
    {
        if (field.equals("facing") && prevFacing != facing)
        {
            worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
            prevFacing = facing;
        }
        if (field.equals("powered") && prevPowered != powered)
        {
            worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
            worldObj.updateAllLightTypes(xCoord, yCoord, zCoord);
            prevPowered = powered;
        }
    }

    public void onNetworkEvent(EntityPlayer entityplayer, int i)
    {
        setDisplaySettings(i);
    }
    
    public TileEntityInfoPanel()
    {
        super();
        inventory = new ItemStack[2];//card + range upgrades
        deltaX = 0;
        deltaY = 0;
        deltaZ = 0;
        init = false;
        tickRate = -1;
        updateTicker = 0;
        displaySettings = DISPLAY_DEFAULT;
        prevDisplaySettings = DISPLAY_DEFAULT;
        powered = false;
        prevPowered = false;
        facing = 0;
        prevFacing = 0;
    }
    
    @Override
    public List<String> getNetworkedFields()
    {
        List<String> list = new ArrayList<String>(8);
        list.add("powered");
        list.add("displaySettings");
        list.add("heat");
        list.add("output");
        list.add("reactorPowered");
        list.add("timeLeft");
        list.add("maxHeat");
        list.add("facing");
        return list;
    }
    
    protected void readData()
    {
        onInventoryChanged();

        TileEntity reactor = NuclearHelper.getReactorAt(worldObj, xCoord+deltaX, yCoord+deltaY, zCoord+deltaZ);
        if(reactor != null){
            if(tickRate == -1)
            {
                tickRate = NuclearHelper.getReactorTickRate(reactor);
                if(tickRate == 0)
                    tickRate = 1;
                updateTicker = tickRate;
            }
            setHeat(NuclearHelper.getReactorHeat(reactor));
            setMaxHeat(NuclearHelper.getMaxHeat(reactor));
            setReactorPowered(NuclearHelper.getReactorIsProducingEnergy(reactor));
            setOutput(NuclearHelper.getReactorOutput(reactor));
            IInventory inventory = (IInventory)reactor; 
            int slotCount = inventory.getSizeInventory();
            int timeLeft = 0;
            int uraniumId = Items.getItem("uraniumCell").itemID;
            for(int i = 0; i < slotCount; i++)
            {
                ItemStack stack = inventory.getStackInSlot(i);
                if(stack!=null && stack.itemID == uraniumId)
                {
                    timeLeft = Math.max(timeLeft, stack.getMaxDamage() - stack.getItemDamage());
                }
            }
            setTimeLeft(timeLeft);
        }
    }

    protected void initData()
    {
        if(worldObj.isRemote){
            NetworkHelper.requestInitialData(this);
        }
        else
        {
            RedstoneHelper.checkPowered(worldObj, this);
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
        if (!worldObj.isRemote)
        {
            if (tickRate != -1 && updateTicker-- > 0)
                return;
            updateTicker = tickRate;
            readData();
        }      
        super.updateEntity();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        prevFacing = facing =  nbttagcompound.getShort("facing");
        prevDisplaySettings = displaySettings = nbttagcompound.getInteger("displaySettings");
        NBTTagList nbttaglist = nbttagcompound.getTagList("Items");
        inventory = new ItemStack[getSizeInventory()];
        for (int i = 0; i < nbttaglist.tagCount(); i++)
        {
            NBTTagCompound compound = (NBTTagCompound)nbttaglist.tagAt(i);
            byte slotNum = compound.getByte("Slot");

            if (slotNum >= 0 && slotNum < inventory.length)
            {
                inventory[slotNum] = ItemStack.loadItemStackFromNBT(compound);
            }
        }
        onInventoryChanged();
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setShort("facing", facing);
        nbttagcompound.setInteger("displaySettings", displaySettings);

        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < inventory.length; i++)
        {
            if (inventory[i] != null)
            {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setByte("Slot", (byte)i);
                inventory[i].writeToNBT(compound);
                nbttaglist.appendTag(compound);
            }
        }
        nbttagcompound.setTag("Items", nbttaglist);
    }

    @Override
    public int getSizeInventory()
    {
        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slotNum)
    {
        return inventory[slotNum];
    }

    @Override
    public ItemStack decrStackSize(int slotNum, int amount)
    {
        if(inventory[slotNum]!=null)
        {
            if (inventory[slotNum].stackSize <= amount)
            {
                ItemStack itemStack = inventory[slotNum];
                inventory[slotNum] = null;
                return itemStack;
            }
            
            ItemStack taken = inventory[slotNum].splitStack(amount);
            if (inventory[slotNum].stackSize == 0)
            {
                inventory[slotNum] = null;
            }
            return taken;
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int var1)
    {
        return null;
    }
    
    @Override
    public void setInventorySlotContents(int slotNum, ItemStack itemStack)
    {
        inventory[slotNum] = itemStack;

        if (itemStack != null && itemStack.stackSize > getInventoryStackLimit())
        {
            itemStack.stackSize = getInventoryStackLimit();
        }
    }

    @Override
    public String getInvName()
    {
        return "block.StatusDisplay";
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this &&
                player.getDistanceSq((double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D) <= 64D;
    }

    @Override
    public void openChest()
    {
    }

    @Override
    public void closeChest()
    {
    }
    
    @Override
    public void onInventoryChanged() 
    {
        super.onInventoryChanged();
        int upgradeCountRange = 0;
        ItemStack itemStack = inventory[SLOT_UPGRADE];
        if(itemStack != null && itemStack.getItem() instanceof ItemRangeUpgrade)
        {
            upgradeCountRange = itemStack.stackSize;
        }
        if(inventory[SLOT_CARD]!=null)
        {
            int[] coordinates = ItemSensorLocationCard.getCoordinates(inventory[SLOT_CARD]);
            if(coordinates!=null)
            {
                deltaX = coordinates[0] - xCoord;
                deltaY = coordinates[1] - yCoord;
                deltaZ = coordinates[2] - zCoord;
                if(upgradeCountRange > 7)
                    upgradeCountRange = 7;
                int range = LOCATION_RANGE * (int)Math.pow(2, upgradeCountRange);
                if(Math.abs(deltaX) > range || 
                    Math.abs(deltaY) > range || 
                    Math.abs(deltaZ) > range)
                {
                    deltaX = deltaY = deltaZ = 0;
                }
            }
            else
            {
                deltaX = 0;
                deltaY = 0;
                deltaZ = 0;
            }
        }
        else
        {
            deltaX = 0;
            deltaY = 0;
            deltaZ = 0;
        }
    };

    @Override
    public boolean isItemValid(int slotIndex, ItemStack itemstack)
    {
        switch (slotIndex)
        {
            case SLOT_CARD:
                return itemstack.getItem() instanceof ItemSensorLocationCard;
            default:
                return itemstack.getItem() instanceof ItemRangeUpgrade; 
        }
        
    }
    
    @Override
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int face) {
        return getFacing() != face;
    };

    @Override
    public float getWrenchDropRate()
    {
        return 1;
    }

    @Override
    public boolean wrenchCanRemove(EntityPlayer entityPlayer)
    {
        return true;
    }

    @Override
    public int modifyTextureIndex(int texture)
    {
        if(texture!=11 || !powered)
            return texture;
        return texture + 16;
    }
}
