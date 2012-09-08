package shedar.mods.ic2.nuclearcontrol;

import net.minecraft.src.Entity;
import net.minecraft.src.GameSettings;
import net.minecraft.src.SoundManager;
import net.minecraft.src.SoundPoolEntry;
import paulscode.sound.SoundSystem;
import cpw.mods.fml.client.FMLClientHandler;

public class SoundHelper
{
    private static final String SOUND_ID_PREFIX = "ic2NuclearControl_";
    private static final float DEFAULT_RANGE = 16F; 
    
    private static int internalId = 0;

    
    private static String playSound(String name, float x, float y, float z, float volume)
    {
        SoundManager mgr = FMLClientHandler.instance().getClient().sndManager;
        GameSettings settings =  FMLClientHandler.instance().getClient().gameSettings; 
        if (settings.soundVolume != 0.0F)
        {
            SoundSystem sndSystem = SoundManager.sndSystem;
            if(sndSystem == null)
                return null;
            SoundPoolEntry sound = mgr.soundPoolSounds.getRandomSoundFromSoundPool(name);

            if (sound != null && volume > 0.0F)
            {
                internalId = (internalId + 1) % 256;
                String soundId = SOUND_ID_PREFIX + internalId;
                float range = DEFAULT_RANGE;

                if (volume > 1.0F)
                {
                    range *= volume;
                }

                sndSystem.newSource(volume > 1.0F, soundId, sound.soundUrl, sound.soundName, false, x, y, z, 2, range);
                sndSystem.setPitch(soundId, 1);

                if (volume > 1.0F)
                {
                    volume = 1.0F;
                }

                sndSystem.setVolume(soundId, volume * settings.soundVolume);
                sndSystem.play(soundId);
                return soundId;
            }
        }
        return null;
    }

    public static String playAlarm(double x, double y, double z, String name, float volume)
    {
        float range = DEFAULT_RANGE;

        if (volume > 1.0F)
        {
            range *= volume;
        }

        Entity person = FMLClientHandler.instance().getClient().renderViewEntity;

        if (person!= null && person.getDistanceSq(x, y, z) < (double)(range * range))
        {
            return playSound(name, (float)x, (float)y, (float)z, volume);
        }
        return null;
    }
    
    public static boolean isPlaying(String soundId)
    {
        SoundSystem snd = SoundManager.sndSystem;
        return snd != null && snd.playing(soundId);
    }
    
    public static void stopAlarm(String soundId)
    {
        SoundManager.sndSystem.stop(soundId);
        SoundManager.sndSystem.removeSource(soundId);
    }

}
