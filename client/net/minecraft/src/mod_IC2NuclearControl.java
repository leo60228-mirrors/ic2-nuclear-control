package net.minecraft.src;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.forge.Configuration;
import net.minecraft.src.forge.IGuiHandler;
import net.minecraft.src.forge.MinecraftForge;
import net.minecraft.src.forge.MinecraftForgeClient;
import net.minecraft.src.forge.NetworkMod;
import net.minecraft.src.forge.Property;
import net.minecraft.src.ic2.api.Ic2Recipes;
import net.minecraft.src.ic2.api.Items;
import net.minecraft.src.nuclearcontrol.BlockNuclearControlMain;
import net.minecraft.src.nuclearcontrol.ContainerRemoteThermo;
import net.minecraft.src.nuclearcontrol.GuiIC2Thermo;
import net.minecraft.src.nuclearcontrol.GuiRemoteThermo;
import net.minecraft.src.nuclearcontrol.ItemNuclearControlMain;
import net.minecraft.src.nuclearcontrol.ItemRangeUpgrade;
import net.minecraft.src.nuclearcontrol.ItemRemoteSensorKit;
import net.minecraft.src.nuclearcontrol.ItemSensorLocationCard;
import net.minecraft.src.nuclearcontrol.ItemToolDigitalThermometer;
import net.minecraft.src.nuclearcontrol.ItemToolThermometer;
import net.minecraft.src.nuclearcontrol.ThermometerVersion;
import net.minecraft.src.nuclearcontrol.TileEntityIC2Thermo;
import net.minecraft.src.nuclearcontrol.TileEntityIC2ThermoRenderer;
import net.minecraft.src.nuclearcontrol.TileEntityRemoteThermo;
import net.minecraft.src.nuclearcontrol.TileEntityRemoteThermoRenderer;

public class mod_IC2NuclearControl extends NetworkMod implements IGuiHandler
{
    private static final String CONFIG_NUCLEAR_CONTROL = "IC2NuclearControl.cfg";
    private static final String CONFIG_NUCLEAR_CONTROL_LANG = "IC2NuclearControl.lang";
    private static final String CONFIG_THERMO_BLOCK = "mod_thermo.cfg";
    private static final String CONFIG_THERMOMETER = "IC2Thermometer.cfg";

    public static Item itemToolThermometer;
    public static Item itemToolDigitalThermometer;
    public static Item itemRemoteSensorKit;
    public static Item itemSensorLocationCard;
    public static Item itemRangeUpgrade;
    public static Block blockNuclearControlMain;
    public static int modelId;
    public static float alarmRange;
    private static mod_IC2NuclearControl instance;
    
    @Override
    public boolean clientSideRequired()
    {
        return true;
    }

    @Override
    public boolean serverSideRequired()
    {
        return false;
    }

    public mod_IC2NuclearControl()
    {

    }
    
    public static boolean isClient()
    {
        return true;
    }
    
    private static File getConfigFile(String name)
    {
    	return new File(new File(Minecraft.getMinecraftDir(), "config"), name);
    }
    
    private static Configuration importConfig() throws IOException
    {
    	int blockId = -1;
    	int thermoAnalog = -1;
    	int thermoDigital = -1;
    	Configuration configuration;
    	
    	File file = getConfigFile(CONFIG_THERMO_BLOCK);
    	if(file.exists() && file.canRead())
    	{
        	Properties props = new Properties();
            props.load(new FileInputStream(file));
            blockId = Integer.parseInt(props.getProperty("thermo_blockid", "192"));
    	}

    	file = getConfigFile(CONFIG_THERMOMETER);
    	if(file.exists() && file.canRead())
    	{
            configuration = new Configuration(file);
            configuration.load();
            thermoAnalog = getOldIdFor(configuration, "itemToolThermometer", 31000);
            thermoDigital = getOldIdFor(configuration, "itemToolDigitalThermometer", 31001);
    	}
    	System.out.println("Imported:"+blockId+" "+thermoAnalog+" "+ thermoDigital);

    	file = getConfigFile(CONFIG_NUCLEAR_CONTROL);
        configuration = new Configuration(file);
        configuration.load();
        if(thermoAnalog != -1)
        	getIdFor(configuration, "itemToolThermometer", thermoAnalog, false);
        if(thermoDigital != -1)
        	getIdFor(configuration, "itemToolDigitalThermometer", thermoDigital, false);
        if(blockId != -1)
        	getIdFor(configuration, "blockNuclearControlMain", blockId, true);
        configuration.save();
        return configuration;

    }

