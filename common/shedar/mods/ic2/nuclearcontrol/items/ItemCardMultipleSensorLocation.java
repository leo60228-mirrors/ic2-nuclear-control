package shedar.mods.ic2.nuclearcontrol.items;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.StringTranslate;
import net.minecraftforge.liquids.ILiquidTank;
import shedar.mods.ic2.nuclearcontrol.api.CardState;
import shedar.mods.ic2.nuclearcontrol.api.ICardWrapper;
import shedar.mods.ic2.nuclearcontrol.api.IPanelMultiCard;
import shedar.mods.ic2.nuclearcontrol.api.IRemoteSensor;
import shedar.mods.ic2.nuclearcontrol.api.PanelSetting;
import shedar.mods.ic2.nuclearcontrol.api.PanelString;
import shedar.mods.ic2.nuclearcontrol.panel.CardWrapperImpl;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityAverageCounter;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityEnergyCounter;
import shedar.mods.ic2.nuclearcontrol.utils.LiquidStorageHelper;
import shedar.mods.ic2.nuclearcontrol.utils.StringUtils;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCardMultipleSensorLocation extends ItemCardBase implements IRemoteSensor, IPanelMultiCard
{
    private static final String HINT_TEMPLATE = "x: %d, y: %d, z: %d";

    public static final int DISPLAY_ENERGY = 1;

    public static final int DISPLAY_LIQUID_NAME= 1;
    public static final int DISPLAY_LIQUID_AMOUNT = 2;
    public static final int DISPLAY_LIQUID_FREE = 4;
    public static final int DISPLAY_LIQUID_CAPACITY = 8;
    public static final int DISPLAY_LIQUID_PERCENTAGE = 16;
    
    
    private static final UUID CARD_TYPE_COUNTER = new UUID(0, 4);;
    private static final UUID CARD_TYPE_LIQUID = UUID.fromString("210dc1f0-118c-48ee-9d08-42bfbee1ea15");

    public ItemCardMultipleSensorLocation(int i)
    {
        super(i, 0);
    }
    
    @Override
    public String getItemNameIS(ItemStack stack) 
    {
        int damage = stack.getItemDamage();
        switch (damage)
        {
        case ItemKitMultipleSensor.TYPE_COUNTER:
            return "item.ItemCounterSensorLocationCard";
        case ItemKitMultipleSensor.TYPE_LIQUID:
            return "item.ItemLiquidSensorLocationCard";
        }
        return "";
    }

    
    @Override
    public CardState update(TileEntity panel, ICardWrapper card, int range)
    {
        int damage = card.getItemStack().getItemDamage();
        switch (damage)
        {
        case ItemKitMultipleSensor.TYPE_COUNTER:
            return updateCounter(panel, card, range);
        case ItemKitMultipleSensor.TYPE_LIQUID:
            return updateLiquid(panel, card, range);
        }
        return CardState.INVALID_CARD;
    }
    
    public CardState updateLiquid(TileEntity panel, ICardWrapper card, int range)
    {
        ChunkCoordinates target = card.getTarget();
        ILiquidTank storage = LiquidStorageHelper.getStorageAt(panel.worldObj, target.posX, target.posY, target.posZ);
        if(storage != null)
        {
            int capacity = storage.getCapacity();
            int amount = 0;
            int liquidId = 0;
            int liquidMeta = 0;
            if(storage.getLiquid()!=null)
            {
                amount = storage.getLiquid().amount;
                if(storage.getLiquid().itemID!=0 && amount > 0)
                {
                    liquidId = storage.getLiquid().itemID;
                    liquidMeta = storage.getLiquid().itemMeta;
                }
            }
            card.setInt("capacity", capacity);
            card.setInt("amount", amount);
            card.setInt("liquidId", liquidId);
            card.setInt("liquidMeta", liquidMeta);
            return CardState.OK;
        }
        else
        {
            return CardState.NO_TARGET;
        }
    }

    public CardState updateCounter(TileEntity panel, ICardWrapper card, int range)
    {
        ChunkCoordinates target = card.getTarget();
        TileEntity tileEntity = panel.worldObj.getBlockTileEntity(target.posX, target.posY, target.posZ);
        if(tileEntity != null && tileEntity instanceof TileEntityEnergyCounter)
        {
            TileEntityEnergyCounter counter  = (TileEntityEnergyCounter)tileEntity;
            card.setLong("energy", counter.counter);
            return CardState.OK;
        }
        else if(tileEntity != null && tileEntity instanceof TileEntityAverageCounter)
        {
            TileEntityAverageCounter avgCounter  = (TileEntityAverageCounter)tileEntity;
            card.setInt("average", avgCounter.getClientAverage());
            return CardState.OK;
        }
        else
        {
            return CardState.NO_TARGET;
        }
    }

    @Override
    public List<PanelSetting> getSettingsList(ICardWrapper card)
    {
        int damage = card.getItemStack().getItemDamage();
        switch (damage)
        {
        case ItemKitMultipleSensor.TYPE_COUNTER:
            return getSettingsListCounter();
        case ItemKitMultipleSensor.TYPE_LIQUID:
            return getSettingsListLiquid();
        }
        return null;
    }

    @Override
    public UUID getCardType(ICardWrapper card)
    {
        int damage = card.getItemStack().getItemDamage();
        switch (damage)
        {
        case ItemKitMultipleSensor.TYPE_COUNTER:
            return CARD_TYPE_COUNTER;
        case ItemKitMultipleSensor.TYPE_LIQUID:
            return CARD_TYPE_LIQUID;
        }
        return null;
    }
    
    @Override
    public List<PanelString> getStringData(int displaySettings, ICardWrapper card, boolean showLabels)
    {
        int damage = card.getItemStack().getItemDamage();
        switch (damage)
        {
        case ItemKitMultipleSensor.TYPE_COUNTER:
            return getStringDataCounter(displaySettings, card, showLabels);
        case ItemKitMultipleSensor.TYPE_LIQUID:
            return getStringDataLiquid(displaySettings, card, showLabels);
        }
        return null;
    }

    public List<PanelString> getStringDataLiquid(int displaySettings, ICardWrapper card, boolean showLabels)
    {
        List<PanelString> result = new LinkedList<PanelString>();
        PanelString line;

        int capacity =  card.getInt("capacity");
        int amount =  card.getInt("amount");

        if((displaySettings & DISPLAY_LIQUID_NAME) > 0)
        {
            int liquidId = card.getInt("liquidId");
            String name;
            if(liquidId == 0)
                name = LanguageRegistry.instance().getStringLocalization("msg.nc.None");
            else
                name = new ItemStack(liquidId, 1, card.getInt("liquidMeta")).getDisplayName();
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelLiquidName",  name, showLabels); 
            result.add(line);
        }
        if((displaySettings & DISPLAY_LIQUID_AMOUNT) > 0)
        {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelLiquidAmount", amount, showLabels); 
            result.add(line);
        }
        if((displaySettings & DISPLAY_LIQUID_FREE) > 0)
        {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelLiquidFree", capacity - amount, showLabels); 
            result.add(line);
        }
        if((displaySettings & DISPLAY_LIQUID_CAPACITY) > 0)
        {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelLiquidCapacity", capacity, showLabels); 
            result.add(line);
        }
        if((displaySettings & DISPLAY_LIQUID_PERCENTAGE) > 0)
        {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelLiquidPercentage", capacity==0? 100:(amount*100/capacity), showLabels); 
            result.add(line);
        }
        return result;
    }
    
    public List<PanelString> getStringDataCounter(int displaySettings, ICardWrapper card, boolean showLabels)
    {
        List<PanelString> result = new LinkedList<PanelString>();
        PanelString line;
        if(card.hasField("average"))
        {//average counter
            if((displaySettings & DISPLAY_ENERGY) > 0)
            {
                line = new PanelString();
                line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelOutput", card.getInt("average"), showLabels); 
                result.add(line);
            }
        }
        else
        {//energy counter
            if((displaySettings & DISPLAY_ENERGY) > 0)
            {
                long energy = card.getLong("energy");
                line = new PanelString();
                line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelEnergyCounter", energy, showLabels); 
                result.add(line);
            }
        }
        return result;
    }
    
    @Override
    public int getIconFromDamage(int damage)
    {
        switch (damage)
        {
        case ItemKitMultipleSensor.TYPE_COUNTER:
            return 52;
        case ItemKitMultipleSensor.TYPE_LIQUID:
            return 54;
        }
        return 0;
    }
    

    public List<PanelSetting> getSettingsListCounter()
    {
        List<PanelSetting> result = new ArrayList<PanelSetting>(3);
        result.add(new PanelSetting(StringTranslate.getInstance().translateKey("msg.nc.cbInfoPanelEnergyCurrent"), DISPLAY_ENERGY, CARD_TYPE_COUNTER));
        return result;
    }

    public List<PanelSetting> getSettingsListLiquid()
    {
        List<PanelSetting> result = new ArrayList<PanelSetting>(3);
        result.add(new PanelSetting(StringTranslate.getInstance().translateKey("msg.nc.cbInfoPanelLiquidName"), DISPLAY_LIQUID_NAME, CARD_TYPE_LIQUID));
        result.add(new PanelSetting(StringTranslate.getInstance().translateKey("msg.nc.cbInfoPanelLiquidAmount"), DISPLAY_LIQUID_AMOUNT, CARD_TYPE_LIQUID));
        result.add(new PanelSetting(StringTranslate.getInstance().translateKey("msg.nc.cbInfoPanelLiquidFree"), DISPLAY_LIQUID_FREE, CARD_TYPE_LIQUID));
        result.add(new PanelSetting(StringTranslate.getInstance().translateKey("msg.nc.cbInfoPanelLiquidCapacity"), DISPLAY_LIQUID_CAPACITY, CARD_TYPE_LIQUID));
        result.add(new PanelSetting(StringTranslate.getInstance().translateKey("msg.nc.cbInfoPanelLiquidPercentage"), DISPLAY_LIQUID_PERCENTAGE, CARD_TYPE_LIQUID));
        return result;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean advanced) 
    {
        CardWrapperImpl helper = new CardWrapperImpl(itemStack);
        ChunkCoordinates target = helper.getTarget();
        if(target != null)
        {
            String title = helper.getTitle();
            if(title != null && !title.isEmpty())
            {
                info.add(title);
            }
            String hint = String.format(HINT_TEMPLATE, target.posX, target.posY, target.posZ);
            info.add(hint);
        }
    }

    @Override
    public List<PanelSetting> getSettingsList()
    {
        return null;
    }

    @Override
    public UUID getCardType()
    {
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(par1, 1, ItemKitMultipleSensor.TYPE_COUNTER));
        par3List.add(new ItemStack(par1, 1, ItemKitMultipleSensor.TYPE_LIQUID));
    }

}