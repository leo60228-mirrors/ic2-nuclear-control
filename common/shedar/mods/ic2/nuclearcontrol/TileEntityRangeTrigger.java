package shedar.mods.ic2.nuclearcontrol;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Facing;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.TileEntity;
import ic2.api.INetworkClientTileEntityEventListener;
import ic2.api.INetworkDataProvider;
import ic2.api.INetworkUpdateListener;
import ic2.api.IWrenchable;
import ic2.api.NetworkHelper;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import shedar.mods.ic2.nuclearcontrol.api.CardState;
import shedar.mods.ic2.nuclearcontrol.api.IPanelDataSource;
import shedar.mods.ic2.nuclearcontrol.api.IRemoteSensor;
import shedar.mods.ic2.nuclearcontrol.panel.CardWrapperImpl;
import cpw.mods.fml.common.FMLCommonHandler;


public class TileEntityRangeTrigger extends TileEntity implements 
    ISlotItemFilter, INetworkDataProvider, INetworkUpdateListener, 
    IWrenchable, ITextureHelper, ISidedInventory, IRotation, 
    INetworkClientTileEntityEventListener
{

    public static final int SLOT_CARD = 0;
    public static final int SLOT_UPGRADE = 1;
    private static final int LOCATION_RANGE = 8;
    
    private static final int STATE_UNKNOWN = -1;
    private static final int STATE_PASSIVE = 0;
    private static final int STATE_ACTIVE = 1;
    
    protected int updateTicker;
    protected int tickRate;
    protected boolean init;
    private ItemStack inventory[];
    private ItemStack card;

    private int prevRotation;
    public int rotation;
    
    private short prevFacing;
    public short facing;
    
    private int prevOnFire;
    private int onFire;
    
    private boolean prevInvertRedstone;
    private boolean invertRedstone;
    
    private long prevLevelStart;
    public long levelStart;

    private long prevLevelEnd;
    public long levelEnd;

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
    
    private void setCard(ItemStack value)
    {
        card = value;
        NetworkHelper.updateTileEntityField(this, "card");
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
            worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
            prevFacing = facing;
        }
        else if (field.equals("card"))
        {
            inventory[SLOT_CARD] = card;
        }
        else if (field.equals("rotation") && prevRotation != rotation)
        {
            worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
            prevRotation = rotation;
        }
        else if (field.equals("onFire") && prevOnFire != onFire)
        {
            worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
            prevOnFire = onFire;
        }
        else if (field.equals("invertRedstone") && prevInvertRedstone != invertRedstone)
        {
            worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
            prevInvertRedstone = invertRedstone;
        }
        
    }
    
    @Override
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
    
    public void setLevelStart(long start)
    {
        levelStart = start;
        if (prevLevelStart != start)
        {
            NetworkHelper.updateTileEntityField(this, "levelStart");
        }
        prevLevelStart = levelStart;
    }
    
    public void setLevelEnd(long end)
    {
        levelEnd = end;
        if (prevLevelEnd != end)
        {
            NetworkHelper.updateTileEntityField(this, "levelEnd");
        }
        prevLevelEnd = levelEnd;
    }
    
    public int getOnFire()
    {
        return onFire;
    }

    public TileEntityRangeTrigger()
    {
        super();
        inventory = new ItemStack[2];//card + range upgrades
        card = null;
        init = false;
        tickRate = IC2NuclearControl.instance.rangeTriggerRefreshPeriod;
        updateTicker = tickRate;
        facing = 0;
        prevFacing = 0;
        prevRotation = 0;
        rotation = 0;
        onFire = prevOnFire = 0;
        prevInvertRedstone = invertRedstone = false;
        levelStart = 10000000;
        levelEnd = 9000000;
    }
    
    @Override
    public List<String> getNetworkedFields()
    {
        List<String> list = new ArrayList<String>(7);
        list.add("facing");
        list.add("rotation");
        list.add("card");
        list.add("onFire");
        list.add("invertRedstone");
        list.add("levelStart");
        list.add("levelEnd");
        return list;
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
    
    @Override
    public void updateEntity()
    {
        if (!init)
        {
            initData();
        }
        if (!worldObj.isRemote)
        {
            if (updateTicker-- > 0)
                return;
            updateTicker = tickRate;
            onInventoryChanged();
        }      
        super.updateEntity();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        prevRotation = rotation = nbttagcompound.getInteger("rotation");
        prevFacing = facing =  nbttagcompound.getShort("facing");
        prevInvertRedstone = invertRedstone = nbttagcompound.getBoolean("invert"); 
        levelStart = nbttagcompound.getLong("levelStart");
        levelEnd = nbttagcompound.getLong("levelEnd");

        NBTTagList nbttaglist = nbttagcompound.getTagList("Items");
        inventory = new ItemStack[getSizeInventory()];
        for (int i = 0; i < nbttaglist.tagCount(); i++)
        {
            NBTTagCompound compound = (NBTTagCompound)nbttaglist.tagAt(i);
            byte slotNum = compound.getByte("Slot");

            if (slotNum >= 0 && slotNum < inventory.length)
            {
                inventory[slotNum] = ItemStack.loadItemStackFromNBT(compound);
                if(slotNum == SLOT_CARD)
                {
                    card = inventory[slotNum];
                }
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
        nbttagcompound.setInteger("rotation", rotation);
        nbttagcompound.setBoolean("invert", isInvertRedstone());
        nbttagcompound.setLong("levelStart", levelStart);
        nbttagcompound.setLong("levelEnd", levelEnd);

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
                if(slotNum == SLOT_CARD)
                    setCard(null);
                return itemStack;
            }
            
            ItemStack taken = inventory[slotNum].splitStack(amount);
            if (inventory[slotNum].stackSize == 0)
            {
                inventory[slotNum] = null;
                if(slotNum == SLOT_CARD)
                    setCard(null);
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
        if(slotNum == SLOT_CARD)
            setCard(itemStack);

        if (itemStack != null && itemStack.stackSize > getInventoryStackLimit())
        {
            itemStack.stackSize = getInventoryStackLimit();
        }
    }

    @Override
    public String getInvName()
    {
        return "block.RangeTrigger";
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
        if(worldObj!= null && FMLCommonHandler.instance().getEffectiveSide().isServer())
        {
            int upgradeCountRange = 0;
            ItemStack itemStack = inventory[SLOT_UPGRADE];
            if(itemStack != null && itemStack.getItem() instanceof ItemUpgrade && itemStack.getItemDamage() == ItemUpgrade.DAMAGE_RANGE)
            {
                upgradeCountRange = itemStack.stackSize;
            }
            ItemStack card = inventory[SLOT_CARD];
            int fire = STATE_UNKNOWN;
            if(card != null)
            {
                Item item = card.getItem();
                if(item instanceof IPanelDataSource)
                {
                    boolean needUpdate = true;
                    if(upgradeCountRange > 7)
                        upgradeCountRange = 7;
                    int range = LOCATION_RANGE * (int)Math.pow(2, upgradeCountRange);
                    CardWrapperImpl cardHelper = new CardWrapperImpl(card);
                    if(item instanceof IRemoteSensor)
                    {
                        ChunkCoordinates target = cardHelper.getTarget();
                        if(target == null)
                        {
                            needUpdate = false;
                            cardHelper.setState(CardState.INVALID_CARD);
                        }
                        else
                        {
                            int dx = target.posX - xCoord;
                            int dy = target.posY - yCoord;
                            int dz = target.posZ - zCoord;
                            if (Math.abs(dx) > range || 
                                Math.abs(dy) > range || 
                                Math.abs(dz) > range)
                            {
                                needUpdate = false;
                                cardHelper.setState(CardState.OUT_OF_RANGE);
                                fire = STATE_UNKNOWN;
                            }
                        }
                    }
                    if(needUpdate)
                    {
                        CardState state = ((IPanelDataSource) item).update(this, cardHelper, range);
                        cardHelper.setState(state);
                        if(state == CardState.OK)
                        {
                            long minV = Math.min(levelStart, levelEnd);
                            long maxV = Math.max(levelStart, levelEnd);
                            long cur = cardHelper.getLong("energyL");
                            if(cur>=maxV)
                            {
                                fire = STATE_ACTIVE;
                            }
                            else if(cur < minV)
                            {
                                fire = STATE_PASSIVE;
                            }
                            else if(onFire == STATE_UNKNOWN)
                            {
                                fire = STATE_PASSIVE;
                            }
                            else
                            {
                                fire = onFire;
                            }
                        }
                        else
                        {
                            fire = STATE_UNKNOWN;
                        }
                        
                    }
                }
            }
            if(fire != getOnFire())
            {
                setOnFire(fire);
                worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
            }
            
        }
    };

    @Override
    public boolean isItemValid(int slotIndex, ItemStack itemstack)
    {
        switch (slotIndex)
        {
            case SLOT_CARD:
                return itemstack.getItem() instanceof ItemCardEnergySensorLocation || 
                       itemstack.getItem() instanceof ItemCardEnergyArrayLocation;
            default:
                return itemstack.getItem() instanceof ItemUpgrade && itemstack.getItemDamage() == ItemUpgrade.DAMAGE_RANGE; 
        }
        
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

    public int modifyTextureIndex(int texture, int x, int y, int z)
    {
        if(texture!=27)
            return texture;
        switch (getOnFire())
        {
            case STATE_ACTIVE:
                texture += 2;
                break;
            case STATE_PASSIVE:
                texture += 1;
                break;
        }
        return texture;
    }
    
    
    @Override
    public int modifyTextureIndex(int texture)
    {
        return modifyTextureIndex(texture, xCoord, yCoord, zCoord);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + xCoord;
        result = prime * result + yCoord;
        result = prime * result + zCoord;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TileEntityRangeTrigger other = (TileEntityRangeTrigger) obj;
        if (xCoord != other.xCoord)
            return false;
        if (yCoord != other.yCoord)
            return false;
        if (zCoord != other.zCoord)
            return false;
        if (worldObj != other.worldObj)
            return false;
        return true;
    }

    @Override
    public int getStartInventorySide(ForgeDirection side)
    {
        if(side == ForgeDirection.DOWN)
            return 1;
        return 0;
    }

    @Override
    public int getSizeInventorySide(ForgeDirection side)
    {
        if(side == ForgeDirection.DOWN || side == ForgeDirection.UP)
            return 1;
        return inventory.length;
    }

    @Override
    public void rotate()
    {
        int r;
        switch (rotation)
        {
            case 0:
                r = 1;
                break;
            case 1:
                r = 3;
                break;
            case 3:
                r = 2;
                break;
            case 2:
                r = 0;
                break;
            default:
                r = 0;
                break;
        }
        setRotation(r);
    }

    @Override
    public int getRotation()
    {
        return rotation;
    }

    @Override
    public void setRotation(int value)
    {
        rotation = value;
        if(rotation!=prevRotation)
        {
            NetworkHelper.updateTileEntityField(this, "rotation");
        }
        prevRotation = rotation;
    }
    
}
