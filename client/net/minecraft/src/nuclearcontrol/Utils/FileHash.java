package net.minecraft.src.nuclearcontrol.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileHash
{
    public static byte[] createChecksum(File file){
        InputStream fis;
        try
        {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            MessageDigest complete = MessageDigest.getInstance("MD5");
            int numRead = fis.read(buffer);
            do {
                if (numRead > 0) {
                    complete.update(buffer, 0, numRead);
                }
                numRead = fis.read(buffer);
            } while (numRead != -1);
            fis.close();
            return complete.digest();
        } catch (FileNotFoundException e)
        {
            return null;
        } catch (NoSuchAlgorithmException e)
        {
            return null;
        } catch (IOException e)
        {
            return null;
        }
    }

    public static String getMD5Checksum(File file) 
    {
        byte[] b = createChecksum(file);
        if(b == null)
            return null;
        StringBuilder result = new StringBuilder();

        for (int i=0; i < b.length; i++) {
            result.append(Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 ));
        }
        return result.toString().toLowerCase();
    }
}
