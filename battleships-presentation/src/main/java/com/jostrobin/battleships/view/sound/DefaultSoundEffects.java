/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jostrobin.battleships.view.sound;

import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import com.jostrobin.battleships.common.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

/**
 * @author rowyss
 *         Date: 21.12.11 Time: 18:36
 */
public class DefaultSoundEffects implements SoundEffects
{
    private static final Logger logger = LoggerFactory.getLogger(DefaultSoundEffects.class);

    public static final String EXPLOSION_SOUND_FILE_PATH = "sounds/explosion.wav";
    public static final String SPLASH_SOUND_FILE_PATH = "sounds/splash.wav";
    private boolean enabled = true;

    @Override
    @Async
    public void explosion()
    {
        playSound(EXPLOSION_SOUND_FILE_PATH);
    }

    @Override
    @Async
    public void splash()
    {
        playSound(SPLASH_SOUND_FILE_PATH);
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    private void playSound(final String path)
    {
        if (enabled)
        {

            InputStream is = ClassLoader.getSystemResourceAsStream(path);
            AudioInputStream audioInputStream = null;
            try
            {
                audioInputStream = AudioSystem.getAudioInputStream(is);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
                while (clip.isRunning())
                {
                    Thread.sleep(10);
                }
            }
            catch (Exception e)
            {
                logger.warn("Could not play sound effect", e);
            }
            finally
            {
                IOUtils.closeSilently(is, audioInputStream);
            }
        }
    }

}
