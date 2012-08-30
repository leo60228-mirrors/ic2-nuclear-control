package net.minecraft.src;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.src.forge.Configuration;
import net.minecraft.src.forge.ISaveEventHandler;
import net.minecraft.src.forge.MinecraftForge;
import net.minecraft.src.forge.MinecraftForgeClient;
import net.minecraft.src.forge.Property;
import net.minecraft.src.nuclearcontrol.BlockNuclearControlMain;
import net.minecraft.src.nuclearcontrol.ContainerEnergyCounter;
import net.minecraft.src.nuclearcontrol.ContainerInfoPanel;
import net.minecraft.src.nuclearcontrol.ContainerRemoteThermo;
import net.minecraft.src.nuclearcontrol.GuiEnergyCounter;
import net.minecraft.src.nuclearcontrol.GuiHowlerAlarm;
import net.minecraft.src.nuclearcontrol.GuiIC2Thermo;
import net.minecraft.src.nuclearcontrol.GuiIndustrialAlarm;
import net.minecraft.src.nuclearcontrol.GuiInfoPanel;
import net.minecraft.src.nuclearcontrol.GuiRemoteThermo;
import net.minecraft.src.nuclearcontrol.IC2NuclearControl;
import net.minecraft.src.nuclearcontrol.IRotation;
import net.minecraft.src.nuclearcontrol.MsgProcessor;
import net.minecraft.src.nuclearcontrol.ScreenManager;
import net.minecraft.src.nuclearcontrol.SoundHelper;
import net.minecraft.src.nuclearcontrol.TileEntityEnergyCounter;
import net.minecraft.src.nuclearcontrol.TileEntityHowlerAlarm;
import net.minecraft.src.nuclearcontrol.TileEntityIC2Thermo;
import net.minecraft.src.nuclearcontrol.TileEntityIC2ThermoRenderer;
import net.minecraft.src.nuclearcontrol.TileEntityInfoPanel;
import net.minecraft.src.nuclearcontrol.TileEntityInfoPanelRenderer;
import net.minecraft.src.nuclearcontrol.TileEntityRemoteThermo;
import net.minecraft.src.nuclearcontrol.TileEntityRemoteThermoRenderer;
import net.minecraft.src.nuclearcontrol.panel.IPanelDataSource;
import net.minecraft.src.nuclearcontrol.utils.FileHash;

import org.lwjgl.opengl.GL11;

public class mod_IC2NuclearControl extends IC2NuclearControl implements ISaveEventHandler
{
    private static final String CONFIG_NUCLEAR_CONTROL_LANG = "IC2NuclearControl.lang";
    
    private static final String[] builtInAlarms = {"alarm-default.ogg", "alarm-sci-fi.ogg"};
    private static final String OLD_ALARM_HASH = "f0b85b5423d306826f08c7fd7c50188e";
    public static List<String> serverAllowedAlarms;
    private static MsgProcessor msgProcessor;

    public static boolean isClient()
    {
        return true;
    }
    
    @Override
    protected File getConfigFile(String name)
    {
    	return new File(new File(Minecraft.getMinecraftDir(), "config"), name);
    }

