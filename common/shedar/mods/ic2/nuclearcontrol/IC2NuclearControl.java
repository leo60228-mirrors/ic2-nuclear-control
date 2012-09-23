package shedar.mods.ic2.nuclearcontrol;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import net.minecraft.src.Block;
import net.minecraft.src.CraftingManager;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ic2.api.Ic2Recipes;
import net.minecraft.src.ic2.api.Items;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod( modid = "IC2NuclearControl", name="Nuclear Control", version="1.3.1", dependencies = "after:IC2")
@NetworkMod(channels = { "NuclearControl" }, clientSideRequired = true, serverSideRequired = false, 
            packetHandler = PacketHandler.class, connectionHandler = ConnectionHandler.class)
public class IC2NuclearControl
{
    public static final String  VER = "1.3.1";
    
    public static final String LOG_PREFIX = "[IC2NuclearControl] ";
    public static final String NETWORK_CHANNEL_NAME = "NuclearControl";
    
    private static final String CONFIG_NUCLEAR_CONTROL_LANG = "IC2NuclearControl.lang";
    public static final String[] builtInAlarms = {"alarm-default.ogg", "alarm-sci-fi.ogg"};
    
    @Instance
    public static IC2NuclearControl instance;
    
    @SidedProxy(clientSide = "shedar.mods.ic2.nuclearcontrol.ClientProxy", serverSide = "shedar.mods.ic2.nuclearcontrol.CommonProxy")
    public static CommonProxy proxy;
    
    protected File configFile;
    protected File configDir;
    
    public String allowedAlarms;
    public List<String> serverAllowedAlarms;
    public Item itemToolThermometer;
    public Item itemToolDigitalThermometer;
    public Item itemRemoteSensorKit;
    public Item itemEnergySensorKit;
    public Item itemCounterSensorKit;
    public Item itemSensorLocationCard;
    public Item itemEnergySensorLocationCard;
    public Item itemCounterSensorLocationCard;
    public Item itemEnergyArrayLocationCard;
    public Item itemTimeCard;
    public Item itemRangeUpgrade;
    public Block blockNuclearControlMain;
    public int modelId;
    public int alarmRange;
    public int SMPMaxAlarmRange;
    public int maxAlarmRange;
    public List<String> availableAlarms;
    public int remoteThermalMonitorEnergyConsumption;
    public ScreenManager screenManager = new ScreenManager();
    public int screenRefreshPeriod;

    public int IC2WrenchId;
    public int IC2ElectricWrenchId;
    
    private String playerId;
    private Boolean statEnabled;
    public boolean isClient;
    
    
    
