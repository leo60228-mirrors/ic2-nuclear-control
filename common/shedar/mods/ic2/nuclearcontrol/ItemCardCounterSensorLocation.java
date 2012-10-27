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
import shedar.mods.ic2.nuclearcontrol.api.CardState;
import shedar.mods.ic2.nuclearcontrol.api.ICardWrapper;
import shedar.mods.ic2.nuclearcontrol.api.IRemoteSensor;
import shedar.mods.ic2.nuclearcontrol.api.PanelSetting;
import shedar.mods.ic2.nuclearcontrol.api.PanelString;
import shedar.mods.ic2.nuclearcontrol.panel.CardWrapperImpl;
import shedar.mods.ic2.nuclearcontrol.utils.StringUtils;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class ItemCardCounterSensorLocation extends ItemCardBase implements IRemoteSensor
{
    private static final String HINT_TEMPLATE = "x: %d, y: %d, z: %d";

    public static final int DISPLAY_ENERGY = 1;
    public static final UUID CARD_TYPE = new UUID(0, 4);;

    public ItemCardCounterSensorLocation(int i, int iconIndex)
    {
        super(i, iconIndex);
    }
    
    @Override
    public CardState update(TileEntity panel, ICardWrapper card, int range)
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
    public UUID getCardType()
    {
        return CARD_TYPE;
    }
    
    @Override
    public List<PanelString> getStringData(int displaySettings, ICardWrapper card, boolean showLabels)
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
                line.textLeft = StringUtils.getFormatted("msg.nc.InfoPanelEnergy", energy, showLabels); 
                result.add(line);
            }
        }
        return result;
    }

    @Override
    public List<PanelSetting> getSettingsList()
    {
        List<PanelSetting> result = new ArrayList<PanelSetting>(3);
        result.add(new PanelSetting(StringTranslate.getInstance().translateKey("msg.nc.cbInfoPanelEnergyCurrent"), DISPLAY_ENERGY, CARD_TYPE));
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
}
