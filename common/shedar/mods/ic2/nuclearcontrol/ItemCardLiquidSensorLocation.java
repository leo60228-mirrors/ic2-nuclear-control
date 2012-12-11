package shedar.mods.ic2.nuclearcontrol;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.TileEntity;
import net.minecraftforge.liquids.ILiquidTank;
import shedar.mods.ic2.nuclearcontrol.api.CardState;
import shedar.mods.ic2.nuclearcontrol.api.ICardWrapper;
import shedar.mods.ic2.nuclearcontrol.api.IRemoteSensor;
import shedar.mods.ic2.nuclearcontrol.api.PanelSetting;
import shedar.mods.ic2.nuclearcontrol.api.PanelString;
import shedar.mods.ic2.nuclearcontrol.panel.CardWrapperImpl;
import shedar.mods.ic2.nuclearcontrol.utils.StringUtils;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ItemCardLiquidSensorLocation extends ItemCardBase implements IRemoteSensor
{
    private static final String HINT_TEMPLATE = "x: %d, y: %d, z: %d";
    
    public static final int DISPLAY_LIQUID_NAME= 1;
    public static final int DISPLAY_AMOUNT = 2;
    public static final int DISPLAY_FREE = 4;
    public static final int DISPLAY_CAPACITY = 8;
    public static final int DISPLAY_PERCENTAGE = 16;
    
    public static final UUID CARD_TYPE = UUID.fromString("210dc1f0-118c-48ee-9d08-42bfbee1ea15");

    public ItemCardLiquidSensorLocation(int i, int iconIndex)
    {
        super(i, iconIndex);
    }
    
    @Override
    public CardState update(TileEntity panel, ICardWrapper card, int range)
    {
        ChunkCoordinates target = card.getTarget();
        ILiquidTank storage = LiquidStorageHelper.getStorageAt(panel.worldObj, target.posX, target.posY, target.posZ);
        if(storage != null)
        {
            int capacity = storage.getCapacity();
            int amount = 0;
            String name = "";
            if(storage.getLiquid()!=null)
            {
                amount = storage.getLiquid().amount;
                if(storage.getLiquid().itemID!=0 && amount > 0)
                {
                    ItemStack stack = storage.getLiquid().asItemStack();
                    name = stack.getItem().getItemDisplayName(stack);
                }
            }
            card.setInt("capacity", capacity);
            card.setInt("amount", amount);
            card.setString("liquid", name);
            return CardState.OK;
        }
        else
        {
            return CardState.NO_TARGET;
        }
    }

    @Override
    public UUID getCardType()
    {
        return CARD_TYPE;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean advanced) 
    {
        CardWrapperImpl helper = new CardWrapperImpl(itemStack);
        ChunkCoordinates target = helper.getTarget();
        if(target!=null)
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
    public List<PanelString> getStringData(int displaySettings, ICardWrapper card, boolean showLabels)
    {
        List<PanelString> result = new LinkedList<PanelString>();
        PanelString line;

        int capacity =  card.getInt("capacity");
        int amount =  card.getInt("amount");
        String liquid =  card.getString("liquid");
        if(liquid == null || "".equals(liquid))
            liquid = LanguageRegistry.instance().getStringLocalization("msg.nc.None");

        if((displaySettings & DISPLAY_LIQUID_NAME) > 0)
        {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelLiquidName", liquid, showLabels); 
            result.add(line);
        }
        if((displaySettings & DISPLAY_AMOUNT) > 0)
        {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelLiquidAmount", amount, showLabels); 
            result.add(line);
        }
        if((displaySettings & DISPLAY_FREE) > 0)
        {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelLiquidFree", capacity - amount, showLabels); 
            result.add(line);
        }
        if((displaySettings & DISPLAY_CAPACITY) > 0)
        {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelLiquidCapacity", capacity, showLabels); 
            result.add(line);
        }
        if((displaySettings & DISPLAY_PERCENTAGE) > 0)
        {
            line = new PanelString();
            line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelLiquidPercentage", capacity==0? 100:(amount*100/capacity), showLabels); 
            result.add(line);
        }
        return result;
    }

    @Override
    public List<PanelSetting> getSettingsList()
    {
        List<PanelSetting> result = new ArrayList<PanelSetting>(3);
        result.add(new PanelSetting(StringTranslate.getInstance().translateKey("msg.nc.cbInfoPanelLiquidName"), DISPLAY_LIQUID_NAME, CARD_TYPE));
        result.add(new PanelSetting(StringTranslate.getInstance().translateKey("msg.nc.cbInfoPanelLiquidAmount"), DISPLAY_AMOUNT, CARD_TYPE));
        result.add(new PanelSetting(StringTranslate.getInstance().translateKey("msg.nc.cbInfoPanelLiquidFree"), DISPLAY_FREE, CARD_TYPE));
        result.add(new PanelSetting(StringTranslate.getInstance().translateKey("msg.nc.cbInfoPanelLiquidCapacity"), DISPLAY_CAPACITY, CARD_TYPE));
        result.add(new PanelSetting(StringTranslate.getInstance().translateKey("msg.nc.cbInfoPanelLiquidPercentage"), DISPLAY_PERCENTAGE, CARD_TYPE));
        return result;
    }

}