    @SuppressWarnings("unchecked")
    protected void addRecipes()
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
                    "F", "M", "T", 
                        Character.valueOf('T'), thermalMonitor, 
                        Character.valueOf('M'), Items.getItem("machine"), 
                        Character.valueOf('F'), Items.getItem("frequencyTransmitter")
                });
        Ic2Recipes.addCraftingRecipe(new ItemStack(blockNuclearControlMain, 1, BlockNuclearControlMain.DAMAGE_INFO_PANEL), new Object[] 
                {
                    "PPP", "LCL", "IRI", 
                        Character.valueOf('P'), Block.thinGlass, 
                        Character.valueOf('L'), new ItemStack(Item.dyePowder, 1, 10), 
                        Character.valueOf('I'), new ItemStack(Item.dyePowder, 1, 0), 
                        Character.valueOf('R'), Item.redstone, 
                        Character.valueOf('C'), Items.getItem("electronicCircuit") 
                });
        Ic2Recipes.addCraftingRecipe(new ItemStack(blockNuclearControlMain, 1, BlockNuclearControlMain.DAMAGE_INFO_PANEL_EXTENDER), new Object[] 
                {
                    "PPP", "WLW", "WWW", 
                        Character.valueOf('P'), Block.thinGlass, 
                        Character.valueOf('L'), new ItemStack(Item.dyePowder, 1, 10), 
                        Character.valueOf('W'), Block.planks, 
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
        Ic2Recipes.addCraftingRecipe(new ItemStack(itemEnergySensorKit, 1), new Object[] 
                {
                    "  F", " D ", "P  ", 
                        Character.valueOf('P'), Item.paper, 
                        Character.valueOf('D'), Items.getItem("ecMeter"), 
                        Character.valueOf('F'), Items.getItem("frequencyTransmitter")
                });
        Ic2Recipes.addCraftingRecipe(new ItemStack(itemRangeUpgrade, 1), new Object[] 
                {
                    "CFC", 
                        Character.valueOf('C'), Items.getItem("insulatedCopperCableItem"), 
                        Character.valueOf('F'), Items.getItem("frequencyTransmitter")
                });
        ItemStack energyCounter = new ItemStack(blockNuclearControlMain, 1, BlockNuclearControlMain.DAMAGE_ENERGY_COUNTER);
        Ic2Recipes.addCraftingRecipe(energyCounter, new Object[]
                {
                    " A ", "FTF", 
                        Character.valueOf('A'), Items.getItem("advancedCircuit"), 
                        Character.valueOf('F'), Items.getItem("glassFiberCableItem"), 
                        Character.valueOf('T'), Items.getItem("mvTransformer")
                });
        Ic2Recipes.addCraftingRecipe(new ItemStack(itemCounterSensorKit, 1), new Object[] 
                {
                    "  F", " C ", "P  ", 
                        Character.valueOf('P'), Item.paper, 
                        Character.valueOf('C'), Items.getItem("electronicCircuit"), 
                        Character.valueOf('F'), Items.getItem("frequencyTransmitter")
                });
        Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(itemTimeCard, 1),  
                Items.getItem("electronicCircuit"), Item.pocketSundial);
        Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Items.getItem("electronicCircuit").getItem(), 2),  
                itemSensorLocationCard );
        Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Items.getItem("electronicCircuit").getItem(), 2),  
                itemEnergySensorLocationCard );
        Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Items.getItem("electronicCircuit").getItem(), 2),  
                itemCounterSensorLocationCard);
        CraftingManager.getInstance().getRecipeList().add(new StorageArrayRecipe());
    }
    
    protected static int getIdFor(Configuration configuration, String name, int i, boolean block)
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
            FMLLog.warning(LOG_PREFIX + "Can't get id for:" + name);
        }

        return i;
    }

    protected void initBlocks(Configuration configuration)
    {
        blockNuclearControlMain = new BlockNuclearControlMain(getIdFor(configuration, "blockNuclearControlMain", 192, true), 0)
                .setBlockName("blockThermalMonitor");
        itemToolThermometer = new ItemToolThermometer(
                getIdFor(configuration, "itemToolThermometer", 31000, false), 
                2)
                .setItemName("ItemToolThermometer");
        itemToolDigitalThermometer = new ItemToolDigitalThermometer(
                getIdFor(configuration, "itemToolDigitalThermometer", 31001, false),
                18, 1, 80, 80)
                .setItemName("ItemToolDigitalThermometer");
        itemSensorLocationCard = new ItemCardReactorSensorLocation(
                getIdFor(configuration, "itemSensorLocationCard", 31003, false), 50)
                .setItemName("ItemSensorLocationCard");
        itemRangeUpgrade = new ItemRangeUpgrade(
                getIdFor(configuration, "itemRangeUpgrade", 31004, false), 66)
                .setItemName("ItemRangeUpgrade");
        itemTimeCard = new ItemTimeCard(
                getIdFor(configuration, "itemTimeCard", 31005, false), 48)
                .setItemName("ItemTimeCard");
        itemEnergySensorLocationCard = new ItemCardEnergySensorLocation(
                getIdFor(configuration, "itemEnergySensorLocationCard", 31007, false), 49)
                .setItemName("ItemEnergySensorLocationCard");
        itemEnergyArrayLocationCard = new ItemCardEnergyArrayLocation(
                getIdFor(configuration, "itemEnergyArrayLocationCard", 31008, false), 51)
                .setItemName("ItemEnergyArrayLocationCard");
        itemCounterSensorLocationCard = new ItemCardCounterSensorLocation(
                getIdFor(configuration, "itemCounterSensorLocationCard", 31010, false), 52)
                .setItemName("ItemCounterSensorLocationCard");
        itemCounterSensorKit = new ItemKitCounterSensor(
                getIdFor(configuration, "itemCounterSensorKit", 31009, false), 68)
                .setItemName("ItemCounterSensorKit");
        itemEnergySensorKit = new ItemKitEnergySensor(
                getIdFor(configuration, "itemEnergySensorKit", 31006, false), 65)
                .setItemName("ItemEnergySensorKit");
        itemRemoteSensorKit = new ItemKitReactorSensor(
                getIdFor(configuration, "itemRemoteSensorKit", 31002, false),34)
                .setItemName("ItemRemoteSensorKit");
    }
    
    @PostInit
    public void modsLoaded(FMLPostInitializationEvent evt)
    {
        IC2WrenchId = Items.getItem("wrench").itemID;
        IC2ElectricWrenchId = Items.getItem("electricWrench").itemID;
        addRecipes();
    }    

    public void registerBlocks()
    {
        GameRegistry.registerBlock(blockNuclearControlMain, ItemNuclearControlMain.class);
    }
    
    @PreInit
    public void preInit(FMLPreInitializationEvent event) 
    {
        configFile = event.getSuggestedConfigurationFile();
        configDir = event.getModConfigurationDirectory();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(proxy);
    }

    @Init
    public void init(FMLInitializationEvent evt)
    {
        isClient = evt.getSide().equals(Side.CLIENT);
        Configuration configuration;
        configuration = new Configuration(configFile);
        configuration.load();
        if(isClient)
        {
            MinecraftForgeClient.preloadTexture("/img/texture_thermo.png");
            LanguageHelper.addNames(new File(configDir, CONFIG_NUCLEAR_CONTROL_LANG));
            playerId = configuration.getOrCreateProperty("uuid", Configuration.CATEGORY_GENERAL, UUID.randomUUID().toString()).value.replaceAll(" ", "");
            statEnabled = configuration.getOrCreateBooleanProperty("stat", Configuration.CATEGORY_GENERAL, true).getBoolean(true);
            if(statEnabled)
            {
                URL url;
                try
                {
                    url = new URL("http://nc.bqt.me/stat?user="+playerId+"&version="+VER);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(1000);
                    connection.setReadTimeout(1000);
                    connection.getInputStream();
                } catch (MalformedURLException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        initBlocks(configuration);
        registerBlocks();
        alarmRange = new Integer(configuration.getOrCreateIntProperty("alarmRange", Configuration.CATEGORY_GENERAL, 64).value).intValue();
        maxAlarmRange = new Integer(configuration.getOrCreateIntProperty("maxAlarmRange", Configuration.CATEGORY_GENERAL, 128).value).intValue();
        allowedAlarms = configuration.getOrCreateProperty("allowedAlarms", Configuration.CATEGORY_GENERAL, "default,sci-fi").value.replaceAll(" ", "");
        remoteThermalMonitorEnergyConsumption = new Integer(configuration.getOrCreateIntProperty("remoteThermalMonitorEnergyConsumption", Configuration.CATEGORY_GENERAL, 1).value).intValue();
        screenRefreshPeriod = new Integer(configuration.getOrCreateIntProperty("infoPanelRefreshPeriod", Configuration.CATEGORY_GENERAL, 20).value).intValue();
        SMPMaxAlarmRange = new Integer(configuration.getOrCreateIntProperty("SMPMaxAlarmRange", Configuration.CATEGORY_GENERAL, 256).value).intValue();

        proxy.registerTileEntities();
        NetworkRegistry.instance().registerGuiHandler(instance, proxy);
        configuration.save();
    }
}
