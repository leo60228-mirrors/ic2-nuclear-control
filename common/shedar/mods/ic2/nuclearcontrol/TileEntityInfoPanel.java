package shedar.mods.ic2.nuclearcontrol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cpw.mods.fml.common.FMLCommonHandler;

import shedar.mods.ic2.nuclearcontrol.api.CardState;
import shedar.mods.ic2.nuclearcontrol.api.ICardWrapper;
import shedar.mods.ic2.nuclearcontrol.api.IPanelDataSource;
import shedar.mods.ic2.nuclearcontrol.api.IPanelMultiCard;
import shedar.mods.ic2.nuclearcontrol.api.IRemoteSensor;
import shedar.mods.ic2.nuclearcontrol.api.PanelString;
import shedar.mods.ic2.nuclearcontrol.panel.CardWrapperImpl;

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


public class TileEntityInfoPanel extends TileEntity implements 
    ISlotItemFilter, INetworkDataProvider, INetworkUpdateListener, 
    INetworkClientTileEntityEventListener, IWrenchable, IRedstoneConsumer,
    ITextureHelper, IScreenPart, ISidedInventory, IRotation
{
    private static final int[] COLORS_HEX = {0, 0xe93535, 0x82e306, 0x702b14, 0x1f3ce7, 
                                            0x8f1fea, 0x1fd7e9, 0xcbcbcb, 0x222222, 0xe60675, 
                                            0x1fe723, 0xe9cc1f, 0x06aee4, 0xb006e3, 0xe7761f };

    public static final int BORDER_NONE = 0;
    public static final int BORDER_LEFT = 1;
    public static final int BORDER_RIGHT = 2;
    public static final int BORDER_TOP = 4;
    public static final int BORDER_BOTTOM = 8;
    
    public static final int DISPLAY_DEFAULT = Integer.MAX_VALUE;
    
    public static final int SLOT_CARD = 0;
    public static final int SLOT_UPGRADE_RANGE = 1;
    public static final int SLOT_UPGRADE_COLOR = 2;
    private static final int LOCATION_RANGE = 8;
    
    protected int updateTicker;
    protected int dataTicker;
    protected int tickRate;
    protected boolean init;
    private ItemStack inventory[];
    private Screen screen;
    private ItemStack card;

    private boolean prevPowered;
    public boolean powered;

    public Map<UUID, Integer> displaySettings;
    
    private int prevRotation;
    public int rotation;
    
    private boolean prevShowLabels;
    public boolean showLabels;
    
    private short prevFacing;
    public short facing;

    private int prevColorBackground;
    public int colorBackground;
    
    private int  prevColorText;
    public int colorText;
    
    private boolean  prevColored;
    public boolean colored;
    
    List<PanelString> cardData;
    
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
    
    
    public void setColored(boolean c)
    {
        colored = c;
        if (prevColored != c)
        {
            NetworkHelper.updateTileEntityField(this, "colored");
        }
        prevColored = colored;
    }

    public boolean getColored()
    {
        return colored;
    }    
    
    public void setColorBackground(int c)
    {
        c&=0xf;
        colorBackground = c;
        if (prevColorBackground != c)
        {
            NetworkHelper.updateTileEntityField(this, "colorBackground");
        }
        prevColorBackground = colorBackground;
    }

    public int getColorBackground()
    {
        return colorBackground;
    }    
    
    public void setColorText(int c)
    {
        c&=0xf;
        colorText = c;
        if (prevColorText != c)
        {
            NetworkHelper.updateTileEntityField(this, "colorText");
        }
        prevColorText = colorText;
    }

    public int getColorText()
    {
        return colorText;
    }    
    
    public int getColorTextHex()
    {
        return COLORS_HEX[colorText];
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
        UUID cardType = null;
        ItemStack stack = inventory[SLOT_CARD]; 
        if(stack!=null)
        {
            if(stack.getItem() instanceof IPanelMultiCard)
            {
                cardType = ((IPanelMultiCard)stack.getItem()).getCardType(new CardWrapperImpl(stack));
            }
            else if(stack.getItem() instanceof IPanelDataSource)
            {
                cardType = ((IPanelDataSource)inventory[SLOT_CARD].getItem()).getCardType();
            }
        }
        boolean update = !displaySettings.containsKey(cardType)  || displaySettings.get(cardType) != s;
        displaySettings.put(cardType, s);
        if (update && FMLCommonHandler.instance().getEffectiveSide().isServer())
        {
            NuclearNetworkHelper.sendDisplaySettingsUpdate(this, cardType, s);
        }
    }
    
    
    @Override
    public void onNetworkUpdate(String field)
    {
        if (field.equals("facing") && prevFacing != facing)
        {
            if(FMLCommonHandler.instance().getEffectiveSide().isClient())
            {
                IC2NuclearControl.instance.screenManager.unregisterScreenPart(this);
                IC2NuclearControl.instance.screenManager.registerInfoPanel(this);
            }
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            prevFacing = facing;
        }
        if (field.equals("colorBackground") || field.equals("colored"))
        {
            if(screen!=null)
            {
                screen.markUpdate();
            }
            else
            {
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
            prevColored = colored;
            prevColorBackground = colorBackground;
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
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                worldObj.updateAllLightTypes(xCoord, yCoord, zCoord);
            }
            prevPowered = powered;
        }
        if (field.equals("rotation") && prevRotation != rotation)
        {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
        inventory = new ItemStack[3];//card + range upgrade + color upgrade
        screen = null;
        card = null;
        init = false;
        tickRate = IC2NuclearControl.instance.screenRefreshPeriod;
        updateTicker = tickRate;
        dataTicker = 4;
        displaySettings = new HashMap<UUID, Integer>();
        powered = false;
        prevPowered = false;
        facing = 0;
        prevFacing = 0;
        prevRotation = 0;
        rotation = 0;
        showLabels = true;
        colored = false;
        colorBackground = IC2NuclearControl.COLOR_GREEN;
        cardData = null;
    }
    
    @Override
    public List<String> getNetworkedFields()
    {
        List<String> list = new ArrayList<String>(8);
        list.add("powered");
        list.add("facing");
        list.add("rotation");
        list.add("card");
        list.add("showLabels");
        list.add("colorBackground");
        list.add("colorText");
        list.add("colored");
        return list;
    }
    
    protected void initData()
    {
        if(worldObj.isRemote)
        {
            NetworkHelper.requestInitialData(this);
            NuclearNetworkHelper.requestDisplaySettings(this);
        }
        else
        {
            RedstoneHelper.checkPowered(worldObj, this);
        }
        if(FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            IC2NuclearControl.instance.screenManager.registerInfoPanel(this);
        }
        init = true;
    }

    public void resetCardData()
    {
        cardData = null;
    }
    
    public List<PanelString> getCardData(int settings, IPanelDataSource card, ICardWrapper helper)
    {
        List<PanelString> data = cardData;
        if(data==null)
        {
            data = card.getStringData(settings, helper, getShowLabels());
            String title = helper.getTitle();
            if( data != null && title!=null && !title.isEmpty())
            {
                PanelString titleString = new PanelString();
                titleString.textCenter = title;
                data.add(0, titleString);
            }
            
            cardData = data;
        }
        return data;
    }
    
    @Override
    public void updateEntity()
    {
        if (!init)
        {
            initData();
        }
        dataTicker--;
        if(dataTicker <= 0)
        {
            cardData = null;
            dataTicker = 4;
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

        if(nbttagcompound.hasKey("colorBackground"))
        {
            colorText = nbttagcompound.getInteger("colorText");
            colorBackground = nbttagcompound.getInteger("colorBackground");
        }
        else
        {
            //1.4.1 compatibility
            colorText = 0;
            colorBackground = IC2NuclearControl.COLOR_GREEN;
        }

        if(nbttagcompound.hasKey("dSettings"))
        {
            NBTTagList settingsList = nbttagcompound.getTagList("dSettings");
            for (int i = 0; i < settingsList.tagCount(); i++)
            {
                NBTTagCompound compound = (NBTTagCompound)settingsList.tagAt(i);
                UUID key = UUID.fromString(compound.getString("key"));
                int value = compound.getInteger("value");
                displaySettings.put(key, value);
            }
        }
      
        if(nbttagcompound.hasKey("dSets"))
        {//v.1.3.2 compatibility
            
            int[] dSets = nbttagcompound.getIntArray("dSets");
            for(int i=0; i<dSets.length; i++)
            {
                displaySettings.put(new UUID(0, i), dSets[i]);
            }
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
        NBTTagList settingsList = new NBTTagList();
        for (Map.Entry<UUID, Integer> item : displaySettings.entrySet())
        {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString("key", item.getKey().toString());
            compound.setInteger("value", item.getValue());
            settingsList.appendTag(compound);
        }
        nbttagcompound.setTag("dSettings", settingsList);
        nbttagcompound.setInteger("rotation", rotation);
        nbttagcompound.setBoolean("showLabels", getShowLabels());

        nbttagcompound.setInteger("colorBackground", colorBackground);
        nbttagcompound.setInteger("colorText", colorText);
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
        if(worldObj!= null && FMLCommonHandler.instance().getEffectiveSide().isServer())
        {
            int upgradeCountRange = 0;
            ItemStack itemStack = inventory[SLOT_UPGRADE_COLOR];
            setColored(itemStack != null && itemStack.getItem() instanceof ItemUpgrade && itemStack.getItemDamage() == ItemUpgrade.DAMAGE_COLOR);
            itemStack = inventory[SLOT_UPGRADE_RANGE];
            if(itemStack != null && itemStack.getItem() instanceof ItemUpgrade && itemStack.getItemDamage() == ItemUpgrade.DAMAGE_RANGE)
            {
                upgradeCountRange = itemStack.stackSize;
            }
            ItemStack card = inventory[SLOT_CARD]; 
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
                            }
                        }
                    }
                    if(needUpdate)
                    {
                        CardState state = ((IPanelDataSource) item).update(this, cardHelper, range);
                        cardHelper.setInt("state", state.getIndex());
                    }
                    cardHelper.commit(this);
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
            case SLOT_UPGRADE_RANGE:
                return itemstack.getItem() instanceof ItemUpgrade && itemstack.getItemDamage() == ItemUpgrade.DAMAGE_RANGE; 
            case SLOT_UPGRADE_COLOR:
                return itemstack.getItem() instanceof ItemUpgrade && itemstack.getItemDamage() == ItemUpgrade.DAMAGE_COLOR; 
            default:
                return false;
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
        if(texture!=47)
            return texture;
        texture -= 15;
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
        if(colored)
        {
            texture = texture - 32 + colorBackground*16;
        }

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
    public int getStartInventorySide(ForgeDirection side)
    {
        // upgrade slots  
        if(side == ForgeDirection.DOWN)
            return 1;
        return 0;
    }

    @Override
    public int getSizeInventorySide(ForgeDirection side)
    {
        //upgrades
        if(side == ForgeDirection.DOWN)
            return 2;
        //card
        if(side == ForgeDirection.UP)
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
        ItemStack card = inventory[SLOT_CARD]; 
        if(card == null)
            return 0;
        UUID cardType = null;
        if(card.getItem() instanceof IPanelMultiCard)
        {
            cardType = ((IPanelMultiCard)card.getItem()).getCardType(new CardWrapperImpl(card));
        }
        else if(card.getItem() instanceof IPanelDataSource)
        {
            cardType = ((IPanelDataSource)card.getItem()).getCardType();
        }
        if(displaySettings.containsKey(cardType))
            return displaySettings.get(cardType);
        return DISPLAY_DEFAULT;
    }
    
     
    @Override
    public ItemStack getWrenchDrop(EntityPlayer entityPlayer)
    {
        return new ItemStack(IC2NuclearControl.instance.blockNuclearControlMain.blockID, 1, BlockNuclearControlMain.DAMAGE_INFO_PANEL);
    }
}