    @Override
    public void load()
    {
        instance = this;
        ModLoader.setInGameHook(this, true, false);

        MinecraftForgeClient.preloadTexture("/img/texture_thermo.png");
        msgProcessor = new MsgProcessor();
        MinecraftForge.registerChatHandler(msgProcessor);
        MinecraftForge.registerSaveHandler(this);
        Configuration configuration;
        try
        {
        	File file = getConfigFile(CONFIG_NUCLEAR_CONTROL);
        	if(!file.exists()){
        		configuration = importConfig();
        	}
        	else
        	{
            	configuration = new Configuration(file);
            	configuration.load();
        	}
        }
        catch (IOException ioexception)
        {
            ioexception.printStackTrace();
            configuration = null;
        }

        initBlocks(configuration);
        registerBlocks();
        addNames();
        importSound(configuration);
        remoteThermalMonitorEnergyConsumption = new Integer(configuration.getOrCreateIntProperty("remoteThermalMonitorEnergyConsumption", Configuration.CATEGORY_GENERAL, 1).value).intValue();
        screenRefreshPeriod = new Integer(configuration.getOrCreateIntProperty("infoPanelRefreshPeriod", Configuration.CATEGORY_GENERAL, 20).value).intValue();
        TileEntityIC2ThermoRenderer renderThermalMonitor = new TileEntityIC2ThermoRenderer();
        TileEntityRemoteThermoRenderer renderRemoteThermo = new TileEntityRemoteThermoRenderer();
        TileEntityInfoPanelRenderer renderInfoPanel = new TileEntityInfoPanelRenderer(); 
        
        ModLoader.registerTileEntity(net.minecraft.src.nuclearcontrol.TileEntityIC2Thermo.class, "IC2Thermo", renderThermalMonitor);
        ModLoader.registerTileEntity(net.minecraft.src.nuclearcontrol.TileEntityHowlerAlarm.class, "IC2HowlerAlarm");
        ModLoader.registerTileEntity(net.minecraft.src.nuclearcontrol.TileEntityIndustrialAlarm.class, "IC2IndustrialAlarm");
        ModLoader.registerTileEntity(net.minecraft.src.nuclearcontrol.TileEntityRemoteThermo.class, "IC2RemoteThermo", renderRemoteThermo);
        ModLoader.registerTileEntity(net.minecraft.src.nuclearcontrol.TileEntityInfoPanel.class, "IC2NCInfoPanel", renderInfoPanel);
        ModLoader.registerTileEntity(net.minecraft.src.nuclearcontrol.TileEntityInfoPanelExtender.class, "IC2NCInfoPanelExtender");
        ModLoader.registerTileEntity(net.minecraft.src.nuclearcontrol.TileEntityEnergyCounter.class, "IC2NCEnergyCounter");
        modelId = ModLoader.getUniqueBlockModelID(this, true);
        MinecraftForge.setGuiHandler(this, this);
        if(configuration!=null)
        {
        	configuration.save();
        }
    }
    
