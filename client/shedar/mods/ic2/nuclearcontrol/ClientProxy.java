package shedar.mods.ic2.nuclearcontrol;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import shedar.mods.ic2.nuclearcontrol.panel.IPanelDataSource;

import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.SoundPool;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.Player;

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
    public void onWorldLoad(WorldEvent.Load event)
    {
        IC2NuclearControl.instance.screenManager = new ScreenManager();
    }

    @ForgeSubscribe
    public void importSound(SoundLoadEvent event)
    {
        File soundDir =  new File(new File(Minecraft.getMinecraftDir(), "resources"), "newsound");
        File ncSoundDir = new File(soundDir, "ic2nuclearControl");
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
        SoundPool pool = event.manager.soundPoolSounds;
        
        boolean isGetRandomSound = pool.isGetRandomSound;
        pool.isGetRandomSound = false;
        for(File alarmItem: alarms)
        {
            String name = alarmItem.getName();
            name = name.substring(6, name.length()-4);
            ncInstance.availableAlarms.add(name);
            event.manager.addSound("ic2nuclearControl/alarm-"+name+".ogg", alarmItem);
        }
        pool.isGetRandomSound = isGetRandomSound;
        ncInstance.serverAllowedAlarms = new ArrayList<String>();
    }    
    
    @Override
    public void registerTileEntities()
    {
        TileEntityIC2ThermoRenderer renderThermalMonitor = new TileEntityIC2ThermoRenderer();
        TileEntityRemoteThermoRenderer renderRemoteThermo = new TileEntityRemoteThermoRenderer();
        TileEntityInfoPanelRenderer renderInfoPanel = new TileEntityInfoPanelRenderer(); 
        
        ModLoader.registerTileEntity(shedar.mods.ic2.nuclearcontrol.TileEntityIC2Thermo.class, "IC2Thermo", renderThermalMonitor);
        ModLoader.registerTileEntity(shedar.mods.ic2.nuclearcontrol.TileEntityHowlerAlarm.class, "IC2HowlerAlarm");
        ModLoader.registerTileEntity(shedar.mods.ic2.nuclearcontrol.TileEntityIndustrialAlarm.class, "IC2IndustrialAlarm");
        ModLoader.registerTileEntity(shedar.mods.ic2.nuclearcontrol.TileEntityRemoteThermo.class, "IC2RemoteThermo", renderRemoteThermo);
        ModLoader.registerTileEntity(shedar.mods.ic2.nuclearcontrol.TileEntityInfoPanel.class, "IC2NCInfoPanel", renderInfoPanel);
        ModLoader.registerTileEntity(shedar.mods.ic2.nuclearcontrol.TileEntityInfoPanelExtender.class, "IC2NCInfoPanelExtender");
        ModLoader.registerTileEntity(shedar.mods.ic2.nuclearcontrol.TileEntityEnergyCounter.class, "IC2NCEnergyCounter");
        int modelId = RenderingRegistry.getNextAvailableRenderId();
        IC2NuclearControl.instance.modelId = modelId;
        RenderingRegistry.registerBlockHandler(new MainBlockRenderer(modelId));
    }
    
    @Override
    public void onPacketData(NetworkManager manager, Packet250CustomPayload packet, Player player)
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
                    message = StringTranslate.getInstance().translateKey("msg.nc."+chunks[0]);
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
                    ItemStack stack = panel.getStackInSlot(TileEntityInfoPanel.SLOT_CARD);
                    if(stack == null || !(stack.getItem() instanceof IPanelDataSource))
                    {
                        return;
                    }
                    IPanelDataSource card = (IPanelDataSource)stack.getItem();
                    int fieldCount =  dat.readShort();
                    for(int i=0; i<fieldCount; i++)
                    {
                        String name = dat.readUTF();
                        int value = dat.readInt();
                        card.networkUpdate(name, value, stack);
                    }
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
                    ItemStack itemStack = panel.getStackInSlot(TileEntityInfoPanel.SLOT_CARD);
                    if(itemStack == null || !(itemStack.getItem() instanceof IPanelDataSource))
                    {
                        return;
                    }
                    ((IPanelDataSource)itemStack.getItem()).setTitle(itemStack, dat.readUTF());
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
    
                default:
                    FMLLog.warning("%sUnknown packet type: %d", IC2NuclearControl.LOG_PREFIX, packetType);
                    break;
            }
        }        
    }
    
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity= world.getBlockTileEntity(x, y, z);
        switch (ID)
        {
            case BlockNuclearControlMain.DAMAGE_THERMAL_MONITOR:
                return new GuiIC2Thermo((TileEntityIC2Thermo)tileEntity);
            case BlockNuclearControlMain.DAMAGE_HOWLER_ALARM:
                return new GuiHowlerAlarm((TileEntityHowlerAlarm)tileEntity);
            case BlockNuclearControlMain.DAMAGE_INDUSTRIAL_ALARM:
                return new GuiIndustrialAlarm((TileEntityHowlerAlarm)tileEntity);
            case BlockNuclearControlMain.DAMAGE_REMOTE_THERMO:
                ContainerRemoteThermo container = new ContainerRemoteThermo(player, (TileEntityRemoteThermo)tileEntity);
                return new GuiRemoteThermo(container);
            case BlockNuclearControlMain.DAMAGE_INFO_PANEL:
                ContainerInfoPanel containerPanel = new ContainerInfoPanel(player, (TileEntityInfoPanel)tileEntity);
                return new GuiInfoPanel(containerPanel);
            case BlockNuclearControlMain.DAMAGE_ENERGY_COUNTER:
                ContainerEnergyCounter containerCounter = new ContainerEnergyCounter(player, (TileEntityEnergyCounter)tileEntity);
                return new GuiEnergyCounter(containerCounter);
            default:
                return null;
        }
    }    

}
