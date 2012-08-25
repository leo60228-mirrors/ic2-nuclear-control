package net.minecraft.src.nuclearcontrol;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Facing;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.TileEntity;
import net.minecraft.src.mod_IC2NuclearControl;
import net.minecraft.src.forge.ISidedInventory;
import net.minecraft.src.ic2.api.INetworkClientTileEntityEventListener;
import net.minecraft.src.ic2.api.INetworkDataProvider;
import net.minecraft.src.ic2.api.INetworkUpdateListener;
import net.minecraft.src.ic2.api.IWrenchable;
import net.minecraft.src.ic2.api.NetworkHelper;
import net.minecraft.src.nuclearcontrol.panel.IPanelDataSource;


public class TileEntityInfoPanel extends TileEntity implements 
    ISlotItemFilter, INetworkDataProvider, INetworkUpdateListener, 
    INetworkClientTileEntityEventListener, IWrenchable, IRedstoneConsumer,
    ITextureHelper, IScreenPart, ISidedInventory, IRotation
{
    
    private static final int CARD_TYPE_MAX = 3;
    // 0 - reactor sensor location card
    // 1 - time card
    // 2 - storage sensor location card
    // 3 - storage array

    public static final int BORDER_NONE = 0;
    public static final int BORDER_LEFT = 1;
    public static final int BORDER_RIGHT = 2;
    public static final int BORDER_TOP = 4;
    public static final int BORDER_BOTTOM = 8;
    
    public static final int DISPLAY_DEFAULT = Integer.MAX_VALUE;
    
    public static final int SLOT_CARD = 0;
    public static final int SLOT_UPGRADE = 1;
    private static final int LOCATION_RANGE = 8;
    
    protected int updateTicker;
    protected int tickRate;
    protected boolean init;
    private ItemStack inventory[];
    private Screen screen;
    private ItemStack card;

    private boolean prevPowered;
    public boolean powered;

    public int[] displaySettings;
    
    private int prevRotation;
    public int rotation;
    
    private boolean prevShowLabels;
    public boolean showLabels;
    
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
    
    
    public void setShowLabels(boolean p)
    {
        showLabels = p;
        if (prevShowLabels != p)
        {
            NetworkHelper.updateTileEntityField(this, "showLabels");
        }
        prevShowLabels = showLabels;
    }

    public boolean getShowLabels()
    {
        return showLabels;
    }    
    
    public void setDisplaySettings(int s)
    {
        int cardType = 0;
        if(inventory[0]!=null && inventory[0].getItem() instanceof IPanelDataSource)
        {
            cardType = ((IPanelDataSource)inventory[0].getItem()).getCardType();
        }
        if(cardType > CARD_TYPE_MAX)
            cardType = 0;
        boolean update = displaySettings[cardType] != s;
        displaySettings[cardType] = s;
        if (update)
        {
            NetworkHelper.updateTileEntityField(this, "displaySettings");
        }
    }
    
    
    @Override
    public void onNetworkUpdate(String field)
    {
        if (field.equals("facing") && prevFacing != facing)
        {
            if(mod_IC2NuclearControl.isClient())
            {
                mod_IC2NuclearControl.screenManager.unregisterScreenPart(this);
                mod_IC2NuclearControl.screenManager.registerInfoPanel(this);
            }
            worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
            prevFacing = facing;
        }
        if (field.equals("card"))
        {
            inventory[SLOT_CARD] = card;
        }
        if (field.equals("showLabels"))
        {
            prevShowLabels = showLabels;
        }
        if (field.equals("powered") && prevPowered != powered)
        {
            if(screen!=null)
            {
                screen.turnPower(powered);
            }
            else
            {
                worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
                worldObj.updateAllLightTypes(xCoord, yCoord, zCoord);
            }
            prevPowered = powered;
        }
        if (field.equals("rotation") && prevRotation != rotation)
        {
            worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
            prevRotation = rotation;
        }
        
    }

    @Override
    public void onNetworkEvent(EntityPlayer entityplayer, int i)
    {
        if(i == -1)
            setShowLabels(true);
        else if(i == -2)
            setShowLabels(false);
        else
            setDisplaySettings(i);
    }
    
    public TileEntityInfoPanel()
    {
        super();
        inventory = new ItemStack[2];//card + range upgrades
        screen = null;
        card = null;
        init = false;
        tickRate = IC2NuclearControl.screenRefreshPeriod;
        updateTicker = tickRate;
        displaySettings = new int[CARD_TYPE_MAX+1];
        for(int i=0; i<=CARD_TYPE_MAX; i++)
            displaySettings[i] = DISPLAY_DEFAULT;
        powered = false;
        prevPowered = false;
        facing = 0;
        prevFacing = 0;
        prevRotation = 0;
        rotation = 0;        
    }
    
    @Override
    public List<String> getNetworkedFields()
    {
        List<String> list = new ArrayList<String>(5);
        list.add("powered");
        list.add("displaySettings");
        list.add("facing");
        list.add("rotation");
        list.add("card");
        list.add("showLabels");
        return list;
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
        if(mod_IC2NuclearControl.isClient())
        {
            mod_IC2NuclearControl.screenManager.registerInfoPanel(this);
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
        if(nbttagcompound.hasKey("rotation"))
        {
            prevRotation = rotation = nbttagcompound.getInteger("rotation");
        }
        if(nbttagcompound.hasKey("showLabels"))
        {
            prevShowLabels = showLabels = nbttagcompound.getBoolean("showLabels");
        }
        else
        {
            //v.1.1.11 compatibility
            prevShowLabels = showLabels = true; 
        }
        prevFacing = facing =  nbttagcompound.getShort("facing");
        if(nbttagcompound.hasKey("dSets"))
        {
            int[] dSets =nbttagcompound.getIntArray("dSets");
            if(dSets.length == displaySettings.length)
            {
                displaySettings = dSets;
            }
            else
            {
                for(int i=0; i<dSets.length; i++)
                {
                    displaySettings[i] = dSets[i];
                }
            }
        }
        else
        {
            displaySettings[0] = nbttagcompound.getInteger("displaySettings");
        }
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
        if(mod_IC2NuclearControl.isClient())
        {
            mod_IC2NuclearControl.screenManager.unregisterScreenPart(this);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setShort("facing", facing);
        nbttagcompound.setIntArray("dSets", displaySettings);
        nbttagcompound.setInteger("rotation", rotation);
        nbttagcompound.setBoolean("showLabels", getShowLabels());

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
        if(worldObj!= null && !worldObj.isRemote)
        {
            int upgradeCountRange = 0;
            ItemStack itemStack = inventory[SLOT_UPGRADE];
            if(itemStack != null && itemStack.getItem() instanceof ItemRangeUpgrade)
            {
                upgradeCountRange = itemStack.stackSize;
            }
            if(inventory[SLOT_CARD]!=null)
            {
                Item item = inventory[SLOT_CARD].getItem();
                if(item instanceof IPanelDataSource)
                {
                    if(upgradeCountRange > 7)
                        upgradeCountRange = 7;
                    int range = LOCATION_RANGE * (int)Math.pow(2, upgradeCountRange);
                    ((IPanelDataSource) item).update(this, inventory[SLOT_CARD], range);
                }
            }
        }
    };

    @Override
    public boolean isItemValid(int slotIndex, ItemStack itemstack)
    {
        switch (slotIndex)
        {
            case SLOT_CARD:
                return itemstack.getItem() instanceof IPanelDataSource;
            default:
                return itemstack.getItem() instanceof ItemRangeUpgrade; 
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
        if(texture!=80)
            return texture;
        if(screen != null)
        {
            boolean left = false;
            boolean right = false;
            boolean top = false;
            boolean bottom = false;
            boolean ccw = false;
            //facing
            //     top / left 
            // 0 - minZ, minX
            // 1 - minZ, maxX
            // 2 - maxY, minX
            // 3 - maxY, maxX
            // 4 - maxY, maxZ
            // 5 - maxY, minZ
            switch(facing)
            {
                case 0:
                    if(x == screen.minX)
                        left = true;
                    if(x == screen.maxX)
                        right = true;
                    if(z == screen.minZ)
                        top = true;
                    if(z == screen.maxZ)
                        bottom = true;
                break;
                case 1:
                    if(x == screen.minX)
                        left = true;
                    if(x == screen.maxX)
                        right = true;
                    if(z == screen.minZ)
                        top = true;
                    if(z == screen.maxZ)
                        bottom = true;
                   // ccw = true;
                break;
                case 2:
                    if(x == screen.minX)
                        left = true;
                    if(x == screen.maxX)
                        right = true;
                    if(y == screen.maxY)
                        top = true;
                    if(y == screen.minY)
                        bottom = true;
                break;
                case 3:
                    if(x == screen.minX)
                        right = true;
                    if(x == screen.maxX)
                        left = true;
                    if(y == screen.maxY)
                        top = true;
                    if(y == screen.minY)
                        bottom = true;
                    ccw = true;
                break;
                case 4:
                    if(z == screen.minZ)
                        right = true;
                    if(z == screen.maxZ)
                        left = true;
                    if(y == screen.maxY)
                        top = true;
                    if(y == screen.minY)
                        bottom = true;
                    ccw = true;
                break;
                case 5:
                    if(z == screen.minZ)
                        left = true;
                    if(z == screen.maxZ)
                        right = true;
                    if(y == screen.maxY)
                        top = true;
                    if(y == screen.minY)
                        bottom = true;
                break;
            }
            if(rotation == 0)
            {
                if(left) texture+=BORDER_LEFT;
                if(right) texture+=BORDER_RIGHT;
                if(top) texture+=BORDER_TOP;
                if(bottom) texture+=BORDER_BOTTOM;
            }
            else
            if( !ccw && rotation == 1)
            {
                if(facing == 1)
                {
                    if(left) texture+=BORDER_TOP;
                    if(right) texture+=BORDER_BOTTOM;
                    if(top) texture+=BORDER_RIGHT;
                    if(bottom) texture+=BORDER_LEFT;
                }
                else
                {
                    if(left) texture+=BORDER_BOTTOM;
                    if(right) texture+=BORDER_TOP;
                    if(top) texture+=BORDER_LEFT;
                    if(bottom) texture+=BORDER_RIGHT;
                }
            }
            else
            if( ccw && rotation == 1)
            {
                if(left) texture+=BORDER_BOTTOM;
                if(right) texture+=BORDER_TOP;
                if(top) texture+=BORDER_RIGHT;
                if(bottom) texture+=BORDER_LEFT;
            }
            else
            if(rotation == 3)
            {
                if(left) texture+=BORDER_RIGHT;
                if(right) texture+=BORDER_LEFT;
                if(top) texture+=BORDER_BOTTOM;
                if(bottom) texture+=BORDER_TOP;
            }
            else
            if( !ccw && rotation == 2)
            {
                if(facing == 1)
                {
                    if(left) texture+=BORDER_BOTTOM;
                    if(right) texture+=BORDER_TOP;
                    if(top) texture+=BORDER_LEFT;
                    if(bottom) texture+=BORDER_RIGHT;
                }
                else
                {
                    if(left) texture+=BORDER_TOP;
                    if(right) texture+=BORDER_BOTTOM;
                    if(top) texture+=BORDER_RIGHT;
                    if(bottom) texture+=BORDER_LEFT;
                }
            }
            else
            if( ccw && rotation == 2)
            {
                if(left) texture+=BORDER_TOP;
                if(right) texture+=BORDER_BOTTOM;
                if(top) texture+=BORDER_LEFT;
                if(bottom) texture+=BORDER_RIGHT;
            }
        }
        else
        {
            texture+=15;
        }
        if(powered)
           texture+=16;
        return texture;
    }
    
    
    @Override
    public int modifyTextureIndex(int texture)
    {
        return modifyTextureIndex(texture, xCoord, yCoord, zCoord);
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
        TileEntityInfoPanel other = (TileEntityInfoPanel) obj;
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
    public int getStartInventorySide(int side)
    {
        if(side == 0)
            return 1;
        return 0;
    }

    @Override
    public int getSizeInventorySide(int side)
    {
        if(side == 0 || side == 1)
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
    
    public int getDisplaySettings()
    {
        if(inventory[SLOT_CARD] == null)
            return 0;
        int cardType = 0;
        if(inventory[0]!=null && inventory[0].getItem() instanceof IPanelDataSource)
        {
            cardType = ((IPanelDataSource)inventory[0].getItem()).getCardType();
        }
        if(cardType > CARD_TYPE_MAX)
            cardType = 0;
        return displaySettings[cardType];
    }
    
     
}
