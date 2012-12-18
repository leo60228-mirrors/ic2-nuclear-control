package shedar.mods.ic2.nuclearcontrol;

import java.io.File;
import java.util.Locale;
import java.util.Map;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class LanguageHelper
{
    
    private static void setPhrase(Configuration configuration, String key, String defaultValue)
    {
        configuration.get("locale.en.US", key, defaultValue);
    }
    
    private static void setPhraseRename(Configuration configuration, String key, String oldValue, String defaultValue)
    {
        Property property = configuration.get("locale.en.US", key, defaultValue);
        if(oldValue.equals(property.value))
        {
            property.value = defaultValue;
        }
    }
    
    public static void addNames(File file)
    {
        try
        {
            Configuration configuration = new Configuration(file);
            configuration.load();
            setPhrase(configuration, "item.ItemToolThermometer.name","Thermometer");
            setPhrase(configuration, "item.ItemToolDigitalThermometer.name", "Digital Thermometer");
            setPhrase(configuration, "item.ItemRemoteSensorKit.name", "Remote Sensor Kit");
            setPhrase(configuration, "item.ItemEnergySensorKit.name", "Energy Sensor Kit");
            setPhrase(configuration, "item.ItemCounterSensorKit.name", "Counter Sensor Kit");
            setPhrase(configuration, "item.ItemLiquidSensorKit.name", "Liquid Sensor Kit");
            setPhraseRename(configuration, "item.ItemSensorLocationCard.name", "Sensor Location Card", "Reactor Sensor Location Card");
            setPhrase(configuration, "item.ItemEnergySensorLocationCard.name", "Energy Sensor Location Card");
            setPhrase(configuration, "item.ItemCounterSensorLocationCard.name", "Counter Sensor Location Card");
            setPhrase(configuration, "item.ItemLiquidSensorLocationCard.name", "Liquid Sensor Location Card");
            setPhrase(configuration, "item.ItemEnergyArrayLocationCard.name", "Energy Array Location Card");
            setPhrase(configuration, "item.ItemRangeUpgrade.name", "Range Upgrade");
            setPhrase(configuration, "item.ItemColorUpgrade.name", "Color Upgrade");
            setPhrase(configuration, "item.ItemTimeCard.name", "Time Card");
            setPhrase(configuration, "item.ItemTextCard.name", "Text Card");

            setPhrase(configuration, "tile.blockThermalMonitor.name", "Thermal Monitor");
            setPhrase(configuration, "tile.blockIndustrialAlarm.name", "Industrial Alarm");
            setPhrase(configuration, "tile.blockHowlerAlarm.name", "Howler Alarm");
            setPhrase(configuration, "tile.blockRemoteThermo.name", "Remote Thermal Monitor");
            setPhraseRename(configuration, "tile.blockInfoPanel.name", "Reactor Information Panel", "Industrial Information Panel");
            setPhrase(configuration, "tile.blockInfoPanelExtender.name", "Information Panel Extender");
            setPhrase(configuration, "tile.blockEnergyCounter.name", "Energy Counter");
            setPhrase(configuration, "tile.blockAverageCounter.name", "Average Counter");
            setPhrase(configuration, "tile.blockRangeTrigger.name", "Range Trigger");

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

            setPhrase(configuration, "msg.nc.InfoPanelEnergyCounter", "Energy: %sEU");

            setPhrase(configuration, "msg.nc.InfoPanelEnergy", "Energy: %s");
            setPhrase(configuration, "msg.nc.InfoPanelEnergyFree", "Free: %s");
            setPhrase(configuration, "msg.nc.InfoPanelEnergyStorage", "Storage: %s");
            setPhrase(configuration, "msg.nc.InfoPanelEnergyPercentage", "Fill: %s%%");
            
            setPhrase(configuration, "msg.nc.cbInfoPanelLiquidName", "Liquid name");
            setPhrase(configuration, "msg.nc.cbInfoPanelLiquidAmount", "Amount");
            setPhrase(configuration, "msg.nc.cbInfoPanelLiquidCapacity", "Capacity");
            setPhrase(configuration, "msg.nc.cbInfoPanelLiquidFree", "Free");
            setPhrase(configuration, "msg.nc.cbInfoPanelLiquidPercentage", "Fill percentage");
            
            setPhrase(configuration, "msg.nc.InfoPanelLiquidName", "Name: %s");
            setPhrase(configuration, "msg.nc.InfoPanelLiquidAmount", "Amount: %s");
            setPhrase(configuration, "msg.nc.InfoPanelLiquidCapacity", "Capacity: %s");
            setPhrase(configuration, "msg.nc.InfoPanelLiquidFree", "Free: %s");
            setPhrase(configuration, "msg.nc.InfoPanelLiquidPercentage", "Fill: %s%%");
            
            setPhrase(configuration, "msg.nc.InfoPanelEnergyN", "#%d Energy: %s");
            setPhrase(configuration, "msg.nc.InfoPanelEnergyFreeN", "#%d Free: %s");
            setPhrase(configuration, "msg.nc.InfoPanelEnergyStorageN", "#%d Storage: %s");
            setPhrase(configuration, "msg.nc.InfoPanelEnergyPercentageN", "#%d Fill: %s%%");

            setPhrase(configuration, "msg.nc.InfoPanelTime", "Time: %s");
            
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
            setPhrase(configuration, "msg.nc.AverageCounterPeriod", "Period: %ssec");
            
            setPhrase(configuration, "msg.nc.Reset", "Reset");
            setPhrase(configuration, "msg.nc.ScreenColor", "Screen Color");
            setPhrase(configuration, "msg.nc.TextColor", "Text Color");
            setPhrase(configuration, "msg.nc.None", "N/A");
            
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
                    LanguageRegistry.instance().addStringLocalization(property.getName(), locale.toString(), property.value);
                }
            
            }
            configuration.save();
        }
        catch (Exception exception)
        {
            FMLLog.severe(IC2NuclearControl.LOG_PREFIX + "Error occured while loading "+file.getAbsolutePath()+exception.toString());
        }
    }
}
