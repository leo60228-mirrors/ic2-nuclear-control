package shedar.mods.ic2.nuclearcontrol;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import shedar.mods.ic2.nuclearcontrol.api.IPanelDataSource;
import shedar.mods.ic2.nuclearcontrol.panel.CardWrapperImpl;
import shedar.mods.ic2.nuclearcontrol.renderers.MainBlockRenderer;
import shedar.mods.ic2.nuclearcontrol.renderers.TileEntityIC2ThermoRenderer;
import shedar.mods.ic2.nuclearcontrol.renderers.TileEntityInfoPanelRenderer;
import shedar.mods.ic2.nuclearcontrol.renderers.TileEntityRemoteThermoRenderer;
import shedar.mods.ic2.nuclearcontrol.subblocks.Subblock;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityAverageCounter;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityEnergyCounter;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityInfoPanel;
import shedar.mods.ic2.nuclearcontrol.utils.NuclearNetworkHelper;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ClientProxy extends CommonProxy
{
    @Override
    public String playAlarm(double x, double y, double z, String name, float volume)
    {
        return SoundHelper.playAlarm(x, y, z, name, volume);
    }
    
    @Override
    public void stopAlarm(String soundId)
    {
        SoundHelper.stopAlarm(soundId);
    }
    
    @Override
    public boolean isPlaying(String soundId)
    {
        return SoundHelper.isPlaying(soundId);
    }
    
    @ForgeSubscribe
    public void importSound(SoundLoadEvent event)
    {
        File soundDir =  new File(new File(Minecraft.getMinecraft().mcDataDir, "resources"), "newsound");
        File ncSoundDir = new File(soundDir, SoundHelper.SOUND_FOLDER);
        IC2NuclearControl ncInstance = IC2NuclearControl.instance; 
        if(!ncSoundDir.exists())
        {
            ncSoundDir.mkdirs();
        }
        for (String alarmName : IC2NuclearControl.builtInAlarms)
        {
            File alarmFile = new File(ncSoundDir, alarmName);
            if(!alarmFile.exists())
            {
                try
                {
                    if(!alarmFile.createNewFile() || !alarmFile.canWrite())
                        return;
                    InputStream input = getClass().getResourceAsStream("/sound/"+alarmName);
                    FileOutputStream output = new FileOutputStream(alarmFile);
                    byte[] buf = new byte[8192];
                    while (true) {
                      int length = input.read(buf);
                      if (length < 0)
                        break;
                      output.write(buf, 0, length);
                    }
                    input.close();
                    output.close();
                } 
                catch (IOException e)
                {
                    FMLLog.warning(IC2NuclearControl.LOG_PREFIX + "Can't import sound file");
                }
            }
        }
        
        File[] alarms = ncSoundDir.listFiles(new FilenameFilter() { 
            public boolean accept(File dir, String filename)
            { return filename.endsWith(".ogg") && filename.startsWith("alarm-"); }
        });
        
        ncInstance.availableAlarms = new ArrayList<String>();

        for(File alarmItem: alarms)
        {
            String name = alarmItem.getName();
            name = name.substring(6, name.length()-4);
            ncInstance.availableAlarms.add(name);
            event.manager.addSound(SoundHelper.SOUND_FOLDER+"/alarm-"+name+".ogg");
        }
        ncInstance.serverAllowedAlarms = new ArrayList<String>();
    }    
    
    @Override
    public void registerTileEntities()
    {
        TileEntityIC2ThermoRenderer renderThermalMonitor = new TileEntityIC2ThermoRenderer();
        TileEntityRemoteThermoRenderer renderRemoteThermo = new TileEntityRemoteThermoRenderer();
        TileEntityInfoPanelRenderer renderInfoPanel = new TileEntityInfoPanelRenderer(); 
        
        ClientRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityIC2Thermo.class, "IC2Thermo", renderThermalMonitor);
        GameRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityHowlerAlarm.class, "IC2HowlerAlarm");
        GameRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityIndustrialAlarm.class, "IC2IndustrialAlarm");
        ClientRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityRemoteThermo.class, "IC2RemoteThermo", renderRemoteThermo);
        ClientRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityInfoPanel.class, "IC2NCInfoPanel", renderInfoPanel);
        ClientRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityInfoPanelExtender.class, "IC2NCInfoPanelExtender", renderInfoPanel);
        ClientRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityAdvancedInfoPanel.class, "IC2NCAdvancedInfoPanel", renderInfoPanel);
        ClientRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityAdvancedInfoPanelExtender.class, "IC2NCAdvancedInfoPanelExtender", renderInfoPanel);
        GameRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityEnergyCounter.class, "IC2NCEnergyCounter");
        GameRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityAverageCounter.class, "IC2NCAverageCounter");
        GameRegistry.registerTileEntity(shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityRangeTrigger.class, "IC2NCRangeTrigger");
        int modelId = RenderingRegistry.getNextAvailableRenderId();
        IC2NuclearControl.instance.modelId = modelId;
        RenderingRegistry.registerBlockHandler(new MainBlockRenderer(modelId));
    }
    
    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        super.onPacketData(manager, packet, player);
        if (!(player instanceof EntityPlayerMP))
        {
            World world;
            int x,y,z;
            TileEntity ent;
            TileEntityInfoPanel panel;
            ByteArrayDataInput dat = ByteStreams.newDataInput(packet.data);
            short packetType = dat.readShort();
            switch (packetType)
            {
                case PacketHandler.PACKET_CHAT:
                    String message = dat.readUTF();
                    String[] chunks = message.split(":");
                    message = LanguageRegistry.instance().getStringLocalization("msg.nc."+chunks[0]);
                    if(chunks.length > 1)
                    {
                        List<String> list = new ArrayList<String>(Arrays.asList(chunks));
                        list.remove(0);
                        chunks = list.toArray(chunks);
                        message = String.format(message, (Object[])chunks);
                    }
                    ((EntityPlayer)player).addChatMessage(message);
                    break;
                case PacketHandler.PACKET_ALARM:
                    IC2NuclearControl.instance.maxAlarmRange = dat.readInt();
                    IC2NuclearControl.instance.serverAllowedAlarms = new ArrayList<String>(Arrays.asList(dat.readUTF().split(",")));
                    break;
                case PacketHandler.PACKET_SENSOR:
                    world = FMLClientHandler.instance().getClient().theWorld;
                    x = dat.readInt();
                    y = dat.readInt();
                    z = dat.readInt();
                    ent = world.getBlockTileEntity(x, y, z);
                    if(ent == null || !(ent instanceof TileEntityInfoPanel))
                    {
                        return;
                    }
                    panel = (TileEntityInfoPanel)ent;
                    byte slot =  dat.readByte();
                    ItemStack stack = panel.getStackInSlot(slot);
                    if(stack == null || !(stack.getItem() instanceof IPanelDataSource))
                    {
                        return;
                    }
                    CardWrapperImpl helper = new CardWrapperImpl(stack, slot);
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
                        case NuclearNetworkHelper.FIELD_TAG:
                            NBTTagCompound tag;
                            try
                            {
                                tag = (NBTTagCompound)NBTTagCompound.readNamedTag(dat);
                                helper.setTag(name, tag);
                            } catch (IOException e)
                            {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            break;
                        case NuclearNetworkHelper.FIELD_NULL:
                            helper.clearField(name);
                            break;
                        default:
                            FMLLog.warning("Invalid field type: %d", type);
                            break;
                        }
                    }
                    panel.resetCardData();
                    break;
                case PacketHandler.PACKET_SENSOR_TITLE:
                    world = FMLClientHandler.instance().getClient().theWorld;
                    x = dat.readInt();
                    y = dat.readInt();
                    z = dat.readInt();
                    ent = world.getBlockTileEntity(x, y, z);
                    if(ent == null || !(ent instanceof TileEntityInfoPanel))
                    {
                        return;
                    }
                    panel = (TileEntityInfoPanel)ent;
                    slot = dat.readByte();
                    ItemStack itemStack = panel.getStackInSlot(slot);
                    if(itemStack == null || !(itemStack.getItem() instanceof IPanelDataSource))
                    {
                        return;
                    }
                    new CardWrapperImpl(itemStack, slot).setTitle(dat.readUTF());
                    panel.resetCardData();
                    break;
                case PacketHandler.PACKET_ECOUNTER:
                    world = FMLClientHandler.instance().getClient().theWorld;
                    x = dat.readInt();
                    y = dat.readInt();
                    z = dat.readInt();
                    ent = world.getBlockTileEntity(x, y, z);
                    if(ent == null || !(ent instanceof TileEntityEnergyCounter))
                    {
                        return;
                    }
                    TileEntityEnergyCounter counter = (TileEntityEnergyCounter)ent;
                    counter.counter = dat.readLong();
                    break;
                case PacketHandler.PACKET_ACOUNTER:
                    world = FMLClientHandler.instance().getClient().theWorld;
                    x = dat.readInt();
                    y = dat.readInt();
                    z = dat.readInt();
                    ent = world.getBlockTileEntity(x, y, z);
                    if(ent == null || !(ent instanceof TileEntityAverageCounter))
                    {
                        return;
                    }
                    TileEntityAverageCounter avgCounter = (TileEntityAverageCounter)ent;
                    avgCounter.setClientAverage(dat.readInt());
                    break;
                case PacketHandler.PACKET_DISP_SETTINGS_ALL:
                    world = FMLClientHandler.instance().getClient().theWorld;
                    x = dat.readInt();
                    y = dat.readInt();
                    z = dat.readInt();
                    ent = world.getBlockTileEntity(x, y, z);
                    if(ent == null || !(ent instanceof TileEntityInfoPanel))
                    {
                        return;
                    }
                    panel = (TileEntityInfoPanel)ent;
                    byte count = dat.readByte();
                    for(int i=0; i<count; i++)
                    {
                        slot = dat.readByte();
                        short dCount = dat.readShort();
                        Map<UUID, Integer> settings = panel.getDisplaySettingsForSlot(slot);
                        for(int j=0; j<dCount; j++)
                        {
                            long most = dat.readLong();
                            long least = dat.readLong();
                            settings.put(new UUID(most, least), dat.readInt()); 
                        }
                    }
                    panel.resetCardData();
                    break;
                case PacketHandler.PACKET_DISP_SETTINGS_UPDATE:
                    world = FMLClientHandler.instance().getClient().theWorld;
                    x = dat.readInt();
                    y = dat.readInt();
                    z = dat.readInt();
                    slot = dat.readByte();
                    ent = world.getBlockTileEntity(x, y, z);
                    if(ent == null || !(ent instanceof TileEntityInfoPanel))
                    {
                        return;
                    }
                    long most = dat.readLong();
                    long least = dat.readLong();
                    panel = (TileEntityInfoPanel)ent;
                    int settings = dat.readInt();
                    panel.getDisplaySettingsForSlot(slot).put(new UUID(most, least), settings);
                    panel.resetCardData();
                    break;
                default:
                    FMLLog.warning("%sUnknown packet type: %d", IC2NuclearControl.LOG_PREFIX, packetType);
                    break;
            }
        }        
    }
    
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        Subblock block = IC2NuclearControl.instance.blockNuclearControlMain.getSubblock(ID);
        if(block == null)
            return null;
        return block.getClientGuiElement(tileEntity, player);
    }    

}