    @Override
    public void load()
    {
        instance = this;
        ModLoader.setInGameHook(this, true, false);

        MinecraftForgeClient.preloadTexture("/img/texture_thermo.png");
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
        TileEntityIC2ThermoRenderer renderThermalMonitor = new TileEntityIC2ThermoRenderer();
        TileEntityRemoteThermoRenderer renderRemoteThermo = new TileEntityRemoteThermoRenderer();
        
        ModLoader.registerTileEntity(net.minecraft.src.nuclearcontrol.TileEntityIC2Thermo.class, "IC2Thermo", renderThermalMonitor);
        ModLoader.registerTileEntity(net.minecraft.src.nuclearcontrol.TileEntityHowlerAlarm.class, "IC2HowlerAlarm");
        ModLoader.registerTileEntity(net.minecraft.src.nuclearcontrol.TileEntityIndustrialAlarm.class, "IC2IndustrialAlarm");
        ModLoader.registerTileEntity(net.minecraft.src.nuclearcontrol.TileEntityRemoteThermo.class, "IC2RemoteThermo", renderRemoteThermo);
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
            ncSoundDir.mkdir();
        }
        File alarmFile = new File(ncSoundDir, "alarm.ogg");
        if(!alarmFile.exists()){
            try
            {
                if(!alarmFile.createNewFile() || !alarmFile.canWrite())
                    return;
                InputStream input = getClass().getResourceAsStream("/sound/nuclear-alarm.ogg");
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
            } catch (IOException e)
            {
                System.out.println("[IC2NuclearControl] can't import sound file");
            }
        }
        alarmRange = new Float(configuration.getOrCreateIntProperty("alarmRange", Configuration.CATEGORY_GENERAL, 64).value).floatValue() / 16F;
        ModLoader.getMinecraftInstance().sndManager.addSound("ic2nuclearControl/alarm.ogg", alarmFile);
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
       if(model == modelId){
           render.renderStandardBlock(block, x, y, z);
       }
       return false;
    }

    @Override
    public void modsLoaded()
    {
        super.modsLoaded();
        addRecipes();
    }

    private static int getIdFor(Configuration configuration, String name, int i, boolean block)
    {
        try
        {
            if (block)
                return new Integer(configuration.getOrCreateBlockIdProperty(name, i).value).intValue();
            else
                return new Integer(configuration.getOrCreateIntProperty(name, "item", i).value).intValue();
        } 
        catch (Exception exception)
        {
            System.out.println("Can't get id for :" + name);
        }

        return i;
    }

    private static int getOldIdFor(Configuration configuration, String name, int i)
    {
        try
        {
            return new Integer(configuration.getOrCreateIntProperty(name, "general", i).value).intValue();
        } 
        catch (Exception exception)
        {
            System.out.println("Can't get id for :" + name);
        }

        return i;
    }

    public void initBlocks(Configuration configuration)
    {
		blockNuclearControlMain = new BlockNuclearControlMain(getIdFor(configuration, "blockNuclearControlMain", 192, true), 0)
					.setHardness(0.5F)
					.setBlockName("blockThermalMonitor")
					.setRequiresSelfNotify();
		itemToolThermometer = new ItemToolThermometer(
					getIdFor(configuration, "itemToolThermometer", 31000, false), 
					2, ThermometerVersion.ANALOG)
					.setItemName("ItemToolThermometer");
		itemToolDigitalThermometer = new ItemToolDigitalThermometer(
					getIdFor(configuration, "itemToolDigitalThermometer", 31001, false),
					18, ThermometerVersion.DIGITAL, 1, 80, 80)
					.setItemName("ItemToolDigitalThermometer");
		itemRemoteSensorKit = new ItemRemoteSensorKit(
		            getIdFor(configuration, "itemRemoteSensorKit", 31002, false),34)
		            .setItemName("ItemRemoteSensorKit");
		itemSensorLocationCard = new ItemSensorLocationCard(
		            getIdFor(configuration, "itemSensorLocationCard", 31003, false), 50)
		            .setItemName("ItemSensorLocationCard");
		itemRangeUpgrade = new ItemRangeUpgrade(
                getIdFor(configuration, "itemRangeUpgrade", 31004, false), 66)
                .setItemName("ItemRangeUpgrade");
    }

    public void registerBlocks()
    {
        ModLoader.registerBlock(blockNuclearControlMain, ItemNuclearControlMain.class);
    }

    public void addRecipes()
    {
        ItemStack thermalMonitor = new ItemStack(blockNuclearControlMain, 1, BlockNuclearControlMain.DAMAGE_THERMAL_MONITOR);
        Ic2Recipes.addCraftingRecipe(thermalMonitor, new Object[]
                {
                    "GGG", "GCG", "GRG", 
                        Character.valueOf('G'), Items.getItem("reinforcedGlass"), 
                        Character.valueOf('R'), Item.redstone, 
                        Character.valueOf('C'), Items.getItem("advancedCircuit")
                });
        ItemStack howler = new ItemStack(blockNuclearControlMain, 1, BlockNuclearControlMain.DAMAGE_HOWLER_ALARM);
        Ic2Recipes.addCraftingRecipe(howler, new Object[]
                {
                    "NNN", "ICI", "IRI", 
                        Character.valueOf('I'), Item.ingotIron, 
                        Character.valueOf('R'), Item.redstone, 
                        Character.valueOf('N'), Block.music, 
                        Character.valueOf('C'), Items.getItem("electronicCircuit")
                });

        ItemStack industrialAlarm = new ItemStack(blockNuclearControlMain, 1, BlockNuclearControlMain.DAMAGE_INDUSTRIAL_ALARM);
        Ic2Recipes.addCraftingRecipe(industrialAlarm, new Object[]
                {
                    "GOG", "GHG", "GRG", 
                        Character.valueOf('G'), Items.getItem("reinforcedGlass"), 
                        Character.valueOf('O'), new ItemStack(Item.dyePowder, 1, 14), 
                        Character.valueOf('R'), Item.redstone, 
                        Character.valueOf('H'), howler 
                });

        Ic2Recipes.addCraftingRecipe(new ItemStack(blockNuclearControlMain, 1, BlockNuclearControlMain.DAMAGE_REMOTE_THERMO), new Object[] 
                {
                    " F ", " M ", " T ", 
                        Character.valueOf('T'), thermalMonitor, 
                        Character.valueOf('M'), Items.getItem("machine"), 
                        Character.valueOf('F'), Items.getItem("frequencyTransmitter")
                });
        Ic2Recipes.addCraftingRecipe(new ItemStack(itemToolThermometer, 1), new Object[] 
                {
                    "IG ", "GWG", " GG", 
                        Character.valueOf('G'), Block.glass, 
                        Character.valueOf('I'), Item.ingotIron, 
                        Character.valueOf('W'), Items.getItem("waterCell")
                });
        ItemStack digitalThermometer = new ItemStack(itemToolDigitalThermometer, 1);
        Ic2Recipes.addCraftingRecipe(digitalThermometer, new Object[] 
                {
                    "I  ", "IC ", " GI", 
                        Character.valueOf('G'), Item.lightStoneDust, 
                        Character.valueOf('I'), Items.getItem("refinedIronIngot"), 
                        Character.valueOf('C'), Items.getItem("electronicCircuit")
                });
        Ic2Recipes.addCraftingRecipe(new ItemStack(itemRemoteSensorKit, 1), new Object[] 
                {
                    "  F", " D ", "P  ", 
                        Character.valueOf('P'), Item.paper, 
                        Character.valueOf('D'), digitalThermometer, 
                        Character.valueOf('F'), Items.getItem("frequencyTransmitter")
                });
        Ic2Recipes.addCraftingRecipe(new ItemStack(itemRangeUpgrade, 1), new Object[] 
                {
                    "   ", "CFC", "   ", 
                        Character.valueOf('C'), Items.getItem("insulatedCopperCableItem"), 
                        Character.valueOf('F'), Items.getItem("frequencyTransmitter")
                });
    }
    
    private static void setPhrase(Configuration configuration, String key, String defaultValue)
    {
        configuration.getOrCreateProperty(key, "locale.en.US", defaultValue);
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
            setPhrase(configuration, "item.ItemSensorLocationCard.name", "Sensor Location Card");
            setPhrase(configuration, "item.ItemRangeUpgrade.name", "Range Upgrade");
            setPhrase(configuration, "tile.blockThermalMonitor.name", "Thermal Monitor");
            setPhrase(configuration, "tile.blockIndustrialAlarm.name", "Industrial Alarm");
            setPhrase(configuration, "tile.blockHowlerAlarm.name", "Howler Alarm");
            setPhrase(configuration, "tile.blockRemoteThermo.name", "Remote Thermal Monitor");
            
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
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            System.out.println("[IC2NuclearControl] Error occured while loading "+CONFIG_NUCLEAR_CONTROL_LANG);
        }
    }

    public static void launchGui(World world, int x, int y, int z, EntityPlayer entityplayer, int blockType)
    {
        entityplayer.openGui(instance, blockType, world, x, y, z);
    }

    public static void chatMessage(EntityPlayer entityplayer, String message)
    {
    	ModLoader.getMinecraftInstance().ingameGUI.addChatMessage(message);
    }
    
    @Override
    public Object getGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity= world.getBlockTileEntity(x, y, z);
        switch (ID)
        {
            case BlockNuclearControlMain.DAMAGE_THERMAL_MONITOR:
                return new GuiIC2Thermo(world, x, y, z, player, (TileEntityIC2Thermo)tileEntity);
            case BlockNuclearControlMain.DAMAGE_REMOTE_THERMO:
                ContainerRemoteThermo container = new ContainerRemoteThermo(player, (TileEntityRemoteThermo)tileEntity);
                return new GuiRemoteThermo(container);
            default:
                return null;
        }
    }
    
    @Override
    public String getVersion()
    {
        return "v1.1.6";
    }

}