    private void importSound(Configuration configuration)
    {
        File soundDir =  new File(new File(Minecraft.getMinecraftDir(), "resources"), "newsound");
        File ncSoundDir = new File(soundDir, "ic2nuclearControl");
        if(!ncSoundDir.exists()){
            ncSoundDir.mkdirs();
        }
        File alarmFile = new File(ncSoundDir, "alarm.ogg");
        if(alarmFile.exists())//v.1.1.6 -> 1.1.7 migration code
        {
            if(OLD_ALARM_HASH.equals(FileHash.getMD5Checksum(alarmFile)))
            {
                alarmFile.delete();
            }
            else
            {
                alarmFile.renameTo(new File(ncSoundDir, "alarm-custom.ogg"));
            }
        }
        for (String alarmName : builtInAlarms)
        {
            alarmFile = new File(ncSoundDir, alarmName);
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
                    ModLoader.getLogger().log(Level.WARNING, LOG_PREFIX + "Can't import sound file");
                }
            }
        }
        
        File[] alarms = ncSoundDir.listFiles(new FilenameFilter() { 
            public boolean accept(File dir, String filename)
            { return filename.endsWith(".ogg") && filename.startsWith("alarm-"); }
        });
        
        availableAlarms = new ArrayList<String>();
        SoundPool pool = SoundHelper.getSoundPool();
        boolean isGetRandomSound = pool.isGetRandomSound;
        pool.isGetRandomSound = false;
        SoundManager sndManager = ModLoader.getMinecraftInstance().sndManager;  
        for(File alarmItem: alarms)
        {
            String name = alarmItem.getName();
            name = name.substring(6, name.length()-4);
            availableAlarms.add(name);
            sndManager.addSound("ic2nuclearControl/alarm-"+name+".ogg", alarmItem);
        }
        pool.isGetRandomSound = isGetRandomSound;

        alarmRange = new Integer(configuration.getOrCreateIntProperty("alarmRange", Configuration.CATEGORY_GENERAL, 64).value).intValue();
        SMPMaxAlarmRange = new Integer(configuration.getOrCreateIntProperty("SMPMaxAlarmRange", Configuration.CATEGORY_GENERAL, 256).value).intValue();
        serverAllowedAlarms = new ArrayList<String>();
    }
    
    @Override
    public void renderInvBlock(RenderBlocks render, Block block, int metadata, int model)
    {
        if(model == modelId){
            float[] size = BlockNuclearControlMain.blockSize[metadata];
            block.setBlockBounds(size[0], size[1], size[2], size[3], size[4], size[5]);
            Tessellator tesselator = Tessellator.instance;
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            tesselator.startDrawingQuads();
            tesselator.setNormal(0.0F, -1.0F, 0.0F);
            render.renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(0, metadata));
            tesselator.draw();
            tesselator.startDrawingQuads();
            tesselator.setNormal(0.0F, 1.0F, 0.0F);
            render.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(1, metadata));
            tesselator.draw();
            tesselator.startDrawingQuads();
            tesselator.setNormal(0.0F, 0.0F, -1.0F);
            render.renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(2, metadata));
            tesselator.draw();
            tesselator.startDrawingQuads();
            tesselator.setNormal(0.0F, 0.0F, 1.0F);
            render.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(3, metadata));
            tesselator.draw();
            tesselator.startDrawingQuads();
            tesselator.setNormal(-1.0F, 0.0F, 0.0F);
            render.renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(4, metadata));
            tesselator.draw();
            tesselator.startDrawingQuads();
            tesselator.setNormal(1.0F, 0.0F, 0.0F);
            render.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(5, metadata));
            tesselator.draw();
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        }
    }
    
    @Override
    public boolean renderWorldBlock(RenderBlocks render, IBlockAccess blockAccess, int x, int y, int z, Block block, int model)
    {
       if(model == modelId)
       {
           TileEntity tileEntity = blockAccess.getBlockTileEntity(x, y, z);
           if(tileEntity instanceof IRotation)
           {
               switch(((IRotation) tileEntity).getFacing())
               {
                   case 0:
                       render.uvRotateBottom = ((IRotation) tileEntity).getRotation();
                       break;
                   case 1:
                       render.uvRotateTop = ((IRotation) tileEntity).getRotation();
                       break;
                   case 2:
                       render.uvRotateEast = ((IRotation) tileEntity).getRotation();
                       break;
                   case 3:
                       render.uvRotateWest = ((IRotation) tileEntity).getRotation();
                       break;
                   case 4:
                       render.uvRotateNorth = ((IRotation) tileEntity).getRotation();
                       break;
                   case 5:
                       render.uvRotateSouth = ((IRotation) tileEntity).getRotation();
                       break;
                       
               }
           }
           render.renderStandardBlock(block, x, y, z);
           render.uvRotateBottom = 0;
           render.uvRotateEast = 0;
           render.uvRotateNorth= 0;
           render.uvRotateSouth = 0;
           render.uvRotateTop = 0;
           render.uvRotateWest = 0;
           return true;
       }
       return false;
    }

    private static void setPhrase(Configuration configuration, String key, String defaultValue)
    {
        configuration.getOrCreateProperty(key, "locale.en.US", defaultValue);
    }
    
    private static void setPhraseRename(Configuration configuration, String key, String oldValue, String defaultValue)
    {
        Property property = configuration.getOrCreateProperty(key, "locale.en.US", defaultValue);
        if(oldValue.equals(property.value))
        {
            property.value = defaultValue;
        }
    }
    
    public void addNames()
    {
        try
        {
            Configuration configuration = new Configuration(getConfigFile(CONFIG_NUCLEAR_CONTROL_LANG));
            configuration.load();
            setPhrase(configuration, "item.ItemToolThermometer.name","Thermometer");
            setPhrase(configuration, "item.ItemToolDigitalThermometer.name", "Digital Thermometer");
            setPhrase(configuration, "item.ItemRemoteSensorKit.name", "Remote Sensor Kit");
            setPhrase(configuration, "item.ItemEnergySensorKit.name", "Energy Sensor Kit");
            setPhrase(configuration, "item.ItemCounterSensorKit.name", "Counter Sensor Kit");
            setPhraseRename(configuration, "item.ItemSensorLocationCard.name", "Sensor Location Card", "Reactor Sensor Location Card");
            setPhrase(configuration, "item.ItemEnergySensorLocationCard.name", "Energy Sensor Location Card");
            setPhrase(configuration, "item.ItemCounterSensorLocationCard.name", "Counter Sensor Location Card");
            setPhrase(configuration, "item.ItemEnergyArrayLocationCard.name", "Energy Array Location Card");
            setPhrase(configuration, "item.ItemRangeUpgrade.name", "Range Upgrade");
            setPhrase(configuration, "item.ItemTimeCard.name", "Time Card");
            setPhrase(configuration, "tile.blockThermalMonitor.name", "Thermal Monitor");
            setPhrase(configuration, "tile.blockIndustrialAlarm.name", "Industrial Alarm");
            setPhrase(configuration, "tile.blockHowlerAlarm.name", "Howler Alarm");
            setPhrase(configuration, "tile.blockRemoteThermo.name", "Remote Thermal Monitor");
            setPhraseRename(configuration, "tile.blockInfoPanel.name", "Reactor Information Panel", "Industrial Information Panel");
            setPhrase(configuration, "tile.blockInfoPanelExtender.name", "Information Panel Extender");
            setPhrase(configuration, "tile.blockEnergyCounter.name", "Energy Counter");

            setPhrase(configuration, "msg.nc.HowlerAlarmSoundRange", "Sound range: %s");
            setPhrase(configuration, "msg.nc.HowlerAlarmSound", "Sound");
            setPhrase(configuration, "msg.nc.ThermalMonitorSave", "Save setting");
            setPhrase(configuration, "msg.nc.ThermalMonitorSignalAt", "Signal at %s heat");
            setPhrase(configuration, "msg.nc.Thermo", "Hull heat: %s");
            setPhrase(configuration, "msg.nc.ThermoDigital", "Hull heat: %s (Water evaporate: %s / melting: %s)");
            setPhrase(configuration, "msg.nc.SensorKit", "Remote Sensor mounted, Sensor Location Card received");

            setPhrase(configuration, "msg.nc.InfoPanelOutOfRange", "Out Of Range");
            setPhrase(configuration, "msg.nc.InfoPanelInvalidCard", "Invalid Card");
            setPhrase(configuration, "msg.nc.InfoPanelNoTarget", "Target Not Found");
            
            setPhrase(configuration, "msg.nc.InfoPanelOn", "On");
            setPhrase(configuration, "msg.nc.InfoPanelOff", "Off");
            setPhrase(configuration, "msg.nc.InfoPanelHeat", "T: %s");
            setPhrase(configuration, "msg.nc.InfoPanelMaxHeat", "MaxHeat: %s");
            setPhrase(configuration, "msg.nc.InfoPanelMelting", "Melting: %s");
            setPhrase(configuration, "msg.nc.InfoPanelOutput", "Output: %sEU/t");
            setPhrase(configuration, "msg.nc.InfoPanelTimeRemaining", "Remaining: %s");

            setPhrase(configuration, "msg.nc.InfoPanelEnergy", "Energy: %s");
            setPhrase(configuration, "msg.nc.InfoPanelEnergyFree", "Free: %s");
            setPhrase(configuration, "msg.nc.InfoPanelEnergyStorage", "Storage: %s");
            setPhrase(configuration, "msg.nc.InfoPanelEnergyPercentage", "Fill: %s%%");
            
            setPhrase(configuration, "msg.nc.InfoPanelEnergyN", "#%d Energy: %s");
            setPhrase(configuration, "msg.nc.InfoPanelEnergyFreeN", "#%d Free: %s");
            setPhrase(configuration, "msg.nc.InfoPanelEnergyStorageN", "#%d Storage: %s");
            setPhrase(configuration, "msg.nc.InfoPanelEnergyPercentageN", "#%d Fill: %s%%");
            
            setPhrase(configuration, "msg.nc.cbInfoPanelOnOff", "On/Off status");
            setPhrase(configuration, "msg.nc.cbInfoPanelHeat", "Heat level");
            setPhrase(configuration, "msg.nc.cbInfoPanelMaxHeat", "Max heat");
            setPhrase(configuration, "msg.nc.cbInfoPanelMelting", "Melting temp");
            setPhrase(configuration, "msg.nc.cbInfoPanelOutput", "Output (EU/t)");
            setPhrase(configuration, "msg.nc.cbInfoPanelTimeRemaining", "Time to cycle end");

            setPhrase(configuration, "msg.nc.cbInfoPanelEnergyCurrent", "Energy");
            setPhrase(configuration, "msg.nc.cbInfoPanelEnergyStorage", "Storage");
            setPhrase(configuration, "msg.nc.cbInfoPanelEnergyFree", "Free");
            setPhrase(configuration, "msg.nc.cbInfoPanelEnergyEach", "Each card info");
            setPhrase(configuration, "msg.nc.cbInfoPanelEnergyTotal", "Summary");
            setPhrase(configuration, "msg.nc.cbInfoPanelEnergyPercentage", "Fill percentage");
            setPhrase(configuration, "msg.nc.EnergyCardQuantity", "Cards quantity: %d");
            
            setPhrase(configuration, "msg.nc.Reset", "Reset");
            
            for(Map.Entry<String, Map<String, Property>> category : configuration.categories.entrySet())
            {
                String rawLocale = category.getKey(); 
                if(rawLocale == null || !rawLocale.startsWith("locale."))
                    continue;
                rawLocale = rawLocale.substring(7);
                String[] chunks = rawLocale.split("\\.");
                Locale locale;
                if(chunks.length>1)
                    locale = new Locale(chunks[0], chunks[1]);
                else
                    locale = new Locale(chunks[0]);
                
                for(Property property : category.getValue().values())
                {
                    ModLoader.addLocalization(property.name, locale.toString(),  new String(property.value.getBytes("8859_1"),"UTF-8"));
                }
            
            }
            configuration.save();
        }
        catch (Exception exception)
        {
            ModLoader.getLogger().log(Level.SEVERE, 
                    LOG_PREFIX + "Error occured while loading "+CONFIG_NUCLEAR_CONTROL_LANG);
            exception.printStackTrace();
        }
    }

    public static void chatMessage(EntityPlayer entityplayer, String message)
    {
    	ModLoader.getMinecraftInstance().ingameGUI.addChatMessage(msgProcessor.onClientChatRecv(message));
    }
    
    @Override
    public Object getGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
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
    
    public static void setNewAlarmSound(int x, int y, int z, String soundName)
    {
        ByteArrayOutputStream arrayOutput = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(arrayOutput);
        try
        {
            output.writeInt(x);
            output.writeInt(y);
            output.writeInt(z);
            output.writeUTF(soundName);
            Packet250CustomPayload packet = new Packet250CustomPayload();
            packet.channel = NETWORK_CHANNEL_NAME;
            packet.isChunkDataPacket = false;
            packet.data = arrayOutput.toByteArray();
            packet.length = arrayOutput.size();
            ModLoader.getMinecraftInstance().getSendQueue().addToSendQueue(packet);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onPacketData(NetworkManager network, String channel, byte[] data)
    {
        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(data));
        try
        {
            World world;
            int x,y,z;
            TileEntity ent;
            TileEntityInfoPanel panel;
            short packetType = dataStream.readShort();
            switch (packetType)
            {
                case PACKET_ALARM:
                    maxAlarmRange = dataStream.readInt();
                    serverAllowedAlarms = new ArrayList<String>(Arrays.asList(dataStream.readUTF().split(",")));
                    break;
                case PACKET_SENSOR:
                    world = ModLoader.getMinecraftInstance().theWorld;
                    x = dataStream.readInt();
                    y = dataStream.readInt();
                    z = dataStream.readInt();
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
                    int fieldCount =  dataStream.readShort();
                    for(int i=0; i<fieldCount; i++)
                    {
                        String name = dataStream.readUTF();
                        int value = dataStream.readInt();
                        card.networkUpdate(name, value, stack);
                    }
                    break;
                case PACKET_SENSOR_TITLE:
                    world = ModLoader.getMinecraftInstance().theWorld;
                    x = dataStream.readInt();
                    y = dataStream.readInt();
                    z = dataStream.readInt();
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
                    ((IPanelDataSource)itemStack.getItem()).setTitle(itemStack, dataStream.readUTF());
                    break;
    
                default:
                    ModLoader.getLogger().log(Level.WARNING, LOG_PREFIX + "Unknown packet type: "+packetType);
                    break;
            }
            
        } catch (IOException e)
        {
            ModLoader.getLogger().log(Level.WARNING, LOG_PREFIX + "Invalid packet: " + e.getMessage());
        }
    }

    @Override
    public void onWorldLoad(World world)
    {
        //new screen manager for new world
        screenManager = new ScreenManager();
    }

    @Override
    public void onWorldSave(World world)
    {
    }

    @Override
    public void onChunkLoad(World world, Chunk chunk)
    {
    }

    @Override
    public void onChunkUnload(World world, Chunk chunk)
    {
    }

    @Override
    public void onChunkSaveData(World world, Chunk chunk, NBTTagCompound data)
    {
    }

    @Override
    public void onChunkLoadData(World world, Chunk chunk, NBTTagCompound data)
    {
    }
    
    public static void setSensorCardField(TileEntityInfoPanel panel,  Map<String, Integer> fields)
    {
        
    }    
}
