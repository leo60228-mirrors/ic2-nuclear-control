package net.minecraft.src.nuclearcontrol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.mod_IC2NuclearControl;
import net.minecraft.src.ic2.api.IReactor;
import net.minecraft.src.ic2.api.Items;
import net.minecraft.src.nuclearcontrol.panel.PanelSetting;
import net.minecraft.src.nuclearcontrol.panel.PanelString;
import net.minecraft.src.nuclearcontrol.utils.ItemStackUtils;

public abstract class ItemSensorLocationCardBase extends ItemCardBase
{
    protected static final String HINT_TEMPLATE = "x: %d, y: %d, z: %d";

    public static final int DISPLAY_ONOFF = 1;
    public static final int DISPLAY_HEAT = 2;
    public static final int DISPLAY_MAXHEAT = 4;
    public static final int DISPLAY_OUTPUT = 8;
    public static final int DISPLAY_TIME = 16;
    public static final int DISPLAY_MELTING = 32;
    
    public static final int CARD_TYPE = 0;
    
    public ItemSensorLocationCardBase(int i, int iconIndex)
    {
        super(i, iconIndex);
    }

    @Override
    public void update(TileEntityInfoPanel panel, ItemStack stack, int range)
    {
        NBTTagCompound nbtTagCompound = ItemStackUtils.getTagCompound(stack);
        int[] coordinates = getCoordinates(stack);
        Map<String, Integer> updateSet = new HashMap<String, Integer>();
        if(coordinates == null)
        {
            setField("state", STATE_INVALID_CARD, nbtTagCompound, panel, updateSet);
            return;
        }
        int dx = coordinates[0] - panel.xCoord;
        int dy = coordinates[1] - panel.yCoord;
        int dz = coordinates[2] - panel.zCoord;
        if(Math.abs(dx) > range || 
            Math.abs(dy) > range || 
            Math.abs(dz) > range)
        {
            setField("state", STATE_OUT_OF_RANGE, nbtTagCompound, panel, updateSet);
        }
        else
        {
            IReactor reactor = NuclearHelper.getReactorAt(panel.worldObj, 
                    coordinates[0], coordinates[1], coordinates[2]);
            if(reactor != null)
            {
                setField("state", STATE_OK, nbtTagCompound, panel, updateSet);
                setField("heat", reactor.getHeat(), nbtTagCompound, panel, updateSet);
                setField("maxHeat", reactor.getMaxHeat(), nbtTagCompound, panel, updateSet);
                setField("reactorPowered", NuclearHelper.isProducing(reactor), nbtTagCompound, panel, updateSet);
                setField("output", reactor.getOutput(), nbtTagCompound, panel, updateSet);

                IInventory inventory = (IInventory)reactor; 
                int slotCount = inventory.getSizeInventory();
                int timeLeft = 0;
                int uraniumId = Items.getItem("uraniumCell").itemID;
                for(int i = 0; i < slotCount; i++)
                {
                    ItemStack rStack = inventory.getStackInSlot(i);
                    if(rStack!=null && rStack.itemID == uraniumId)
                    {
                        timeLeft = Math.max(timeLeft, rStack.getMaxDamage() - rStack.getItemDamage());
                    }
                }
                setField("timeLeft", timeLeft, nbtTagCompound, panel, updateSet);
            }
            else
            {
                setField("state", STATE_NO_TARGET, nbtTagCompound, panel, updateSet);
            }
        }
        if(!updateSet.isEmpty())
            mod_IC2NuclearControl.setSensorCardField(panel, updateSet);
    }

    @Override
    public int getCardType()
    {
        return CARD_TYPE;
    }

    @Override
    abstract public void networkUpdate(String fieldName, int value, ItemStack stack);

    @Override
    abstract public List<PanelString> getStringData(int displaySettings, ItemStack itemStack, boolean showLabels);

    @Override
    abstract public List<PanelSetting> getSettingsList();
}
