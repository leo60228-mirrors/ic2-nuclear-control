package shedar.mods.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;


public class StatisticReport
{
    private class StatisticObject
    {
        public String prefix;
        public String version;
        
        public StatisticObject(String prefix, String version)
        {
            this.prefix = prefix;
            this.version = version;
        }
    }
    
    private static final String version = "2";
    
    private static final String urlTemplate = "http://nc.bqt.me/statv%s?user=%s&data=%s&sign=%s";

    public static StatisticReport instance = new StatisticReport();
    
    private boolean initialized;

    private List<StatisticObject> data;
    
    public StatisticReport()
    {
        initialized = false;
    }
    
    private void init()
    {
        initialized = true;
        data = new ArrayList<StatisticReport.StatisticObject>();
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public void add(String prefix, String version)
    {
        if(!initialized)
            init();
        data.add(new StatisticObject(prefix, version));
    }
    
    private static String toHexString(byte[] bytes) {
        char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j*2] = hexArray[v/16];
            hexChars[j*2 + 1] = hexArray[v%16];
        }
        return new String(hexChars);
    }
    
    private String getPlayerId() throws IOException
    {
        File statDir =  new File(Minecraft.getMinecraftDir(), "stats");
        if(!statDir.exists())
        {
            statDir.mkdirs();
        }
        String mac = "";
        try
        {
            InetAddress address = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(address);
            byte[] macArray = ni.getHardwareAddress();
            if(macArray != null)
            {
                mac = toHexString(macArray);
            }
        }
        catch(Exception ex)
        {
        }
        File uidFile = new File(statDir, "player.uid");
        if(uidFile.exists() && uidFile.canRead() && uidFile.length() == 32+mac.length())
        {
            String data = Files.toString(uidFile, Charsets.US_ASCII);
            String storedMac = data.substring(32);
            if(storedMac.equalsIgnoreCase(mac))
                return data.substring(0, 32);
        }
        uidFile.createNewFile();
        if(uidFile.canWrite())
        {
            String uid = UUID.randomUUID().toString().replace("-", "");
            FileOutputStream output = new FileOutputStream(uidFile);
            output.write((uid+mac).getBytes());
            output.close();
            return uid;
        }
        return "";
    }
    
    private String getSignature(String data)
    {
        return Hashing.md5().hashString(data).toString();
    }
    
    private String getData()
    {
        StringBuilder b = new StringBuilder();
        for (StatisticObject item : data)
        {
            b.append(item.prefix).append("-").append(item.version).append("!");
        }
        return b.toString();
    }
    
    @ForgeSubscribe
    public void postInit(WorldEvent.Load event)
    {
        if(data!=null && data.size()>0 && FMLCommonHandler.instance().getSide().isClient())
        {
            try
            {
                String data = getData();
                String hash = getSignature(data);
                URL url = new URL(String.format(urlTemplate, version, getPlayerId(), data, hash));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(1000);
                connection.setReadTimeout(1000);
                connection.getInputStream();        
            } catch (MalformedURLException e)
            {
                FMLLog.warning("Invalid stat report url");
            } catch (IOException e)
            {
                FMLLog.info("Stat wasn't reported");
            }
            data.clear();
        }
    }    
    
}
