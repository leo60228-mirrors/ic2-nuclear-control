package shedar.mods.ic2.nuclearcontrol;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import shedar.mods.ic2.nuclearcontrol.api.IPanelDataSource;
import shedar.mods.ic2.nuclearcontrol.containers.ContainerAverageCounter;
import shedar.mods.ic2.nuclearcontrol.containers.ContainerEmpty;
import shedar.mods.ic2.nuclearcontrol.containers.ContainerEnergyCounter;
import shedar.mods.ic2.nuclearcontrol.containers.ContainerInfoPanel;
import shedar.mods.ic2.nuclearcontrol.containers.ContainerRangeTrigger;
import shedar.mods.ic2.nuclearcontrol.containers.ContainerRemoteThermo;
import shedar.mods.ic2.nuclearcontrol.panel.CardWrapperImpl;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityAverageCounter;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityEnergyCounter;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityHowlerAlarm;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityInfoPanel;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityRangeTrigger;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityRemoteThermo;
import shedar.mods.ic2.nuclearcontrol.utils.NuclearNetworkHelper;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy implements IGuiHandler
{
    public boolean isPlaying(String soundId)
    {
        return false;
    }
    
    public void stopAlarm(String soundId)
    {}
    
    public String playAlarm(double x, double y, double z, String name, float volume)
    {
        return null;
    }
    
    public void registerTileEntities()
    {
        GameRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityIC2Thermo.class, "IC2Thermo");
        GameRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityHowlerAlarm.class, "IC2HowlerAlarm");
        GameRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityIndustrialAlarm.class, "IC2IndustrialAlarm");
        GameRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityRemoteThermo.class, "IC2RemoteThermo");
        GameRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityInfoPanel.class, "IC2NCInfoPanel");
        GameRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityInfoPanelExtender.class, "IC2NCInfoPanelExtender");
        GameRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityEnergyCounter.class, "IC2NCEnergyCounter");
        GameRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityAverageCounter.class, "IC2NCAverageCounter");
        GameRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityRangeTrigger.class, "IC2NCRangeTrigger");
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        switch (ID)
        {
            case BlockNuclearControlMain.DAMAGE_REMOTE_THERMO:
                return new ContainerRemoteThermo(player, (TileEntityRemoteThermo)tileEntity);
            case BlockNuclearControlMain.DAMAGE_INFO_PANEL:
                return new ContainerInfoPanel(player, (TileEntityInfoPanel)tileEntity);
            case BlockNuclearControlMain.DAMAGE_ENERGY_COUNTER:
                return new ContainerEnergyCounter(player, (TileEntityEnergyCounter)tileEntity);
            case BlockNuclearControlMain.DAMAGE_AVERAGE_COUNTER:
                return new ContainerAverageCounter(player, (TileEntityAverageCounter)tileEntity);
            case BlockNuclearControlMain.DAMAGE_RANGE_TRIGGER:
                return new ContainerRangeTrigger(player, (TileEntityRangeTrigger)tileEntity);
            case BlockNuclearControlMain.DAMAGE_HOWLER_ALARM:
            case BlockNuclearControlMain.DAMAGE_INDUSTRIAL_ALARM:
            case BlockNuclearControlMain.DAMAGE_THERMAL_MONITOR:
                return new ContainerEmpty(tileEntity);
            default:
                return null;
        }
    }
    
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        if (player instanceof EntityPlayerMP)//server
        {
            // used to set sound alarm from client's GUI
            ByteArrayDataInput dat = ByteStreams.newDataInput(packet.data);
            byte packetId = dat.readByte();
            int x = dat.readInt();
            int y = dat.readInt();
            int z = dat.readInt();
            switch (packetId)
            {
            case PacketHandler.PACKET_CLIENT_SOUND:
                String soundName = dat.readUTF();
                TileEntity tileEntity = ((EntityPlayerMP) player).worldObj.getBlockTileEntity(x, y, z);
                if (tileEntity instanceof TileEntityHowlerAlarm)
                {
                    ((TileEntityHowlerAlarm) tileEntity).setSoundName(soundName);
                } 
                else if (tileEntity instanceof TileEntityInfoPanel)
                {
                    ItemStack stack = ((TileEntityInfoPanel) tileEntity).getStackInSlot(TileEntityInfoPanel.SLOT_CARD);
                    if (stack == null || !(stack.getItem() instanceof IPanelDataSource))
                    {
                        return;
                    }
                    new CardWrapperImpl(stack).setTitle(soundName);
                    NuclearNetworkHelper.setSensorCardTitle((TileEntityInfoPanel)tileEntity, soundName);
                }
                break;
            case PacketHandler.PACKET_CLIENT_REQUEST:
                NuclearNetworkHelper.sendDisplaySettingsToPlayer(x, y, z, (EntityPlayerMP)player);
                break;
            case PacketHandler.PACKET_CLIENT_RANGE_TRIGGER:
                long value = dat.readLong();
                boolean isEnd = dat.readBoolean();
                tileEntity = ((EntityPlayerMP) player).worldObj.getBlockTileEntity(x, y, z);
                if (tileEntity instanceof TileEntityRangeTrigger)
                {
                    if(isEnd)
                    {
                        ((TileEntityRangeTrigger)tileEntity).setLevelEnd(value);
                    }
                    else
                    {
                        ((TileEntityRangeTrigger)tileEntity).setLevelStart(value);
                    }
                }
                break;
            case PacketHandler.PACKET_CLIENT_COLOR:
                int colors= dat.readInt();
                tileEntity = ((EntityPlayerMP) player).worldObj.getBlockTileEntity(x, y, z);
                if (tileEntity instanceof TileEntityInfoPanel)
                {
                    int back = colors >> 4;
                    int text = colors & 0xf;
                    ((TileEntityInfoPanel)tileEntity).setColorBackground(back);
                    ((TileEntityInfoPanel)tileEntity).setColorText(text);
                }
                break;
            case PacketHandler.PACKET_CLIENT_SENSOR:
                tileEntity = ((EntityPlayerMP) player).worldObj.getBlockTileEntity(x, y, z);
                if (tileEntity instanceof TileEntityInfoPanel)
                {
                    TileEntityInfoPanel panel = (TileEntityInfoPanel) tileEntity; 
                    ItemStack stack = panel.getStackInSlot(TileEntityInfoPanel.SLOT_CARD);
                    if (stack == null || !(stack.getItem() instanceof IPanelDataSource))
                    {
                        return;
                    }
                    String className = dat.readUTF();
                    if(!stack.getItem().getClass().getName().equals(className))
                    {
                        System.out.println(className+"!="+stack.getItem().getClass().getName());
                        return;
                    }
                    CardWrapperImpl helper = new CardWrapperImpl(stack);
                    int fieldCount =  dat.readShort();
                    for(int i=0; i<fieldCount; i++)
                    {
                        String name = dat.readUTF();
                        byte type = dat.readByte();
                        switch (type)
                        {
                        case NuclearNetworkHelper.FIELD_INT:
                            helper.setInt(name, dat.readInt());
                            break;
                        case NuclearNetworkHelper.FIELD_BOOLEAN:
                            helper.setBoolean(name, dat.readBoolean());
                            break;
                        case NuclearNetworkHelper.FIELD_LONG:
                            helper.setLong(name, dat.readLong());
                            break;
                        case NuclearNetworkHelper.FIELD_STRING:
                            helper.setString(name, dat.readUTF());
                            break;
                        default:
                            FMLLog.warning("Invalid field type: %d", type);
                            break;
                        }
                    }
                    helper.commit(panel);
                }
                break;

            default:
                break;
            }
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        // null on server
        return null;
    }

}
